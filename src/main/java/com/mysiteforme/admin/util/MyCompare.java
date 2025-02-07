package com.mysiteforme.admin.util;

import com.mysiteforme.admin.entity.Menu;

import java.util.Comparator;

/**
 * Created by wangl on 2017/12/3.
 * todo:菜单排序
 */
/**
 * 菜单排序比较器
 * 用于对菜单列表进行排序
 */
public class MyCompare implements Comparator<Menu> {
    
    /**
     * 比较两个菜单的排序号
     * @param a 菜单对象1
     * @param b 菜单对象2
     * @return 比较结果
     */
    @Override
    public int compare(Menu a, Menu b) {
        if(a.getParentIds().contains(b.getParentIds())){
            return -1;
        }else{
            return 1;
        }
    }
}
