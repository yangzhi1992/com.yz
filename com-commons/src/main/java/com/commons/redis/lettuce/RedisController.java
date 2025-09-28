package com.commons.redis.lettuce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {
    
    private final MultiRedisService multiRedisService;
    
    public RedisController(MultiRedisService multiRedisService) {
        this.multiRedisService = multiRedisService;
    }
    
    @PostMapping("/{dataSource}/set")
    public ResponseEntity<String> setValue(
            @PathVariable String dataSource,
            @RequestParam String key,
            @RequestParam String value) {
        
        multiRedisService.set(dataSource, key, value);
        return ResponseEntity.ok("设置成功");
    }
    
    @GetMapping("/{dataSource}/get")
    public ResponseEntity<Object> getValue(
            @PathVariable String dataSource,
            @RequestParam String key) {
        
        Object value = multiRedisService.get(dataSource, key, Object.class);
        return ResponseEntity.ok(value);
    }
    
    @GetMapping("/{dataSource}/info")
    public ResponseEntity<MultiRedisService.RedisInfo> getInfo(@PathVariable String dataSource) {
        MultiRedisService.RedisInfo info = multiRedisService.getRedisInfo(dataSource);
        return ResponseEntity.ok(info);
    }
}