package com.lazylite.mod.widget.bottomTabLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;

import com.example.basemodule.R;


/**
 * tab 里面的子控件
 * Created by tc :)
 */
public class BottomTabItemView extends View {

    // tab的宽高
    private int mWidth, mHeight;

    private Paint mNormalIconPaint, mSelectIconPaint;
    private Paint mNormalTextPaint, mSelectTextPaint;
    private Paint mRedDotPaint;
    private Paint mMessageBkgPaint;
    private Paint mMessageTextPaint;

    // icon图片区域
    private Rect mIconDrawRect;
    // icon计算之后的可用区域
    private Rect mIconRealDrawRect;
    // 文字区域
    private Rect mTextRect;
    // 红点区域
    private RectF mRedDotRect;
    // 消息区域
    private RectF mMessageRect;

    // 消息开个新画布，处理完再画到老画布上
    private Canvas mMessageCanvas;

    // 是否显示红点
    private boolean isShowRedDot;
    // 是否显示文字
    private boolean  isShowText;
    // 红点大小
    private int mRedDotSize;

    // icon大小
    private int mIconSize;
    // 默认icon
    private Bitmap mNormalIcon;
    // 选中icon
    private Bitmap mSelectIcon;

    private int mNormalIconRes;
    private int mSelectIconRes;

    // 文字内容标题
    private String mText;

    // 是否选中状态
    private boolean isSelected;

    // 未读消息数量
    private int mUnReadMessage;

    // 文本消息
    private String mContentMessage;

    // 消息数量的文字大小
    private int mMessageTextSize;

    public BottomTabItemView(Context context) {
        this(context, null);
    }

    public BottomTabItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public String getTabName() {
        return mText;
    }

    public void updateIcon(int normalRes, int selectRes) {
        if (this.mNormalIconRes == normalRes && this.mSelectIconRes == selectRes) {
            return;
        }
        this.mNormalIconRes = normalRes;
        this.mSelectIconRes = selectRes;
        setIconBitmap();
        invalidate();
    }

    private void init() {
        mNormalIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 红点还有别的颜色的吗？
        mRedDotPaint.setColor(Color.RED);
        mMessageBkgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMessageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mMessageRect = new RectF();
        mMessageCanvas = new Canvas();
    }

    public void initialize(String title, @DrawableRes int normalRes, @DrawableRes int selectRes) {
        this.mText = title;
        this.mNormalIconRes = normalRes;
        this.mSelectIconRes = selectRes;
        setIconBitmap();
        setTextPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        calculate();
    }

    /**
     * 文字和icon，红点等绘制区域计算
     */
    private void calculate() {
        // 获取文字的绘制区域
        mTextRect = new Rect();

        // 获取icon的绘制区域，也就是输出区域
        mIconDrawRect = new Rect(0, 0, mNormalIcon.getWidth(), mNormalIcon.getHeight());

        // 分别计算比较
        // 比较可用范围和手动xml设置icon大小
        // 比较可用范围和图片原始大小
        // 得到真实的icon绘制区域，也就是输入区域，这部分着实费脑
        if (mIconSize > 0) {
            mIconRealDrawRect = calculateWithSize(mIconSize);
        } else {
            mIconRealDrawRect = calculateWithIcon(
                    new int[]{mNormalIcon.getWidth(), mNormalIcon.getHeight()});
        }

        handlerTextIconSeat();

        // 红点区域
        mRedDotRect = new RectF(mIconRealDrawRect.right,
                mIconRealDrawRect.top,
                mIconRealDrawRect.right + mRedDotSize,
                mIconRealDrawRect.top + mRedDotSize);
    }

    /**
     * 做图文竖排的居中处理
     */
    private void handlerTextIconSeat() {
        int availableWidth, availableHeight;

//        // 明星主题不用管文字区域
//        if (StarThemeUtil.isStarTheme()) {
//            availableWidth = mIconRealDrawRect.width();
//            availableHeight = mIconRealDrawRect.height();
//            int iconLeft = (mWidth - availableWidth) / 2;
//            int iconRight = iconLeft + availableWidth;
//            int iconTop = (mHeight - availableHeight) / 2;
//            int iconBottom = iconTop + mIconRealDrawRect.height();
//            mIconRealDrawRect.set(iconLeft, iconTop, iconRight, iconBottom);
//            mTextRect.set(0, 0, 0, 0);
//            return;
//        }

        mNormalTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);

