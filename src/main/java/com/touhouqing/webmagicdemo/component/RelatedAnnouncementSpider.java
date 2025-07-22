package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.RelatedAnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.RelatedAnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相关公告爬虫
 * 专门用于从数据库读取相关公告链接并抓取详细采购信息
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class RelatedAnnouncementSpider {

    @Autowired
    private RelatedAnnouncementPageProcessor relatedAnnouncementPageProcessor;

    @Autowired
    private RelatedAnnouncementPipeline relatedAnnouncementPipeline;

    @Autowired
    private RelatedAnnouncementLinkMapper relatedAnnouncementLinkMapper;

    /**
     * 启动相关公告爬虫
     * 
     * @param batchSize 每批处理的链接数量，默认为5
     */
    public void crawlRelatedAnnouncements(int batchSize) {
        if (batchSize <= 0) {
            batchSize = 5; // 默认每批处理5个链接
        }
        
        System.out.println("开始抓取相关公告详细信息，每批处理 " + batchSize + " 个链接...");
        
        // 查询未处理的相关公告链接
        List<RelatedAnnouncementLink> unprocessedLinks = relatedAnnouncementLinkMapper.selectUnprocessedLinks(batchSize);
        
        if (unprocessedLinks.isEmpty()) {
            System.out.println("没有找到未处理的相关公告链接");
            return;
        }
        
        System.out.println("找到 " + unprocessedLinks.size() + " 个未处理的相关公告链接");
        
        // 创建爬虫
        Spider spider = Spider.create(relatedAnnouncementPageProcessor)
                .addPipeline(relatedAnnouncementPipeline);
        
        // 为每个链接创建请求
        for (RelatedAnnouncementLink link : unprocessedLinks) {
            Request request = new Request(link.getRelatedUrl());
            setRequestHeaders(request);
            
            // 传递相关链接ID，用于后续标记为已处理
            Map<String, Object> extras = new HashMap<>();
            extras.put("relatedLinkId", link.getId());
            request.setExtras(extras);
            
            spider.addRequest(request);
            
            System.out.println("添加相关公告请求: " + link.getRelatedUrl());
        }
        
        // 启动爬虫
        spider.start();
        
        System.out.println("相关公告抓取任务完成");
    }

    /**
     * 启动相关公告爬虫（使用默认批次大小）
     */
    public void crawlRelatedAnnouncements() {
        crawlRelatedAnnouncements(5);
    }

    /**
     * 设置请求头
     */
    private void setRequestHeaders(Request request) {
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        request.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        request.addHeader("Cache-Control", "max-age=0");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.ccgp-tianjin.gov.cn");
        request.addHeader("Upgrade-Insecure-Requests", "1");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    }
}
