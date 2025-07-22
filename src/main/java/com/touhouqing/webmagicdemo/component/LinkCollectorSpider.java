package com.touhouqing.webmagicdemo.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 链接收集爬虫
 * 专门用于从列表页收集详情页链接并保存到数据库
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class LinkCollectorSpider {

    @Autowired
    private LinkCollectorPageProcessor linkCollectorPageProcessor;

    @Autowired
    private LinkCollectorPipeline linkCollectorPipeline;

    // 天津市政府采购网 - 采购公告市级页面（使用动态时间戳）
    private static final String URL = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1&stmp=" + System.currentTimeMillis();

    /**
     * 启动链接收集爬虫
     */
    public void collectLinks() {
        System.out.println("开始收集招标公告链接...");
        
        Request request = new Request(URL);
        setRequestHeaders(request);
        
        Spider.create(linkCollectorPageProcessor)
                .addPipeline(linkCollectorPipeline)
                .addRequest(request)
                .start();
                
        System.out.println("链接收集任务完成");
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
