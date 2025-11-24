package com.commons.test.controller;

import com.commons.test.hibernateValidator.UserDto;
import com.commons.test.hibernateValidator.group.CreateGroup;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class HibernateValidatorController {

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        // 如果验证失败，会抛出MethodArgumentNotValidException
        return ResponseEntity.ok("验证通过");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable @Min(1) Long id,
            @RequestParam @NotBlank String name) {
        // 方法参数验证
        return ResponseEntity.ok("验证通过");
    }

    @PostMapping
    public ResponseEntity<?> createUserValidatorByGroup(@Validated(CreateGroup.class) @RequestBody UserDto user) {
        // 如果验证失败，会抛出MethodArgumentNotValidException
        return ResponseEntity.ok("验证通过");
    }

}
