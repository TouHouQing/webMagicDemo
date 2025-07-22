package com.touhouqing.webmagicdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招标公告链接实体类
 * 用于存储从列表页抓取到的详情页链接
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Data
@TableName("announcement_link")
public class AnnouncementLink {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 详情页URL
     */
    private String detailUrl;

    /**
     * 公告ID（从URL中提取）
     */
    private String announcementId;

    /**
     * 是否已处理（0-未处理，1-已处理）
     */
    private Integer processed;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 备注信息
     */
    private String remark;
}
