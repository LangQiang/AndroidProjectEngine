package com.lazylite.mod.widget.pulltorefresh.internal;

import android.content.Context;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.basemodule.R;
import com.lazylite.mod.widget.pulltorefresh.PullToRefreshBase;

public class MainHomeLoadingLayout extends LoadingLayout {

    private LottieAnimationView lottieAnimationView;

    public MainHomeLoadingLayout(Context context, boolean isWhite) {
        super(context, PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH, "", "", "", isWhite);
    }

    @Override
    public void reset() {
        if (lottieAnimationView != null && lottieAnimationView.isAnimating()) {
            lottieAnimationView.cancelAnimation();
        }
    }

    @Override
    public void releaseToRefresh() {
    }

    @Override
    public void pullToRefresh() {
    }

    @Override
    public void refreshing() {
        if (lottieAnimationView != null && !lottieAnimationView.isAnimating()) {
            lottieAnimationView.playAnimation();
        }
    }

    @Override
    protected int getLayoutId(boolean isWhite) {
        if (isWhite){
            return R.layout.pull_main_home_refresh_loading_white;
        } else {
            return R.layout.pull_main_home_refresh_loading;
        }
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        lottieAnimationView = view.findViewById(R.id.pull_refresh_lottie_view);
    }
}