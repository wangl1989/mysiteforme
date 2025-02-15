/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:39:32
 * @ Description: 定时任务日志控制器 提供定时任务日志的增删改查功能
 */

package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;

import jakarta.servlet.ServletRequest;

import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.Map;


@Controller
@RequestMapping("/admin/quartzTaskLog")
public class QuartzTaskLogController {

    private QuartzTaskLogService quartzTaskLogService;

    public QuartzTaskLogController() {}

    @Autowired
    public QuartzTaskLogController(QuartzTaskLogService quartzTaskLogService) {
        this.quartzTaskLogService = quartzTaskLogService;
    }

    @GetMapping("list")
    @SysLog("跳转任务执行日志列表")
    public String list(){
        return "/admin/quartzTaskLog/list";
    }

    @PostMapping("list")
    @ResponseBody
    public LayerData<QuartzTaskLog> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<QuartzTaskLog> layerData = new LayerData<>();
        QueryWrapper<QuartzTaskLog> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isNotBlank(name)) {
                wrapper.like("name",name);
            }else{
                map.remove("name");
            }

        }
        IPage<QuartzTaskLog> pageData = quartzTaskLogService.page(new Page<>(page,limit),wrapper);
        layerData.setData(pageData.getRecords());
        layerData.setCount((int)pageData.getTotal());
        return layerData;
    }

    @GetMapping("add")
    public String add(){
        return "/admin/quartzTaskLog/add";
    }

    @PostMapping("add")
    @ResponseBody
    public RestResponse add(QuartzTaskLog quartzTaskLog){
        quartzTaskLogService.save(quartzTaskLog);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        QuartzTaskLog quartzTaskLog = quartzTaskLogService.getById(id);
        model.addAttribute("quartzTaskLog",quartzTaskLog);
        return "/admin/quartzTaskLog/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    public RestResponse edit(QuartzTaskLog quartzTaskLog){
        if(null == quartzTaskLog.getId() || 0 == quartzTaskLog.getId()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskLogService.updateById(quartzTaskLog);
        return RestResponse.success();
    }

    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除任务执行日志数据")
    public RestResponse delete(@RequestParam(value = "id",required = false)Long id){
        if(null == id || 0 == id){
            return RestResponse.failure("ID不能为空");
        }
        QuartzTaskLog quartzTaskLog = quartzTaskLogService.getById(id);
        quartzTaskLog.setDelFlag(true);
        quartzTaskLogService.updateById(quartzTaskLog);
        return RestResponse.success();
    }

}