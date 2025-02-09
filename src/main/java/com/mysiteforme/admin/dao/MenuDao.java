package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@Mapper
public interface MenuDao extends BaseMapper<Menu> {

    List<Menu> showAllMenusList(Map<String,Object> map);

    List<Menu> getMenus(Map<String,Object> map);

    List<ShowMenu> selectShowMenuByUser(Map<String,Object> map);
}