package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class MyPipeline implements Pipeline {

    @Autowired
    private AnnouncementInfoMapper announcementInfoMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出招标公告数据
        AnnouncementInfo announcementInfo = resultItems.get("announcementInfo");
        if (announcementInfo != null) {
            // 保存数据库
            announcementInfoMapper.insert(announcementInfo);
            System.out.println("保存招标公告信息: " + announcementInfo.getTitle());
        }
    }
}
