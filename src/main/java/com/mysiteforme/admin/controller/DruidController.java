package com.mysiteforme.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Druid监控页面控制器
 * 提供监控统计信息的展示
 */
@Controller
@RequestMapping("/admin/druid")
public class DruidController {

    /**
     * 显示监控首页
     * @return 监控页面路径
     */
    @GetMapping("list")
    public String index() {
        return "admin/system/druid/index";
    }

}