        // 图文内容占用宽取icon和文字最大者
        availableWidth = Math.max(mTextRect.width(), mIconRealDrawRect.width());
        // 图文内容占用高取icon和文字之和
        availableHeight = mTextRect.height() + mIconRealDrawRect.height();
        // 如果以icon宽为宽，需要微调字的绘制区域使其居中
        if (availableWidth == mIconRealDrawRect.width()) {
            int iconLeft = (mWidth - availableWidth) / 2;
            int iconRight = iconLeft + availableWidth;

            int iconTop = (mHeight - availableHeight) / 2 - dip2px(2);
            int iconBottom = iconTop + mIconRealDrawRect.height();
            mIconRealDrawRect.set(iconLeft, iconTop, iconRight, iconBottom);

            int textLeft = iconLeft + (availableWidth - mTextRect.width()) / 2;
            int textRight = textLeft + mTextRect.width();
            // 绘制文字的时候，是以文字的基线为top的，得下移文字的高度
            int textTop = iconBottom + mTextRect.height() + dip2px(1);
            int textBottom = textTop + mTextRect.height() /*+ dip2px(2)*/;
            mTextRect.set(textLeft, textTop, textRight, textBottom);
        } else {
            // 如果以字宽为宽，需要微调icon的绘制区域使其居中
            int iconLeft = (mWidth - availableWidth) / 2 + (availableWidth - mIconRealDrawRect.width()) / 2;
            int iconRight = iconLeft + mIconRealDrawRect.width();

            int iconTop = (mHeight - availableHeight) / 2 - dip2px(2);
            int iconBottom = iconTop + mIconRealDrawRect.height();
            mIconRealDrawRect.set(iconLeft, iconTop, iconRight, iconBottom);

            int textLeft = (mWidth - availableWidth) / 2;
            int textRight = textLeft + mTextRect.width();
            // 绘制文字的时候，是以文字的基线为top的，得下移文字的高度
            int textTop = iconBottom + mTextRect.height() + dip2px(1);
            int textBottom = textTop + mTextRect.height()/* + dip2px(2)*/;
            mTextRect.set(textLeft, textTop, textRight, textBottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected) {

            canvas.drawText(mText, mTextRect.left, mTextRect.top, mSelectTextPaint);
            canvas.drawBitmap(mSelectIcon, mIconDrawRect, mIconRealDrawRect, mSelectIconPaint);
        } else {

            canvas.drawText(mText, mTextRect.left, mTextRect.top, mNormalTextPaint);

            canvas.drawBitmap(mNormalIcon, mIconDrawRect, mIconRealDrawRect, mNormalIconPaint);
        }
        drawMessageOrRedDot(canvas);
    }

    /**
     * 画消息或者红点
     *
     * @param canvas
     */
    private void drawMessageOrRedDot(Canvas canvas) {
        if (mUnReadMessage > 0) {
            String number = mUnReadMessage > 99 ? "99+" : String.valueOf(mUnReadMessage);
            // 数字消息转成文本消息
            drawStr(canvas, number);
        } else if (mUnReadMessage == 0) {
            // 消息为空，不处理
        } else {
            if (isShowRedDot) {
                canvas.drawOval(mRedDotRect, mRedDotPaint);
            } else if(isShowText){
                // 画纯消息文字
                drawStr(canvas, mContentMessage);
            }
            // 。。。。。或许还有其他new标什么的？？？
        }
    }

