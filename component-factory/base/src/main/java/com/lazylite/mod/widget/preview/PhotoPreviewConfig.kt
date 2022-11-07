package com.lazylite.mod.widget.preview

import android.view.View
import java.lang.ref.WeakReference

class PhotoPreviewConfig(
    val shareEleView: WeakReference<View>,
    val imgUrl: String,
    val excludeStatusBar: Boolean = false,
    val animDuration: Long = 320L,
    val withEndSpringAnim: Boolean = true
)