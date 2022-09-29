package com.godq.deeplink.intercept;

import android.net.Uri;

import com.godq.deeplink.DeeplinkResult;

public interface IIntercept {

    default Uri beforeRoute(Uri uri) {return uri;}

    default DeeplinkResult afterRoute(DeeplinkResult result, Uri uri) {return result;}
}
