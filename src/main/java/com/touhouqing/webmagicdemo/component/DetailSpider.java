package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情页爬虫
 * 专门用于从数据库读取链接并抓取详情页信息
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class DetailSpider {

    @Autowired
    private DetailPageProcessor detailPageProcessor;

    @Autowired
    private DetailPipeline detailPipeline;

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

    /**
     * 启动详情页爬虫
     * 
     * @param batchSize 每批处理的链接数量，默认为10
     */
    public void crawlDetails(int batchSize) {
        if (batchSize <= 0) {
            batchSize = 10; // 默认每批处理10个链接
        }
        
        System.out.println("开始抓取招标公告详情信息，每批处理 " + batchSize + " 个链接...");
        
        // 查询未处理的链接
        List<AnnouncementLink> unprocessedLinks = announcementLinkMapper.selectUnprocessedLinks(batchSize);
        
        if (unprocessedLinks.isEmpty()) {
            System.out.println("没有找到未处理的链接");
            return;
        }
        
        System.out.println("找到 " + unprocessedLinks.size() + " 个未处理的链接");
        
        // 创建爬虫
        Spider spider = Spider.create(detailPageProcessor)
                .addPipeline(detailPipeline);
        
        // 为每个链接创建请求
        for (AnnouncementLink link : unprocessedLinks) {
            Request request = new Request(link.getDetailUrl());
            setRequestHeaders(request);
            
            // 传递链接ID，用于后续标记为已处理
            Map<String, Object> extras = new HashMap<>();
            extras.put("linkId", link.getId());
            request.setExtras(extras);
            
            spider.addRequest(request);
            
            System.out.println("添加详情页请求: " + link.getDetailUrl());
        }
        
        // 启动爬虫
        spider.start();
        
        System.out.println("详情页抓取任务完成");
    }

    /**
     * 启动详情页爬虫（使用默认批次大小）
     */
    public void crawlDetails() {
        crawlDetails(10);
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
