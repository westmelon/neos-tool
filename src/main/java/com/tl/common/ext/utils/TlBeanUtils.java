package com.tl.common.ext.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TlBeanUtils {

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target){
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }


    //获取一个类及其父类的所有字段
    public static Field[] getAllFields(Object object){
        Class clazz = object.getClass();
        return getAllFields(clazz);
    }

    //获取一个类及其父类的所有字段
    public static Field[] getAllFields(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * Object obj：要操作的对象
     * String att：要操作的属性
     * Object value：要设置的属性内容
     * Class<?> type：要设置的属性类型
     */
    public static void setter(Object obj, String att, Object value, Class<?> type) {
        try {
            Method met = obj.getClass().getMethod("set" + initStr(att), type);    // 得到setter方法
            met.invoke(obj, value); // 设置setter的内容
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getter(Object obj, String att) {
        try {
            Method met = obj.getClass().getMethod("get" + initStr(att)); // 得到setter方法
            return met.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String initStr(String attr) {//如果第二个字母是大写，第一个字母不转大写
        boolean isUpperCase = Character.isUpperCase(attr.charAt(1));
        if(isUpperCase){
            return attr;
        }
        return Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
    }

    public static void genPrintSetMethodCode(Object obj,String srcParmName,String trgParmName){
        Field[] fields = getAllFields(obj);
        for(Field field:fields){
            System.out.println(trgParmName+".set"+initStr(field.getName())+"("+srcParmName+");");
        }
    }
}
