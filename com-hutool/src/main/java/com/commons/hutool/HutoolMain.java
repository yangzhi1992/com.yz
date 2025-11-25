package com.commons.hutool;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

import java.sql.SQLException;

public class HutoolMain {

    private static final Db db = Db.use("mysql");

    public static void main(String[] args) throws SQLException {
        Long id = db.insertForGeneratedKey(Entity.create("user").set("name", "unitTestUser").set("age", 66));
    }
}
