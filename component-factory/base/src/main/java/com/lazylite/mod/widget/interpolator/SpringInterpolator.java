package com.lazylite.mod.widget.interpolator;

import android.view.animation.Interpolator;

public class SpringInterpolator implements Interpolator {

    private float factor = 0.8f;

    private static final double PI = Math.PI;

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * PI) / factor) + 1);
    }
}
