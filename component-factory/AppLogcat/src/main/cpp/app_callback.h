//
// Created by 神强 on 2023/4/24.
//

#ifndef ANDROIDPROJECTENGINE_APP_CALLBACK_H
#define ANDROIDPROJECTENGINE_APP_CALLBACK_H

#include <jni.h>
#include <exception>
#include "constants.h"
#include "Log.h"


class AppCallback {

private:
    AppCallback() = default;
    ~AppCallback() = default;
    AppCallback(AppCallback&)=delete;
    AppCallback& operator=(const AppCallback&)=delete;

public:
    static AppCallback& getInstance() {
        static AppCallback instance;
        return instance;
    }


    void onLog(const char *tag, const char *log) const;

    jobject callback_J;
};


#endif //ANDROIDPROJECTENGINE_APP_CALLBACK_H
