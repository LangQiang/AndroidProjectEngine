package com.godq.deeplink.intercept;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.godq.deeplink.DeeplinkResult;

public interface IIntercept {

    @NonNull
    default Uri beforeRoute(@NonNull Uri uri) {return uri;}

    @NonNull
    default DeeplinkResult afterRoute(@NonNull DeeplinkResult result, @NonNull Uri uri) {return result;}
}
