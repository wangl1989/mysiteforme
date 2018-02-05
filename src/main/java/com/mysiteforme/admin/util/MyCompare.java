package com.mysiteforme.admin.util;

import com.mysiteforme.admin.entity.Menu;

import java.util.Comparator;

/**
 * Created by wangl on 2017/12/3.
 * todo:菜单排序
 */
public class MyCompare implements Comparator<Menu> {
    @Override
    public int compare(Menu a, Menu b) {
        if(a.getParentIds().contains(b.getParentIds())){
            return -1;
        }else{
            return 1;
        }
    }
}
