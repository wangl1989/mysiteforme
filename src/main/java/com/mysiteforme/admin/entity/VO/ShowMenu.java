/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:48:24
 * @ Description: 菜单展示对象
 */

package com.mysiteforme.admin.entity.VO;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class ShowMenu implements Serializable{
    private static final long serialVersionUID = 6962439201546719734L;
    private Long id;
    private  Long pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread = false;
    private List<ShowMenu> children = Lists.newArrayList();

}
