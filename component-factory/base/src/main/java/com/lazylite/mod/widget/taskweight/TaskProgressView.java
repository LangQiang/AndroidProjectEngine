package com.lazylite.mod.widget.taskweight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.lazylite.mod.widget.taskweight.model.ITask;
import com.lazylite.mod.widget.taskweight.model.mark.LandMarkState;

import java.util.ArrayList;
import java.util.List;

public class TaskProgressView<T extends ITask> extends View {

    private static final int DEFAULT_BG_COLOR = 0xff5553F7;
    private static final int DEFAULT_PROGRESS_COLOR = 0xff5553F7;

    private int backgroundColor = DEFAULT_BG_COLOR;
    private int ProgressColor = DEFAULT_PROGRESS_COLOR;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private float radius;
    private int progressHeight;
    private int landMarkWH;

    private int curProgress;
    private float curViewProgress; //不包含两边的radius的长度
    private float maxViewProgress; //不包含两边的radius的长度

    private Rect landMarkRect = new Rect();

    private List<T> landMarks;

    private float landMarkOffset;

    private int marginLeft, marginRight, verticalOffset;

    private int oldArrivePos = -1;

    private Paint drawPaint;

    private OnLandMarkStateChangedListener onLandMarkStateChangedListener;

    public TaskProgressView(Context context) {
        this(context, null);
    }

    public TaskProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(ProgressColor);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void forceInvalidate() {
        oldArrivePos = -1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        check();
        drawBg(canvas); //背景
        drawProgress(canvas); //进度
        drawLandMark(canvas); //里程碑标记点
    }

    private void check() {
        if (progressHeight == 0) {
            progressHeight = getHeight();
        }
        if (radius == 0) {
            radius = progressHeight / 2.0f;
        }

        if (maxViewProgress == 0) {
            maxViewProgress = getWidth() - progressHeight - marginLeft - marginRight;
        }

        if (curViewProgress == 0 && curProgress != 0) {
            curViewProgress = operationCurrentProgress();
        }
    }

    private void drawBg(Canvas canvas) {
        backgroundPaint.setStrokeWidth(progressHeight); //线高度
        canvas.drawLine(radius + marginLeft, getHeight() / 2.0f + verticalOffset, getWidth() - marginRight - radius , getHeight() / 2.0f + verticalOffset, backgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        if (curViewProgress == 0) {
            return;
        }
        progressPaint.setStrokeWidth(progressHeight); //线高度
        canvas.drawLine(radius + marginLeft, getHeight() / 2.0f + verticalOffset, radius + marginLeft + curViewProgress, getHeight() / 2.0f + verticalOffset, progressPaint);
    }

    private void drawLandMark(Canvas canvas) {
        if (landMarks == null) {
            return;
        }
        int newArrivePos = -1;
        for (int i = 0; i < landMarks.size(); i++) {
            T iLandMark = landMarks.get(i);
            if (iLandMark != null) {
                float markCenterX = getMarkCenterX(i);
                int landmarkState;
                if (curViewProgress + radius + marginLeft >= markCenterX) {
                    newArrivePos = i;
                    landmarkState = LandMarkState.STATE_OVERRIDE;
                } else {
                    landmarkState = LandMarkState.STATE_DEFAULT;
                }
                landMarkRect.set((int)(markCenterX - landMarkWH / 2.0f), (getHeight() - landMarkWH) / 2 + verticalOffset, (int)(markCenterX + landMarkWH / 2.0f), (getHeight() - landMarkWH) / 2 + landMarkWH + verticalOffset);
                iLandMark.getLandMark().draw(getResources(), canvas, landMarkRect, landmarkState, drawPaint);
            }
        }
        notifyChanged(oldArrivePos, newArrivePos);
        oldArrivePos = newArrivePos;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            return;
        }
        this.curProgress = progress;
        if (getWidth() != 0 && maxViewProgress != 0 && landMarks != null) {
            curViewProgress = operationCurrentProgress();
        }
        invalidate();
    }

