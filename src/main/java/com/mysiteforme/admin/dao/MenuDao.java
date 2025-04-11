/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:42:12
 * @ Description: 菜单数据层接口 提供菜单的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.VO.MenuTreeVO;
import com.mysiteforme.admin.entity.VO.ShowMenu;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Mapper
public interface MenuDao extends BaseMapper<Menu> {

    List<Menu> showAllMenusList(Map<String,Object> map);

    List<Menu> getMenus(Map<String,Object> map);

    List<ShowMenu> selectShowMenuByUser(Map<String,Object> map);

    List<MenuTreeVO> getUserMenus(Map<String,Object> map);

    Set<Long> getUserIdsByMenuId(Long menuId);
}