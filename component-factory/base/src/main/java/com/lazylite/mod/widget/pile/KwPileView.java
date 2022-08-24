package com.lazylite.mod.widget.pile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.basemodule.R;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.IDownloadImageListener;
import com.lazylite.mod.utils.ScreenUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouchong on 2019/9/24.
 *  半月形头像堆叠，参考：https://blog.csdn.net/xiangzhihong8/article/details/78282488
 */
public class KwPileView extends ViewGroup {
    public static final int VISIBLE_COUNT = 3;//默认显示个数

    private Context context;

    protected int verticalSpace;//垂直间隙
    protected int pileWidth = 0;//重叠宽度
    // 头像宽高，默认30dp
    private int imageSize;
    private int visibleCount;

    private int requestMarkCount = 0;

    private List<String> defaultImg = new ArrayList<>();

    private boolean isShowEmptyDefaultImg;

    private final List<ImageView> imageViews = new ArrayList<>();

    public KwPileView(Context context) {
        this(context, null, 0);
    }

    public KwPileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KwPileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        defaultImg.add("default");
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KwPileView);
        verticalSpace = ta.getDimensionPixelSize(R.styleable.KwPileView_KwPileView_verticalSpace, ScreenUtility.dip2px(4));
        pileWidth = ta.getDimensionPixelSize(R.styleable.KwPileView_KwPileView_pileWidth, ScreenUtility.dip2px(10));
        imageSize = ta.getDimensionPixelSize(R.styleable.KwPileView_KwPileView_imageSize, ScreenUtility.dip2px(30));
        visibleCount = ta.getInt(R.styleable.KwPileView_KwPileView_visibleCount, VISIBLE_COUNT);
        ta.recycle();

        removeAllViews();
        for (int i = 0; i < VISIBLE_COUNT; i++) {
            ImageView image = new ImageView(context);
            imageViews.add(image);
            addView(image, new MarginLayoutParams(imageSize, imageSize));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //AT_MOST
        int width = 0;
        int height = 0;
        int rawWidth = 0;//当前行总宽度
        int rawHeight = 0;// 当前行高

        int rowIndex = 0;//当前行位置
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                if(i == count - 1){
                    //最后一个child
                    height += rawHeight;
                    width = Math.max(width, rawWidth);
                }
                continue;
            }

            //调用measureChildWithMargins 而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth()  + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if(rawWidth + childWidth  - (rowIndex > 0 ? pileWidth : 0)> widthSpecSize - getPaddingLeft() - getPaddingRight()){
                //换行
                width = Math.max(width, rawWidth);
                rawWidth = childWidth;
                height += rawHeight + verticalSpace;
                rawHeight = childHeight;
                rowIndex = 0;
            } else {
                rawWidth += childWidth;
                if(rowIndex > 0){
                    rawWidth -= pileWidth;
                }
                rawHeight = Math.max(rawHeight, childHeight);
            }

            if(i == count - 1){
                width = Math.max(rawWidth, width);
                height += rawHeight;
            }

            rowIndex++;
        }

        setMeasuredDimension(
                widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight(),
                heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = r - l;
        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rowMaxHeight = 0;
        int rowIndex = 0;//当前行位置
        View childView;
        int count = getChildCount();
        for (int w = 0; w < count; w++) {
            childView = getChildAt(w);
            if (childView.getVisibility() == GONE){
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            // 如果加上当前子View的宽度后超过了ViewGroup的宽度，就换行
            int occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin;
            if (leftOffset + occupyWidth + getPaddingRight() > viewWidth) {
                leftOffset = getPaddingLeft();  // 回到最左边
                topOffset += rowMaxHeight + verticalSpace;  // 换行
                rowMaxHeight = 0;

                rowIndex = 0;
            }

            int left = leftOffset + lp.leftMargin;
            int top = topOffset + lp.topMargin;
            int right = leftOffset + lp.leftMargin + childView.getMeasuredWidth();
            int bottom = topOffset + lp.topMargin + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
            // 横向偏移
            leftOffset += occupyWidth;
            // 试图更新本行最高View的高度
            int occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if (rowIndex != count - 1) {
                leftOffset -= pileWidth;
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight);
            rowIndex++;
        }
    }

    public void setWithDefaultImg(boolean isWithDefaultImg) {
        isShowEmptyDefaultImg = isWithDefaultImg;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public void setImageList(List<String> imageList) {
        setImageList(imageList, visibleCount);
    }

    public void setImageList(List<String> imageList, int visibleCount) {
        increment();
        if (imageList == null || imageList.isEmpty()) {
            if (!isShowEmptyDefaultImg) {
                for (ImageView imageView : imageViews) {
                    imageView.setVisibility(GONE);
                }
                setVisibility(GONE);
                return;
            } else {
                imageList = defaultImg;
            }
        }

        setVisibility(VISIBLE);
        List<String> visibleList;
        if (imageList.size() > visibleCount) {
            visibleList = imageList.subList(0, visibleCount);
        } else {
            visibleList = imageList;
        }


        for (int i = 0; i < visibleCount; i++) {
            ImageView imageView = imageViews.get(i);
            if (i < visibleList.size()) {
                loadImage(i != 0, visibleList.get(i), imageView);
                imageView.setVisibility(VISIBLE);
            } else {
                imageView.setVisibility(GONE);
            }
        }
    }

    private void loadImage(final boolean isHalfMoon, String url, final ImageView imageView) {
        imageView.setTag(url + requestMarkCount);
        ImageLoaderWapper.getInstance().load(url, imageSize, imageSize, new IDownloadImageListener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap result) {
                String lastMark = url + requestMarkCount;
                Object tag = imageView.getTag();
                if (!(tag instanceof String)) {
                    return;
                }
                if (!lastMark.equals((String) tag)) {
                    return;
                }
                imageView.setImageDrawable(toRounded(result, isHalfMoon));
            }

            @Override
            public void onFailure(Throwable throwable) {
                String lastMark = url + requestMarkCount;
                Object tag = imageView.getTag();
                if (!(tag instanceof String)) {
                    return;
                }
                if (!lastMark.equals((String) tag)) {
                    return;
                }
                Bitmap def = BitmapFactory.decodeResource(getResources(), R.drawable.default_people);
                imageView.setImageDrawable(toRounded(def, isHalfMoon));
            }

            @Override
            public void onProgress(float progress) {
//                Bitmap def = BitmapFactory.decodeResource(getResources(), R.drawable.default_people);
//                imageView.setImageDrawable(toRounded(def, isHalfMoon));
            }
        });
    }

    private RoundedDrawable toRounded(Bitmap bitmap, boolean isHalfMoon) {
        RoundedDrawable rounded;
        if (isHalfMoon) {
            rounded = new HalfMoonDrawable(bitmap);
        } else {
            rounded = new RoundedDrawable(bitmap);
        }
        rounded.setCircle();
        return rounded;
    }

    private void increment() {
        if (requestMarkCount == Integer.MAX_VALUE) {
            requestMarkCount = 0;
        } else {
            requestMarkCount = requestMarkCount + 1;
        }
    }
}
