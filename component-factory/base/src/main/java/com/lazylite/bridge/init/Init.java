package com.lazylite.bridge.init;

import android.content.Context;
import android.util.Pair;

public abstract class Init {

    public Init() {}

    public abstract void init(Context context);

    public abstract void initAfterAgreeProtocol(Context context);

    public abstract Pair<String, Object> getServicePair();
}
