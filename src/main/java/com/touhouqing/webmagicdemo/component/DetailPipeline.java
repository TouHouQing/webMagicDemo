package com.touhouqing.webmagicdemo.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import com.touhouqing.webmagicdemo.entity.RelatedAnnouncementLink;
import com.touhouqing.webmagicdemo.mapper.AnnouncementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.AnnouncementLinkMapper;
import com.touhouqing.webmagicdemo.mapper.RelatedAnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

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

    @Autowired
    private RelatedAnnouncementLinkMapper relatedAnnouncementLinkMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出招标公告数据
        AnnouncementInfo announcementInfo = resultItems.get("announcementInfo");
        @SuppressWarnings("unchecked")
        List<RelatedAnnouncementLink> relatedLinks = resultItems.get("relatedLinks");
        Long linkId = resultItems.get("linkId");

        if (announcementInfo != null) {
            try {
                // 保存招标公告信息到数据库
                announcementInfoMapper.insert(announcementInfo);
                System.out.println("保存招标公告信息成功: " + announcementInfo.getTitle());

                // 保存相关公告链接
                if (relatedLinks != null && !relatedLinks.isEmpty()) {
                    int savedRelatedCount = 0;
                    int duplicateRelatedCount = 0;

                    for (RelatedAnnouncementLink relatedLink : relatedLinks) {
                        try {
                            // 检查是否已存在相同的相关公告链接
                            QueryWrapper<RelatedAnnouncementLink> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("related_announcement_id", relatedLink.getRelatedAnnouncementId());

                            RelatedAnnouncementLink existingRelatedLink = relatedAnnouncementLinkMapper.selectOne(queryWrapper);

                            if (existingRelatedLink == null) {
                                // 不存在，保存新的相关公告链接
                                relatedAnnouncementLinkMapper.insert(relatedLink);
                                savedRelatedCount++;
                                System.out.println("保存相关公告链接: " + relatedLink.getRelatedUrl());
                            } else {
                                duplicateRelatedCount++;
                                System.out.println("相关公告链接已存在，跳过: " + relatedLink.getRelatedUrl());
                            }
                        } catch (Exception e) {
                            System.err.println("保存相关公告链接时出错: " + relatedLink.getRelatedUrl() + ", 错误: " + e.getMessage());
                        }
                    }

                    System.out.println("相关公告链接保存完成 - 新保存: " + savedRelatedCount + " 个，重复跳过: " + duplicateRelatedCount + " 个");
                }

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
