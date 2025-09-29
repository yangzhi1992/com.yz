package com.commons.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import java.util.Date;
import java.util.List;

/**
 * <!-- Alibaba FastJSON -->
 *  <dependency>
 *      <groupId>com.alibaba</groupId>
 *      <artifactId>fastjson</artifactId>
 *      <version>2.0.42</version>
 *  </dependency>
 */
public class JsonTest {
    public static void main(String[] args) {
        //将对象转换为JSON字符串
        User user = new User("John", 30);
        String jsonString = JSON.toJSONString(user);

        //将JSON字符串转换为对象
        user = JSON.parseObject(jsonString, User.class);

        //将JSON字符串转换为JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String name = jsonObject.getString("name");
        int age = jsonObject.getIntValue("age");

        //将对象转换为JSONObject
        JSONObject jsonObject2 = (JSONObject) JSON.toJSON(user);

        //将JSON字符串转换为数组或列表
        String jsonArrayString = "[{\"name\":\"John\",\"age\":30}, {\"name\":\"Jane\",\"age\":25}]";
        List<User> users = JSON.parseArray(jsonArrayString, User.class);

        Object value = JSONPath.eval(users, "$.users[1].name");
        System.out.println(value);


    }
}
