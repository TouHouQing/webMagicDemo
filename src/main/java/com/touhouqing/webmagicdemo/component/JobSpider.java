package com.touhouqing.webmagicdemo.component;

import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

public class JobSpider {

    @Autowired
    private JobPageProcessor jobPageProcessor;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MyPipeline myPipeline;

    private static final String URL = "https://search.51job.com/list/080200,000000,0000,32,9,99,Java%25E5%25BC%2580%25E5%258F%2591,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";

    public void  doCrawler(){
        Request request = new Request(URL);
        request.addHeader("Accept","application/json, text/javascript, */*; q=0.01");
        Map<String, Object> map = new HashMap<>();
        map.put("level", "list");
        request.setExtras(map);
        Spider.create(jobPageProcessor)
                .addPipeline(myPipeline)
                //.setScheduler(scheduler)
                .addRequest(request)
                .start();
    }
}
