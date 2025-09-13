package com.commons.timer.spring.spring3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当访问上述 /run-async 时，你会看到立即返回的响应 Task is running in the background!。
 */
@RestController
public class AsyncController {

    @Autowired
    private MyAsyncService asyncService;

    @GetMapping("/run-async")
    public String runAsyncTask() {
        asyncService.executeAsyncTask();  // 异步执行
        return "Task is running in the background!";
    }
}
