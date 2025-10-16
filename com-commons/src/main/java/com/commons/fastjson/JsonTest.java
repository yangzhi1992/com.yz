package com.commons.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
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
        //1、将对象转换为JSON字符串
        User user = new User("John", 30);
        String jsonString = JSON.toJSONString(user);

        //2、将JSON字符串转换为对象
        user = JSON.parseObject(jsonString, User.class);

        //3、将JSON字符串转换为JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String name = jsonObject.getString("name");
        int age = jsonObject.getIntValue("age");
        System.out.println("name:" + name + ",age:" + age);

        //4、将对象转换为JSONObject
        JSONObject jsonObject2 = (JSONObject) JSON.toJSON(user);

        //5、将JSON字符串转换为数组或列表
        String jsonArrayString = "[{\"name\":\"John\",\"age\":30}, {\"name\":\"Jane\",\"age\":25}]";
        List<User> users = JSON.parseArray(jsonArrayString, User.class);
        System.out.println(users);

        //6、通过 JSONPath（类似 XPath 的 JSON 查询语法）快速定位并提取嵌套 JSON 中的字段值，避免手动逐层解析。
        String jsonStr = "{\n"
                + "    \"users\": [\n"
                + "        {\"id\": 1, \"name\": \"Alice\"},\n"
                + "        {\"id\": 2, \"name\": \"Bob\"},\n"
                + "        {\"id\": 3, \"name\": \"Charlie\"}\n"
                + "    ]\n"
                + "}";
        JSONObject jsonObject3 = JSON.parseObject(jsonStr);

        // 6.1、 提取 users 数组第 2 个元素的 name 字段
        Object name2 = JSONPath.eval(jsonObject3, "$.users[1].name");
        System.out.println(name2);

        // 6.2、提取 users 中 id > 1 的所有用户名
        List<Object> names = (List<Object>) JSONPath.eval(jsonObject3, "$.users[?(@.id > 1)].name");
        System.out.println(names);

        // 6.3、提取所有用户的 id
        List<Object> names2 = (List<Object>) JSONPath.eval(jsonObject3, "$.users[*].id");
        System.out.println(names2);

        // 6.4、递归搜索所有层级的 name 字段
        List<Object> names3 = (List<Object>) JSONPath.eval(jsonObject3, "$..name");
        System.out.println(names3);
    }
}
