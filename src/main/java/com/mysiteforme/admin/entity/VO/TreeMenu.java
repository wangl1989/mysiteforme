package com.mysiteforme.admin.entity.VO;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tnt on 2017/12/6.
 * MENU菜单树形结构数据
 */
@Data
@Getter
@Setter
public class TreeMenu implements Serializable{

    private static final long serialVersionUID = 6962439201546719734L;
    private Long id;
    private Long pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    List<TreeMenu> children = Lists.newArrayList();

    public TreeMenu(Boolean spread) {
        this.spread = false;
    }

    public TreeMenu(Long id, Long pid, String title,String icon, String href) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.icon = icon;
        this.href = href;
    }
}
