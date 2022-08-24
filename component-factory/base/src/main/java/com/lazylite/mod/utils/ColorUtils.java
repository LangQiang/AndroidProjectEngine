package com.lazylite.mod.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.collection.LruCache;
import androidx.palette.graphics.Palette;

import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.SimpleDownloaderListener;


public class ColorUtils {

    public static LruCache<String, Integer> paletteCache = new LruCache<>(30);


    public static int transColorWithAlpha(int color, float alpha) {
        try {
            int a = (int) ((color >>> 24) * alpha);
            int rgb = color & 0x00ffffff;
            return (a << 24) | rgb;
        } catch (Exception ignore) {
            return color;
        }
    }

    public static int parseColor(String colorStr, int defaultColor) {
        try {
            return Color.parseColor(colorStr);
        } catch (Exception e) {
            return defaultColor;
        }
    }

    public static void getPaletteDrawable(String imgUrl,
                                          Float alpha,
                                          Integer gradientColor,
                                          GradientDrawable.Orientation orientation,
                                          ColorUtils.IColorFilter colorFilter,
                                          OnPaletteDrawableFetchCallback onPaletteDrawableFetchCallback)  {

        if (imgUrl == null) {
            if (onPaletteDrawableFetchCallback != null) {
                onPaletteDrawableFetchCallback.onFetch(null);
            }
            return;
        }
        if (imgUrl.endsWith(".gif")) {
            if (onPaletteDrawableFetchCallback != null) {
                onPaletteDrawableFetchCallback.onFetch(null);
            }
            return;
        }
        Integer paletteColor = ColorUtils.paletteCache.get(imgUrl);
        if (paletteColor == null) {
            ImageLoaderWapper.getInstance().load(imgUrl,
                    new SimpleDownloaderListener() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            super.onFailure(throwable);
                            if (onPaletteDrawableFetchCallback != null) {
                                onPaletteDrawableFetchCallback.onFetch(null);
                            }
                        }

                        @Override
                        public void onSuccess(Bitmap result) {
                            if (result == null) {
                                if (onPaletteDrawableFetchCallback != null) {
                                    onPaletteDrawableFetchCallback.onFetch(null);
                                }
                                return;
                            }
                            Palette.from(result).generate(palette -> {
                                if (palette == null) {
                                    if (onPaletteDrawableFetchCallback != null) {
                                        onPaletteDrawableFetchCallback.onFetch(null);
                                    }
                                    return;
                                }
                                Palette.Swatch swatch = palette.getMutedSwatch();
                                if (swatch == null) {
                                    swatch = palette.getVibrantSwatch();
                                    if (swatch == null) {
                                        for (Palette.Swatch paletteSwatch : palette.getSwatches()) {
                                            if (paletteSwatch != null) {
                                                swatch = paletteSwatch;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (swatch != null) {
                                    int rgb = swatch.getRgb();
                                    ColorUtils.paletteCache.put(imgUrl, rgb);
                                    getDrawable(rgb, alpha, gradientColor, orientation, colorFilter, onPaletteDrawableFetchCallback);
                                } else {
                                    if (onPaletteDrawableFetchCallback != null) {
                                        onPaletteDrawableFetchCallback.onFetch(null);
                                    }
                                }
                            });
                        }
            });
        } else {
            getDrawable(paletteColor, alpha, gradientColor, orientation, colorFilter, onPaletteDrawableFetchCallback);
        }
    }

    public static void getPaletteColor(String imgUrl,
                                       Float alpha,
                                       ColorUtils.IColorFilter colorFilter,
                                       OnPaletteColorFetchCallback onPaletteColorFetchCallback) {
        if (imgUrl == null) {
            if (onPaletteColorFetchCallback != null) {
                onPaletteColorFetchCallback.onFetch(null);
            }
            return;
        }
        if (imgUrl.endsWith(".gif")) {
            if (onPaletteColorFetchCallback != null) {
                onPaletteColorFetchCallback.onFetch(null);
            }
            return;
        }
        Integer paletteColor = ColorUtils.paletteCache.get(imgUrl);
        if (paletteColor == null) {
            ImageLoaderWapper.getInstance().load(imgUrl,
                    new SimpleDownloaderListener() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            super.onFailure(throwable);
                            if (onPaletteColorFetchCallback != null) {
                                onPaletteColorFetchCallback.onFetch(null);
                            }
                        }
                        @Override
                        public void onSuccess(Bitmap result) {
                            if (result == null) {
                                if (onPaletteColorFetchCallback != null) {
                                    onPaletteColorFetchCallback.onFetch(null);
                                }
                                return;
                            }
                            Palette.from(result).generate(palette -> {
                                if (palette == null) {
                                    if (onPaletteColorFetchCallback != null) {
                                        onPaletteColorFetchCallback.onFetch(null);
                                    }
                                    return;
                                }
                                Palette.Swatch swatch = palette.getMutedSwatch();
                                if (swatch == null) {
                                    swatch = palette.getVibrantSwatch();
                                    if (swatch == null) {
                                        for (Palette.Swatch paletteSwatch : palette.getSwatches()) {
                                            if (paletteSwatch != null) {
                                                swatch = paletteSwatch;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (swatch != null) {
                                    int rgb = swatch.getRgb();
                                    ColorUtils.paletteCache.put(imgUrl, rgb);
                                    getColor(rgb, alpha, colorFilter, onPaletteColorFetchCallback);
                                } else {
                                    if (onPaletteColorFetchCallback != null) {
                                        onPaletteColorFetchCallback.onFetch(null);
                                    }
                                }
                            });
                        }
                    });
        } else {
            getColor(paletteColor, alpha, colorFilter, onPaletteColorFetchCallback);
        }
    }

    private static void getDrawable(
                                 int rgb,
                                 Float alpha,
                                 Integer gradientColor,
                                 GradientDrawable.Orientation orientation,
                                 ColorUtils.IColorFilter colorFilter,
                                 OnPaletteDrawableFetchCallback onPaletteDrawableFetchCallback) {
        int  curPaletteColor;
        if (colorFilter != null) {
            curPaletteColor = colorFilter.exeFilter(rgb);
        } else {
            curPaletteColor = rgb;
        }

        if (alpha != null) {
            curPaletteColor = ColorUtils.transColorWithAlpha(curPaletteColor, alpha);
        }
        if (gradientColor != null) {
            GradientDrawable.Orientation tempOrientation;
            if (orientation != null) {
                tempOrientation = orientation;
            } else {
                tempOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
            }
            GradientDrawable drawable = new GradientDrawable(tempOrientation, new int[]{curPaletteColor, gradientColor});
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            if (onPaletteDrawableFetchCallback != null) {
                onPaletteDrawableFetchCallback.onFetch(drawable);
            }
        } else {
            if (onPaletteDrawableFetchCallback != null) {
                onPaletteDrawableFetchCallback.onFetch(new ColorDrawable(curPaletteColor));
            }
        }
    }

    private static void getColor(
            int rgb,
            Float alpha,
            ColorUtils.IColorFilter colorFilter,
            OnPaletteColorFetchCallback onPaletteColorFetchCallback) {
        int  curPaletteColor;
        if (colorFilter != null) {
            curPaletteColor = colorFilter.exeFilter(rgb);
        } else {
            curPaletteColor = rgb;
        }

        if (alpha != null) {
            curPaletteColor = ColorUtils.transColorWithAlpha(curPaletteColor, alpha);
        }
        if (onPaletteColorFetchCallback != null) {
            onPaletteColorFetchCallback.onFetch(curPaletteColor);
        }
    }

    public interface OnPaletteDrawableFetchCallback {
        void onFetch(Drawable drawable);
    }

    public interface OnPaletteColorFetchCallback {
        void onFetch(Integer color);
    }


    public interface IColorFilter {
        int exeFilter(int oriColor);
    }

    public static final IColorFilter COMMON_BG_COLOR_FILTER = oriColor -> {
        float[] hsv = new float[3];
        try {
            Color.colorToHSV(oriColor, hsv);
            hsv[1] = hsv[1] * 0.1f + 0.75f;
            hsv[2] = hsv[2] * 0.1f + 0.4f;
            return Color.HSVToColor(hsv);
        }catch (Exception e){
            return oriColor;
        }
    };

    public static void getPaletteTopColor(String imgUrl,
                                       OnPaletteColorFetchCallback onPaletteColorFetchCallback) {
        if (imgUrl == null) {
            if (onPaletteColorFetchCallback != null) {
                onPaletteColorFetchCallback.onFetch(null);
            }
            return;
        }
        if (imgUrl.endsWith(".gif")) {
            if (onPaletteColorFetchCallback != null) {
                onPaletteColorFetchCallback.onFetch(null);
            }
            return;
        }
        Integer paletteColor = ColorUtils.paletteCache.get(imgUrl + "top");
        if (paletteColor == null) {
            ImageLoaderWapper.getInstance().load(imgUrl,
                    new SimpleDownloaderListener() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            super.onFailure(throwable);
                            if (onPaletteColorFetchCallback != null) {
                                onPaletteColorFetchCallback.onFetch(null);
                            }
                        }
                        @Override
                        public void onSuccess(Bitmap result) {
                            if (result == null) {
                                if (onPaletteColorFetchCallback != null) {
                                    onPaletteColorFetchCallback.onFetch(null);
                                }
                                return;
                            }
                            Bitmap bitmap = Bitmap.createBitmap(result, 0, 0, result.getWidth(), ScreenUtility.dip2px(25));
                            Palette.from(bitmap).generate(palette -> {
                                if (palette == null) {
                                    if (onPaletteColorFetchCallback != null) {
                                        onPaletteColorFetchCallback.onFetch(null);
                                    }
                                    return;
                                }
                                Palette.Swatch swatch = palette.getDominantSwatch();
                                if (swatch == null) {
                                    swatch = palette.getVibrantSwatch();
                                    if (swatch == null) {
                                        for (Palette.Swatch paletteSwatch : palette.getSwatches()) {
                                            if (paletteSwatch != null) {
                                                swatch = paletteSwatch;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (swatch != null) {
                                    int rgb = swatch.getRgb();
                                    ColorUtils.paletteCache.put(imgUrl + "top", rgb);
                                    getColor(rgb, null, null, onPaletteColorFetchCallback);
                                } else {
                                    if (onPaletteColorFetchCallback != null) {
                                        onPaletteColorFetchCallback.onFetch(null);
                                    }
                                }
                            });
                        }
                    });
        } else {
            getColor(paletteColor, null, null, onPaletteColorFetchCallback);
        }
    }

    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }
}
