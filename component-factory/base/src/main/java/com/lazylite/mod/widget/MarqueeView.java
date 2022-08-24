package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.AnimRes;
import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;

import com.example.basemodule.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author qyh
 * email：yanhui.qiao@tencentmusic.com
 * @date 2021/10/26.
 * description：自定义上下翻页View，每次取2个消息组成"文字1 | 文字2"样式；
 * 如果每次只取一条,请修改{@link #buildMessage()}调用处逻辑
 */

public class MarqueeView extends ViewFlipper {

    private static final int STARTANIM_NUM = 2;
    private int interval = 3000;
    private boolean hasSetAnimDuration = false;
    private int animDuration = 1000;
    private int textSize = 14;
    private int textColor = 0xff000000;
    private boolean singleLine = false;
    private boolean isReset;
    private boolean isAnimStart = false;

    private Typeface typeface;

    @AnimRes
    private int inAnimResId = R.anim.lrlite_base_anim_bottom_in;
    @AnimRes
    private int outAnimResId = R.anim.lrlite_base_anim_top_out;

    private int position;
    private List<String> messages = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private List<String> messageList;
    private HashMap<Integer, List<String>> listHashMap = new HashMap();

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.MarqueeViewStyle, defStyleAttr, 0);
        interval = typedArray.getInteger(R.styleable.MarqueeViewStyle_mvInterval, interval);
        hasSetAnimDuration = typedArray.hasValue(R.styleable.MarqueeViewStyle_mvAnimDuration);
        animDuration = typedArray.getInteger(R.styleable.MarqueeViewStyle_mvAnimDuration, animDuration);
        singleLine = typedArray.getBoolean(R.styleable.MarqueeViewStyle_mvSingleLine, false);
        textColor = typedArray.getColor(R.styleable.MarqueeViewStyle_mvTextColor, textColor);
        @FontRes int fontRes = typedArray.getResourceId(R.styleable.MarqueeViewStyle_mvFont, 0);
        if (fontRes != 0) {
            typeface = ResourcesCompat.getFont(context, fontRes);
        }
        typedArray.recycle();
        setFlipInterval(interval);
    }

    /**
     * 根据列表，启动翻页
     *
     * @param messages 字符串列表
     */
    public void startWithList(List<String> messages) {
        startWithList(messages, inAnimResId, outAnimResId);
    }

    /**
     * 根据列表，启动翻页
     *
     * @param messages     字符串列表
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    public void startWithList(List<String> messages, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        if (messages == null || messages.size() == 0) return;
        stopAnim();
        setMessages(messages);
        post(() -> start(inAnimResId, outAnimResID));
    }


    private void start(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        reset();
        position = 0;
        View view = createTextView(buildMessage());
        if (view.getParent() == null) {
            addView(view);
        }

        if (messages.size() > STARTANIM_NUM) {
            setInAndOutAnimation(inAnimResId, outAnimResID);
            startFlipping();
            animationListener();
        }
    }

    private void animationListener() {
        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (isAnimStart) {
                        animation.cancel();
                    }
                    isAnimStart = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    checkPosition();
                    View view = createTextView(buildMessage());
                    if (view.getParent() == null) {
                        addView(view);
                    }
                    isAnimStart = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private void checkPosition() {
        position++;
        if (position >= messages.size()) {
            position = 0;
        }
    }

    // 拼成 "left | right"
    private String buildMessage() {
        if (messageList == null) {
            messageList = new ArrayList<>();
        } else {
            messageList.clear();
        }
        String left = messages.get(position);
        messageList.add(messages.get(position));
        if (messages.size() > position + 1) {
            position++;
            String right = messages.get(position);
            messageList.add(right);
            insertMap();
            return left + "  |  " + right;
        } else {
            insertMap();
        }
        return left;
    }

    private void insertMap() {
        if (listHashMap != null && !listHashMap.containsValue(messageList)) {
            listHashMap.put(position, new ArrayList<>(messageList));
        }
    }

    private TextView createTextView(String message) {
        int index;
        if (!isReset) {
            // 只创建2个View，其他复用
            index = (getDisplayedChild() + 1) % 2;
        } else {
            index = 0;
            isReset = false;
        }
        TextView textView = (TextView) getChildAt(index);
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setIncludeFontPadding(true);
            textView.setSingleLine(singleLine);
            if (singleLine) {
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            if (typeface != null) {
                textView.setTypeface(typeface);
            }

            setOnClickListener(v -> {
                if (onItemClickListener != null && listHashMap != null) {
                    onItemClickListener.onItemClick(listHashMap.get(getPosition()));
                }
            });
        }
        textView.setText(message);
        textView.setTag(position);
        return textView;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void stopAnim() {
        if (isFlipping()) {
            stopFlipping();
        }
        if (getInAnimation() != null) {
            getInAnimation().cancel();
        }
    }

    private void reset() {
        isReset = true;
        position = 0;
        setDisplayedChild(0);
        clearAnimation();
        if (listHashMap != null) {
            listHashMap.clear();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(List<String> list);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        if (hasSetAnimDuration) inAnim.setDuration(animDuration);
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        if (hasSetAnimDuration) outAnim.setDuration(animDuration);
        setOutAnimation(outAnim);
    }
}
