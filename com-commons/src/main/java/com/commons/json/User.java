package com.commons.json;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @JSONField(ordinal=1)//配置序列化的字段顺序（1.1.42版本之后才支持）
 * @JSONField(serialize=false) //是否参与序列化：该字段不输出  但是如果加了final，这个字段就无法被过滤
 * @JSONField(derialize=false) //是否参与反序列化：该字段不输出  但是如果加了final，这个字段就无法被过滤
 * @JSONField(format="yyyy-MM-dd HH:mm:ss") //日期按照指定格式序列化
 * @JSONField(name="别名");     //使用字段别名
 * @JSONField(serialzeFeatures={SerialzeFeatures属性}); //序列化规则
 * @JSONField(parseFeatures={Features属性}); //反序列化规则
 */
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
