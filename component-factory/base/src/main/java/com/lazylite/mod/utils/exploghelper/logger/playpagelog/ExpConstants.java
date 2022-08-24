package com.lazylite.mod.utils.exploghelper.logger.playpagelog;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        ExpConstants.KEY_PLAY_PAGE_BIG_IMG_AD,
        ExpConstants.KEY_PLAY_PAGE_LIST_AD,
        ExpConstants.KEY_PLAY_PAGE_GAME,
        ExpConstants.KEY_PLAY_PAGE_RECOMMEND
})


@Retention(RetentionPolicy.SOURCE)
public @interface ExpConstants {


    int KEY_PLAY_PAGE_BIG_IMG_AD = 0;
    int KEY_PLAY_PAGE_LIST_AD = 1;
    int KEY_PLAY_PAGE_GAME = 2;
    int KEY_PLAY_PAGE_RECOMMEND = 3;
}
