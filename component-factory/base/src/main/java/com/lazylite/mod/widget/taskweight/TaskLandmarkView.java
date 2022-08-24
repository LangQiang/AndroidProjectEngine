package com.lazylite.mod.widget.taskweight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lazylite.mod.widget.taskweight.model.ITask;
import com.lazylite.mod.widget.taskweight.model.mark.LandMarkState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 进度条：默认垂直居中显示，宽度为父空间宽度
 *        progressMarginLeft、progressMarginRight控制绘制进度条横向位置和宽度
 *        verticalOffset 进度条的垂直偏移量
 *
 *        progressHeight进度条高度
 *        landMarkWH里程碑点宽高，W=H
 *        landMarkOffset进度条边缘里程碑点左右偏移量 注：默认左边偏移量参与进度计算，右边偏移量不参与进度计算
 *        progress进度
 *
 *        binding?.taskView?.setProgressHeight(ScreenUtility.dip2px(1f))
 *          binding?.taskView?.setLandMarkWH(ScreenUtility.dip2px(24f))
 *          binding?.taskView?.setProgressMarginLeft(ScreenUtility.dip2px(42f))
 *          binding?.taskView?.setProgressMarginRight(ScreenUtility.dip2px(42f))
 *          binding?.taskView?.setVerticalOffset(ScreenUtility.dip2px(-2.25f))
 *
 *          vm.landMarkDataCallback = {
 *              binding?.taskView?.setAdapter(DetailLandViewAdapter(it))
 *              binding?.taskView?.setProgress(progress)
 *          }
 *
 * @author lq
 *
 * */
public class TaskLandmarkView <T extends ITask>  extends ViewGroup {
    private int mDefaultWidth;
    private int mDefaultHeight;
    private int progress;

    private TaskProgressView<T> taskProgressView;

    private Adapter adapter;

    public TaskLandmarkView(Context context) {
        this(context, null);
    }

    public TaskLandmarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskLandmarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDefaultWidth = wm.getDefaultDisplay().getWidth();
        mDefaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                getResources().getDisplayMetrics());

        taskProgressView = new TaskProgressView<>(context);
        taskProgressView.setOnLandMarkStateChangedListener(new TaskProgressView.OnLandMarkStateChangedListener() {
            @Override
            public void onChangedRange(int oldArrivePos, int newArrivePos) {
                if (newArrivePos > oldArrivePos) {
                    for (int i = oldArrivePos + 1; i < newArrivePos + 1; i++) {
                        adapter.getView(getContext(), getChildAt(i + 1), i, LandMarkState.STATE_OVERRIDE);
                    }
                } else {
                    for (int i = newArrivePos + 1; i < oldArrivePos + 1; i++) {
                        adapter.getView(getContext(), getChildAt(i + 1), i, LandMarkState.STATE_DEFAULT);
                    }
                }
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = handlerMeasure(widthMeasureSpec, mDefaultWidth);
        int height = handlerMeasure(heightMeasureSpec, mDefaultHeight);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        // 可用宽高
        int availableWidth = width - paddingLeft - paddingRight;
        int availableHeight = height - paddingTop - paddingBottom;
        int count = getChildCount();

        //测量进度条 一定是第一个子view
        if (count > 0) {
            View progressView = getChildAt(0);
            LayoutParams params = progressView.getLayoutParams();
            progressView.measure(getProgressViewMeasureSpec(availableWidth, params.width),
                    getProgressViewMeasureSpec(availableHeight, params.height));
        }
        for (int i = 1; i < count; i++) {
            View view = getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            view.measure(getLandMaskViewMeasureSpec(availableWidth, params.width), MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();

        int childLeft = parentLeft;
        int count = getChildCount();
        TaskProgressView taskProgressView = null;

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof TaskProgressView) {
                taskProgressView = (TaskProgressView)view;
                layoutTaskProgressView(taskProgressView);
            } else {
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
                if (taskProgressView != null) {
                    childLeft = (int)(taskProgressView.getMarkCenterX(i - 1) - width / 2);
                }
                view.layout(childLeft, parentTop, childLeft + width, parentTop + height);
            }
        }

    }

    /**
     * 设置进度条view的位置
     * 默认纵向居中
     * */
    private void layoutTaskProgressView(TaskProgressView view) {
        final int parentLeft = getPaddingLeft();
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        view.layout(parentLeft, 0, parentLeft + width, height);
    }

    private int getProgressViewMeasureSpec(int available, int requestHW) {
        int spec;
        switch (requestHW) {
            case LayoutParams.WRAP_CONTENT:
                spec = MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST);
                break;
            case LayoutParams.MATCH_PARENT:
                spec = MeasureSpec.makeMeasureSpec(available, MeasureSpec.EXACTLY);
                break;
            default:
                spec = MeasureSpec.makeMeasureSpec(Math.min(requestHW, available),
                        MeasureSpec.EXACTLY);
                break;
        }
        return spec;
    }

    private int getLandMaskViewMeasureSpec(int available, int requestHW) {
        int spec;
        switch (requestHW) {
            case LayoutParams.WRAP_CONTENT:
                spec = MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST);
                break;
            case LayoutParams.MATCH_PARENT:
                spec = MeasureSpec.makeMeasureSpec(available, MeasureSpec.EXACTLY);
                break;
            default:
                spec = MeasureSpec.makeMeasureSpec(Math.min(requestHW, available),
                        MeasureSpec.EXACTLY);
                break;
        }
        return spec;
    }

    public int handlerMeasure(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(defaultSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    public void setAdapter(Adapter<T> adapter) {
        this.adapter = adapter;
        taskProgressView.setNewLandMarks(adapter.getData());
        removeAllViews();
        addView(taskProgressView,0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        for (int i = 0; i < adapter.getData().size(); i++) {
            addView(adapter.getView(getContext(),null, i, LandMarkState.STATE_DEFAULT));
        }
        taskProgressView.forceInvalidate();
    }

    public static abstract class Adapter<T extends ITask> {

        private List<T> data;

        public Adapter(List<T> data) {
            this.data = new ArrayList<>();
            if (data != null) {
                this.data.addAll(data);
            }
        }

        public List<T> getData() {
            return data;
        }

        protected abstract View getView(Context context,View viewContainer, int position, @LandMarkState int landmarkState);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        taskProgressView.setProgress(progress);
    }

    public void setLandMarkOffset(float offset) {
        taskProgressView.setLandMarkOffset(offset);
        requestLayout();
    }

    public void setProgressHeight(int progressHeight) {
        taskProgressView.setProgressHeight(progressHeight);
    }

    public void setProgressBackgroundColor(int backgroundColor) {
        taskProgressView.setProgressBgColor(backgroundColor);
    }

    public void setProgressColor(int progressColor) {
        taskProgressView.setProgressColor(progressColor);
    }

    public void setLandMarkWH(int WH) {
        taskProgressView.setLandMarkWH(WH);
    }

    public void setProgressMarginLeft(int progressMarginLeft) {
        taskProgressView.setMarginLeft(progressMarginLeft);
        requestLayout();
    }

    public void setProgressMarginRight(int progressMarginRight) {
        taskProgressView.setMarginRight(progressMarginRight);
        requestLayout();
    }

    public void setVerticalOffset(int verticalOffset) {
        taskProgressView.setVerticalOffset(verticalOffset);
    }

}
