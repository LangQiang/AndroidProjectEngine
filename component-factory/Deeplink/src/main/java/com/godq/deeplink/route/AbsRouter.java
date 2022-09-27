package com.godq.deeplink.route;

import android.net.Uri;

public abstract class AbsRouter {

    protected Uri originUri;

    protected boolean isRunning;

    final void internalParse(Uri uri) {
        originUri = uri;
        parse(uri);
    }

    protected abstract void parse(Uri uri);

    protected abstract boolean route();

    protected boolean hasBackgroundTask() {
        return false;
    }

    protected void runInBackground() {
    }
}
