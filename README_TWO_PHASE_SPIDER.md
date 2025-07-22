# 两阶段爬虫系统使用说明

## 概述

本项目实现了一个两阶段的爬虫系统，专门用于抓取天津政府采购网的招标公告信息。

### 问题背景
原来的爬虫系统存在以下问题：
- 从列表页 `http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1&stmp=1753164654401` 只能获取到详情页链接
- 需要通过这些链接（如 `http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=750648883&ver=2`）来获取具体的招标公告信息

### 解决方案
实现两阶段爬虫：
1. **第一阶段**：链接收集 - 从列表页收集所有详情页链接并保存到数据库
2. **第二阶段**：详情抓取 - 从数据库读取链接，访问详情页获取具体信息

## 系统架构

### 数据库表结构

#### announcement_link 表（链接存储表）
```sql
CREATE TABLE `announcement_link` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `detail_url` varchar(500) NOT NULL COMMENT '详情页URL',
    `announcement_id` varchar(100) NOT NULL COMMENT '公告ID（从URL中提取）',
    `processed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已处理（0-未处理，1-已处理）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `process_time` datetime NULL COMMENT '处理时间',
    `remark` varchar(255) NULL COMMENT '备注信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_announcement_id` (`announcement_id`),
    KEY `idx_processed` (`processed`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招标公告链接表';
```

### 核心组件

#### 第一阶段：链接收集
- `LinkCollectorPageProcessor` - 列表页处理器
- `LinkCollectorPipeline` - 链接保存管道
- `LinkCollectorSpider` - 链接收集爬虫

#### 第二阶段：详情抓取
- `DetailPageProcessor` - 详情页处理器
- `DetailPipeline` - 详情信息保存管道
- `DetailSpider` - 详情抓取爬虫

## API 接口

### 原有接口
- `GET /announcement/crawler` - 启动原有的一体化爬虫
- `GET /announcement/list` - 查看所有招标公告
- `GET /announcement/count` - 查看招标公告数量统计

### 新增接口

#### 1. 收集链接（第一阶段）
```
GET /announcement/collect-links
```
从列表页收集详情页链接并保存到数据库。

#### 2. 抓取详情（第二阶段）
```
GET /announcement/crawl-details?batchSize=10
```
从数据库读取未处理的链接并抓取详情信息。
- `batchSize`：每批处理的链接数量，默认为10

#### 3. 查看链接状态
```
GET /announcement/link-status
```
查看链接收集和处理状态统计。

#### 4. 查看链接列表
```
GET /announcement/links
```
查看所有收集到的链接。

## 使用流程

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 创建数据库表
执行 `src/main/resources/sql/announcement_link.sql` 中的SQL语句创建链接表。

### 3. 第一阶段：收集链接
```bash
curl http://localhost:8080/announcement/collect-links
```

### 4. 查看链接收集状态
```bash
curl http://localhost:8080/announcement/link-status
```

### 5. 第二阶段：抓取详情
```bash
# 抓取10个链接的详情（默认）
curl http://localhost:8080/announcement/crawl-details

# 或指定批次大小
curl http://localhost:8080/announcement/crawl-details?batchSize=5
```

### 6. 查看结果
```bash
# 查看招标公告数量
curl http://localhost:8080/announcement/count

# 查看所有招标公告
curl http://localhost:8080/announcement/list
```

## 优势

1. **可控性**：可以分别控制链接收集和详情抓取的进度
2. **可恢复性**：如果详情抓取中断，可以从数据库中继续处理未完成的链接
3. **去重性**：自动去重，避免重复收集相同的链接
4. **批量处理**：支持批量处理，可以控制每次处理的数量
5. **状态跟踪**：可以实时查看处理进度和状态

## 测试

运行测试类验证功能：
```bash
mvn test -Dtest=TwoPhaseSpiderTest
```

测试包括：
- 链接收集测试
- 详情抓取测试
- 完整流程测试
- 数据库状态查看

## 注意事项

1. 确保数据库连接正常
2. 第一次使用前需要创建 `announcement_link` 表
3. 建议先小批量测试，确认无误后再大批量处理
4. 网络请求有延时设置，避免对目标网站造成过大压力
5. 如果遇到反爬虫机制，可以调整请求头和延时设置
