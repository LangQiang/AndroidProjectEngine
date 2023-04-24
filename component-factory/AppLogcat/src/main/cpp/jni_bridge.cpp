#include <jni.h>
#include <string>

#include "hooker.h"
#include "app_callback.h"
#include "constants.h"

JavaVM *javaVM = nullptr;


extern "C" JNIEXPORT void JNICALL
Java_cn_godq_applogcat_proxy_SysLogHooker_init(
        JNIEnv* env,
        jobject /* this */,
        jobject pltCallback) {
    (*env).GetJavaVM(&javaVM);
    AppCallback::getInstance().callback_J = (*env).NewGlobalRef(pltCallback);
    Hook::exeHook();
}
