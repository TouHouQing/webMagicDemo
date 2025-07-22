package com.touhouqing.webmagicdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touhouqing.webmagicdemo.entity.AnnouncementLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 招标公告链接Mapper接口
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Mapper
public interface AnnouncementLinkMapper extends BaseMapper<AnnouncementLink> {

    /**
     * 查询未处理的链接
     * 
     * @param limit 限制数量
     * @return 未处理的链接列表
     */
    List<AnnouncementLink> selectUnprocessedLinks(@Param("limit") int limit);

    /**
     * 标记链接为已处理
     * 
     * @param id 链接ID
     * @return 更新行数
     */
    @Update("UPDATE announcement_link SET processed = 1, process_time = NOW() WHERE id = #{id}")
    int markAsProcessed(@Param("id") Long id);

    /**
     * 批量标记链接为已处理
     * 
     * @param ids 链接ID列表
     * @return 更新行数
     */
    int batchMarkAsProcessed(@Param("ids") List<Long> ids);
}
