package com.commons.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.collections.map.TransformedMap;

/**
 *
 */
public final class CollectionTool {

    /**
     * 私有构造函数,防止误用
     */
    private CollectionTool() {
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合类型的对象
     * @param <T> 类型参数
     * @return 如果集合对象为空或者集合的size为0就返回true, 否则返回false
     */
    public static <T> boolean isBlank(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Map是否为空
     */
    public static boolean isBlank(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotBlank(Map map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断是否为空数组
     *
     * @param array 传入的数组
     * @param <T> 数组的类型参数
     * @return 如果数组为null或者数组的长度为0就返回true, 否则返回false,请调用ArrayTool.isBlank
     */
    @Deprecated
    public static <T> boolean isBlank(T[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * 合并所有集合
     *
     * @param lists List类型的集合
     * @param <T> 集合的参数类型
     * @return 合并后的集合
     */
    public static <T> List<T> mergeAll(List<T>... lists) {
        List<T> mergedList = new ArrayList<>();
        for (int i = 0, len = lists.length; i < len; i++) {
            List<T> list = lists[i];
            if (list != null && !list.isEmpty()) {
                for (T obj : list) {
                    if (obj != null) {
                        mergedList.add(obj);
                    }
                }
            }
        }
        return mergedList;
    }

    /**
     * 创建一个ArrayList
     *
     * @param objs 元素
     * @param <T> 元素的类型
     * @return ArrayList
     */
    public static <T> List<T> asList(T... objs) {
        if (objs == null) {
            return Collections.EMPTY_LIST;
        }
        List<T> list = new ArrayList<>(objs.length);
        Collections.addAll(list, objs);
        return list;
    }

    public static <T> Set<T> asSet(T... objs) {
        if (objs == null) {
            return Collections.EMPTY_SET;
        }
        Set<T> set = new HashSet<>(objs.length);
        Collections.addAll(set, objs);
        return set;
    }

    /**
     * 创建简单Map的快捷方法
     *
     * @param key 键
     * @param value 值
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return Map
     */
    public static <K, V> Map<K, V> makeMap(K key, V value) {
        Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    /**
     * 创建简单Map的快捷方法
     *
     * @param key1 键1
     * @param value1 值1
     * @param key2 键2
     * @param value2 值2
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return Map
     */
    public static <K, V> Map<K, V> makeMap(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    /**
     * 用分隔符将字符串数组连接起来，构成一个字符串
     *
     * @param words 字符串数组
     * @param seperator 分隔符
     * @return 返回用分隔符连起来的字符串
     */
    public static String join(Object[] words, String seperator) {
        StringBuilder sb = new StringBuilder();
        if (words != null) {
            for (int i = 0; i < words.length; i++) {
                sb.append(words[i]);
                if (i < words.length - 1) {
                    sb.append(seperator);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将集合中的元素用分隔符连起来，构成一个字符串
     *
     * @param maps Map
     * @param seperator 分隔符
     * @return 返回用分隔符连起来的字符串
     */
    public static String join(Map<String, String> maps, String seperator) {
        Object[] objs = new Object[maps.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            objs[i++] = entry.getKey() + ":" + entry.getValue();
        }
        return join(objs, seperator);
    }

    /**
     * 将集合中的元素用分隔符连起来，构成一个字符串
     *
     * @param collection 集合
     * @param separator 分隔符
     * @return 返回用分隔符连起来的字符串
     */
    public static String join(Collection<?> collection, String separator) {
        Object[] objs = new Object[collection.size()];
        collection.toArray(objs);
        return join(objs, separator);
    }

    /**
     * 复制一个数组
     *
     * @param sourceArray 要复制的目标数组
     * @param <T> 要复制的数组泛型类型
     * @return 新的数组
     */
    public static <T> T[] copy(T[] sourceArray) {
        if (sourceArray == null) {
            return null;
        }
        return Arrays.copyOf(sourceArray, sourceArray.length);
    }

    /**
     * 将Map的value转换为String,不支持递归转换
     */
    public static Map<String, String> convert2StringMap(Map<String, Object> objectMap) {
        if (CollectionTool.isBlank(objectMap)) {
            return new HashMap<>(0);
        }
        Map<String, String> stringMap = new HashMap<>(objectMap.size());
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            Object value = entry.getValue();
            if (entry.getValue() != null) {
                if (value instanceof Date) {
                    Date d = (Date) value;
                    stringMap.put(entry.getKey(), DateTool.format(d));
                } else {
                    stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
            } else {
                stringMap.put(entry.getKey(), "");
            }
        }
        return stringMap;
    }


    /**
     * 将MAP的value转换为String,支持递归转换
     */
    public static Object convertObj2String(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        } else if (obj instanceof String || obj instanceof Number) {
            return StringTool.valueOf(obj);
        } else if (obj instanceof Boolean) {
            return obj;
        } else if (obj instanceof Date) {
            return DateTool.format((Date) obj);
        } else if (obj instanceof Map) {
            return convertMap2String((Map) obj);
        } else if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            Collection result = obj instanceof List ? new ArrayList() : new HashSet<>();
            for (Object record : collection) {
                result.add(convertObj2String(record));
            }
            return result;
        } else {
            return convertMap2String(JsonTool.convertValue(obj, Map.class));
        }
    }

    private static Map convertMap2String(Map<String, Object> objMap) {
        return TransformedMap
                .decorateTransform(objMap, StringValueTransformer.getInstance(), STRING);
    }

    private final static Transformer STRING = input -> {
        if (input == null) {
            return null;
        }
        return convertObj2String(input);
    };


    public static <T> List<T> removeDuplicates(List<T> list) {
        if (isBlank(list)) {
            return list;
        }
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    public static List<Integer> convert2Int(Collection<String> strList) {
        if (isBlank(strList)) {
            return null;
        }
        List<Integer> result = strList.stream().map(StringTool::parseInt)
                .collect(Collectors.toList());
        return result;
    }

    public static List<Long> convert2Long(Collection<String> strList) {
        if (isBlank(strList)) {
            return null;
        }
        List<Long> result = strList.stream().map(StringTool::parseLong)
                .collect(Collectors.toList());
        return result;
    }

    public static MapBuilder mapBuilder(boolean keepOrder) {
        return new MapBuilder(keepOrder);
    }

    public static <K, V> MapBuilder<K, V> mapBuilder() {
        return new MapBuilder<>(false);
    }

    public static class MapBuilder<K, V> {

        private Map<K, V> map;

        private MapBuilder(boolean keepOrder) {
            map = keepOrder ? new LinkedHashMap() : new HashMap();
        }

        public MapBuilder put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public MapBuilder remove(K k) {
            map.remove(k);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }
    }

}
