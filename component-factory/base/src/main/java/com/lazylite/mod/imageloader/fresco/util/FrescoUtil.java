package com.lazylite.mod.imageloader.fresco.util;

import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lazylite.mod.imageloader.fresco.config.FrescoConstant;
import com.lazylite.mod.imageloader.fresco.config.ImageViewSize;

import java.lang.reflect.Field;

/**
 * Created by tiancheng on 2016/11/15
 */

public class FrescoUtil {

    private FrescoUtil(){}

    public static Uri parseUri(String uriStr) {
        try {
            return Uri.parse(uriStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri parseUriFromResId(int resId) {
        return new Uri.Builder()
                .scheme(FrescoConstant.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
    }

    public static Uri parseUriFromFile(String url) {
        return new Uri.Builder()
                .scheme(FrescoConstant.LOCAL_FILE_SCHEME)
                .path(url)
                .build();
    }

    public static Uri parseUriFromAssert(String path) {
        return new Uri.Builder()
                .scheme(FrescoConstant.LOCAL_ASSET_SCHEME)
                .path(path)
                .build();
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param t an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkViewNotNull(T t) {
        if (t == null) {
            throw new NullPointerException("View is null!");
        }
        return t;
    }

    public static void checkUriIsLegal(Uri uri) {
        String scheme = getSchemeOrNull(uri);

        if (TextUtils.isEmpty(scheme)) {
            throw new IllegalArgumentException("Uri is illegal,Scheme is null or empty");
        }

        switch (scheme) {
            case FrescoConstant.HTTP_SCHEME:
            case FrescoConstant.HTTPS_SCHEME:
            case FrescoConstant.LOCAL_FILE_SCHEME:
            case FrescoConstant.LOCAL_ASSET_SCHEME:
            case FrescoConstant.LOCAL_CONTENT_SCHEME:
            case FrescoConstant.LOCAL_RESOURCE_SCHEME:
            case FrescoConstant.DATA_SCHEME:
                break;
            default:
                throw new IllegalArgumentException("Uri is illegal,Scheme not supported");
        }
        validate(uri);
    }



    public static ImageViewSize getImageViewSize(ImageView imageView) {

        ImageViewSize imageSize = new ImageViewSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        int width = imageView.getWidth();
        if (lp != null) {
            if (width <= 0) {
                width = lp.width;
            }
        }
        if (width <= 0) {
            width = imageView.getMaxWidth();
        }
        int height = imageView.getHeight();
        if (lp != null) {
            if (height <= 0) {
                height = lp.height;
            }
        }
        if (height <= 0) {
            height = imageView.getMaxHeight();
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }


    /**
     * Performs validation.
     * ???facebook??????????????????????????????????????????????????????
     * @param uri
     */
    private static void validate(Uri uri) {
        // For local resource we require caller to specify statically generated resource id as a path.
        if (isLocalResourceUri(uri)) {
            if (!uri.isAbsolute()) {
                throw new RuntimeException("Resource Uri path must be absolute.");
            }
            if (uri.getPath().isEmpty()) {
                throw new RuntimeException("Resource Uri must not be empty");
            }
            try {
                Integer.parseInt(uri.getPath().substring(1));
            } catch (NumberFormatException ignored) {
                throw new RuntimeException("Resource Uri path must be a resource id.", ignored.getCause());
            }
        }
        // For local asset we require caller to specify absolute path of an asset, which will be
        // resolved by AssetManager relative to configured asset folder of an app.
        if (isLocalAssetUri(uri) && !uri.isAbsolute()) {
            throw new RuntimeException("Asset Uri path must be absolute.");
        }
    }

    private static String getSchemeOrNull(Uri uri) {
        return uri == null ? null : uri.getScheme();
    }

    public static boolean isNetworkUri(Uri uri) {
        return FrescoConstant.HTTPS_SCHEME.equals(getSchemeOrNull(uri)) || FrescoConstant.HTTP_SCHEME.equals(getSchemeOrNull(uri));
    }

    public static boolean isLocalFileUri(Uri uri) {
        return FrescoConstant.LOCAL_FILE_SCHEME.equals(getSchemeOrNull(uri));
    }

    public static boolean isLocalContentUri(Uri uri) {
        return FrescoConstant.LOCAL_CONTENT_SCHEME.equals(getSchemeOrNull(uri));
    }

    public static boolean isLocalAssetUri(Uri uri) {
        return FrescoConstant.LOCAL_ASSET_SCHEME.equals(getSchemeOrNull(uri));
    }

    public static boolean isLocalResourceUri(Uri uri) {
        return FrescoConstant.LOCAL_RESOURCE_SCHEME.equals(getSchemeOrNull(uri));
    }

    public static boolean isDataUri(Uri uri) {
        return FrescoConstant.DATA_SCHEME.equals(getSchemeOrNull(uri));
    }
}
