package com.mysiteforme.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.service.QuartzTaskService;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.annotation.SysLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 定时任务  前端控制器
 * </p>
 *
 * @author wangl
 * @since 2018-01-24
 */
@Controller
@RequestMapping("/admin/quartzTask")
public class QuartzTaskController {

    private QuartzTaskService quartzTaskService;

    public QuartzTaskController() {}

    @Autowired
    public QuartzTaskController(QuartzTaskService quartzTaskService) {
        this.quartzTaskService = quartzTaskService;
    }

    /**
     * 显示任务列表页面
     * @return 列表页面路径
     */
    @GetMapping("list")
    @SysLog("跳转定时任务列表")
    public String list(){
        return "/admin/quartzTask/list";
    }

    /**
     * 获取任务列表数据
     * @param limit 每页数量
     * @param page 分页参数
     * @return 分页数据
     */
    @RequiresPermissions("quartz:task:list")
    @PostMapping("list")
    @ResponseBody
    public LayerData<QuartzTask> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                      @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                      ServletRequest request){
        Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<QuartzTask> layerData = new LayerData<>();
        QueryWrapper<QuartzTask> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String name = (String) map.get("name");
            if(StringUtils.isNotBlank(name)) {
                wrapper.like("name",name);
            }else{
                map.remove("name");
            }

            String status = (String) map.get("status");
            if(StringUtils.isNotBlank(status)) {
                wrapper.eq("status",status);
            }else{
                map.remove("status");
            }

        }
        IPage<QuartzTask> pageData = quartzTaskService.queryList(wrapper,new Page<>(page,limit));
        layerData.setData(pageData.getRecords());
        layerData.setCount((int)pageData.getTotal());
        return layerData;
    }

    /**
     * 显示任务添加页面
     * @return 添加页面路径
     */
    @GetMapping("add")
    public String add(){
        return "/admin/quartzTask/add";
    }

    /**
     * 保存任务
     * @param quartzTask 任务对象
     * @return 操作结果
     */
    @RequiresPermissions("quartz:task:add")
    @PostMapping("add")
    @SysLog("保存新增定时任务数据")
    @ResponseBody
    public RestResponse add(QuartzTask quartzTask){
        quartzTaskService.saveQuartzTask(quartzTask);
        return RestResponse.success();
    }

    /**
     * 编辑任务
     * @param id 任务ID
     * @param model 模型对象
     * @return 编辑页面路径
     */
    @GetMapping("edit")
    public String edit(Long id,Model model){
        QuartzTask quartzTask = quartzTaskService.getById(id);
        model.addAttribute("quartzTask",quartzTask);
        return "/admin/quartzTask/edit";
    }

    /**
     * 更新任务
     * @param quartzTask 任务对象
     * @return 操作结果
     */
    @RequiresPermissions("quartz:task:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑定时任务数据")
    public RestResponse edit(QuartzTask quartzTask){
        if(null == quartzTask.getId() || 0 == quartzTask.getId()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskService.updateQuartzTask(quartzTask);
        return RestResponse.success();
    }

    /**
     * 删除任务
     * @param ids 任务ID集合
     * @return 操作结果
     */
    @RequiresPermissions("quartz:task:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除定时任务数据")
    public RestResponse delete(@RequestParam(value = "ids[]",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskService.deleteBatchTasks(ids);
        return RestResponse.success();
    }

    /**
     * 暂停选中的定时任务
     * @param ids 任务ID List
     */
    @RequiresPermissions("quartz:task:paush")
    @PostMapping("paush")
    @ResponseBody
    public RestResponse paush(@RequestParam(value = "ids[]",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskService.paush(ids);
        return RestResponse.success();
    }

    /**
     * 恢复选中的定时任务运行
     * @param ids 任务ID List
     */
    @RequiresPermissions("quartz:task:resume")
    @PostMapping("resume")
    @ResponseBody
    public RestResponse resume(@RequestParam(value = "ids[]",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskService.resume(ids);
        return RestResponse.success();
    }

    /**
     * 立即执行选中的定时任务
     * @param ids 任务ID List
     */
    @RequiresPermissions("quartz:task:run")
    @PostMapping("run")
    @ResponseBody
    public RestResponse run(@RequestParam(value = "ids[]",required = false)List<Long> ids){
        if(null == ids || ids.isEmpty()){
            return RestResponse.failure("ID不能为空");
        }
        quartzTaskService.run(ids);
        return RestResponse.success();
    }

}