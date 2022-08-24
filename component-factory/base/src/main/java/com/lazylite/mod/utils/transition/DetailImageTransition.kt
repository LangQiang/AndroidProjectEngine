package com.lazylite.mod.utils.transition

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.core.animation.doOnEnd
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.facebook.drawee.drawable.FadeDrawable
import com.facebook.drawee.drawable.ScaleTypeDrawable
import com.facebook.drawee.generic.RootDrawable
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig
import com.lazylite.mod.imageloader.fresco.listener.IDisplayImageListener
import com.lazylite.mod.utils.DeviceInfo
import com.lazylite.mod.utils.ScreenUtility
import timber.log.Timber

class DetailImageTransition(
    private val context: Context,
    private val imgUrl: String,
    private val rootView: ViewGroup,
    private val fadeView: View,
    private val enterShareEleView: View,
    private val targetView: View) {

    var entering: Boolean = false

    var exiting: Boolean = false

    var mDuration: Long = 320L

    private val deviceGrade = DeviceInfo.GRADE_MIDDLE

    private val springStartValue = -25f

    private var imageConfig = ImageLoadConfig.Builder().setFadeDuration(0).setFailureDrawable(null).setLoadingDrawable(null).create()

    private var enterAnimatorSet: AnimatorSet? = null

    private var enterAnimValue: ValueAnimator? = null

    private var enterFadeAnim: ObjectAnimator? = null

    private var exitAnimatorSet: AnimatorSet? = null

    private var exitAnimValue: ValueAnimator? = null

    private var exitFadeAnim: ObjectAnimator? = null

    private val shareEleLocation = IntArray(2)
    private val targetLocation = IntArray(2)

    var onEnterAnimEndListener: (() -> Unit)? = null

    var onEnterAnimStartListener: (() -> Unit)? = null

    var onExitAnimEndListener: (() -> Unit)? = null

    var onExitAnimStartListener: (() -> Unit)? = null

    @MainThread
    fun enterTransition() {

        entering = true

        cancel()

        onEnterAnimStartListener?.invoke()

        rootView.visibility = View.INVISIBLE

        val interceptView = InterceptView(context)

        rootView.addView(interceptView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))


        rootView.post {
            val grade = DeviceInfo.getDevicePerformanceGrade()
            if (grade > DeviceInfo.UNKNOWN && grade < deviceGrade) {
                rootView.visibility = View.VISIBLE
                entering = false
                onEnterAnimEndListener?.invoke()
                rootView.removeView(interceptView)
                return@post
            }

            rootView.visibility = View.VISIBLE

            enterShareEleView.getLocationOnScreen(shareEleLocation)
            targetView.getLocationOnScreen(targetLocation)

            val targetWidth = targetView.width
            val targetHeight = targetView.height

            val enterShareEleViewWidth = enterShareEleView.width
            val enterShareEleViewHeight = enterShareEleView.height


            var finalWidth: Int = enterShareEleViewWidth
            var finalHeight: Int = enterShareEleViewHeight
            var diffW: Int = targetWidth - enterShareEleViewWidth
            var diffH: Int = targetHeight - enterShareEleViewHeight

            //强制 调整高度 不需要根据宽高比来动态计算
            if (targetWidth != 0 && targetHeight != 0 && enterShareEleViewWidth != 0 && enterShareEleViewHeight != 0) {
//                if (targetWidth.toFloat() / targetHeight > enterShareEleViewWidth.toFloat() / enterShareEleViewHeight) {
//                    finalHeight = (targetHeight * enterShareEleViewWidth.toFloat() / targetWidth).toInt()
//                    diffH = enterShareEleViewHeight - finalHeight
//                    finalWidth = enterShareEleViewWidth
//                    diffW = 0
//                } else {
//                    finalWidth = (targetWidth.toFloat() * enterShareEleViewHeight / targetHeight).toInt()
//                    diffW = enterShareEleViewWidth - finalWidth
//                    finalHeight = enterShareEleViewHeight
//                    diffH = 0
//                }
                finalHeight = (targetHeight * enterShareEleViewWidth.toFloat() / targetWidth).toInt()
                diffH = enterShareEleViewHeight - finalHeight
                finalWidth = enterShareEleViewWidth
                diffW = 0
            }
            val scaleRatio = if (finalHeight != 0) targetHeight.toFloat() / finalHeight else 1f

            Timber.d("scale: $scaleRatio  fw: $finalWidth  fh: $finalHeight  dw: $diffW  dh: $diffH")

            val animView = SimpleDraweeView(context)
            animView.scaleType = ImageView.ScaleType.CENTER_CROP
            animView.pivotX = 0f
            animView.pivotY = 0f

            animView.layoutParams = ViewGroup.LayoutParams(enterShareEleViewWidth, enterShareEleViewHeight)
            rootView.addView(animView)

            setAnimViewSource(animView, enterShareEleView, targetView, true) {
                enterShareEleView.alpha = 0f
            }


            enterFadeAnim = ObjectAnimator.ofFloat(fadeView, View.ALPHA, 0f, 1f)
            enterFadeAnim?.duration = (mDuration * 0.67f).toLong()
            enterFadeAnim?.start()

            val animX = ObjectAnimator.ofFloat(animView, View.TRANSLATION_X, shareEleLocation[0].toFloat(), targetLocation[0].toFloat())
            val animY = ObjectAnimator.ofFloat(animView, View.TRANSLATION_Y, shareEleLocation[1].toFloat(), targetLocation[1].toFloat() + springStartValue)

            val scaleX = ObjectAnimator.ofFloat(animView, View.SCALE_X, 1f, scaleRatio)
            val scaleY = ObjectAnimator.ofFloat(animView, View.SCALE_Y, 1f, scaleRatio)

            enterAnimValue = ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    (it.animatedValue as? Float)?.apply {
                        if (diffH != 0 || diffW != 0) {
                            animView.layoutParams.width =
                                enterShareEleViewWidth - (this * diffW).toInt()
                            animView.layoutParams.height =
                                enterShareEleViewHeight - (this * diffH).toInt()
                            animView.requestLayout()
                        }
                    }
                }
            }
            enterAnimValue?.duration = (mDuration / 5f * 2).toLong()
            enterAnimValue?.start()


            enterAnimatorSet = AnimatorSet().apply {
                playTogether(animX, animY, scaleX, scaleY)
                duration = mDuration
                doOnEnd {
                    val springForce = SpringForce(targetLocation[1].toFloat())
                    springForce.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                    springForce.stiffness = SpringForce.STIFFNESS_LOW
                    val springAnimY = SpringAnimation(animView, SpringAnimation.TRANSLATION_Y)
                    springAnimY.addEndListener(object : DynamicAnimation.OnAnimationEndListener{
                        override fun onAnimationEnd(
                            animation: DynamicAnimation<*>?,
                            canceled: Boolean,
                            value: Float,
                            velocity: Float
                        ) {
                            animation?.removeEndListener(this)
                            entering = false
                            onEnterAnimEndListener?.invoke()
                            enterShareEleView.alpha = 1f
                            rootView.removeView(animView)
                            rootView.removeView(interceptView)
                        }

                    })
                    springAnimY.spring = springForce
                    springAnimY.setStartValue(targetLocation[1].toFloat() + springStartValue)
                    springAnimY.start()

                }
                start()
            }

        }

    }

    //退出动画是反向的
    fun exitTransition() {

        exiting = true

        cancel()

        val grade = DeviceInfo.getDevicePerformanceGrade()
        if (grade > DeviceInfo.UNKNOWN && grade < deviceGrade) {
            exiting = false
            onExitAnimEndListener?.invoke()
            return
        }

        val interceptView = InterceptView(context)

        rootView.addView(interceptView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))


        enterShareEleView.getLocationOnScreen(shareEleLocation)
        targetView.getLocationOnScreen(targetLocation)

        val targetWidth = enterShareEleView.width
        val targetHeight = enterShareEleView.height

        val enterShareEleViewWidth = targetView.width * targetView.scaleX
        val enterShareEleViewHeight = targetView.height * targetView.scaleY

        var finalWidth: Float = enterShareEleViewWidth
        var finalHeight: Float = enterShareEleViewHeight
        var diffW: Float = targetWidth - enterShareEleViewWidth
        var diffH: Float = targetHeight - enterShareEleViewHeight

        //强制 调整高度 不需要根据宽高比来动态计算
        if (targetWidth != 0 && targetHeight != 0 && enterShareEleViewWidth != 0f && enterShareEleViewHeight != 0f) {
//            if (targetWidth.toFloat() / targetHeight > enterShareEleViewWidth.toFloat() / enterShareEleViewHeight) {
//                finalHeight = (targetHeight * enterShareEleViewWidth.toFloat() / targetWidth).toInt()
//                diffH = enterShareEleViewHeight - finalHeight
//                finalWidth = enterShareEleViewWidth
//                diffW = 0
//            } else {
//                finalWidth = (targetWidth.toFloat() * enterShareEleViewHeight / targetHeight).toInt()
//                diffW = enterShareEleViewWidth - finalWidth
//                finalHeight = enterShareEleViewHeight
//                diffH = 0
//            }
            finalHeight = targetHeight * enterShareEleViewWidth / targetWidth
            diffH = enterShareEleViewHeight - finalHeight
            finalWidth = enterShareEleViewWidth
            diffW = 0f
        }
        val scaleRatio = if (finalHeight != 0f) targetHeight.toFloat() / finalHeight else 1f
        Timber.d("scale: $scaleRatio  fw: $finalWidth  fh: $finalHeight  dw: $diffW  dh: $diffH")

        val animView = SimpleDraweeView(context)
        animView.scaleType = ImageView.ScaleType.CENTER_CROP
        animView.pivotX = 0f
        animView.pivotY = 0f

        setAnimViewSource(animView, targetView, enterShareEleView, false) {
            enterShareEleView.alpha = 0f
            onExitAnimStartListener?.invoke()
        }

        animView.layoutParams = ViewGroup.LayoutParams(targetWidth, targetHeight)
        rootView.addView(animView)

        exitFadeAnim = ObjectAnimator.ofFloat(fadeView, View.ALPHA, 1f, 0f)
        exitFadeAnim?.duration = (mDuration * 0.67f).toLong()
        exitFadeAnim?.start()

        val animX = ObjectAnimator.ofFloat(animView, View.TRANSLATION_X, targetLocation[0].toFloat(), shareEleLocation[0].toFloat())
        val animY = ObjectAnimator.ofFloat(animView, View.TRANSLATION_Y, targetLocation[1].toFloat(), shareEleLocation[1].toFloat() - springStartValue)

        val scaleX = ObjectAnimator.ofFloat(animView, View.SCALE_X, 1f, scaleRatio)
        val scaleY = ObjectAnimator.ofFloat(animView, View.SCALE_Y, 1f, scaleRatio)

        exitAnimValue = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                (it.animatedValue as? Float)?.apply {
                    animView.layoutParams.width = (enterShareEleViewWidth - this * diffW).toInt()
                    animView.layoutParams.height = (enterShareEleViewHeight - this * diffH).toInt()
                    animView.requestLayout()
                }
            }
        }
        exitAnimValue?.duration = (mDuration / 5f * 2).toLong()
        exitAnimValue?.start()

        exitAnimatorSet = AnimatorSet().apply {
            playTogether(animX, animY, scaleX, scaleY)
            duration = mDuration
            doOnEnd {
                val springForce = SpringForce(shareEleLocation[1].toFloat())
                springForce.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                springForce.stiffness = SpringForce.STIFFNESS_LOW
                val springAnimY = SpringAnimation(animView, SpringAnimation.TRANSLATION_Y)
                springAnimY.addEndListener(object : DynamicAnimation.OnAnimationEndListener{
                    override fun onAnimationEnd(
                        animation: DynamicAnimation<*>?,
                        canceled: Boolean,
                        value: Float,
                        velocity: Float
                    ) {
                        animation?.removeEndListener(this)
                        exiting = false
                        enterShareEleView.alpha = 1f
                        rootView.removeView(animView)
                        rootView.removeView(interceptView)
                        onExitAnimEndListener?.invoke()
                    }

                })
                springAnimY.spring = springForce
                springAnimY.setStartValue(shareEleLocation[1].toFloat() - springStartValue)
                springAnimY.start()
            }
            start()
        }
    }


    private fun setAnimViewSource(animView:SimpleDraweeView, enterShareEleView: View, targetView: View, isEnter: Boolean, onSetFinishListener: () -> Unit) {
        val targetWidth = targetView.width
        val targetHeight = targetView.height

        val enterShareEleViewWidth = enterShareEleView.width
        val enterShareEleViewHeight = enterShareEleView.height

        if (targetHeight == 0 || targetWidth == 0 || enterShareEleViewWidth == 0 || enterShareEleViewHeight == 0) {
            return
        }

        try {
            if (targetHeight * enterShareEleViewWidth == targetWidth * enterShareEleViewHeight) {
                val bitmap = ScreenUtility.getViewSnapshot(enterShareEleView)
                animView.setImageBitmap(bitmap)
                onSetFinishListener()
            } else {
                val drawable = (enterShareEleView as? ImageView)?.drawable

                var w = 0
                var h = 0
                try {
                    val rootDrawable = drawable as? RootDrawable
                    val fadeDrawable = rootDrawable?.drawable as? FadeDrawable
                    val scaleTypeDrawable = fadeDrawable?.getDrawable(2) as? ScaleTypeDrawable
                    w = scaleTypeDrawable?.current?.intrinsicWidth ?: 0
                    h = scaleTypeDrawable?.current?.intrinsicHeight ?: 0
                } catch (e: Exception) {
                }


                Timber.d("realW:$w  realH:$h")

                if (drawable != null && w != 0 && h != 0) {
                    val config = Bitmap.Config.ARGB_8888
                    val bitmap = Bitmap.createBitmap(w, h, config)
                    val canvas = Canvas(bitmap)
                    val rect = Rect(drawable.bounds)
                    drawable.setBounds(0, 0, w, h)
                    drawable.draw(canvas)
                    drawable.setBounds(rect.left, rect.top, rect.right, rect.bottom)
                    animView.setImageBitmap(bitmap)
                    onSetFinishListener()
                } else {
                    ImageLoaderWapper.getInstance().load(animView, imgUrl, imageConfig, object : IDisplayImageListener<ImageInfo>{
                        override fun onSuccess(result: ImageInfo?, animatable: Animatable?) {
                            onSetFinishListener()
                        }

                        override fun onFailure(throwable: Throwable?) {
                            onSetFinishListener()
                        }

                    })
                }
            }
        } catch (e: Exception) {

        }
    }

    fun cancel() {
        enterAnimValue?.removeAllListeners()
        enterAnimValue?.removeAllUpdateListeners()
        enterAnimatorSet?.removeAllListeners()
        if (enterAnimatorSet?.isRunning == true || enterAnimatorSet?.isStarted == true) {
            enterAnimatorSet?.cancel()
        }
        if (enterFadeAnim?.isRunning == true || enterFadeAnim?.isStarted == true) {
            enterFadeAnim?.cancel()
        }
        if (enterAnimValue?.isRunning == true || enterAnimValue?.isStarted == true) {
            enterAnimValue?.cancel()
        }

        exitAnimValue?.removeAllListeners()
        exitAnimValue?.removeAllUpdateListeners()
        exitAnimatorSet?.removeAllListeners()
        if (exitAnimatorSet?.isRunning == true || exitAnimatorSet?.isStarted == true) {
            exitAnimatorSet?.cancel()
        }
        if (exitFadeAnim?.isRunning == true || exitFadeAnim?.isStarted == true) {
            exitFadeAnim?.cancel()
        }
        if (exitAnimValue?.isRunning == true || exitAnimValue?.isStarted == true) {
            exitAnimValue?.cancel()
        }
    }
}