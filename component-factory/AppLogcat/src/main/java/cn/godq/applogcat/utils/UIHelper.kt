package cn.godq.applogcat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.DisplayMetrics
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import cn.godq.applogcat.mgr.AppLogcat
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object UIHelper {

    private var TITLE_BAR_DP = 25

    private var dateFormat: SimpleDateFormat? = null

    @SuppressLint("LogNotTimber")
    fun safeDismissDialog(dialog: Dialog?) {
        val context = dialog?.context?: return
        AppLogcat.getInstance().log("")
        if (context is Activity && context.isFinishing) {
            Log.e("UIHelper", "Dialog  is your activity running ?")
            return
        }
        try {
            dialog.dismiss()
        } catch (ignore: Exception) {
        }
    }

    @SuppressLint("LogNotTimber")
    fun safeDismissDialog(dialog: DialogInterface?, context: Context?) {
        dialog?: return
        if (context is Activity && context.isFinishing) {
            Log.e("UIHelper", "Dialog  is your activity running ?")
            return
        }
        try {
            dialog.dismiss()
        } catch (ignore: Exception) {
        }
    }

    fun getFormatDate(format: String?, timestamp: Long): String {
        if (dateFormat == null) {
            dateFormat = SimpleDateFormat(format, Locale.CHINA)
        } else {
            dateFormat?.applyPattern(format)
        }
        return dateFormat?.format(Date(timestamp))?: ""
    }

    @JvmStatic
    fun getTitleBarHeight(context: Context?): Int {
        var height = dip2px(TITLE_BAR_DP.toFloat())
        if (context != null) {
            try {
                val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    height = context.resources.getDimensionPixelSize(resourceId)
                }
            } catch (e: Exception) {
                height = dip2px(TITLE_BAR_DP.toFloat())
            }
        }
        return height
    }

    fun px2dip(pxValue: Float): Int {
        val context = AppLogcat.INSTANCE.mContext?: return 0
        val scale = getDensity(context)
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dip2px(dpValue: Float): Int {
        val context = AppLogcat.INSTANCE.mContext?: return 0
        val scale = getDensity(context)
        return (dpValue * scale + 0.5f).toInt()
    }

    private var sDensity = 0f
    private fun getDensity(context: Context): Float {
        if (sDensity == 0f) {
            try {
                val dm = DisplayMetrics()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(dm)
                } else {
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(dm)
                }
                sDensity = dm.density
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sDensity
    }

    fun showToast(toastStr: String?) {
        val context = AppLogcat.INSTANCE.mContext?: return
        runOnUiThread(0, Runnable {
            try {
                val toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT)
                safelyShow(toast)
            } catch (ignored: Exception) {
            }
        })
    }

    private fun safelyShow(toast: Toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            ToastCompat.setSafelyToastHandler(toast)
        }
        toast.show()
    }

    fun parseColor(colorStr: String?, defaultColor: Int): Int {
        return try {
            Color.parseColor(colorStr)
        } catch (e: Exception) {
            defaultColor
        }
    }
}