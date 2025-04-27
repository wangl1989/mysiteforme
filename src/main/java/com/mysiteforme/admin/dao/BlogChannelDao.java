/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:40:18
 * @ Description: 博客栏目数据层接口 提供博客栏目的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.BlogChannel;
import com.mysiteforme.admin.entity.VO.BlogChannelVO;
import com.mysiteforme.admin.entity.VO.ZtreeVO;

import java.util.List;
import java.util.Map;

public interface BlogChannelDao extends BaseMapper<BlogChannel> {

    List<ZtreeVO> selectZtreeData(Map<String,Object> map);

    List<BlogChannelVO> selectChannelData(Map<String, Object> map);
}
