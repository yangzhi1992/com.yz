package com.commons.controller;

import com.commons.db.mapper.DbCommonMapper;
import com.commons.db.model.DbCommonEntity;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbController {

    @Autowired
    @Qualifier("giftSpecialPackageEntityMapper")
    private DbCommonMapper giftSpecialPackageEntityMapper;

    @PostMapping("/health1")
    public String health(@RequestParam String msg) throws IOException {
        List<Object> objects = giftSpecialPackageEntityMapper.selectDb(DbCommonEntity.builder().build());
        return "Message sent: " + objects;
    }

}