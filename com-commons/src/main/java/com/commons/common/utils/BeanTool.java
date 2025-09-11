package com.commons.common.utils;

import com.google.common.collect.Lists;
import com.commons.common.exception.BusinessException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

/**
 *
 */
public final class BeanTool {

    private final static Logger logger = LoggerFactory.getLogger(BeanTool.class);
    /**
     * 用来缓存BeanCopier的缓存
     */
    private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE =
            new ConcurrentHashMap<>();

    private BeanTool() {

    }

    /**
     * 复制某个对象为目标对象类型的对象 当source与target对象属性名相同, 但数据类型不一致时，source的属性值不会复制到target对象
     *
     * @param <T> 目标对象类型参数
     * @param source 源对象
     * @param destType 目标对象类型
     * @return 复制后的结果对象
     */
    public static <T> T copyAs(Object source, Class<T> destType) {
        if (source == null || destType == null) {
            return null;
        }
        try {
            BeanCopier beanCopier = getBeanCopier(source.getClass(), destType);
            T dest = destType.newInstance();
            beanCopier.copy(source, dest, null);
            return dest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制源对象集合到目标对象列表
     *
     * @param source 源对象
     * @param destType 目标对象
     * @param <T> 源对象类型参数
     * @param <K> 目标对象类型参数
     * @return 结果集合, 一个list
     */
    public static <T, K> List<K> copyAs(Collection<T> source, Class<K> destType) {
        if (source == null || source.size() == 0 || destType == null) {
            return Lists.newArrayList();
        }

        List<K> result = new ArrayList<K>();
        if (source.isEmpty()) {
            return result;
        }
        try {
            Iterator<T> iterator = source.iterator();
            Class<?> sourceClass = iterator.next().getClass();
            BeanCopier beanCopier = getBeanCopier(sourceClass, destType);
            for (Object object : source) {
                K dest = destType.newInstance();
                beanCopier.copy(object, dest, null);
                result.add(dest);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 复制属性：从源对象复制和目标对象相同的属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copy(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanCopier beanCopier = getBeanCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
    }

    /**
     * 复制属性：从源对象复制和目标对象相同的属性，除了忽略的属性之外 如果属性名相同，但数据类型不同，会抛出运行时异常FatalBeanException
     *
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 忽略属性
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 用于model修改时的对象复制,把srcModel复制到destModel,srcModel中为null的字段不复制，同名且类型相同的属性才复制
     *
     * @param srcModel 表单提交的源对象
     * @param destModel 数据库中的目标对象
     */
    public static void copyNotNullProperties(Object srcModel, Object destModel) {
        if (srcModel == null || destModel == null) {
            return;
        }

        try {
            PropertyDescriptor[] srcDescriptors =
                    Introspector.getBeanInfo(srcModel.getClass()).getPropertyDescriptors();
            PropertyDescriptor[] destDescriptors =
                    Introspector.getBeanInfo(destModel.getClass()).getPropertyDescriptors();
            Map<String, PropertyDescriptor> destPropertyNameDescriptorMap = new HashMap<>(
                    destDescriptors.length);
            for (PropertyDescriptor destPropertyDescriptor : destDescriptors) {
                destPropertyNameDescriptorMap
                        .put(destPropertyDescriptor.getName(), destPropertyDescriptor);
            }
            for (PropertyDescriptor srcDescriptor : srcDescriptors) {
                PropertyDescriptor destDescriptor =
                        destPropertyNameDescriptorMap.get(srcDescriptor.getName());
                // 类型相同的属性才复制
                if (destDescriptor != null && destDescriptor.getPropertyType() == srcDescriptor
                        .getPropertyType()
                        && destDescriptor.getPropertyType() != Class.class) {
                    if (srcDescriptor.getReadMethod() != null) {
                        Object val = srcDescriptor.getReadMethod().invoke(srcModel);
                        if (val != null && destDescriptor.getWriteMethod() != null) {
                            destDescriptor.getWriteMethod().invoke(destModel, val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException("copyNotNullProperties:拷贝非空属性失败", e);
        }
    }

    /**
     * 适用于copy非集合类的属性，主要适用场景，源对象和目标对象有相同名称且为集合类的属性 使用该方法存在的问题：对于集合类的属性，即使类型相同，也没有办法copy成功
     * 添加该方法的原因：对于集合类，在运行期间会丢失类型信息
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyNonCollectionProperties(Object source, Object target) {
        List<String> ignoreProperties = new ArrayList<>();
        Field[] declaredFields = target.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (Collection.class.isAssignableFrom(field.getType()) || Map.class
                    .isAssignableFrom(field.getType())) {
                ignoreProperties.add(field.getName());
            }
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                ignoreProperties.toArray(new String[ignoreProperties.size()]));
    }

    /**
     * 将对象转换为Map,不支持递归转换
     */
    public static Map<String, String> convert2Map(Object bean) {
        Map<String, String> result = new HashMap<>();
        if (bean == null) {
            return result;
        }

        Map<String, Object> beanMap = JsonTool.convertValue(bean, Map.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof String) {
                    result.put(key, (String) value);
                }
                if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                    result.put(key, String.valueOf(value));
                } else if (value instanceof Date) {
                    Date d = (Date) value;
                    result.put(key, DateTool.format(d));
                } else if (value instanceof BigDecimal) {
                    BigDecimal digit = (BigDecimal) value;
                    result.put(key, digit.toPlainString());
                }
            } else {
                result.put(key, "");
            }
        }
        return result;
    }

    /**
     * 设置Field值
     *
     * @param bean 要设置对象
     * @param fieldName 字段名
     * @param value 值
     */
    public static void setFieldValue(Object bean, String fieldName, Object value) {
        try {
            Field field = findField(bean.getClass(), fieldName);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (Exception e) {
            throw new BusinessException("通过反射设置私有属性[" + fieldName + "]失败!", e);
        }
    }

    /**
     * 取得指定名称的Field, 子类找不到, 去父类里找
     *
     * @param clz 类
     * @param fieldName 指定名称
     * @return 找不到返回null
     */
    public static Field findField(Class<?> clz, String fieldName) {
        Field f = null;
        try {
            f = clz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clz.getSuperclass() != null) {
                f = findField(clz.getSuperclass(), fieldName);
            }
            logger.debug(e.getMessage(), e);
        }
        return f;
    }


    private static BeanCopier getBeanCopier(Class<?> sourceClass, Class<?> destClass) {
        String key = sourceClass.getCanonicalName() + ":" + destClass.getCanonicalName();
        BeanCopier beanCopier = BEAN_COPIER_CACHE.get(key);
        if (beanCopier == null) {
            beanCopier = BeanCopier.create(sourceClass, destClass, false);
            BEAN_COPIER_CACHE.putIfAbsent(key, beanCopier);
        }
        return beanCopier;
    }
}
