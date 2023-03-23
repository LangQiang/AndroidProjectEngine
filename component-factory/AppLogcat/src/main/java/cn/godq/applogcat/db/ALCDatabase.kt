package cn.godq.applogcat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.godq.applogcat.db.log.LogDBDao
import cn.godq.applogcat.db.log.LogDBEntity


/**
 * @author  GodQ
 * @date  2023/1/3 2:39 下午
 */
@Database(entities = [LogDBEntity::class], version = ALCDBManager.VERSION, exportSchema = false)
abstract class ALCDatabase : RoomDatabase() {
    abstract fun logDBDao(): LogDBDao
}