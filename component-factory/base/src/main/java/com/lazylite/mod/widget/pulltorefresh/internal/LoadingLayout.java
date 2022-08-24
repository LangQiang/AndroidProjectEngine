package com.lazylite.mod.widget.pulltorefresh.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.basemodule.R;
import com.lazylite.mod.widget.pulltorefresh.PullToRefreshBase;

public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	protected final ImageView headerImage;
	protected final ProgressBar headerProgress;
	protected final TextView headerText;

	private String pullLabel;
	private String refreshingLabel;
	private String releaseLabel;

	private final Animation rotateAnimation, resetRotateAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel) {
		this(context, mode, releaseLabel, pullLabel, refreshingLabel, false);
	}

	public LoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel, boolean isWhite) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(getLayoutId(isWhite), this);
		headerText = (TextView) header.findViewById(R.id.pull_refresh_text);
		headerText.setText(pullLabel);
		headerImage = (ImageView) header.findViewById(R.id.pull_refresh_image);
		headerProgress = (ProgressBar) header.findViewById(R.id.pull_refresh_progress);

		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		switch (mode) {
			case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
				headerImage.setBackgroundResource(R.drawable.pullrefresh_up_arrow);
				break;
			case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
			default:
				headerImage.setBackgroundResource(R.drawable.pullrefresh_down_arrow);
				break;
		}
		onViewCreated(header);
	}

	protected void onViewCreated(View view) {}

	protected int getLayoutId(boolean isWhite){
		return R.layout.pull_refresh_loading;
	}

	public void reset() {
		headerText.setText(pullLabel);
		headerImage.setVisibility(View.VISIBLE);
		headerImage.clearAnimation();
		headerProgress.setVisibility(View.GONE);
	}

	public void releaseToRefresh() {
		headerText.setText(releaseLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(rotateAnimation);
	}

	public void pullToRefresh() {
		headerText.setText(pullLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(resetRotateAnimation);
	}

	public void refreshing() {
		headerText.setText(refreshingLabel);
		headerImage.clearAnimation();
		headerImage.setVisibility(View.INVISIBLE);
		headerProgress.setVisibility(View.VISIBLE);
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
		headerText.setText(pullLabel);
	}

	public void startDragging() {

	}

	public void stopDragging(boolean isRefreshing) {

	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	//使用XML默认颜色
	public void setTextColor(int color) {
		if(headerText!=null){
			headerText.setTextColor(color);
		}
	}

	public void setTextVisibility(int visibility) {
		if(headerText != null){
			headerText.setVisibility(visibility);
		}
	}

}
