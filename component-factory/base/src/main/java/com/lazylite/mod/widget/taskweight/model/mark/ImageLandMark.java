package com.lazylite.mod.widget.taskweight.model.mark;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class ImageLandMark extends AbstractLankMark {

    private @DrawableRes int drawableResDefault;
    private @DrawableRes int drawableResOverride;

    public ImageLandMark(int landmarkLength, @DrawableRes int drawableResDefault, @DrawableRes int drawableResOverride) {
        super(landmarkLength);
        this.drawableResDefault = drawableResDefault;
        this.drawableResOverride = drawableResOverride;
    }

    @Override
    public void draw(@NonNull Resources resources, @NonNull Canvas canvas, Rect rect, @LandMarkState int landMarkState, Paint paint) {
        Drawable drawable;
        switch (landMarkState) {
            case LandMarkState.STATE_DEFAULT:
                drawable = resources.getDrawable(drawableResDefault);
                break;
            case LandMarkState.STATE_OVERRIDE:
                drawable = resources.getDrawable(drawableResOverride);
                break;
            default:
                drawable = resources.getDrawable(drawableResDefault);
                break;
        }
        drawable.setBounds(rect);
        drawable.draw(canvas);
    }

}
