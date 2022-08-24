package com.lazylite.mod.fragmentmgr;

import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Created by lzf on 5/27/21 5:02 PM
 */
public interface IFragment {
    void setFragmentType(@FragmentType int type);

    String tag();//唯一标识

    void onNewIntent(Bundle args);

    boolean onKeyDown(int keyCode, KeyEvent event);
}
