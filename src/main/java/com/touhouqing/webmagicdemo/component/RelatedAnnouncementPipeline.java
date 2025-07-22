package com.touhouqing.webmagicdemo.component;

import com.touhouqing.webmagicdemo.entity.DetailedProcurementInfo;
import com.touhouqing.webmagicdemo.mapper.DetailedProcurementInfoMapper;
import com.touhouqing.webmagicdemo.mapper.RelatedAnnouncementLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 相关公告管道
 * 专门用于保存相关公告页面提取的详细采购信息，并标记相关链接为已处理
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class RelatedAnnouncementPipeline implements Pipeline {

    @Autowired
    private DetailedProcurementInfoMapper detailedProcurementInfoMapper;

    @Autowired
    private RelatedAnnouncementLinkMapper relatedAnnouncementLinkMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Long relatedLinkId = resultItems.get("relatedLinkId");
        Integer projectCount = resultItems.get("projectCount");

        if (projectCount != null && projectCount > 0) {
            int savedCount = 0;
            
            // 遍历所有提取到的采购项目信息
            for (int i = 1; i <= projectCount; i++) {
                DetailedProcurementInfo procurementInfo = resultItems.get("procurementInfo_" + i);
                
                if (procurementInfo != null) {
                    try {
                        // 保存详细采购信息到数据库
                        detailedProcurementInfoMapper.insert(procurementInfo);
                        savedCount++;
                        System.out.println("保存详细采购信息成功: " + procurementInfo.getProcurementProjectName());
                    } catch (Exception e) {
                        System.err.println("保存详细采购信息时出错: " + procurementInfo.getProcurementProjectName() + 
                                         ", 错误: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("本次共保存了 " + savedCount + " 个详细采购项目信息");

            // 标记对应的相关链接为已处理
            if (relatedLinkId != null) {
                try {
                    relatedAnnouncementLinkMapper.markAsProcessed(relatedLinkId);
                    System.out.println("标记相关链接为已处理: relatedLinkId=" + relatedLinkId);
                } catch (Exception e) {
                    System.err.println("标记相关链接为已处理时出错: " + e.getMessage());
                }
            }
        } else {
            System.out.println("没有提取到详细采购信息");
            
            // 即使没有提取到信息，也要标记链接为已处理，避免重复处理
            if (relatedLinkId != null) {
                try {
                    relatedAnnouncementLinkMapper.markAsProcessed(relatedLinkId);
                    System.out.println("标记相关链接为已处理（无数据）: relatedLinkId=" + relatedLinkId);
                } catch (Exception e) {
                    System.err.println("标记相关链接为已处理时出错: " + e.getMessage());
                }
            }
        }
    }
}
