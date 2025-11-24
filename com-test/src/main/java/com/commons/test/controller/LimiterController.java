package com.commons.test.controller;

import com.commons.test.limiter.DemoService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/limiter")
public class LimiterController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Resilience4j Demo Application");
        return "index";
    }

    @GetMapping("/api/call/{param}")
    @ResponseBody
    public ResponseEntity<String> callApi(@PathVariable String param) {
        try {
            String result = demoService.callWithResilience(param);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/rate-limited/{param}")
    @ResponseBody
    public ResponseEntity<String> callRateLimited(@PathVariable String param) {
        try {
            String result = demoService.callWithRateLimit(param);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/async/{param}")
    @ResponseBody
    public ResponseEntity<String> callAsync(@PathVariable String param) {
        try {
            CompletableFuture<String> future = demoService.callWithTimeout(param);
            String result = future.get(); // 阻塞等待结果
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError()
                                 .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/user/{userId}")
    @ResponseBody
    public ResponseEntity<String> getUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok("xx");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}