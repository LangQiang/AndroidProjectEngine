//
// Created by 神强 on 2023/4/23.
//


#include "hooker.h"

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


int Hook::exeHook() {
    bytehook_hook_all(
            nullptr,
            "__android_log_buf_write",
            (void *)proxy_transact,
            nullptr,
            nullptr);

    return 0;
}