    private void drawStr(Canvas canvas, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Bitmap bkg;// 背景比文字大一点就行
        int width;
        int height = dip2px(px2dip(mMessageTextSize) + 4.0f);
        if (str.length() == 1) {
            width = height;
            bkg = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        } else if (str.length() == 2) {
            width = dip2px(px2dip(mMessageTextSize) + 14);
            bkg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            width = dip2px(px2dip(mMessageTextSize) + 10.0f);
            bkg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        mMessageCanvas.setBitmap(bkg);
        mMessageRect.set(0, 0, width, height);
        mMessageCanvas.drawRoundRect(mMessageRect, 50, 50, mMessageBkgPaint);
        Paint.FontMetrics fontMetrics = mMessageTextPaint.getFontMetrics();
        float textTop = fontMetrics.top;//基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//基线到字体下边框的距离
        //基线中间点的y点，x点就是区域中间就行
        int baseLineY = (int) (mMessageRect.centerY() - textTop / 2 - bottom / 2);
        // 以基线为准
        mMessageTextPaint.setTextAlign(Paint.Align.CENTER);
        mMessageTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mMessageCanvas.drawText(str, mMessageRect.centerX(), baseLineY, mMessageTextPaint);
        float left = mIconRealDrawRect.right - 35;
        float top = mIconRealDrawRect.top;
        canvas.drawBitmap(bkg, left, top, null);
        bkg.recycle();
    }

    /**
     * 设置了期望值，就在可用范围内给你期望值，可能会因为icon不匹配发生变形，但没办法，是你要求这么大的
     *
     * @param size 期望的size
     * @return icon实际绘制区域
     */
    private Rect calculateWithSize(int size) {
        Rect rect = new Rect();
        int iconAvailableWidth = mWidth;
        int iconAvailableHeight = mHeight - mTextRect.height();
        // 如果手动设置的值大于了可用范围，限定区域为可用宽高最小值为宽高的正方形区域
        if (size > iconAvailableWidth && size > iconAvailableHeight) {
            int min = Math.min(iconAvailableWidth, iconAvailableHeight);
            rect.set(0, 0, min, min);
            // 如果手动设置的值大于可用高小于可用宽，以可用高为边长限定
        } else if (size > iconAvailableHeight && size < iconAvailableWidth) {
            rect.set(0, 0, iconAvailableHeight, iconAvailableHeight);
            // 如果手动设置的值大于可用宽小于可用高，以可用宽为边长限定
        } else if (size > iconAvailableWidth && size < iconAvailableHeight) {
            rect.set(0, 0, iconAvailableWidth, iconAvailableWidth);
        } else {// 如果手动设置的值在范围内
            rect.set(0, 0, size, size);
        }
        return rect;
    }

    /**
     * 没设置期望值，按icon的宽高比进行自动缩放
     *
     * @param wh icon的宽高
     * @return icon实际绘制区域
     */
    private Rect calculateWithIcon(int[] wh) {
        int w = wh[0];
        int h = wh[1];
        Rect rect = new Rect();
        int iconAvailableWidth = mWidth;
        int iconAvailableHeight = mHeight - mTextRect.height();
        // 如果icon宽高都大于了可用范围
        if (w > iconAvailableWidth && h > iconAvailableHeight) {
            float ratio = Math.max(w * 1.0f / iconAvailableWidth, h * 1.0f / iconAvailableHeight);
            rect.set(0, 0, (int) (w / ratio + 0.5f), (int) (h / ratio + 0.5f));
            // 如果宽小于可用宽，高大于可用高 || 高小于可用高，宽大于可用宽
        } else if ((h > iconAvailableHeight && w < iconAvailableWidth)
                || (w > iconAvailableWidth && h < iconAvailableHeight)) {
            float ratio = Math.min(w * 1.0f / iconAvailableWidth, h * 1.0f / iconAvailableHeight);
            rect.set(0, 0, (int) (w * ratio + 0.5f), (int) (h * ratio + 0.5f));
        } else {
            rect.set(0, 0, w, h);
        }
        return rect;
    }

    /**
     * 文字大小
     *
     * @param size
     * @return
     */
    public BottomTabItemView setTextSize(int size) {
        this.mNormalTextPaint.setTextSize(size);
        this.mSelectTextPaint.setTextSize(size);
        return this;
    }

    /**
     * 标题默认文字颜色
     *
     * @param color
     * @return
     */
    public BottomTabItemView setNormalTextColor(int color) {
        this.mNormalTextPaint.setColor(color);
        return this;
    }

    /**
     * 标题选中文字颜色
     *
     * @param color
     * @return
     */
    public BottomTabItemView setSelectTextColor(int color) {
        this.mSelectTextPaint.setColor(color);
        return this;
    }

    /**
     * 红点size
     *
     * @param size
     * @return
     */
    public BottomTabItemView setRedDotSize(int size) {
        this.mRedDotSize = size;
        return this;
    }

    /**
     * 显示/隐藏红点
     *
     * @param show
     * @return
     */
    public BottomTabItemView showRedDot(boolean show) {
        this.isShowRedDot = show;
        if (show) {
            mUnReadMessage = -1;
        }
        invalidate();
        return this;
    }


    /**
     * 显示/隐藏背景消息
     *
     * @param show
     * @return
     */
    public BottomTabItemView showRedText(boolean show) {
        this.isShowText = show;
        if (show) {
            mUnReadMessage = -1;
        }
        invalidate();
        return this;
    }

    /**
     * 设置是否选中的标记
     *
     * @param selected
     * @return
     */
    public BottomTabItemView selected(boolean selected) {
        this.isSelected = selected;
        invalidate();
        return this;
    }

    /**
     * icon大小
     *
     * @param size
     * @return
     */
    public BottomTabItemView setIconSize(int size) {
        this.mIconSize = size;
        return this;
    }

    /**
     * 设置数字消息数量
     *
     * @param message
     * @return
     */
    public BottomTabItemView setMessage(int message) {
        this.mUnReadMessage = message;
        return this;
    }

    /**
     * 设置文本消息内容
     *
     * @param message
     * @return
     */
    public BottomTabItemView setMessage(String message) {
        this.mContentMessage = message;
        mUnReadMessage = -1;
        isShowText = true;
        return this;
    }

    /**
     * 设置消息背景颜色
     *
     * @param color
     * @return
     */
    public BottomTabItemView setMessageBkgColor(int color) {
        mMessageBkgPaint.setColor(color);
        return this;
    }

    /**
     * 设置消息文字大小
     *
     * @param size
     * @return
     */
    public BottomTabItemView setMessageTextSize(int size) {
        this.mMessageTextSize = size;
        mMessageTextPaint.setTextSize(size);
        return this;
    }

    /**
     * 设置消息文字颜色
     *
     * @param color
     * @return
     */
    public BottomTabItemView setMessageTextColor(int color) {
        mMessageTextPaint.setColor(color);
        return this;
    }

    /**
     * 是否有红点
     *
     * @return
     */
    public boolean hasRedDot() {
        return isShowRedDot;
    }


    /**
     * 是否有红背景消息
     *
     * @return
     */
    public boolean hasRedText() {
        return isShowText;
    }

    /**
     * 获取消息数量
     *
     * @return
     */
    public int getUnReadMessage() {
        return mUnReadMessage;
    }

    public void resetSkin() {
        // 重新获取图片
        setIconBitmap();
        // 重设画笔和setColorFilter
        setTextPaint();
        // 重新测量一下位置，明星主题不要文字重新定位绘制范围
        calculate();
        // OPPO VIVO不调用就不行，小米不调用就可以，到底是哪家改了代码
        invalidate();
    }

    private void setIconBitmap() {
        Drawable normalDrawable = getResources().getDrawable(mNormalIconRes);
        Drawable selectDrawable = getResources().getDrawable(mSelectIconRes);
        this.mNormalIcon = ((BitmapDrawable) normalDrawable).getBitmap();
        this.mSelectIcon = ((BitmapDrawable) selectDrawable).getBitmap();
    }

    private void setTextPaint() {
        // 白底或者明星主题等外部皮肤包  icon不用染色逻辑
//        mSelectIconPaint.setColorFilter(SkinHighColorManager.getInstance().getColorFilter());


        // 白底非明星主题 ，选中文字不用染色逻辑
        mNormalTextPaint.setColor(getResources().getColor(R.color.LRLiteBase_cl_white_alpha_50));
//        mSelectTextPaint.setColorFilter(SkinHighColorManager.getInstance().getColorFilter());
    }

    private int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
