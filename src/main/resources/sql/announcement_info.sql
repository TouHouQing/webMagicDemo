-- 创建招标公告信息表
CREATE TABLE IF NOT EXISTS `announcement_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_number` varchar(100) DEFAULT NULL COMMENT '项目编号',
  `project_name` varchar(500) DEFAULT NULL COMMENT '项目名称',
  `purchase_unit` varchar(200) DEFAULT NULL COMMENT '采购单位',
  `budget_amount` decimal(15,2) DEFAULT NULL COMMENT '预算金额（万元）',
  `max_price` decimal(15,2) DEFAULT NULL COMMENT '最高限价（万元）',
  `publish_date` varchar(50) DEFAULT NULL COMMENT '发布日期',
  `publish_source` varchar(200) DEFAULT NULL COMMENT '发布来源',
  `detail_url` varchar(1000) DEFAULT NULL COMMENT '详情URL',
  `title` varchar(500) DEFAULT NULL COMMENT '公告标题',
  `contract_period` varchar(200) DEFAULT NULL COMMENT '合同履行期限',
  `procurement_requirements` text DEFAULT NULL COMMENT '采购需求描述',
  `contact_info` varchar(500) DEFAULT NULL COMMENT '联系方式',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_number` (`project_number`),
  KEY `idx_publish_date` (`publish_date`),
  KEY `idx_purchase_unit` (`purchase_unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招标公告信息表';
