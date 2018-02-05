package com.mysiteforme.admin.controller;

import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务执行日志  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2018-01-25
 */
@Controller
@RequestMapping("/admin/quartzTaskLog")
public class QuartzTaskLogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzTaskLogController.class);

    @Autowired
    private QuartzTaskLogService quartzTaskLogService;

    @GetMapping("list")
    @SysLog("跳转任务执行日志列表")
    public String list(){
        return "/admin/quartzTaskLog/list";
    }

    @PostMapping("list")
    @ResponseBody
    @SysLog("请求任务执行日志列表数据")
    public LayerData<QuartzTaskLog> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<QuartzTaskLog> layerData = new LayerData<>();
        EntityWrapper<QuartzTaskLog> wrapper = new EntityWrapper<>();
        wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isNotBlank(name)) {
                wrapper.like("name",name);
            }else{
                map.remove("name");
            }

        }
        Page<QuartzTaskLog> pageData = quartzTaskLogService.selectPage(new Page<>(page,limit),wrapper);
        layerData.setData(pageData.getRecords());
        layerData.setCount(pageData.getTotal());
        return layerData;
    }

    @GetMapping("add")
    @SysLog("跳转新增任务执行日志页面")
    public String add(){
        return "/admin/quartzTaskLog/add";
    }

    @PostMapping("add")
    @SysLog("保存新增任务执行日志数据")
    @ResponseBody
    public RestResponse add(QuartzTaskLog quartzTaskLog){
        quartzTaskLogService.insert(quartzTaskLog);
        return RestResponse.success();
    }

    @GetMapping("edit")
    @SysLog("跳转编辑任务执行日志页面")
    public String edit(Long id,Model model){
        QuartzTaskLog quartzTaskLog = quartzTaskLogService.selectById(id);
        model.addAttribute("quartzTaskLog",quartzTaskLog);
        return "/admin/quartzTaskLog/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑任务执行日志数据")
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
        QuartzTaskLog quartzTaskLog = quartzTaskLogService.selectById(id);
        quartzTaskLog.setDelFlag(true);
        quartzTaskLogService.updateById(quartzTaskLog);
        return RestResponse.success();
    }

}