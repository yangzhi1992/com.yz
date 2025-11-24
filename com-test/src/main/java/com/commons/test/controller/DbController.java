package com.commons.test.controller;

import com.commons.test.db.mapper.DbCommonMapper;
import com.commons.test.db.model.DbCommonEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "dbCommon接口", description = "dbCommon接口")
@RestController
@RequestMapping("/db")
public class DbController {

    @Autowired
    @Qualifier("dbCommonMapper")
    private DbCommonMapper dbCommonMapper;

    @Operation(summary = "获取db信息", description = "获取db信息")
    @PostMapping("/selectDb")
    public String selectDb(@RequestParam String msg) throws IOException {
        List<Object> objects = dbCommonMapper.selectDb(DbCommonEntity.builder().build());
        return "Message sent: " + objects;
    }

}