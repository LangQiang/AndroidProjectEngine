//
// Created by 神强 on 2023/4/24.
//

#include "app_callback.h"

void AppCallback::onLog(const char *tag, const char *log) const {
    JNIEnv *env;
    int mNeedDetach = 0;

    try {
        int getEnvStat = (*javaVM).GetEnv((void **)&env,JNI_VERSION_1_4);
        if (getEnvStat == JNI_EDETACHED) {
            //如果没有， 主动附加到jvm环境中，获取到env
            if ((*javaVM).AttachCurrentThread(&env, nullptr) != 0) {
                return;
            }
            mNeedDetach = JNI_TRUE;
        }
        //通过全局变量g_obj 获取到要回调的类
        jclass javaClass = (*env).GetObjectClass(callback_J);

        if (javaClass == nullptr) {
            LOGE("AppCallback Unable to find class: callBack");
            (*javaVM).DetachCurrentThread();
            return;
        }

        //获取要回调的方法ID
        jmethodID javaCallbackId = (*env).GetMethodID(javaClass,"onLog",
                                                      "(Ljava/lang/String;Ljava/lang/String;)V");
        if (javaCallbackId == nullptr) {
            LOGE("AppCallback Unable to find method:%s", "onRecordDataCallback");
            return;
        }
        jstring tagStr = (*env).NewStringUTF(reinterpret_cast<const char *>(tag));
        jstring logStr = (*env).NewStringUTF(reinterpret_cast<const char *>(log));
        (*env).CallVoidMethod(callback_J, javaCallbackId, tagStr, logStr);


        (*env).DeleteLocalRef(javaClass);

        //释放当前线程
        if(mNeedDetach) {
            (*javaVM).DetachCurrentThread();
        }
        env = nullptr;
    } catch (const std::exception& e) {
        //释放当前线程
        if(mNeedDetach) {
            (*javaVM).DetachCurrentThread();
        }
        env = nullptr;
        LOGE("AppCallback %s", e.what());
    }
}

//void AppCallback::onThread() const {
//    JNIEnv *env;
//    int mNeedDetach = 0;
//
//    try {
//        int getEnvStat = (*javaVM).GetEnv((void **)&env,JNI_VERSION_1_4);
//        if (getEnvStat == JNI_EDETACHED) {
//            //如果没有， 主动附加到jvm环境中，获取到env
//            if ((*javaVM).AttachCurrentThread(&env, nullptr) != 0) {
//                return;
//            }
//            mNeedDetach = JNI_TRUE;
//        }
//        //通过全局变量g_obj 获取到要回调的类
//        jclass javaClass = (*env).GetObjectClass(callback_J);
//
//        if (javaClass == nullptr) {
//            LOGE("AppCallback Unable to find class: callBack");
//            (*javaVM).DetachCurrentThread();
//            return;
//        }
//
//        //获取要回调的方法ID
//        jmethodID javaCallbackId = (*env).GetMethodID(javaClass,"onNewTread",
//                                                      "()V");
//        if (javaCallbackId == nullptr) {
//            LOGE("AppCallback Unable to find method:%s", "onRecordDataCallback");
//            return;
//        }
//        (*env).CallVoidMethod(callback_J, javaCallbackId);
//
//
//        (*env).DeleteLocalRef(javaClass);
//
//        //释放当前线程
//        if(mNeedDetach) {
//            (*javaVM).DetachCurrentThread();
//        }
//        env = nullptr;
//    } catch (const std::exception& e) {
//        //释放当前线程
//        if(mNeedDetach) {
//            (*javaVM).DetachCurrentThread();
//        }
//        env = nullptr;
//        LOGE("AppCallback %s", e.what());
//    }
//}
