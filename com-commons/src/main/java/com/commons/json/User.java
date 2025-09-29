package com.commons.json;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String name;

    //自定义序列化字段名
    @JSONField(name = "newName")
    private Integer age;

    //序列化时忽略某些字段
    @JSONField(serialize = false)
    private String desc;

    //序列化时格式化日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthDate;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
