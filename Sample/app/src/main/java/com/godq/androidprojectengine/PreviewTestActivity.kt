package com.godq.androidprojectengine

import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig
import com.lazylite.mod.imageloader.fresco.listener.IDisplayImageListener
import com.lazylite.mod.widget.preview.PhotoPreviewConfig
import com.lazylite.mod.widget.preview.PhotoPreviewFragment
import java.lang.ref.WeakReference

class PreviewTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_test)
        val imageUrl = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/3cd3bec096eb40f7af24fdf330eb45e0"

        val simpleDraweeView = findViewById<SimpleDraweeView>(R.id.test_sdv)
        with(simpleDraweeView) {

            ImageLoaderWapper.getInstance().load(this, imageUrl, ImageLoadConfig.Builder()
                .roundedCorner(8f)
                .setScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .create(), object :
                IDisplayImageListener<ImageInfo> {
                override fun onSuccess(result: ImageInfo?, animatable: Animatable?) {
                    result ?: return
                    simpleDraweeView.layoutParams.height = (result.height.toFloat() / result.width * simpleDraweeView.layoutParams.width).toInt()
                    simpleDraweeView.requestLayout()
                }

                override fun onFailure(throwable: Throwable?) {
                }

            })

            this.setOnClickListener {
                val config = PhotoPreviewConfig(WeakReference(this), imageUrl, true, 320, false)
                supportFragmentManager.beginTransaction()
                    .addToBackStack("1")
                    .add(R.id.fragment_container, PhotoPreviewFragment.getInstance(config), "1")
                    .commit()
//                FragmentOperation.getInstance().showFullFragment(PhotoPreviewFragment.getInstance(this, imageUrl))
            }
        }
    }

}