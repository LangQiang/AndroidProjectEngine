package com.godq.test.skin;

import android.app.Application;
import android.view.View;

import com.godq.test.R;
import com.godq.xskin.SkinConstants;
import com.godq.xskin.SkinManager;
import com.godq.xskin.entity.SkinViewWrapper;
import com.lazylite.mod.App;

/**
 * @author GodQ
 * @date 2023/6/7 4:55 PM
 */
public class SkinJAVATest {


    private SkinManager.SkinChangedListener cb = () -> {

    };

    void text(Application con) {
        SkinManager.INSTANCE.init(con);
        SkinManager.INSTANCE.registerSkinChangedListener(cb);
        SkinManager.INSTANCE.setSkinAttrsWhenAddViewByCode(
                new SkinViewWrapper.Builder(new View(App.getApplication()))
                        .addAttr(SkinConstants.SupportAttributeName.TEXT_COLOR, R.color.rgb4D000000)
                        .build());
    }
}
