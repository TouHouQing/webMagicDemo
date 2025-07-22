package com.touhouqing.webmagicdemo;

import com.touhouqing.webmagicdemo.component.DetailSpider;
import com.touhouqing.webmagicdemo.component.LinkCollectorSpider;
import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 两阶段爬虫测试类
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@SpringBootTest
public class TwoPhaseSpiderTest {

    @Autowired
    private LinkCollectorSpider linkCollectorSpider;

    @Autowired
    private DetailSpider detailSpider;

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

    @Autowired
    private AnnouncementInfoMapper announcementInfoMapper;

    /**
     * 测试第一阶段：收集链接
     */
    @Test
    public void testCollectLinks() {
        System.out.println("=== 开始测试链接收集 ===");
        
        // 收集前的统计
        Long beforeCount = announcementLinkMapper.selectCount(null);
        System.out.println("收集前链接数量: " + beforeCount);
        
        // 执行链接收集
        linkCollectorSpider.collectLinks();
        
        // 收集后的统计
        Long afterCount = announcementLinkMapper.selectCount(null);
        System.out.println("收集后链接数量: " + afterCount);
        System.out.println("新增链接数量: " + (afterCount - beforeCount));
        
        // 显示部分链接
        List<AnnouncementLink> links = announcementLinkMapper.selectUnprocessedLinks(5);
        System.out.println("未处理链接示例:");
        for (AnnouncementLink link : links) {
            System.out.println("- " + link.getDetailUrl());
        }
        
        System.out.println("=== 链接收集测试完成 ===");
    }

    /**
     * 测试第二阶段：抓取详情
     */
    @Test
    public void testCrawlDetails() {
        System.out.println("=== 开始测试详情抓取 ===");
        
        // 抓取前的统计
        Long beforeAnnouncementCount = announcementInfoMapper.selectCount(null);
        Long beforeUnprocessedCount = (long) announcementLinkMapper.selectUnprocessedLinks(0).size();
        
        System.out.println("抓取前招标公告数量: " + beforeAnnouncementCount);
        System.out.println("抓取前未处理链接数量: " + beforeUnprocessedCount);
        
        // 执行详情抓取（只处理3个链接作为测试）
        detailSpider.crawlDetails(3);
        
        // 抓取后的统计
        Long afterAnnouncementCount = announcementInfoMapper.selectCount(null);
        Long afterUnprocessedCount = (long) announcementLinkMapper.selectUnprocessedLinks(0).size();
        
        System.out.println("抓取后招标公告数量: " + afterAnnouncementCount);
        System.out.println("抓取后未处理链接数量: " + afterUnprocessedCount);
        System.out.println("新增招标公告数量: " + (afterAnnouncementCount - beforeAnnouncementCount));
        System.out.println("处理的链接数量: " + (beforeUnprocessedCount - afterUnprocessedCount));
        
        System.out.println("=== 详情抓取测试完成 ===");
    }

    /**
     * 测试完整的两阶段流程
     */
    @Test
    public void testFullTwoPhaseProcess() {
        System.out.println("=== 开始测试完整两阶段流程 ===");
        
        // 第一阶段：收集链接
        System.out.println("第一阶段：收集链接");
        testCollectLinks();
        
        // 等待一段时间
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 第二阶段：抓取详情
        System.out.println("第二阶段：抓取详情");
        testCrawlDetails();
        
        System.out.println("=== 完整两阶段流程测试完成 ===");
    }

    /**
     * 查看数据库状态
     */
    @Test
    public void testDatabaseStatus() {
        System.out.println("=== 数据库状态统计 ===");
        
        // 链接统计
        Long totalLinks = announcementLinkMapper.selectCount(null);
        List<AnnouncementLink> unprocessedLinks = announcementLinkMapper.selectUnprocessedLinks(0);
        Long unprocessedCount = (long) unprocessedLinks.size();
        Long processedCount = totalLinks - unprocessedCount;
        
        System.out.println("链接统计:");
        System.out.println("- 总链接数: " + totalLinks);
        System.out.println("- 已处理: " + processedCount);
        System.out.println("- 未处理: " + unprocessedCount);
        
        // 招标公告统计
        Long announcementCount = announcementInfoMapper.selectCount(null);
        System.out.println("招标公告数量: " + announcementCount);
        
        System.out.println("=== 数据库状态统计完成 ===");
    }
}
