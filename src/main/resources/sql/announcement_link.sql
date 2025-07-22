-- 创建招标公告链接表
CREATE TABLE IF NOT EXISTS `announcement_link` (
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
