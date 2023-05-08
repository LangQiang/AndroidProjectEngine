//
// Created by 神强 on 2023/4/23.
//


#include "hooker.h"
#include "pthread.h"

void *proxy_transact(int bufID, int prio, const char* tag, const char* text) {
    BYTEHOOK_STACK_SCOPE();
    AppCallback::getInstance().onLog(tag, text);
    BYTEHOOK_CALL_PREV(proxy_transact,bufID, prio, tag, text);
    return nullptr;
}

void *proxy_malloc_transact(size_t len) {
    BYTEHOOK_STACK_SCOPE();
//    LOGE("malloc size:%d", len);
    return BYTEHOOK_CALL_PREV(proxy_malloc_transact, len);
}

//void *proxy_thread_transact(pthread_t* _pthread_ptr, pthread_attr_t const* _attr, void* (*_start_routine)(void*), void* args) {
//    BYTEHOOK_STACK_SCOPE();
////    LOGE("malloc size:%d", len);
//    AppCallback::getInstance().onThread();
//    return BYTEHOOK_CALL_PREV(proxy_thread_transact, _pthread_ptr, _attr, _start_routine, args);
//}


int Hook::exeHook() {
    bytehook_hook_all(
            nullptr,
            "__android_log_buf_write",
            (void *)proxy_transact,
            nullptr,
            nullptr);
//    bytehook_hook_all(
//            nullptr,
//            "pthread_create",
//            (void *)proxy_thread_transact,
//            nullptr,
//            nullptr);

    return 0;
}

