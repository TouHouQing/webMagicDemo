-- 创建详细采购信息表
CREATE TABLE IF NOT EXISTS `detailed_procurement_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `related_announcement_id` varchar(100) NOT NULL COMMENT '相关公告ID',
    `title` varchar(500) NULL COMMENT '标题',
    `procurement_project_name` varchar(300) NULL COMMENT '采购项目名称',
    `procurement_requirements` text NULL COMMENT '采购需求概况',
    `budget_amount` decimal(15,2) NULL COMMENT '预算金额（万元）',
    `estimated_time` varchar(100) NULL COMMENT '预计采购时间',
    `policy_execution` varchar(200) NULL COMMENT '执行的政府采购政策',
    `procurement_unit` varchar(200) NULL COMMENT '采购单位',
    `publish_date` varchar(50) NULL COMMENT '发布日期',
    `detail_url` varchar(500) NULL COMMENT '详情页URL',
    `remark` text NULL COMMENT '备注信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_related_announcement_id` (`related_announcement_id`),
    KEY `idx_procurement_project_name` (`procurement_project_name`),
    KEY `idx_procurement_unit` (`procurement_unit`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='详细采购信息表';
