package com.commons.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public final class ArrayTool {

    private ArrayTool() {

    }


    public static boolean contains(byte[] array, byte key) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (byte s : array) {
            if (s == key) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(short[] array, short key) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (short s : array) {
            if (s == key) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] array, int key) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (int s : array) {
            if (s == key) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(long[] array, long key) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (long s : array) {
            if (s == key) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(String[] conatains, String key) {
        if (StringTool.isBlank(key) || conatains == null || conatains.length == 0) {
            return false;
        }
        for (String s : conatains) {
            if (s.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断child的成员是否在parent中都存在
     */
    public static boolean containsAll(String[] parent, String[] child) {
        if (isBlank(parent) || isBlank(child)) {
            return false;
        }
        if (parent.length < child.length) {
            return false;
        }
        Set<String> parentSet = asSet(parent);
        for (String s : child) {
            if (!parentSet.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAny(String[] parent, String[] child) {
        if (isBlank(parent) || isBlank(child)) {
            return false;
        }

        Set<String> parentSet = asSet(parent);
        for (String s : child) {
            if (parentSet.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组为空
     */
    public static <T> boolean isBlank(T[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * 数组非空
     */
    public static <T> boolean isNotBlank(T[] array) {
        return (array != null && array.length > 0);
    }

    public static <T> void add(List<T> target, T[] objectArray) {
        if (target == null) {
            return;
        }
        if (objectArray == null || objectArray.length == 0) {
            return;
        }
        for (T obj : objectArray) {
            target.add(obj);
        }
    }

    public static <T> List<T> asList(T[] objectArray) {
        List<T> result = new ArrayList<>();
        if (objectArray == null || objectArray.length == 0) {
            return result;
        }
        for (T obj : objectArray) {
            result.add(obj);
        }
        return result;
    }

    public static <T> Set<T> asSet(T[] objectArray) {
        Set<T> result = new HashSet<>();
        if (objectArray == null || objectArray.length == 0) {
            return result;
        }
        for (T obj : objectArray) {
            result.add(obj);
        }
        return result;
    }

    public static List<Integer> convert2Int(String[] stringArray) {
        if (isBlank(stringArray)) {
            return null;
        }
        List<Integer> result = new ArrayList<>(stringArray.length);
        for (String str : stringArray) {
            result.add(StringTool.parseInt(str));
        }
        return result;
    }

    public static List<Long> convert2Long(String[] stringArray) {
        if (isBlank(stringArray)) {
            return null;
        }
        List<Long> result = new ArrayList<>(stringArray.length);
        for (String str : stringArray) {
            result.add(StringTool.parseLong(str));
        }
        return result;
    }

    public static String join(Object[] args) {
        if (isBlank(args)) {
            return "";
        }
        StringBuilder result = new StringBuilder(20);
        for (Object n : args) {
            if (n != null) {
                result.append(n);
            }
        }
        return result.toString();
    }

    public static String join(int[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(20);
        for (int n : args) {
            result.append(n);
        }
        return result.toString();
    }

    public static String join(long[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(20);
        for (long n : args) {
            result.append(n);
        }
        return result.toString();
    }

    /**
     * 数组包含
     *
     * @param array 数组
     * @param val 被包含值
     * @param <T> 类型
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T val) {
        if (isBlank(array) || val == null) {
            return false;
        }
        for (T t : array) {
            if (val.equals(t)) {
                return true;
            }
        }
        return false;
    }
}
