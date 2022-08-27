package com.javaoffers.batis.modelhelper.core;

import com.javaoffers.batis.modelhelper.convert.AbstractConver;
import com.javaoffers.batis.modelhelper.convert.Convert;
import com.javaoffers.batis.modelhelper.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

/**
 * @Description: Selector represents and selector uses entry
 * @Auther: create by cmj on 2021/12/9 10:52
 */
public class ConvertRegisterSelectorDelegate {

    private SelectorRegister applicationContext = new ModelApplicationContext();

    public final static ConvertRegisterSelectorDelegate convert = new ConvertRegisterSelectorDelegate();


    private ConvertRegisterSelectorDelegate() { }

    {
        Set<Class<? extends Convert>> converts = ReflectionUtils.getChildOfConvert();
        for (Class c : converts) {
            if(Modifier.isAbstract(c.getModifiers())){
                continue;
            }
            try {
                Constructor constructor = c.getConstructor();
                constructor.setAccessible(true);
                registerConvert( (AbstractConver) constructor.newInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * registerConvert
     * @param convert
     */
    private void registerConvert( AbstractConver convert) {
        convert.register(applicationContext);
    }

    /**
     * selector
     * @return
     */
    private Convert selector(Class src,Class des) {
        Convert convert = applicationContext.selector(new ConverDescriptor(src, des));
        if(convert==null){
            LinkedList<Class> srcSupers = getSupers(src);

            //src 升级
            for(Class srcc : srcSupers){
                convert =  selector(srcc, des);
                if(convert!=null){
                    return convert;
                }
            }
            //dec 降级
            Set<Class<?>> desSupers = ReflectionUtils.getChilds(des);
            for(Class dess : desSupers){
                convert = selector(src,dess);
                if(convert!=null){
                    return convert;
                }
            }
        }
        return convert;
    }

    private LinkedList<Class> getSupers(Class c) {
        Class superclass = c.getSuperclass();
        Class[] interfaces = c.getInterfaces();
        LinkedList<Class> srcs = new LinkedList<>();
        if(superclass != Object.class && superclass != null){
            Collections.addAll(srcs, superclass);
        }
        for(Class srcInter : interfaces){
            if(srcInter!=null){
                Collections.addAll(srcs,srcInter);
            }
        }
        return srcs;

    }

    /**
     * 类型转换
     * @param des 要转换的目标类型
     * @param srcValue 原始值类型
     * @param <T>
     * @return
     */
    public <T> T converterObject(Class<T> des, Object srcValue, Field field)  {
        try {
           return converterObject( des, srcValue);
        }catch (ClassCastException e){

           throw new ClassCastException(e.getMessage()+" the field name is "+field.getName());
        }
    }

    /**
     * 类型转换
     * @param des 要转换的目标类型
     * @param srcValue 原始值类型
     * @param <T>
     * @return
     */
    public <T> T converterObject(Class<T> des, Object srcValue) {
        //基础类型转换为包装类型，如果存在基础类型
        des = baseClassUpgrade(des);
        Class src = baseClassUpgrade(srcValue.getClass());

        if(des == src || des.isAssignableFrom(src)){
            return (T) srcValue;
        }
        //选取 convert
        Convert convert = selector(srcValue.getClass(), des);
        //开始转换
        if(convert ==null){
            throw new ClassCastException("Origin type:" +srcValue.getClass().getName()+" dont convert to  Target type: "+des.getName());
        }
        T desObject = null;
        try {
            desObject = (T) convert.convert(src.cast(srcValue));
        }catch (Exception e){
            e.printStackTrace();
        }
        return desObject;
    }

    /**
     *        @see     java.lang.Boolean#TYPE
     *      * @see     java.lang.Character#TYPE
     *      * @see     java.lang.Byte#TYPE
     *      * @see     java.lang.Short#TYPE
     *      * @see     java.lang.Integer#TYPE
     *      * @see     java.lang.Long#TYPE
     *      * @see     java.lang.Float#TYPE
     *      * @see     java.lang.Double#TYPE
     *      * @see     java.lang.Void#TYPE
     * @param baseClass
     * @return
     */
    private Class baseClassUpgrade(Class baseClass){
        if(baseClass.isPrimitive()){
            if(boolean.class == baseClass){
                return Boolean.class;
            }
            if(char.class == baseClass){
                return Character.class;
            }
            if(byte.class == baseClass){
                return Byte.class;
            }
            if(short.class == baseClass){
                return Short.class;
            }
            if(int.class == baseClass){
                return Integer.class;
            }
            if(long.class == baseClass){
                return Long.class;
            }
            if(float.class == baseClass){
                return Float.class;
            }
            if(double.class == baseClass){
                return Double.class;
            }
            if(void.class == baseClass){
                return Void.class;
            }
        }
        return baseClass;
    }


}
