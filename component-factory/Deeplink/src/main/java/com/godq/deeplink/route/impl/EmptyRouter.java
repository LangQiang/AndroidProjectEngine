package com.godq.deeplink.route.impl;

import android.net.Uri;

import com.godq.deeplink.route.AbsRouter;

public class EmptyRouter extends AbsRouter {

    public static final EmptyRouter EMPTY_ROUTER = new EmptyRouter();

    @Override
    protected void parse(Uri uri) {

    }

    @Override
    public boolean route() {
        return false;
    }

}
