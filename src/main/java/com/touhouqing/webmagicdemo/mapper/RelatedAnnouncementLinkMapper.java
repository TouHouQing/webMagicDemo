package com.touhouqing.webmagicdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touhouqing.webmagicdemo.entity.RelatedAnnouncementLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 相关公告链接Mapper接口
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Mapper
public interface RelatedAnnouncementLinkMapper extends BaseMapper<RelatedAnnouncementLink> {

    /**
     * 查询未处理的相关公告链接
     * 
     * @param limit 限制数量
     * @return 未处理的链接列表
     */
    List<RelatedAnnouncementLink> selectUnprocessedLinks(@Param("limit") int limit);

    /**
     * 标记链接为已处理
     * 
     * @param id 链接ID
     * @return 更新行数
     */
    @Update("UPDATE related_announcement_link SET processed = 1, process_time = NOW() WHERE id = #{id}")
    int markAsProcessed(@Param("id") Long id);

    /**
     * 批量标记链接为已处理
     * 
     * @param ids 链接ID列表
     * @return 更新行数
     */
    int batchMarkAsProcessed(@Param("ids") List<Long> ids);
}
