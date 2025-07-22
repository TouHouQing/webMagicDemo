package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.JobInfo;
import com.touhouqing.webmagicdemo.mapper.JobInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MyPipeline implements Pipeline {

    @Autowired
    private JobInfoMapper jobInfoMapper;


    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出数据
        JobInfo jobinfo = resultItems.get("jobinfo");

        // 保存数据库
        jobInfoMapper.insert(jobinfo);

    }
}
