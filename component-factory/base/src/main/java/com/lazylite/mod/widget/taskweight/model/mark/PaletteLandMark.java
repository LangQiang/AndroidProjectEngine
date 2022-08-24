package com.lazylite.mod.widget.taskweight.model.mark;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.lazylite.mod.App;
import com.lazylite.mod.utils.ScreenUtility;

public class PaletteLandMark extends AbstractLankMark {

    private RectF rectF = new RectF();
    private int defaultPaletteColor;
    private int overridePaletteColor;
    private int strokeWidth;

    public PaletteLandMark(int landmarkLength, int defaultPaletteColor, int overridePaletteColor) {
        super(landmarkLength);
        this.defaultPaletteColor = defaultPaletteColor;
        this.overridePaletteColor = overridePaletteColor;
        strokeWidth = ScreenUtility.dip2px(App.getInstance(), 2);
    }

    public void setPaletteColor(int defaultPaletteColor, int overridePaletteColor) {
        this.defaultPaletteColor = defaultPaletteColor;
        this.overridePaletteColor = overridePaletteColor;
    }

    @Override
    public void draw(Resources resources, Canvas canvas, Rect rect, int landMarkState, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        rectF.set(rect);
        canvas.drawOval(rectF, paint);
        if (landMarkState == LandMarkState.STATE_OVERRIDE) {
            paint.setColor(overridePaletteColor);
        } else {
            paint.setColor(defaultPaletteColor);
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(
                (rect.left + rect.right) / 2f,
                (rect.top + rect.bottom) / 2f,
                Math.min(rect.right - rect.left, rect.bottom - rect.top) / 2f - strokeWidth / 2f - strokeWidth,
                paint
        );
    }

}
