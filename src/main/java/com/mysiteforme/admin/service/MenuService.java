package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import com.mysiteforme.admin.entity.VO.ZtreeVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取所有菜单列表
     * @param map 查询条件
     * @return 菜单列表
     */
    List<Menu> selectAllMenus(Map<String, Object> map);

    /**
     * 保存或更新菜单
     * @param menu 菜单对象
     */
    void saveOrUpdateMenu(Menu menu);

    /**
     * 根据权限标识获取菜单数量
     * @param permission 权限标识
     * @return 菜单数量
     */
    long getCountByPermission(String permission);

    /**
     * 根据菜单名称获取数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    long getCountByName(String name);

    /**
     * 获取树形结构的菜单列表
     * @return 树形菜单列表
     */
    List<ZtreeVO> showTreeMenus();

    /**
     * 获取指定用户的显示菜单
     * @param id 用户ID
     * @return 用户可见的菜单列表
     */
    List<ShowMenu> getShowMenuByUser(Long id);

}
