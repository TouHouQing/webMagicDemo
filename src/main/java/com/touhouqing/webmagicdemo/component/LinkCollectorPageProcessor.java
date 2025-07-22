package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 链接收集器页面处理器
 * 专门用于从列表页收集详情页链接
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class LinkCollectorPageProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        
        // 获取所有的招标公告链接 - 使用更精确的选择器
        List<String> urlList = html.$("li a[href*='/viewer.do?id=']").links().all();
        
        System.out.println("在列表页找到 " + urlList.size() + " 个详情页链接");
        
        List<AnnouncementLink> linkList = new ArrayList<>();
        
        for (String url : urlList) {
            // 从viewer.do链接中提取ID，然后构造documentView.do链接
            if (url.contains("/viewer.do?id=")) {
                // 提取ID
                String id = url.substring(url.indexOf("id=") + 3);
                if (id.contains("&")) {
                    id = id.substring(0, id.indexOf("&"));
                }
                
                // 构造新的详情页URL
                String detailUrl = "http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=" + id + "&ver=2";
                
                System.out.println("收集到链接: " + detailUrl);
                
                // 创建链接对象
                AnnouncementLink announcementLink = new AnnouncementLink();
                announcementLink.setDetailUrl(detailUrl);
                announcementLink.setAnnouncementId(id);
                announcementLink.setProcessed(0); // 未处理
                announcementLink.setCreateTime(LocalDateTime.now());
                announcementLink.setUpdateTime(LocalDateTime.now());
                
                linkList.add(announcementLink);
            }
        }
        
        // 将收集到的链接列表传递给Pipeline
        page.putField("linkList", linkList);
        
        System.out.println("本次共收集到 " + linkList.size() + " 个有效链接");
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setSleepTime(1000)
                .setTimeOut(10000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    }
}
