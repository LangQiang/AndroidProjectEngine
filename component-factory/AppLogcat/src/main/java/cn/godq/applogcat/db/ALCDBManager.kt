package cn.godq.applogcat.db

import android.content.Context
import androidx.room.Room
import cn.godq.applogcat.db.log.LogDBDao


/**
 * @author  GodQ
 * @date  2023/1/3 2:42 下午
 *
 * update
 * private val MIGRATION_1_2 = Migration(1, 2) {
 *     it.execSQL("CREATE TABLE XXXTable (xxx TEXT NOT NULL, zzz INTEGER, PRIMARY KEY(xxx))")
 * }
 * addMigrations(MIGRATION_1_2)
 */
object ALCDBManager {

    const val VERSION = 1

    private var db: ALCDatabase? = null

    fun init(context: Context) {
        db = Room.databaseBuilder(context, ALCDatabase::class.java, "app_log_cat_db")
            .build()
    }

    fun getLogDBDao(): LogDBDao? = db?.logDBDao()

}