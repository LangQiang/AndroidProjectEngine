package com.lazylite.mod.widget.taskweight.model.mark;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public interface ILandMark {
    void draw(Resources resources, Canvas canvas, Rect rect, @LandMarkState int landMarkState, Paint paint);
    int getLandMarkLength();
}
