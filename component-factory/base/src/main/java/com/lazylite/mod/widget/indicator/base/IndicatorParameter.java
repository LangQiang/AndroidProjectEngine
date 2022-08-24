package com.lazylite.mod.widget.indicator.base;

import android.graphics.Shader;
import android.view.Gravity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author DongJr
 *
 * @date 2018/5/30.
 */
public class IndicatorParameter {

    public int lRPadding;
    public int tBPadding;
    public int indicatorHeight;
    public int indicatorColorRid; //改为保存颜色在资源中的id值，适应皮肤切换时，动态获取颜色
    public int radius;
    public int gravity;
    public int showMode;
    public boolean useHighColor;

    //着色器
    public Shader shader;

    public int fixedWidth;
    /**
     * 据底部间距
     */
    public int verticalSpace;
    public Interpolator startInterpolator;
    public Interpolator endInterpolator;

    /**
     * 普通模式
     */
    public static final int MODE_NORMAL = 1;
    /**
     * 自适应标题
     */
    public static final int MODE_FIXED_TITLE = 2;
    /**
     * 圆的
     */
    public static final int MODE_CIRCLE = 3;

    @IntDef(value = {MODE_NORMAL, MODE_FIXED_TITLE,MODE_CIRCLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode{
    }

    public IndicatorParameter(Builder builder){
        this.lRPadding = builder.lRPadding;
        this.tBPadding = builder.tBPadding;
        this.indicatorHeight = builder.indicatorHeight;
        this.indicatorColorRid = builder.indicatorColorRid;
        this.radius = builder.radius;
        this.gravity = builder.gravity;
        this.showMode = builder.showMode;
        this.useHighColor = builder.useHighColor;
        this.verticalSpace = builder.verticalSpace;
        this.startInterpolator = builder.startInterpolator == null ? new LinearInterpolator() : builder.startInterpolator;
        this.endInterpolator = builder.endInterpolator == null ? new LinearInterpolator() : builder.endInterpolator;
        this.shader = builder.shader;
        this.fixedWidth = builder.fixedWidth;
    }

    public static final class Builder{
        private Shader shader;
        private int fixedWidth;
        private int lRPadding;
        private int tBPadding;
        private int indicatorHeight;
        private int indicatorColorRid;
        private int radius;
        private int showMode;
        private int verticalSpace;
        private boolean useHighColor;
        private int gravity = Gravity.BOTTOM;
        private Interpolator startInterpolator;
        private Interpolator endInterpolator;

        public Builder withLRPadding(int lRPadding){
            this.lRPadding = lRPadding;
            return this;
        }

        public Builder withIndicatorHeight(int indicatorHeight){
            this.indicatorHeight = indicatorHeight;
            return this;
        }

        public Builder withIndicatorColorRid(int indicatorColorRid){
            this.indicatorColorRid = indicatorColorRid;
            return this;
        }

        public Builder withRadius(int radius){
            this.radius = radius;
            return this;
        }

        /**
         * Gravity.TOP
         * Gravity.BOTTOM
         */
        public Builder withGravity(int gravity){
            this.gravity = gravity;
            return this;
        }

        public Builder withStartInterpolator(Interpolator startInterpolator){
            this.startInterpolator = startInterpolator;
            return this;
        }

        public Builder withEndInterpolator(Interpolator endInterpolator){
            this.endInterpolator = endInterpolator;
            return this;
        }

        public Builder withShowMode(@Mode int mode){
            this.showMode = mode;
            return this;
        }

        public Builder withVerticalSpace(int space){
            this.verticalSpace = space;
            return this;
        }

        public Builder withUseHighColor(boolean useHighColor){
            this.useHighColor = useHighColor;
            return this;
        }

        public Builder withTBPadding(int tBPadding){
            this.tBPadding = tBPadding;
            return this;
        }

        public Builder withShader(Shader shader){
            this.shader = shader;
            return this;
        }
        public Builder withFixedWidth(int width){
            this.fixedWidth = width;
            return this;
        }

        public IndicatorParameter build(){
            return new IndicatorParameter(this);
        }

    }


}
