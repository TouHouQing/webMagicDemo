package com.touhouqing.webmagicdemo.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

@Configuration
public class JobScheduler {

    @Bean("scheduler")
    public Scheduler scheduler(){
        BloomFilterDuplicateRemover bloomFilterDuplicateRemover = new BloomFilterDuplicateRemover(10000000);
        return new QueueScheduler().setDuplicateRemover(bloomFilterDuplicateRemover);
    }
}
