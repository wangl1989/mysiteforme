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

import java.util.List;


public interface MenuService extends IService<Menu> {

    /**
     * 新增菜单
     * @param menu 菜单对象
     */
    void saveMenu(Menu menu);

    /**
     * 更新菜单
     * @param menu 菜单对象
     */
    void updateMenu(Menu menu);

    void deleteMenu(Long id);

    /**
     * 根据菜单名称获取数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    Long getCountByName(String name,Long id);

    /**
     * 获取指定用户的显示菜单
     * @param id 用户ID
     * @param isDetail 是否展示详情
     * @return 用户可见的菜单列表
     */
    List<MenuTreeVO> getShowMenuByUser(Long id, Boolean isDetail);

}