    private float operationCurrentProgress() {
        float progress;
        int total = 0;
        int index = -1;
        int preLandmarkLength = 0;
        int curWeight = 1;
        for (int i = 0; i < landMarks.size(); i++) {
            int curLandmarkLength = landMarks.get(i).getLandMark().getLandMarkLength();
            curWeight = curLandmarkLength - preLandmarkLength;
            preLandmarkLength = curLandmarkLength;

            total += curWeight;
            if (curProgress < total) {
                total = total - curWeight;
                index = i;
                break;
            }
        }
        if (landMarks.size() == 1 || curWeight == 0) {
            return 0;
        }
        if (index == 0) {
            progress = landMarkOffset * curProgress / curWeight;
        } else if (index == -1) {
            progress = maxViewProgress;
        } else {
            float intervalLength = (maxViewProgress - 2 * landMarkOffset) / (landMarks.size() - 1);
            progress = landMarkOffset + intervalLength * (index - 1) + intervalLength * (curProgress - total) / curWeight;
        }
        return progress;
    }

    private void notifyChanged(int oldArrive, int newArrive) {
        if (onLandMarkStateChangedListener != null && oldArrive != newArrive) {
            onLandMarkStateChangedListener.onChangedRange(oldArrive, newArrive);
        }
    }

    public void setNewLandMarks(List<T> iTasks) {
        if (iTasks == null) {
            if (this.landMarks != null) {
                this.landMarks.clear();
            } else {
                this.landMarks = new ArrayList<>(4);
            }
        } else {
            if (this.landMarks != null) {
                this.landMarks.clear();
            } else {
                this.landMarks = new ArrayList<>(4);
            }
            this.landMarks.addAll(iTasks);
        }
        invalidate();
    }

    public void setLandMarkOffset(float landMarkOffset) {
        if (this.landMarkOffset == landMarkOffset) {
            return;
        }
        this.landMarkOffset = landMarkOffset;
        invalidate();
    }

    public void setProgressHeight(int progressHeight) {
        if (this.progressHeight == progressHeight) {
            return;
        }
        this.progressHeight = progressHeight;
        this.radius = progressHeight / 2.0f;
        invalidate();
    }

    public float getMarkCenterX(int position) {
        if (landMarks == null || landMarks.size() == 0) {
            return 0;
        }
        float markCenterX;
        if (position == 0) {
            markCenterX = radius + marginLeft + landMarkOffset;
        } else if (position == landMarks.size() - 1) {
            markCenterX = getMeasuredWidth() - radius - landMarkOffset - marginRight;
        } else {
            if (landMarks.size() == 1) {
                markCenterX = radius + marginLeft + landMarkOffset;
            } else {
                markCenterX = radius + marginLeft + landMarkOffset + (getMeasuredWidth() - marginLeft - marginRight - progressHeight - landMarkOffset * 2) / (landMarks.size() - 1) * position;
            }
        }
        return markCenterX;
    }

    public void setProgressBgColor(int backgroundColor) {
        if (this.backgroundColor == backgroundColor) {
            return;
        }
        this.backgroundColor = backgroundColor;
        this.backgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        if (this.ProgressColor == progressColor) {
            return;
        }
        this.ProgressColor = progressColor;
        this.progressPaint.setColor(progressColor);
        invalidate();
    }

    public void setLandMarkWH(int wh) {
        if (this.landMarkWH == wh) {
            return;
        }
        this.landMarkWH = wh;
        invalidate();
    }

    public void setMarginLeft(int marginLeft) {
        if (this.marginLeft == marginLeft) {
            return;
        }
        this.marginLeft = marginLeft;
        invalidate();
    }

    public void setMarginRight(int marginRight) {
        if (this.marginRight == marginRight) {
            return;
        }
        this.marginRight = marginRight;
        invalidate();
    }

    public void setVerticalOffset(int verticalOffset) {
        if (this.verticalOffset == verticalOffset) {
            return;
        }
        this.verticalOffset =verticalOffset;
        invalidate();
    }

    public void setOnLandMarkStateChangedListener(OnLandMarkStateChangedListener onLandMarkStateChangedListener) {
        this.onLandMarkStateChangedListener = onLandMarkStateChangedListener;
    }

    interface OnLandMarkStateChangedListener {
        void onChangedRange(int oldArrivePos, int newArrivePos);
    }
}
