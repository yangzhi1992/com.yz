package com.commons.gateway.controller;

import com.commons.gateway.dto.BackendServiceDTO;
import com.commons.gateway.route.RedisBackendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/manage")
public class ManagementController {

    private final RedisBackendService redisBackendService;

    public ManagementController(RedisBackendService redisBackendService) {
        this.redisBackendService = redisBackendService;
    }

    @GetMapping("/services")
    public Flux<BackendServiceDTO> getAllServices(String key) {
        return redisBackendService.getAllServices(key);
    }

    @PostMapping("/services")
    public Mono<ResponseEntity<String>> addService(@RequestBody BackendServiceDTO service) {
        return redisBackendService.addService(service)
                                  .map(count -> ResponseEntity.ok("Service added successfully"))
                                  .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                                                                              .body("Error: " + e.getMessage())));
    }

    @DeleteMapping("/services/{id}")
    public Mono<ResponseEntity<?>> removeService(@PathVariable String id, String key) {
        return redisBackendService.removeService(id, key)
                                  .map(count -> count > 0 ?
                                          ResponseEntity.ok("Service removed successfully") :
                                          ResponseEntity.notFound()
                                                        .build())
                                  .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                                                                              .body("Error: " + e.getMessage())));
    }

    @PutMapping("/services/{id}")
    public Mono<ResponseEntity<?>> updateService(@PathVariable String id, @RequestBody BackendServiceDTO service) {
        service.setId(id); // Ensure ID matches path
        return redisBackendService.updateService(service)
                                  .map(success -> success ?
                                          ResponseEntity.ok("Service updated successfully") :
                                          ResponseEntity.notFound()
                                                        .build())
                                  .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                                                                              .body("Error: " + e.getMessage())));
    }

    @PostMapping("/services/clear")
    public Mono<ResponseEntity<String>> clearAllServices(String key) {
        return redisBackendService.clearAllServices(key)
                                  .map(success -> ResponseEntity.ok("All services cleared"))
                                  .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                                                                              .body("Error: " + e.getMessage())));
    }
}