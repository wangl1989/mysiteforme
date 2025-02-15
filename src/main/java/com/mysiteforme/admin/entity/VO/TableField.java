/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:48:38
 * @ Description: 表字段展示对象
 */

package com.mysiteforme.admin.entity.VO;

import com.mysiteforme.admin.entity.Dict;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class TableField implements Serializable {

    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;
    private String name;
    private Integer length;
    private String type;
    /**
     * 字段是否可以为空
     */
    private String isNullValue;
    /**
     * 字段描述
     */
    private String comment;

    /**
     * 字段用户(输入框..select..等等)
     */
    private String dofor;

    /***
     * 表名
     */
    private String tableName;

    /***
     * 表类型
     */
    private Integer tableType;

    /***
     * 表的注释
     */
    private String tableComment;

    /***
     * 是否为系统默认选项值(switch类型的按钮用得到)
     */
    private Boolean defaultValue;

    /***
     * 多选项目内置属性
     */
    private List<Dict> dictlist;

    /**
     * 字段原名称
     */
    private String oldName;

    /**
     * 列表table里是否显示该字段
     */
    private Boolean listIsShow = true;

    /**
     * 列表上方是否根据此字段来搜索
     */
    private Boolean listIsSearch = false;


}
