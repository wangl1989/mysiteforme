/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:45
 * @ Description: 表服务实现类 提供表的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.dao.TableDao;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.entity.VO.TableField;
import com.mysiteforme.admin.entity.VO.TableVO;
import com.mysiteforme.admin.service.DictService;
import com.mysiteforme.admin.service.TableService;
import com.mysiteforme.admin.util.Underline2Camel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class TableServiceImpl implements TableService {

    private final TableDao tableDao;

    private final DictService dictService;

    @Autowired
    public TableServiceImpl(TableDao tableDao,DictService dictService) {
        this.tableDao = tableDao;
        this.dictService = dictService;
    }

    /**
     * 获取所有数据表信息
     * @return 数据表列表
     */
    @Override
    public List<TableVO> listAll() {
        return tableDao.listAll();
    }

    /**
     * 获取数据表总数
     * @return 数据表数量
     */
    @Override
    public Integer getTableCount() {
        return tableDao.selectTableCount();
    }

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 存在返回1，不存在返回0
     */
    @Override
    public Integer existTable(String tableName) {
        return tableDao.existTable(tableName);
    }

    /**
     * 检查表字段是否存在
     * @param map 包含表名和字段名的参数Map
     * @return 存在返回1，不存在返回0
     */
    @Override
    public Integer existTableField(Map<String, Object> map) {
        return tableDao.existTableField(map);
    }

    /**
     * 创建数据表
     * 包含字段注释处理和数据字典创建
     * @param tableVO 表结构信息对象
     */
    @Override
    public void creatTable(TableVO tableVO) {
        StringBuilder stringBuilder = new StringBuilder();
        //处理字段备注
        for(TableField field : tableVO.getFieldList()){
            if(field.getDofor().equals("timer") || field.getDofor().equals("editor") || field.getDofor().contains("upload") || field.getDofor().equals("switch")){
                stringBuilder.append(field.getDofor()).append("-").append(getFieldName(field.getName(), field.getType())).append("-").append(field.getIsNullValue());
                if(field.getDofor().equals("switch")){
                    stringBuilder.append("-").append(field.getDefaultValue()).append("-").append(field.getName()).append(",");
                }else{
                    stringBuilder.append(",");
                }
            }

            if(field.getDofor().equals("select") || field.getDofor().equals("radio") || field.getDofor().equals("checkbox") || field.getDofor().equals("switch")){
                StringBuilder desc = new StringBuilder();
                desc.append(tableVO.getComment()).append("-").append(field.getComment());
                if(field.getDictlist() != null && !field.getDictlist().isEmpty()) {
                    int i = 0;
                    for (Dict dict : field.getDictlist()) {
                        StringBuilder my = new StringBuilder();
                        my.append(desc);
                        dict.setDescription(my.append("(此数据为系统自动创建:数据表【").append(tableVO.getName()).append("】中的字段【").append(field.getName()).append("】对应的值)").toString());
                        dict.setSort(i);
                        i = i + 1;
                    }
                    dictService.saveDictList(tableVO.getName()+"_"+field.getName(),field.getDictlist());
                }
            }else{
                field.setDefaultValue(false);
            }
            //处理comment
            addFiledCommentValue(field);

        }
        //处理表的备注
        tableVO.setComment(tableVO.getComment()+","+tableVO.getTabletype()+(stringBuilder.length()>0?","+stringBuilder.substring(0,stringBuilder.length()-1):""));
        String json = JSONObject.toJSONString(tableVO);
        Map<String,Object> map = JSON.parseObject(json,new TypeReference<HashMap<String,Object>>() {});
        tableDao.creatTable(map);
    }

    /**
     * 将下划线命名转换为驼峰命名
     * @param name 字段名
     * @param type 字段类型
     * @return 转换后的字段名
     */
    private String getFieldName(String name, String type) {
        if(type.equalsIgnoreCase("bit") && name.indexOf("is_") == 0){
            name = name.replaceFirst("is_","");
        }
        return Underline2Camel.underline2Camel(name,true);
    }

    /**
     * 添加字段注释值
     * @param field 字段信息对象
     */
    private void addFiledCommentValue(TableField field){
        String sv = field.getComment() + "," + field.getDofor() + "," + field.getIsNullValue() + "," +
                field.getDefaultValue() + "," + field.getListIsShow() + "," + field.getListIsSearch();
        field.setComment(sv);
    }

    /**
     * 添加字段到数据字典
     * @param field 字段信息对象
     */
    private void addColumnToDict(TableField field){
        if(field.getDofor().equals("select") || field.getDofor().equals("radio") || field.getDofor().equals("checkbox") || field.getDofor().equals("switch")){
            StringBuilder desc = new StringBuilder();
            desc.append(field.getTableComment()).append("-").append(field.getComment());
            if(field.getDictlist() != null && !field.getDictlist().isEmpty()) {
                int i = 0;
                for (Dict dict : field.getDictlist()) {
                    StringBuilder my = new StringBuilder();
                    my.append(desc);
                    dict.setDescription(my.append("(此数据为系统自动创建:数据表【").append(field.getTableName()).append("】中的字段【").append(field.getName()).append("】对应的值)").toString());
                    dict.setSort(i);
                    i = i + 1;
                }
                dictService.saveDictList(field.getTableName()+"_"+field.getName(),field.getDictlist());
            }
        }else{
            field.setDefaultValue(false);
        }

    }

    /**
     * 添加表字段
     * @param tableField 字段信息对象
     */
    @Override
    public void addColumn(TableField tableField) {
        //添加字典
        addColumnToDict(tableField);
        addFiledCommentValue(tableField);
        String json = JSONObject.toJSONString(tableField);
        Map<String,Object> map = JSON.parseObject(json,new TypeReference<HashMap<String,Object>>() {});
        tableDao.addColumn(map);
        changeTableComment(tableField.getTableName(),tableField.getTableComment(),tableField.getTableType());
    }

    /**
     * 更新表字段
     * @param tableField 字段信息对象
     */
    @Override
    public void updateColumn(TableField tableField) {
        dictService.deleteByType(tableField.getTableName()+"_"+tableField.getOldName());
        if(tableField.getDofor().equals("select") || tableField.getDofor().equals("radio") || tableField.getDofor().equals("checkbox") || tableField.getDofor().equals("switch")){
            StringBuilder desc = new StringBuilder();
            desc.append(tableField.getTableComment()).append("-").append(tableField.getComment());
            if(tableField.getDictlist() != null && !tableField.getDictlist().isEmpty()) {
                for (Dict dict : tableField.getDictlist()) {
                    dict.setDescription(desc + "(此数据为系统自动创建:数据表【" + tableField.getTableName() + "】中的字段【" + tableField.getName() + "】对应的值)");
                    dictService.saveOrUpdateDict(dict);
                }
            }
        }else{
            tableField.setDefaultValue(false);
        }
        addFiledCommentValue(tableField);
        String json = JSONObject.toJSONString(tableField);
        Map<String,Object> map = JSON.parseObject(json,new TypeReference<HashMap<String,Object>>() {});
        if(tableField.getOldName().equals(tableField.getName())){
            tableDao.updateColumnSameName(map);
        }else{
            tableDao.updateColumnDiffName(map);
        }
        changeTableComment(tableField.getTableName(),tableField.getTableComment(),tableField.getTableType());
    }

    /**
     * 删除表字段
     * @param fieldName 字段名
     * @param tableName 表名
     */
    @Override
    public void dropTableField(String fieldName, String tableName) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("fieldName",fieldName);
        map.put("tableName",tableName);
        tableDao.dropTableField(map);
        dictService.deleteByType(tableName+"_"+fieldName);
    }


    /**
     * 删除数据表
     * @param tableName 表名
     */
    @Override
    public void dropTable(String tableName) {
        tableDao.dropTable(tableName);
        dictService.deleteByTableName(tableName);
    }

    /**
     * 分页查询数据表
     * @param objectPage 分页对象
     * @param map 查询条件
     * @return 分页结果
     */
    @Override
    public IPage<TableVO> selectTablePage(IPage<TableVO> objectPage, Map<String,Object> map) {
        List<TableVO> list = tableDao.listPage(objectPage, map);
        objectPage.setRecords(list);
        return objectPage;
    }

    /**
     * 查询表字段列表
     * @param map 查询条件
     * @return 字段列表
     */
    @Override
    public List<TableField> selectFields(Map<String,Object> map) {
        return tableDao.selectFields(map);
    }

    /**
     * 分页查询表字段
     * @param objectPage 分页对象
     * @param map 查询条件
     * @return 分页结果
     */
    @Override
    public IPage<TableField> selectTableFieldPage(IPage<TableField> objectPage, Map<String,Object> map) {
        List<TableField> list = tableDao.selectFields(objectPage,map);
        for (TableField t : list){
            changeTableField(t);
        }
        objectPage.setRecords(list);
        return objectPage;
    }

    /**
     * 处理表字段信息
     * 解析字段注释中的配置信息
     * @param t 字段信息对象
     */
    private void changeTableField(TableField t){
        String[] c = t.getComment().split(",");
        t.setComment(c[0]);
        if(c.length>1) {
            t.setDofor(c[1]);
        }
        if(c.length>2){
            t.setIsNullValue(c[2]);
        }
        if(c.length>3){
            t.setDefaultValue(Boolean.valueOf(c[3]));
        }
    }

    /**
     * 获取表详细信息
     * @param name 表名
     * @return 表详细信息
     */
    @Override
    public TableVO detailTable(String name) {
        return tableDao.selectDetailTable(name);
    }

    /**
     * 修改表名
     * @param name 新表名
     * @param rename 旧表名
     * @param comment 表注释
     * @param tableType 表类型
     */
    @Override
    public void changeTableName(String name, String rename, String comment, Integer tableType) {
        Map<String,Object> tablemap = Maps.newHashMap();
        tablemap.put("name",rename);
        tablemap.put("tableType",tableType);
        List<TableField> tableFields = selectFields(tablemap);
        for (TableField field:tableFields){
            String fieldCommon = field.getComment();
            String[] c = fieldCommon.split(",");
            if(c.length>1){
                if(c[1].equals("select") || c[1].equals("radio") || c[1].equals("checkbox") || c[1].equals("switch")){
                    if(field.getDictlist() != null && !field.getDictlist().isEmpty()) {
                        int i = 0;
                        for (Dict dict : field.getDictlist()) {
                            StringBuilder my = new StringBuilder();
                            my.append(comment).append("-").append(name);
                            dict.setDescription(my.append("(此数据为系统自动创建:数据表【").append(name).append("】中的字段【").append(field.getName()).append("】对应的值)").toString());
                            dict.setType(name + "_" + field.getName());
                            i = i + 1;
                            dictService.saveOrUpdateDict(dict);
                        }
                    }
                }
            }
        }

        Map<String,Object> map = Maps.newHashMap();
        map.put("name",name);
        map.put("oldname",rename);
        tableDao.changeTableName(map);
    }

    /**
     * 修改表注释
     * @param name 表名
     * @param comment 表注释
     * @param tableType 表类型
     */
    @Override
    public void changeTableComment(String name, String comment, Integer tableType) {
        Map<String,Object> tablemap = Maps.newHashMap();
        tablemap.put("name",name);
        tablemap.put("tableType",tableType);
        List<TableField> list = selectFields(tablemap);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(comment).append(",").append(tableType).append(",");
        //处理字段备注
        for(TableField field : list){
            String[] c = field.getComment().split(",");
            if(c.length>1) {
                if (c[1].equals("timer") || c[1].equals("editor") || c[1].contains("upload") || c[1].equals("switch")) {
                    stringBuilder.append(c[1]).append("-").append(getFieldName(field.getName(), field.getType())).append("-").append(field.getIsNullValue());
                    if(c[1].equals("switch")){
                        stringBuilder.append("-").append(c[3]).append("-").append(field.getName()).append(",");
                    }else{
                        stringBuilder.append(",");
                    }
                }
            }
        }
        String str = stringBuilder.substring(0,stringBuilder.length()-1);
        Map<String,Object> map = Maps.newHashMap();
        map.put("tableName",name);
        map.put("comment",str);
        tableDao.changeTableComment(map);
    }
}
