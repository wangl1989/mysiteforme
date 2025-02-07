package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.dao.MenuDao;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.service.MenuService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {

    /**
     * 查询所有菜单
     * 结果会被缓存
     * @param map 查询参数，包含isShow等条件
     * @return 菜单列表
     */
    @Cacheable(value = "allMenus",key = "'allMenus_isShow_'+#map['isShow'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Menu> selectAllMenus(Map<String,Object> map) {
        return baseMapper.getMenus(map);
    }

    /**
     * 保存或更新菜单
     * 同时清除菜单和用户相关的所有缓存
     * @param menu 菜单对象
     */
    @Caching(evict = {
            @CacheEvict(value = "allMenus",allEntries = true),
            @CacheEvict(value = "user",allEntries = true)
    })
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateMenu(Menu menu) {
        saveOrUpdate(menu);
    }

    /**
     * 根据权限标识获取菜单数量
     * @param permission 权限标识
     * @return 菜单数量
     */
    @Override
    public long getCountByPermission(String permission) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("permission",permission);
        return count(wrapper);
    }

    /**
     * 根据菜单名称获取菜单数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    @Override
    public long getCountByName(String name) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return count(wrapper);
    }

    /**
     * 获取树形结构的菜单列表
     * 只包含显示的菜单，按sort排序
     * @return 树形菜单列表
     */
    @Override
    public List<ZtreeVO> showTreeMenus() {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("is_show",true);
        wrapper.orderBy(false,false,"sort");
        List<Menu> totalMenus = baseMapper.selectList(wrapper);
        List<ZtreeVO> treeVOs = Lists.newArrayList();
        return getZTree(null,totalMenus,treeVOs);
    }

    /**
     * 获取指定用户的显示菜单
     * 结果会被缓存
     * @param id 用户ID
     * @return 用户可见的菜单列表
     */
    @Cacheable(value = "allMenus",key = "'user_menu_'+T(String).valueOf(#id)",unless = "#result == null or #result.size() == 0")
    @Override
    public List<ShowMenu> getShowMenuByUser(Long id) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("userId",id);
        map.put("parentId",null);
        return baseMapper.selectShowMenuByUser(map);
    }

    /**
     * 递归构建菜单树
     * @param tree 当前节点，首次调用传null
     * @param total 所有菜单列表
     * @param result 构建结果列表
     * @return 树形结构的菜单列表
     */
    private List<ZtreeVO> getZTree(ZtreeVO tree, List<Menu> total, List<ZtreeVO> result) {
        Long pid = tree == null?null:tree.getId();
        List<ZtreeVO> childList = Lists.newArrayList();
        for (Menu m : total){
            if(Objects.equals(pid, m.getParentId())) {
                ZtreeVO ztreeVO = new ZtreeVO();
                ztreeVO.setId(m.getId());
                ztreeVO.setName(m.getName());
                ztreeVO.setPid(pid);
                childList.add(ztreeVO);
                getZTree(ztreeVO,total,result);
            }
        }
        if(tree != null){
            tree.setChildren(childList);
        }else{
            result = childList;
        }
        return result;
    }

}
