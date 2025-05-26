package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.entity.aiEntity.BusinessRequirement;
import com.mysiteforme.admin.entity.request.ChatRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.ChatService;
import com.mysiteforme.admin.util.Constants;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/ai")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("getHistoryList")
    public Result getHistoryList(@RequestParam(value = "limit",defaultValue = "1",required = false) Integer limit, HttpServletRequest request){
        String deviceId = request.getHeader(Constants.DEVICE_ID);
        if(StringUtils.isBlank(deviceId)){
            return Result.businessMsgError(MessageConstants.User.DEVICE_ID_REQUIRED);
        }
        return Result.success(chatService.getHistoryList(deviceId, limit == null ? 1 : limit));
    }

    @PostMapping(value = "chat")
    public Flux<String> chat(@RequestBody ChatRequest request, HttpServletRequest httpServletRequest){
        if(request == null){
            throw MyException.builder().businessError(MessageConstants.OBJECT_NOT_NULL).build();
        }
        String deviceId = httpServletRequest.getHeader(Constants.DEVICE_ID);
        if(StringUtils.isBlank(deviceId)){
            throw MyException.builder().businessError(MessageConstants.User.DEVICE_ID_REQUIRED).build();
        }
        request.setDeviceId(deviceId);
        return chatService.analyzeUserRequirements(request);
    }
}
