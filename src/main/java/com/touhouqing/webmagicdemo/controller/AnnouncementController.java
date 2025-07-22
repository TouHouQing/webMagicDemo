package com.touhouqing.webmagicdemo.controller;

import com.touhouqing.webmagicdemo.component.AnnouncementSpider;
import com.touhouqing.webmagicdemo.component.DetailSpider;
import com.touhouqing.webmagicdemo.component.LinkCollectorSpider;
import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
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
    private AnnouncementInfoMapper announcementInfoMapper;

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

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
