package com.lazylite.mod.widget.preview

import android.graphics.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.values
import com.example.basemodule.R
import com.facebook.drawee.drawable.ScalingUtils
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig
import com.lazylite.mod.utils.transition.DetailImageTransition
import com.lazylite.mod.widget.BaseFragment
import timber.log.Timber

class PhotoPreviewFragment: BaseFragment() {

    companion object {
        fun getInstance(config: PhotoPreviewConfig): PhotoPreviewFragment {
            val fragment = PhotoPreviewFragment()
            fragment.config = config
            return fragment
        }
    }

    private var config: PhotoPreviewConfig? = null

    private var detailImageTransition: DetailImageTransition? = null

    private var mRootView: View? = null

    private var mContentView: View? = null

    private var imageView: ZoomDraweeView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = View.inflate(context, R.layout.base_photo_preview_fragment, null)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContentView = view.findViewById(R.id.content_view)
        imageView = view.findViewById(R.id.sdv)
        imageView?.setZoomOnClickListener(object : ZoomDraweeView.OnZoomViewListener {
            override fun onClick() {
                detailImageTransition?.exitTransition()
            }

            override fun onRelease(scaleMatrix: Matrix) {
                Timber.tag("matrix").e("scaleMatrix:${scaleMatrix}")
                val scaleX = scaleMatrix.values()[Matrix.MSCALE_X]
                val scaleY = scaleMatrix.values()[Matrix.MSCALE_Y]
                imageView?.pivotX = (imageView?.width ?: 0 ) / 2f
                imageView?.pivotY = (imageView?.height ?: 0 ) / 2f
                imageView?.translationX = scaleMatrix.values()[Matrix.MTRANS_X] - (imageView?.width  ?: 0) * (1 - scaleX) / 2
                imageView?.translationY = scaleMatrix.values()[Matrix.MTRANS_Y] - (imageView?.height  ?: 0) * (1 - scaleY) / 2
                imageView?.scaleX = scaleX
                imageView?.scaleY = scaleY
                detailImageTransition?.exitTransition()

            }

        })
        ImageLoaderWapper.getInstance().load(imageView, config?.imgUrl, ImageLoadConfig.Builder().setScaleType(
            ScalingUtils.ScaleType.FIT_CENTER).create())

        initAnim()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val transition = detailImageTransition ?: return super.onKeyDown(keyCode, event)
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (transition.exiting) {
                return true
            }
            if (transition.entering) {
                transition.cancel()
                closeSelf()
            } else {
                transition.exitTransition()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        detailImageTransition?.cancel()

    }

    private fun closeSelf() {
        close()
    }

    private fun initAnim() {

        val url = config?.imgUrl ?: return

        val tContext = context ?: return

        val enterView = config?.shareEleView?.get() ?: return

        val rootView = mRootView as? ViewGroup ?: return

        val tImageView = imageView?: return

        val tContentView = mContentView?: return

        detailImageTransition = DetailImageTransition(
            tContext,
            url,
            rootView,
            tContentView,
            enterView,
            tImageView,
            config?.excludeStatusBar?: false,
            config?.withEndSpringAnim ?: true)
        detailImageTransition?.mDuration = config?.animDuration ?: 320L

        detailImageTransition?.onEnterAnimStartListener = {
            tImageView.alpha = 0f
        }
        detailImageTransition?.onEnterAnimEndListener = {
            tImageView.alpha = 1f
//            vm.onAnimEnd()
        }
        detailImageTransition?.onExitAnimStartListener = {
            tImageView.alpha = 0f

        }
        detailImageTransition?.onExitAnimEndListener = {
            closeSelf()
        }

        detailImageTransition?.enterTransition()
    }

    override fun isNeedSwipeBack(): Boolean {
        return false
    }
}