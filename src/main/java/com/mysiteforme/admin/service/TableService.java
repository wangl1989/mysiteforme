package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.entity.VO.TableField;
import com.mysiteforme.admin.entity.VO.TableVO;

import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2017/12/25.
 * todo:
 */
public interface TableService {

    List<TableVO> listAll();

    Integer getTableCount();

    Integer existTable(String tableName);

    Integer existTableField(Map<String,Object> map);

    void creatTable(TableVO tableVO);

    void addColumn(TableField tableField);

    void updateColumn(TableField tableField);

    void dropTableField(String fieldName,String tableName);

    void dropTable(String tableName);

    Page<TableVO> selectTablePage(Page<TableVO> objectPage, Map<String,Object> map);

    List<TableField> selectFields(Map<String,Object> map);

    Page<TableField> selectTableFieldPage(Page<TableField> objectPage,Map<String,Object> map);

    TableVO detailTable(String name);

    void changeTableName(String name,String oldname,String comment,Integer tableType);

    void changeTableComment(String name,String comment,Integer tableType);
}
