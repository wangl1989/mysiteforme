/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:28:39
 * @ Description: 菜单Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.VO.MenuTreeVO;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.entity.request.AddMenuRequest;
import com.mysiteforme.admin.entity.request.UpdateMenuRequest;

import java.util.List;
import java.util.Map;


public interface MenuService extends IService<Menu> {

    /**
     * 获取所有菜单列表
     * @param map 查询条件
     * @return 菜单列表
     */
    List<Menu> selectAllMenus(Map<String, Object> map);

    /**
     * 新增菜单
     * @param request 菜单对象
     */
    void saveMenu(AddMenuRequest request);

    /**
     * 更新菜单
     * @param request 菜单对象
     */
    void updateMenu(UpdateMenuRequest request);

    void deleteMenu(Long id);

    /**
     * 根据菜单名称获取数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    long getCountByName(String name);

    /**
     * 获取指定用户的显示菜单
     * @param id 用户ID
     * @return 用户可见的菜单列表
     */
    List<MenuTreeVO> getShowMenuByUser(Long id);

}
