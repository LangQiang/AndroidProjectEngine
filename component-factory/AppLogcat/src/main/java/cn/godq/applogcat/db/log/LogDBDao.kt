package cn.godq.applogcat.db.log

import androidx.room.*


/**
 * @author  GodQ
 * @date  2023/1/3 2:27 下午
 */

@Dao
interface LogDBDao {

    @Query("SELECT * FROM LogTable WHERE logId < :logId ORDER BY logId DESC LIMIT :count")
    fun queryAllLogs(logId: Long, count: Long): List<LogDBEntity>?

    @Query("SELECT * FROM LogTable WHERE tag = :tag AND logId < :logId ORDER BY logId DESC LIMIT :count")
    fun queryLogsByTag(tag: String, logId: Long, count: Long): List<LogDBEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg logDBEntity: LogDBEntity)

    @Query("DELETE FROM LogTable WHERE timestamp < :timestamp")
    fun delete(timestamp: Long)
}