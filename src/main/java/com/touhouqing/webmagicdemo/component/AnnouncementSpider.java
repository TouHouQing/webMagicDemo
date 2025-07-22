package com.touhouqing.webmagicdemo.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnnouncementSpider {

    @Autowired
    private AnnouncementPageProcessor announcementPageProcessor;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MyPipeline myPipeline;

    // 天津市政府采购网 - 采购公告市级页面（使用动态时间戳）
    private static final String URL = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1&stmp=" + System.currentTimeMillis();

    public void doCrawler(){
        Request request = new Request(URL);
        request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("Accept-Encoding","gzip, deflate");
        request.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        request.addHeader("Cache-Control","max-age=0");
        request.addHeader("Connection","keep-alive");
        request.addHeader("Host","www.ccgp-tianjin.gov.cn");
        request.addHeader("Upgrade-Insecure-Requests","1");
        request.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        
        Map<String, Object> map = new HashMap<>();
        map.put("level", "list");
        request.setExtras(map);
        
        Spider.create(announcementPageProcessor)
                .addPipeline(myPipeline)
                //.setScheduler(scheduler)
                .addRequest(request)
                .start();
    }
}
