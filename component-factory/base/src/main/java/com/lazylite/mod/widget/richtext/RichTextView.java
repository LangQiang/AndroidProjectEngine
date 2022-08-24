package com.lazylite.mod.widget.richtext;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.basemodule.R;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.SimpleDownloaderListener;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.utils.psrc.PsrcInfo;
import com.lazylite.mod.widget.richtext.subscaleview.ImageSource;
import com.lazylite.mod.widget.richtext.subscaleview.SubsamplingScaleImageView;

import java.util.List;


/**
 * @author DongJr
 * @date 2020/2/21
 */
public class RichTextView extends LinearLayout implements View.OnClickListener {

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMG = "IMG";
    public static final String TYPE_PLACE_HOLDER = "PLACE_HOLDER";

    private PsrcInfo mPsrcInfo;
    private String mEmptyText;

    private View mExpandView;
    private List<RichTextInfo> mRichTextList;

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public void setInfo(List<RichTextInfo> mRichTextList, String emptyText, PsrcInfo psrcInfo){
        this.mRichTextList = mRichTextList;
        this.mEmptyText = emptyText;
        this.mPsrcInfo = psrcInfo;
        layoutExpandView();
    }

    /**
     * 已展开View
     */
    private void layoutExpandView(){
        removeAllViews();
        mExpandView = this;
        LinearLayout linearLayout = this;
        if (mRichTextList != null && !mRichTextList.isEmpty()){
            for (int i = 0; i < mRichTextList.size(); i++){
                RichTextInfo richTextInfo = mRichTextList.get(i);
                if (TYPE_TEXT.equalsIgnoreCase(richTextInfo.getType())){
                    TextView textView = createTextView(richTextInfo.getContent());
                    RichTextInfo.Style style = richTextInfo.getStyle();
                    if (style != null){
                        int marginBottom = style.getMarginBottom();
                        textView.setPadding(0, 0, 0, marginBottom);
                    }
                    linearLayout.addView(textView);
                } else if (TYPE_IMG.equalsIgnoreCase(richTextInfo.getType())){
                    RichTextInfo.Style style = richTextInfo.getStyle();
                    if (style != null){
                        final SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(getContext());
                        //服务给的宽高是按686像素给的
                        int width = (int) (DeviceInfo.WIDTH - 2 * ScreenUtility.dip2px(15));
                        float ratio = width * 1f / style.getWidth();
                        int height = (int) ((float)style.getHeight() * ratio);
                        MarginLayoutParams params = new MarginLayoutParams(width, height);
                        params.bottomMargin = style.getMarginBottom();
                        imageView.setLayoutParams(params);
                        imageView.setZoomEnabled(false);

                        final String url = richTextInfo.getUrl();
                        ImageLoaderWapper.getInstance().load(url, new SimpleDownloaderListener() {
                            @Override
                            public void onSuccess(Bitmap result) {
                                try {
                                    SimpleCacheKey simpleCacheKey = new SimpleCacheKey(url);
                                    FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().
                                            getMainFileCache().getResource(simpleCacheKey);
                                    imageView.setImage(ImageSource.uri(resource.getFile().getPath()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        linearLayout.addView(imageView);
                    }
                } else if (TYPE_PLACE_HOLDER.equals(richTextInfo.getType())) {
                    View holder = new View(getContext());
                    linearLayout.addView(holder, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtility.dip2px(40)));
                }
            }
        }

        startAppearAnim(mExpandView, 200);
    }


    private void startAppearAnim(View view, int duration){
        if (view == null){
            return;
        }
        ObjectAnimator animation = ObjectAnimator.ofFloat(view,"alpha",0f, 1f);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(duration);
        animation.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private TextView createTextView(String text){
        TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(R.color.black60));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setText(text);
        textView.setLineSpacing(ScreenUtility.dip2px(6), 1f);
        return textView;
    }
}
