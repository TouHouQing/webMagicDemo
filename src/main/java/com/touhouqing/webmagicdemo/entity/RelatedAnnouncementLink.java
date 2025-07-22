package com.touhouqing.webmagicdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相关公告链接实体类
 * 用于存储从详情页抓取到的相关公告链接
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Data
@TableName("related_announcement_link")
public class RelatedAnnouncementLink {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父公告ID（来源公告ID）
     */
    private String parentAnnouncementId;

    /**
     * 相关公告URL
     */
    private String relatedUrl;

    /**
     * 相关公告ID（从URL中提取）
     */
    private String relatedAnnouncementId;

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
