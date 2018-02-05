package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.base.TreeEntity;
import com.mysiteforme.admin.dao.MenuDao;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.ShowMenu;
import com.mysiteforme.admin.entity.VO.TreeMenu;
import com.mysiteforme.admin.entity.VO.ZtreeVO;
import com.mysiteforme.admin.service.MenuService;
import com.mysiteforme.admin.util.MyCompare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Cacheable(value = "allMenus",key = "'allMenus_isShow_'+#map['isShow'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Menu> selectAllMenus(Map<String,Object> map) {
        return baseMapper.getMenus(map);
    }

    @Caching(evict = {
            @CacheEvict(value = "allMenus",allEntries = true),
            @CacheEvict(value = "user",allEntries = true)
    })
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateMenu(Menu menu) {
        insertOrUpdate(menu);
    }

    @Override
    public int getCountByPermission(String permission) {
        EntityWrapper<Menu> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("permission",permission);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public int getCountByName(String name) {
        EntityWrapper<Menu> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public List<ZtreeVO> showTreeMenus() {
        EntityWrapper<Menu> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("is_show",true);
        wrapper.orderBy("sort",false);
        List<Menu> totalMenus = baseMapper.selectList(wrapper);
        List<ZtreeVO> ztreeVOs = Lists.newArrayList();
        return getZTree(null,totalMenus,ztreeVOs);
    }

    @Cacheable(value = "allMenus",key = "'user_menu_'+T(String).valueOf(#id)",unless = "#result == null or #result.size() == 0")
    @Override
    public List<ShowMenu> getShowMenuByUser(Long id) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("userId",id);
        map.put("parentId",null);
        return baseMapper.selectShowMenuByUser(map);
    }

    /**
     * 递归拉取菜单树的数据
     */
    private  List<ZtreeVO> getZTree(ZtreeVO tree,List<Menu> total,List<ZtreeVO> result){
        Long pid = tree == null?null:tree.getId();
        List<ZtreeVO> childList = Lists.newArrayList();
        for (Menu m : total){
            if(pid == m.getParentId()) {
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
