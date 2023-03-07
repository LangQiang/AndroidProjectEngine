package cn.godq.applogcat.ui


/**
 * @author  GodQ
 * @date  2023/3/6 5:41 下午
 */
class LogcatRepository {

    private val allLogs = ArrayList<LogcatEntity>()

    fun insertLog(log: LogcatEntity) {
        allLogs.add(log)
    }

    fun getLogsByTag(tag: String): List<LogcatEntity> {
        return ArrayList<LogcatEntity>().apply {
            allLogs.forEach {
                if (it.tag == tag || tag == LogcatVm.DEFAULT_TAG) {
                    this.add(it)
                }
            }
        }
    }
}