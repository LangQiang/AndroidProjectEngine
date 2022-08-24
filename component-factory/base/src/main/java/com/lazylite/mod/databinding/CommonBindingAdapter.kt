package com.lazylite.mod.databinding

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper
import com.lazylite.mod.imageloader.fresco.config.ImageConfigFactory
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig
import com.lazylite.mod.utils.ColorUtils
import com.lazylite.mod.widget.richtext.RichTextInfo
import com.lazylite.mod.widget.richtext.RichTextView
import java.lang.ref.WeakReference

@BindingAdapter(value = ["frescoImgUrl", "frescoConfig"], requireAll = false)
fun loadFrescoImg(simpleDraweeView: SimpleDraweeView?, imageUrl: String?, config: ImageLoadConfig?) {
    imageUrl ?: return
    val tempConfig = config ?: ImageConfigFactory.createFrescoConfig(ImageConfigFactory.TINGSHU_DEFAULT_SQUARE)
    ImageLoaderWapper.getInstance().load(simpleDraweeView, imageUrl, tempConfig)
}

@BindingAdapter("richList", "richEmptyStr")
fun setRichText(view: RichTextView, richTexts:List<RichTextInfo>?, emptyStr: String?) {
    view.setInfo(richTexts, emptyStr, null)
}

@BindingAdapter("bgPaletteColor", "bgPaletteAlpha", "bgGradientColor", "bgOrientation", "bgColorFilter", "bgDefaultColor", requireAll = false)
fun setPaletteViewBg(view: View, imgUrl: String?, alpha: Float?, gradientColor: Int?, orientation: GradientDrawable.Orientation?, colorFilter: ColorUtils.IColorFilter?, defaultColor: Int?) {
    imgUrl ?: return
    val weakReference = WeakReference(view)
    ColorUtils.getPaletteDrawable(imgUrl, alpha, gradientColor, orientation, colorFilter) {
        if (it == null) {
            if (defaultColor != null) {
                weakReference.get()?.background = ColorDrawable(defaultColor)
            }
        } else {
            weakReference.get()?.background = it
        }
    }
}
@BindingAdapter("TvPaletteColor", "TvPaletteAlpha", "TvColorFilter", "TvDefaultColor", requireAll = false)
fun setPaletteTvColor(textView: TextView, imgUrl: String?, alpha: Float?, colorFilter: ColorUtils.IColorFilter?, defaultColor: Int?) {
    imgUrl ?: return
    val weakReference = WeakReference(textView)
    ColorUtils.getPaletteColor(imgUrl, alpha, colorFilter) {
        if (it == null) {
            if (defaultColor != null) {
                weakReference.get()?.setTextColor(defaultColor)
            }
        } else {
            weakReference.get()?.setTextColor(it)
        }
    }
}