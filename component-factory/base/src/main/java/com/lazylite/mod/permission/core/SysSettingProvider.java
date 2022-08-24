package com.lazylite.mod.permission.core;

import android.content.Context;
import android.content.Intent;

/**
 * 获取 系统权限设置页的 Intent
 * <p/>
 * Created by lizhaofei on 2018/3/28 15:07
 */
public interface SysSettingProvider {
    Intent get(Context context);
}
