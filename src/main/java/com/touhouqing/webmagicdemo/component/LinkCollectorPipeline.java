package com.touhouqing.webmagicdemo.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * 链接收集器管道
 * 专门用于保存收集到的详情页链接
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class LinkCollectorPipeline implements Pipeline {

    @Autowired
    private AnnouncementLinkMapper announcementLinkMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出收集到的链接列表
        @SuppressWarnings("unchecked")
        List<AnnouncementLink> linkList = resultItems.get("linkList");
        
        if (linkList != null && !linkList.isEmpty()) {
            int savedCount = 0;
            int duplicateCount = 0;
            
            for (AnnouncementLink link : linkList) {
                try {
                    // 检查是否已存在相同的链接
                    QueryWrapper<AnnouncementLink> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("announcement_id", link.getAnnouncementId());
                    
                    AnnouncementLink existingLink = announcementLinkMapper.selectOne(queryWrapper);
                    
                    if (existingLink == null) {
                        // 不存在，保存新链接
                        announcementLinkMapper.insert(link);
                        savedCount++;
                        System.out.println("保存新链接: " + link.getDetailUrl());
                    } else {
                        duplicateCount++;
                        System.out.println("链接已存在，跳过: " + link.getDetailUrl());
                    }
                } catch (Exception e) {
                    System.err.println("保存链接时出错: " + link.getDetailUrl() + ", 错误: " + e.getMessage());
                }
            }
            
            System.out.println("链接收集完成 - 新保存: " + savedCount + " 个，重复跳过: " + duplicateCount + " 个");
        } else {
            System.out.println("没有收集到任何链接");
        }
    }
}
