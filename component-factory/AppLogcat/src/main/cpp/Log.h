//
// Created by GE SONG on 2021/12/20.
//

#ifndef RECORDKIT_LOG_H
#define RECORDKIT_LOG_H

#include  <android/log.h>
// log标签
#define  TAG    "PLTHook"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)



#endif //RECORDKIT_LOG_H
