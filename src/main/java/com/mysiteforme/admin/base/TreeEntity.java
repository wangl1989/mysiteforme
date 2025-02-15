package com.mysiteforme.admin.base;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 树形结构实体基类
 * 提供树形结构所需的父子关系字段
 * @author chenjianann
 * @since 2014-05-16
 */
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Data
public class TreeEntity<T> extends DataEntity {

    /**
     * varchar(64) NULL父id
     */
    @TableField(value = "parent_id")
    protected Long parentId;

    /**
     * 节点层次（第一层，第二层，第三层....）
     */
    protected Integer level;
    /**
     * varchar(1000) NULL路径
     */
    @TableField(value = "parent_ids")
    protected String parentIds;
    /**
     * int(11) NULL排序
     */
    protected Integer sort;

    @TableField(exist = false)
    protected List<T> children;

    @TableField(exist = false)
    protected T parentTree;

    /**
     * 获取父节点ID路径
     * @return 父节点ID路径字符串
     */
    public String getParentIds() {
        return parentIds;
    }

    /**
     * 默认构造函数
     * 初始化排序值为30
     */
    public TreeEntity() {
        super();
        this.sort = 30;
    }

    /**
     * 带ID的构造函数
     * @param id 实体ID
     */
    public TreeEntity(Long id) {
        super(id);
    }

}
