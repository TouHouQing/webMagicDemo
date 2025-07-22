package com.touhouqing.webmagicdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 详细采购信息实体类
 * 用于存储从相关公告页面抓取到的详细采购信息
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Data
@TableName("detailed_procurement_info")
public class DetailedProcurementInfo {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 相关公告ID
     */
    private String relatedAnnouncementId;

    /**
     * 标题
     */
    private String title;

    /**
     * 采购项目名称
     */
    private String procurementProjectName;

    /**
     * 采购需求概况
     */
    private String procurementRequirements;

    /**
     * 预算金额（万元）
     */
    private BigDecimal budgetAmount;

    /**
     * 预计采购时间
     */
    private String estimatedTime;

    /**
     * 执行的政府采购政策
     */
    private String policyExecution;

    /**
     * 采购单位
     */
    private String procurementUnit;

    /**
     * 发布日期
     */
    private String publishDate;

    /**
     * 详情页URL
     */
    private String detailUrl;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
