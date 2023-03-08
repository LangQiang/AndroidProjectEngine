package cn.godq.applogcat.init

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import cn.godq.applogcat.BuildConfig
import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.utils.isDebug
import cn.godq.applogcat.utils.printAppInfo


/**
 * @author  GodQ
 * @date  2023/3/3 3:22 下午
 */
class ALCInitProvider: ContentProvider() {
    override fun onCreate(): Boolean {
        (context as? Application)?.takeIf {
            isDebug(it) || BuildConfig.FORCE_SHOW_ALC
        }?.apply {
            printAppInfo(this)
            AppLogcat.INSTANCE.init(this)
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}