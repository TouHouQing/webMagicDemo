package com.touhouqing.webmagicdemo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招标公告信息
 * </p>
 *
 * @author TouHouQing
 * @since 2025-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("announcement_info")
public class AnnouncementInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目编号
     */
    @TableField("project_number")
    private String projectNumber;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 采购单位
     */
    @TableField("purchase_unit")
    private String purchaseUnit;

    /**
     * 预算金额（万元）
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

    /**
     * 最高限价（万元）
     */
    @TableField("max_price")
    private BigDecimal maxPrice;

    /**
     * 发布日期
     */
    @TableField("publish_date")
    private String publishDate;

    /**
     * 发布来源
     */
    @TableField("publish_source")
    private String publishSource;

    /**
     * 详情URL
     */
    @TableField("detail_url")
    private String detailUrl;

    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 合同履行期限
     */
    @TableField("contract_period")
    private String contractPeriod;

    /**
     * 采购需求描述
     */
    @TableField("procurement_requirements")
    private String procurementRequirements;

    /**
     * 联系方式
     */
    @TableField("contact_info")
    private String contactInfo;

}
