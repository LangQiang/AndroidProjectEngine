package cn.godq.applogcat.db.log

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LogTable")
data class LogDBEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long? = null,
    @ColumnInfo var tag: String,
    @ColumnInfo var content: String,
    @ColumnInfo var level: Int,
    @ColumnInfo var isMainThread: Boolean,
    @ColumnInfo var isMainProcess: Boolean,
    @ColumnInfo val timestamp: Long,
    @ColumnInfo val optFlag: Long,
    @ColumnInfo val uuid: String,
    @ColumnInfo val bootMark: String,
)
