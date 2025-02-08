package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.VO.TableField;
import com.mysiteforme.admin.entity.VO.TableVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by wangl on 2017/12/25.
 * todo: 数据库操作表
 */
@Mapper
public interface TableDao {

    List<TableVO> listAll();

    List<TableVO> listPage(IPage<TableVO> page, @Param("params")Map<String,Object> map);

    Integer selectTableCount();

    Integer existTable(String tableName);

    Integer existTableField(Map<String,Object> map);

    void creatTable(Map<String,Object> map);

    void addColumn(Map<String,Object> map);

    void updateColumnSameName(Map<String,Object> map);

    void updateColumnDiffName(Map<String,Object> map);

    void dropTable(@Param("tableName") String tableName);

    void dropTableField(Map<String,Object> map);

    List<TableField> selectFields(Map<String,Object> map);

    TableVO selectDetailTable(String name);

    List<TableField> selectFields(IPage<TableField> objectPage,Map<String,Object> map);

    void changeTableName(Map<String,Object> map);

    void changeTableComment(Map<String,Object> map);
}
