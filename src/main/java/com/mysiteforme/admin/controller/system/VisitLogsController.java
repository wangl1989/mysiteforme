package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.request.PageListVisitLogsRequest;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.mysiteforme.admin.service.VisitLogsService;
import org.springframework.web.bind.annotation.RestController;
import com.mysiteforme.admin.util.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * 访问记录表 前端控制器
 * </p>
 *
 * @author wangl1989
 * @since 2025-05-04
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/visitLogs")
@RequiredArgsConstructor
public class VisitLogsController {

    private final VisitLogsService visitLogsService;


    @GetMapping("list")
    public Result list(PageListVisitLogsRequest request){
        return  Result.success(visitLogsService.selectPageVisitLogs(request));
    }

}
