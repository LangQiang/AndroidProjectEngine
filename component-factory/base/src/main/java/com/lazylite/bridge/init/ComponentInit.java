package com.lazylite.bridge.init;

import android.content.Context;

import com.lazylite.bridge.router.IService;
import com.lazylite.bridge.router.ServiceImpl;
import com.lazylite.mod.global.BaseConfig;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.ApplicationUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class ComponentInit {

    private static final String TAG = ComponentInit.class.getSimpleName();

    private final static List<String> classNames = new ArrayList<>();

    private static boolean sIsInit;

    private static boolean sIsInitAfterAgreeProtocol;

    public static void initOnAppCreate(Context context, BaseConfig config) {

        if (sIsInit) {
            return;
        }

        sIsInit = true;

        try {

            CommonInit.initOnAppCreate(context, config);

            /*
            初始化方式方式
            * 1。runtime时扫描dex中的class文件（作为保护兜底方案）
            * 2。直接拼接class全名反射
            * 3。aop 自定义gradle插件 利用ams库改字节码自动注册（当前方案）
            * */

            initOnAppCreateAll(context, true);

        } catch (Exception e) {
            LogMgr.e(TAG, e.getMessage() + "");
        }
    }


    public static void initAfterAgreeProtocol(Context context, BaseConfig config) {
        if(sIsInitAfterAgreeProtocol) {
            return;
        }

        sIsInitAfterAgreeProtocol = true;

        try {

            CommonInit.initAfterAgreeProtocol(context,config);

            initOnAppCreateAll(context, false);

        } catch (Exception e) {
            LogMgr.e(TAG, e.getMessage() + "");
        }
    }

    private static void initOnAppCreateAll(Context context, boolean isOnCreate) {
        findInitClassFullNames(context);
        for (String className : classNames) {
            Object o;
            try {
//                Log.e("LazyLite", className + (isOnCreate ? " init" : " initAfterAgreeProtocol"));
                o = Class.forName(className).getConstructor().newInstance();
            } catch (ClassNotFoundException
                    | IllegalAccessException
                    | InstantiationException
                    | NoSuchMethodException
                    | InvocationTargetException notFoundE) {
                continue;
            }
            if (o instanceof Init) {
                Init init = (Init)o;
                try {
                    if (isOnCreate) {
                        IService instance = ServiceImpl.getInstance();
                        if (instance instanceof ServiceImpl
                                && init.getServicePair() != null
                                && init.getServicePair().first != null
                                && init.getServicePair().second != null
                        ) {
                            ((ServiceImpl) instance).register(init.getServicePair().first, init.getServicePair().second);
                        }
                        init.init(context);
                    } else {
                        init.initAfterAgreeProtocol(context);
                    }

                } catch (Throwable throwable) {
                    continue;
                }

                LogMgr.e("base", o.getClass().getSimpleName() + (isOnCreate ? " 【init】" : " 【initAfterAgreeProtocol】") + " process:" + ApplicationUtils.getCurrentProcessName());
            }
        }
    }


    private static void findInitClassFullNames(final Context context) {

        try {
            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();
                if (className.contains("com.lazy.lite.auto")) {
                    classNames.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
