package com.lazylite.mod.widget.taskweight.model.mark;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        LandMarkState.STATE_OVERRIDE, LandMarkState.STATE_DEFAULT
})

@Retention(RetentionPolicy.SOURCE)
public @interface LandMarkState {
    int STATE_DEFAULT = 0;      //默认状态
    int STATE_OVERRIDE = 1;     //被进度覆盖后状态
}
