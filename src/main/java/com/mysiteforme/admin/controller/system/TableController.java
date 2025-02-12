package com.mysiteforme.admin.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.VO.TableField;
import com.mysiteforme.admin.entity.VO.TableVO;
import com.mysiteforme.admin.service.TableService;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * &#064;Author:tnt
 * &#064;Description:$
 * &#064;Date:  Create in 17:51 2017/12/25.
 */
@Controller
@RequestMapping("admin/system/table")
public class TableController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(TableController.class);

    public TableController(TableService tableService, CreateTableFiles createTableFiles, SiteService siteService) {
        this.tableService = tableService;
        this.createTableFiles = createTableFiles;
        this.siteService = siteService;
    }

    private static final String[] keywords = {"public","protected","private","protected","class","interface","abstract","implements","extends","new",
    "import","package","byte","char","boolean","short","int","float","long","double","void","null","true","false","if","else","while","for","switch",
    "case","default","do","break","continue","return","instanceof","static","final","super","this","native","strictfp","synchronized","transient","volatile",
    "catch","try","finally","throw","throws","enum","assert","throw","throws","this"};

    private CreateTableFiles createTableFiles;

    public TableController() {
        super();
    }

    @Autowired
    public TableController(CreateTableFiles createTableFiles) {
        this.createTableFiles = createTableFiles;
    }

    /**
     */
    @GetMapping("list")
    @SysLog("跳转数据表列表页面")
    public String list(){
        return "admin/system/table/list";
    }

    /***
     * 所有数据表分页查询
     */
    @RequiresPermissions("sys:table:list")
    @PostMapping("list")
    @ResponseBody
    public LayerData<TableVO> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                    @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                    ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<TableVO> tableLayerData = new LayerData<>();
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isBlank(name)) {
                map.remove("name");
            }
        }
        IPage<TableVO> tablePage = tableService.selectTablePage(new Page<>(page,limit),map);
        tableLayerData.setCount((int)tablePage.getTotal());
        tableLayerData.setData(tablePage.getRecords());
        return tableLayerData;
    }

    @GetMapping("add")
    public String add(){
        return "admin/system/table/add";
    }

    @RequiresPermissions("sys:table:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存数据表新增数据")
    public RestResponse add(@RequestBody TableVO tableVO){
        if(StringUtils.isBlank(tableVO.getName())){
            return RestResponse.failure("表名不能为空");
        }
        if(ArrayUtils.contains(keywords,tableVO.getName())){
            return RestResponse.failure("表名包含java关键字");
        }
        if(StringUtils.isBlank(tableVO.getComment())){
            return RestResponse.failure("表的备注不能为空");
        }
        if(tableVO.getFieldList() == null || tableVO.getFieldList().isEmpty()){
            return RestResponse.failure("表的字段不能为空");
        }
        if(tableService.existTable(tableVO.getName().toLowerCase())>0){
            return RestResponse.failure("已存在表名为【"+tableVO.getName()+"】的数据表");
        }
        String entityName = "dict,group,menu,role,site,user,table,file";
        if(entityName.equals(tableVO.getName().toLowerCase())){
            return RestResponse.failure("不允许使用【"+tableVO.getName()+"】做数据库名称");
        }
        tableService.creatTable(tableVO);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(String name,Model model){
        TableVO tableVO = tableService.detailTable(name);
        String[] comments = tableVO.getComment().split(",");
        tableVO.setComment(comments[0]);
        int tabletype = comments.length>1?Integer.parseInt(comments[1]):0;
        tableVO.setTabletype(tabletype);

        String base  = "id,create_by,create_date,update_by,update_date,del_flag";
        String tree  = "id,parent_id,level,parent_ids,sort,create_by,create_date,update_by,update_date,del_flag";

        List<String> allNames = Lists.newArrayList();
        for (TableField t : tableVO.getFieldList()){
            if(tabletype == 1){
                if(!base.contains(t.getName())){
                    changeTableField(t);
                    allNames.add(t.getName());
                }
            }
            if(tabletype == 2){
                if(!tree.contains(t.getName())){
                    changeTableField(t);
                    allNames.add(t.getName());
                }
            }
        }
        model.addAttribute("tableVO",tableVO);
        model.addAttribute("allNames",JSONObject.toJSONString(allNames));
        return "admin/system/table/edit";
    }

    @RequiresPermissions("sys:table:edit")
    @PostMapping("editTable")
    @ResponseBody
    @SysLog("保存数据表编辑数据")
    public RestResponse editTable(@RequestParam(value = "name",required = false)String name,
                                  @RequestParam(value = "oldname",required = false)String oldname,
                                  @RequestParam(value = "comment",required = false)String comment,
                                  @RequestParam(value = "tabletype",required = false)Integer tableType){
        if(StringUtils.isNotBlank(name)){
            if(StringUtils.isBlank(oldname)){
                return RestResponse.failure("原来的名称不能为空");
            }
            if(ArrayUtils.contains(keywords,name)){
                return RestResponse.failure("表名包含java关键字");
            }
            if(!name.equals(oldname)){
                if(tableService.existTable(name)>0){
                    return RestResponse.failure("已存在该数据表的名字");
                }
                tableService.changeTableName(name,oldname,comment,tableType);
            }
        }
        if(StringUtils.isNotBlank(comment)){
            if(tableType == null){
                return RestResponse.failure("数据表类型不能为空");
            }
        }
        tableService.changeTableComment(name,comment,tableType);
        return RestResponse.success();
    }

    @RequiresPermissions("sys:table:list")
    @PostMapping("fieldlist")
    @ResponseBody
    @SysLog("请求字段展示数据(分页显示)")
    public LayerData<TableField> fieldlist(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                           @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                           ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isBlank(name)) {
                map.remove("name");
            }
            String tableType = (String)map.get("tableType");
            if(!tableType.equals("1") && !tableType.equals("2") && !tableType.equals("3")){
                map.remove("tableType");
            }else{
                map.put("tableType",Integer.valueOf(tableType));
            }
        }
        IPage<TableField> tablePage = tableService.selectTableFieldPage(new Page<>(page,limit),map);
        LayerData<TableField> layerData = new LayerData<>();
        layerData.setData(tablePage.getRecords());
        layerData.setCount((int)tablePage.getTotal());
        return layerData;
    }

    private void changeTableField(TableField t){
        String[] c = t.getComment().split(",");
        t.setComment(c[0]);
        if(c.length>1) {
            t.setDofor(c[1]);
        }
        if(c.length>3){
            t.setDefaultValue(Boolean.valueOf(c[3]));
        }else {
            t.setDefaultValue(false);
        }
        if(c.length>4){
            t.setListIsShow(Boolean.valueOf(c[4]));
        }
        if(c.length>5){
            t.setListIsSearch(Boolean.valueOf(c[5]));
        }
    }

    @RequiresPermissions("sys:table:list")
    @PostMapping("showFields")
    @ResponseBody
    @SysLog("请求字段展示数据(全部显示)")
    public LayerData<TableField> showFields(@RequestParam(value = "s_name",required = false)String name,
                                            @RequestParam(value = "s_tableType",required = false)Integer tableType){
        LayerData<TableField> tableLayerData = new LayerData<>();
        if(StringUtils.isBlank(name)){
            return null;
        }
        if(tableType != 1 && tableType != 2 && tableType != 3){
            return null;
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("name",name);
        map.put("tableType",tableType);
        List<TableField> list = tableService.selectFields(map);
        for(TableField t : list){
            changeTableField(t);
        }
        tableLayerData.setData(list);
        tableLayerData.setCount(list.size());
        return tableLayerData;
    }

    @RequiresPermissions("sys:table:addField")
    @PostMapping("addField")
    @ResponseBody
    @SysLog("保存单独新增字段数据")
    public RestResponse addField(@RequestBody TableField tableField){
        if(StringUtils.isBlank(tableField.getName())){
            return RestResponse.failure("字段名称不能为空");
        }
        if(ArrayUtils.contains(keywords,tableField.getName())){
            return RestResponse.failure("字段名中包含java关键字");
        }
        if(StringUtils.isBlank(tableField.getComment())){
            return RestResponse.failure("字段注释不能为空");
        }
        if(StringUtils.isBlank(tableField.getDofor())){
            return RestResponse.failure("字段功能不能为空");
        }
        if(StringUtils.isBlank(tableField.getIsNullValue())){
            return RestResponse.failure("字段非空不能为空");
        }
        if(StringUtils.isBlank(tableField.getType())){
            return RestResponse.failure("字段类型不能为空");
        }
        if(StringUtils.isBlank(tableField.getTableName())){
            return RestResponse.failure("数据表名称不能为空");
        }
        if(tableField.getTableType() == 1) {
            String base = "id,create_by,create_date,update_by,update_date,del_flag";
            if (base.contains(tableField.getName())) {
                return RestResponse.failure("基本数据表不能包含字段【" + tableField.getName() + "】");
            }
        }
        if(tableField.getTableType() == 2) {
            String tree = "id,parent_id,level,parent_ids,sort,create_by,create_date,update_by,update_date,del_flag";
            if (tree.contains(tableField.getName())) {
                return RestResponse.failure("树类型结构表不能包含字段【" + tableField.getName() + "】");
            }
        }
        if(tableField.getTableType() == 3) {
            if("id".equalsIgnoreCase(tableField.getName())){
                return RestResponse.failure("辅助表不允许包含id字段");
            }
        }
        tableService.addColumn(tableField);
        return RestResponse.success();
    }

    @RequiresPermissions("sys:table:editField")
    @PostMapping("editField")
    @ResponseBody
    @SysLog("保存单独编辑字段数据")
    public RestResponse editField(@RequestBody TableField tableField){
        if(StringUtils.isBlank(tableField.getName())){
            return RestResponse.failure("字段名称不能为空");
        }
        if(ArrayUtils.contains(keywords,tableField.getName())){
            return RestResponse.failure("字段名中包含java关键字");
        }
        if(StringUtils.isBlank(tableField.getComment())){
            return RestResponse.failure("字段注释不能为空");
        }
        if(StringUtils.isBlank(tableField.getDofor())){
            return RestResponse.failure("字段功能不能为空");
        }
        if(StringUtils.isBlank(tableField.getIsNullValue())){
            return RestResponse.failure("字段非空不能为空");
        }
        if(StringUtils.isBlank(tableField.getType())){
            return RestResponse.failure("字段类型不能为空");
        }
        if(StringUtils.isBlank(tableField.getTableName())){
            return RestResponse.failure("数据表名称不能为空");
        }
        if(StringUtils.isBlank(tableField.getOldName())){
            return RestResponse.failure("原字段名称不能为空");
        }
        if(tableField.getTableType() == 1) {
            String base = "id,create_by,create_date,update_by,update_date,del_flag";
            if (base.contains(tableField.getName())) {
                return RestResponse.failure("基本数据表不能包含字段【" + tableField.getName() + "】");
            }
        }
        if(tableField.getTableType() == 2) {
            String tree = "id,parent_id,level,parent_ids,sort,create_by,create_date,update_by,update_date,del_flag";
            if (tree.contains(tableField.getName())) {
                return RestResponse.failure("树类型结构表不能包含字段【" + tableField.getName() + "】");
            }
        }
        if(tableField.getTableType() == 3) {
            if("id".equalsIgnoreCase(tableField.getName())){
                return RestResponse.failure("辅助表不允许包含id字段");
            }
        }
        tableService.updateColumn(tableField);
        return RestResponse.success();
    }

    @PostMapping("fieldIsExist")
    @ResponseBody
    public RestResponse fieldIsExist(@RequestParam(value = "fieldName",required = false)String fieldName,
                                    @RequestParam(value = "tableName",required = false)String tableName){
        if(StringUtils.isBlank(fieldName)){
            return RestResponse.failure("字段名不能为空");
        }
        if(ArrayUtils.contains(keywords,fieldName)){
            return RestResponse.failure("字段名中包含java关键字");
        }
        if(StringUtils.isBlank(tableName)){
            return RestResponse.failure("数据表名不能为空");
        }
        if(ArrayUtils.contains(keywords,tableName)){
            return RestResponse.failure("数据表名中包含java关键字");
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("fieldName",fieldName);
        map.put("tableName",tableName);
        if(tableService.existTableField(map)>0){
            return RestResponse.failure("已存在该字段");
        }
        return RestResponse.success();
    }


    @RequiresPermissions("sys:table:deleteField")
    @PostMapping("deleteField")
    @ResponseBody
    @SysLog("删除字段数据")
    public RestResponse deleteField(@RequestParam(value = "fieldName",required = false)String fieldName,
                                    @RequestParam(value = "tableName",required = false)String tableName){
        if(StringUtils.isBlank(fieldName)){
            return RestResponse.failure("字段名不能为空");
        }
        if(StringUtils.isBlank(tableName)){
            return RestResponse.failure("数据表名不能为空");
        }
        tableService.dropTableField(fieldName,tableName);
        return RestResponse.success();
    }

    @RequiresPermissions("sys:table:deleteTable")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据表数据")
    public RestResponse delete(@RequestParam(value = "tableName",required = false)String tableName){
        if(StringUtils.isBlank(tableName)){
            return RestResponse.failure("数据表名称不能为空");
        }
        if(tableName.contains("sys_")){
            return RestResponse.failure("不能删除系统表");
        }
        tableService.dropTable(tableName);
        return RestResponse.success();
    }

    @RequiresPermissions("sys:table:download")
    @PostMapping("download")
    @ResponseBody
    @SysLog("下载JAVA源码")
    public RestResponse download(@RequestParam(required = false)String[] baseTables,
            @RequestParam(required = false)String[] treeTables,
            HttpServletResponse response) throws Exception {
        if(baseTables != null && treeTables != null){
            if(baseTables.length == 0 && treeTables.length == 0){
                return RestResponse.failure("数据表不能为空");
            }
        }

        synchronized(this){
            File baseFloder = new File(createTableFiles.baseDic);
            ZipUtil.deleteDir(baseFloder);
            if(baseTables != null && baseTables.length>0){
                createTableFiles.createFile(baseTables,1, siteService.getCurrentSite());
            }
            if(treeTables != null && treeTables.length>0){
                createTableFiles.createFile(treeTables,2, siteService.getCurrentSite());
            }
            File f = new File(createTableFiles.zipFile);
            try {
                if(f.exists() && !f.delete()){
                    LOGGER.error("删除文件失败:{}", f.getName());
                }
                com.xiaoleilu.hutool.util.ZipUtil.zip(createTableFiles.baseDic, createTableFiles.zipFile);
            } catch (SecurityException e) {
                LOGGER.error("压缩文件失败:{}", e.getMessage());
            }
            String filename = new String(f.getName().getBytes("GB2312"), "ISO8859-1");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength((int) f.length());
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            
            try (BufferedInputStream br = new BufferedInputStream(Files.newInputStream(f.toPath()));
                 OutputStream out = response.getOutputStream()) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = br.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }
            
            if(!f.delete()) {
                LOGGER.error("删除文件失败:{}", f.getName());
            }
        }
        return RestResponse.success();
    }
}
