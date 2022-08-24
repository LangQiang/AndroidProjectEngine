package com.lazylite.mod.utils.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TypeAdapters;
import com.lazylite.mod.utils.gson.data.BigDecimalTypeAdapter;
import com.lazylite.mod.utils.gson.data.BooleanTypeAdapter;
import com.lazylite.mod.utils.gson.data.DoubleTypeAdapter;
import com.lazylite.mod.utils.gson.data.FloatTypeAdapter;
import com.lazylite.mod.utils.gson.data.IntegerTypeAdapter;
import com.lazylite.mod.utils.gson.data.LongTypeAdapter;
import com.lazylite.mod.utils.gson.data.StringTypeAdapter;
import com.lazylite.mod.utils.gson.element.CollectionTypeAdapterFactory;
import com.lazylite.mod.utils.gson.element.ReflectiveTypeAdapterFactory;
import com.lazylite.mod.utils.gson.impl.JsonCallback;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author qyh
 * @date 2021/12/17
 * describe:Gson解析容错处理工具类。对于类型不对的字段不会抛出异常，对于常见情况处理规则如下：
 * <p>
 * 1.如果后台返回的类型和客户端定义的类型不匹配，框架就不解析这个字段
 * <p>
 * 2.如果客户端定义的是整数，但后台返回浮点数，框架就对数值进行取整并赋值给字段
 * <p>
 * 3.如果客户端定义布尔值，但是后台返回整数，框架则将非 0 的数值则赋值为 true，否则为 false
 */
public class GsonFactory {

    private static final HashMap<Type, InstanceCreator<?>> INSTANCE_CREATORS = new HashMap<>(0);

    private static final List<TypeAdapterFactory> TYPE_ADAPTER_FACTORIES = new ArrayList<TypeAdapterFactory>();

    private static JsonCallback sJsonCallback;

    private static volatile Gson sGson;

    private GsonFactory() {
    }

    /**
     * 获取单例的 Gson 对象
     */
    public static Gson getSingletonGson() {
        // 加入双重校验锁
        if (sGson == null) {
            synchronized (GsonFactory.class) {
                if (sGson == null) {
                    sGson = newGsonBuilder().create();
                }
            }
        }
        return sGson;
    }

    /**
     * 设置单例的 Gson 对象
     */
    public static void setSingletonGson(Gson gson) {
        sGson = gson;
    }

    /**
     * 注册类型适配器
     */
    public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        TYPE_ADAPTER_FACTORIES.add(factory);
    }

    /**
     * 设置Json解析容错监听，可以将失败信息上传apm
     *
     * @param callback
     */
    public static void setJsonCallback(JsonCallback callback) {
        GsonFactory.sJsonCallback = callback;
    }

    public static JsonCallback getJsonCallback() {
        return sJsonCallback;
    }

    /**
     * 注册构造函数创建器
     *
     * @param type    对象类型
     * @param creator 实例创建器
     */
    public static void registerInstanceCreator(Type type, InstanceCreator<?> creator) {
        INSTANCE_CREATORS.put(type, creator);
    }

    /**
     * 创建 Gson 构建对象
     */
    public static GsonBuilder newGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory typeAdapterFactory : TYPE_ADAPTER_FACTORIES) {
            gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory);
        }
        ConstructorConstructor constructor = new ConstructorConstructor(INSTANCE_CREATORS);
        return gsonBuilder.registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(BigDecimal.class, new BigDecimalTypeAdapter()))
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructor))
                .registerTypeAdapterFactory(new ReflectiveTypeAdapterFactory(constructor, FieldNamingPolicy.IDENTITY, Excluder.DEFAULT));
    }
}
