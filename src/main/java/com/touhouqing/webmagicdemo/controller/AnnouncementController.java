package com.touhouqing.webmagicdemo.controller;

import com.touhouqing.webmagicdemo.component.AnnouncementSpider;
import com.touhouqing.webmagicdemo.component.DetailSpider;
import com.touhouqing.webmagicdemo.component.LinkCollectorSpider;
import com.touhouqing.webmagicdemo.component.RelatedAnnouncementSpider;
import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import com.touhouqing.webmagicdemo.entity.DetailedProcurementInfo;
import com.touhouqing.webmagicdemo.entity.RelatedAnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import com.touhouqing.webmagicdemo.mapper.DetailedProcurementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.RelatedAnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 招标公告前端控制器
 * </p>
 *
 * @author TouHouQing
 * @since 2025-07-22
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementSpider announcementSpider;

    @Autowired
    private LinkCollectorSpider linkCollectorSpider;

    @Autowired
    private DetailSpider detailSpider;

    @Autowired
    private RelatedAnnouncementSpider relatedAnnouncementSpider;

    @Autowired
    private AnnouncementInfoMapper announcementInfoMapper;

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

    @Autowired
    private RelatedAnnouncementLinkMapper relatedAnnouncementLinkMapper;

    @Autowired
    private DetailedProcurementInfoMapper detailedProcurementInfoMapper;

    @GetMapping("/crawler")
    public String doCrawler(){
        announcementSpider.doCrawler();
        return "招标公告爬取任务已启动";
    }

    @GetMapping("/list")
    public List<AnnouncementInfo> getAnnouncements(){
        return announcementInfoMapper.selectList(null);
    }

    @GetMapping("/count")
    public String getAnnouncementCount(){
        Long count = announcementInfoMapper.selectCount(null);
        return "数据库中共有 " + count + " 条招标公告信息";
    }

    // ========== 新的两阶段爬虫接口 ==========

    /**
     * 第一阶段：收集链接
     * 从列表页收集详情页链接并保存到数据库
     */
    @GetMapping("/collect-links")
    public String collectLinks() {
        try {
            linkCollectorSpider.collectLinks();
            return "链接收集任务已启动";
        } catch (Exception e) {
            return "链接收集任务启动失败: " + e.getMessage();
        }
    }

    /**
     * 第二阶段：抓取详情
     * 从数据库读取链接并抓取详情页信息
     */
    @GetMapping("/crawl-details")
    public String crawlDetails(@RequestParam(defaultValue = "10") int batchSize) {
        try {
            detailSpider.crawlDetails(batchSize);
            return "详情抓取任务已启动，批次大小: " + batchSize;
        } catch (Exception e) {
            return "详情抓取任务启动失败: " + e.getMessage();
        }
    }

    /**
     * 查看链接收集状态
     */
    @GetMapping("/link-status")
    public String getLinkStatus() {
        // 统计总链接数
        Long totalLinks = announcementLinkMapper.selectCount(null);

        // 统计已处理的链接数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AnnouncementLink> processedQuery =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        processedQuery.eq("processed", 1);
        Long processedLinks = announcementLinkMapper.selectCount(processedQuery);

        // 统计未处理的链接数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AnnouncementLink> unprocessedQuery =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        unprocessedQuery.eq("processed", 0);
        Long unprocessedLinks = announcementLinkMapper.selectCount(unprocessedQuery);

        return String.format("链接状态统计 - 总计: %d, 已处理: %d, 未处理: %d",
                            totalLinks, processedLinks, unprocessedLinks);
    }

    /**
     * 查看链接列表
     */
    @GetMapping("/links")
    public List<AnnouncementLink> getLinks() {
        return announcementLinkMapper.selectList(null);
    }

    // ========== 第三阶段：相关公告爬虫接口 ==========

    /**
     * 第三阶段：抓取相关公告详细信息
     * 从数据库读取相关公告链接并抓取详细采购信息
     */
    @GetMapping("/crawl-related-announcements")
    public String crawlRelatedAnnouncements(@RequestParam(defaultValue = "5") int batchSize) {
        try {
            relatedAnnouncementSpider.crawlRelatedAnnouncements(batchSize);
            return "相关公告抓取任务已启动，批次大小: " + batchSize;
        } catch (Exception e) {
            return "相关公告抓取任务启动失败: " + e.getMessage();
        }
    }

    /**
     * 查看相关公告链接状态
     */
    @GetMapping("/related-link-status")
    public String getRelatedLinkStatus() {
        // 统计总相关链接数
        Long totalRelatedLinks = relatedAnnouncementLinkMapper.selectCount(null);

        // 统计已处理的相关链接数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RelatedAnnouncementLink> processedQuery =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        processedQuery.eq("processed", 1);
        Long processedRelatedLinks = relatedAnnouncementLinkMapper.selectCount(processedQuery);

        // 统计未处理的相关链接数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RelatedAnnouncementLink> unprocessedQuery =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        unprocessedQuery.eq("processed", 0);
        Long unprocessedRelatedLinks = relatedAnnouncementLinkMapper.selectCount(unprocessedQuery);

        return String.format("相关公告链接状态统计 - 总计: %d, 已处理: %d, 未处理: %d",
                            totalRelatedLinks, processedRelatedLinks, unprocessedRelatedLinks);
    }

    /**
     * 查看相关公告链接列表
     */
    @GetMapping("/related-links")
    public List<RelatedAnnouncementLink> getRelatedLinks() {
        return relatedAnnouncementLinkMapper.selectList(null);
    }

    /**
     * 查看详细采购信息列表
     */
    @GetMapping("/detailed-procurement-info")
    public List<DetailedProcurementInfo> getDetailedProcurementInfo() {
        return detailedProcurementInfoMapper.selectList(null);
    }

    /**
     * 查看详细采购信息数量统计
     */
    @GetMapping("/detailed-procurement-count")
    public String getDetailedProcurementCount() {
        Long count = detailedProcurementInfoMapper.selectCount(null);
        return "数据库中共有 " + count + " 条详细采购信息";
    }

    /**
     * 三阶段爬虫状态总览
     */
    @GetMapping("/three-phase-status")
    public String getThreePhaseStatus() {
        // 第一阶段统计
        Long totalLinks = announcementLinkMapper.selectCount(null);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AnnouncementLink> processedQuery1 =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        processedQuery1.eq("processed", 1);
        Long processedLinks = announcementLinkMapper.selectCount(processedQuery1);

        // 第二阶段统计
        Long announcementCount = announcementInfoMapper.selectCount(null);
        Long totalRelatedLinks = relatedAnnouncementLinkMapper.selectCount(null);

        // 第三阶段统计
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RelatedAnnouncementLink> processedQuery3 =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        processedQuery3.eq("processed", 1);
        Long processedRelatedLinks = relatedAnnouncementLinkMapper.selectCount(processedQuery3);
        Long detailedProcurementCount = detailedProcurementInfoMapper.selectCount(null);

        return String.format(
            "三阶段爬虫状态总览:\n" +
            "第一阶段 - 链接收集: 总计 %d, 已处理 %d\n" +
            "第二阶段 - 基本信息: 招标公告 %d 条, 相关链接 %d 个\n" +
            "第三阶段 - 详细信息: 已处理相关链接 %d 个, 详细采购信息 %d 条",
            totalLinks, processedLinks, announcementCount, totalRelatedLinks,
            processedRelatedLinks, detailedProcurementCount
        );
    }

    /**
     * 调试页面解析
     */
    @GetMapping("/debug-page")
    public String debugPage(@RequestParam String url) {
        try {
            // 使用web-fetch来获取页面内容进行调试
            return "请使用web-fetch工具来调试页面: " + url;
        } catch (Exception e) {
            return "调试失败: " + e.getMessage();
        }
    }
}
