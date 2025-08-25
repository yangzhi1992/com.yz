package com.commons.utils;

import com.commons.exception.JsonException;
import com.commons.support.ReturnValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapLikeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.yaml.snakeyaml.Yaml;

/**
 *
 */
public final class JsonTool {
    private final static String PREFIX = "/";
    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // 避免Java对象中未定义属性而报错
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 键支持不带双引号
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+08"));
    }

    private JsonTool() {
    }

    public static <T> T parseObject(String content, Class<T> valueType) {
        if (StringTool.isBlank(content)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(content, valueType);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + content, e);
        }
    }

    /**
     * 用于 Value 是对象
     *
     * @param cv value类型
     */
    public static <V> Map<String, V> parseMap(String json, Class<V> cv) {
        if (StringTool.isBlank(json)) {
            return null;
        }
        try {
            MapLikeType type = OBJECT_MAPPER.getTypeFactory()
                                            .constructMapType(HashMap.class, String.class, cv);
            return OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + json, e);
        }
    }

    public static <K, V> Map<K, V> parseMap(String json, Class<K> k, Class<V> v) {
        if (StringTool.isBlank(json)) {
            return null;
        }
        try {
            MapLikeType type = OBJECT_MAPPER.getTypeFactory()
                                            .constructMapType(HashMap.class, k, v);
            return OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + json, e);
        }
    }

    public static void main(String[] args) {
        Map<Long, String> map = new HashMap<>();
        map.put(1L, "1");
        map.put(115487555555556666L, "115487555555556666");
        String json = JsonTool.toJSONString(map);
        Map<Long, String> newMap = JsonTool.parseMap(json, Long.class, String.class);
        newMap.forEach((k, v) -> {
            System.out.println(k + ":" + k.getClass()
                                          .getSimpleName());
        });
    }

    public static <V> Map<String, List<V>> parseListMap(String json, Class<V> vz) {
        if (StringTool.isBlank(json)) {
            return null;
        }
        try {
            final Iterator<Map.Entry<String, JsonNode>> nodes = OBJECT_MAPPER.readTree(json)
                                                                             .fields();
            final HashMap<String, List<V>> result = new HashMap<>();
            Map.Entry<String, JsonNode> entity = null;
            while (nodes.hasNext()) {
                entity = nodes.next();
                if (entity.getValue() == null) {
                    result.put(entity.getKey(), null);
                } else {
                    result.put(entity.getKey(), parseList(entity.getValue()
                                                                .toString(), vz));
                }
            }
            return result;
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + json, e);
        }
    }

    /**
     * 用于 value 是简单类型
     */
    public static <K, V> Map<K, V> parseMap(String json) {
        if (StringTool.isBlank(json)) {
            return null;
        }
        try {
            TypeReference<HashMap<K, V>> typeRef = new TypeReference<HashMap<K, V>>() {
            };
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + json, e);
        }
    }

    public static <T> ReturnValue<T> parseGenericObject(String content, Class<T> valueType) {
        if (StringTool.isBlank(content)) {
            return null;
        }
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                                             .constructParametricType(ReturnValue.class, valueType);
            return OBJECT_MAPPER.readValue(content, javaType);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + content, e);
        }
    }

    public static <T> ReturnValue<List<T>> parseGenericList(String content, Class<T> valueType) {
        if (StringTool.isBlank(content)) {
            return null;
        }
        try {
            JavaType listType =
                    OBJECT_MAPPER
                            .getTypeFactory()
                            .constructParametricType(ArrayList.class, valueType);
            JavaType resultType =
                    OBJECT_MAPPER
                            .getTypeFactory()
                            .constructParametricType(ReturnValue.class, listType);
            return OBJECT_MAPPER.readValue(content, resultType);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + content, e);
        }
    }

    public static <T> List<T> parseList(String content, Class<T> valueType) {
        if (StringTool.isBlank(content)) {
            return null;
        }
        try {
            JavaType listType =
                    OBJECT_MAPPER
                            .getTypeFactory()
                            .constructParametricType(ArrayList.class, valueType);
            return OBJECT_MAPPER.readValue(content, listType);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + content, e);
        }
    }

    public static <T> Set<T> parseSet(String content, Class<T> valueType) {
        if (StringTool.isBlank(content)) {
            return null;
        }
        try {
            JavaType listType =
                    OBJECT_MAPPER.getTypeFactory()
                                 .constructParametricType(HashSet.class, valueType);
            return OBJECT_MAPPER.readValue(content, listType);
        } catch (Exception e) {
            throw new JsonException("将字符串转换为对象失败" + content, e);
        }
    }

    public static String getByPath(String json, String... prperties) {
        if (StringTool.isBlank(json) || ArrayTool.isBlank(prperties)) {
            return null;
        }
        String jsonPath = PREFIX + CollectionTool.join(prperties, "/");
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json)
                                         .at(jsonPath);
            if (node.isTextual()) {
                return node.textValue();
            } else if (node.isContainerNode()) {
                return node.toString();
            } else if (node.isMissingNode()) {
                return null;
            }
            return node.asText();
        } catch (IOException e) {
            throw new JsonException("从字符串查询path失败" + jsonPath, e);
        }
    }

    public static <T> T execute(String json, JsonCallback<T> callback) {
        if (StringTool.isBlank(json)) {
            return null;
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            return callback.execute(node);
        } catch (IOException e) {
            throw new JsonException("JsonTool.execute失败" + json, e);
        }
    }

    public static <T> T parseObject(String json, Class<T> valueType, String... prperties) {
        if (StringTool.isBlank(json) || ArrayTool.isBlank(prperties)) {
            return null;
        }
        String jsonPath = PREFIX + CollectionTool.join(prperties, "/");
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json)
                                         .at(jsonPath);
            return OBJECT_MAPPER.convertValue(node, valueType);
        } catch (IOException e) {
            throw new JsonException("从字符串查询path失败" + jsonPath, e);
        }
    }

    public static String toJSONString(Object obj) {
        return toJSONString(obj, null, false);
    }

    public static String toJSONString(Object obj, boolean pretty) {
        return toJSONString(obj, null, pretty);
    }

    public static String toJSONString(Object obj, Class<?> serializationView) {
        return toJSONString(obj, serializationView, false);
    }

    /**
     * 将Java对象转换为字符串
     *
     * @param obj java对象
     * @param pretty 是否格式化
     */
    public static String toJSONString(Object obj, Class<?> serializationView, boolean pretty) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        try {
            ObjectWriter writer = OBJECT_MAPPER.writer();
            if (pretty) {
                writer = writer.withDefaultPrettyPrinter();
            }
            if (serializationView != null) {
                writer = writer.withView(serializationView);
            }
            return writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException("将对象转换为字符串失败" + obj.getClass()
                                                                  .getSimpleName(), e);
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        if (fromValue == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static String convertJson2Yml(String json) {
        Map jsonMap = parseObject(json, Map.class);
        if (jsonMap != null) {
            try {
                Yaml yaml = new Yaml();//Yaml非线程安全,必须每次创建
                return yaml.dump(jsonMap);
            } catch (Exception e) {
            }
        }
        return null;
    }
}
