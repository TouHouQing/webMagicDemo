package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 详情页管道
 * 专门用于保存详情页提取的招标公告信息，并标记链接为已处理
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class DetailPipeline implements Pipeline {

    @Autowired
    private AnnouncementInfoMapper announcementInfoMapper;

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出招标公告数据
        AnnouncementInfo announcementInfo = resultItems.get("announcementInfo");
        Long linkId = resultItems.get("linkId");

        if (announcementInfo != null) {
            try {
                // 保存招标公告信息到数据库
                announcementInfoMapper.insert(announcementInfo);
                System.out.println("保存招标公告信息成功: " + announcementInfo.getTitle());

                // 标记对应的链接为已处理
                if (linkId != null) {
                    announcementLinkMapper.markAsProcessed(linkId);
                    System.out.println("标记链接为已处理: linkId=" + linkId);
                }

            } catch (Exception e) {
                System.err.println("保存招标公告信息时出错: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("没有提取到招标公告信息");
        }
    }
}
