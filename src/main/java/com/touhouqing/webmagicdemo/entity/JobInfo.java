package com.touhouqing.webmagicdemo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招聘信息
 * </p>
 *
 * @author TouHouQing
 * @since 2025-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("job_info")
public class JobInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("company_name")
    private String companyName;

    @TableField("company_addr")
    private String companyAddr;

    @TableField("company_info")
    private String companyInfo;

    @TableField("job_name")
    private String jobName;

    @TableField("job_addr")
    private String jobAddr;

    @TableField("job_desc")
    private String jobDesc;

    @TableField("salary_min")
    private Float salaryMin;

    @TableField("salary_max")
    private Float salaryMax;

    @TableField("url")
    private String url;

    @TableField("time")
    private String time;


}
