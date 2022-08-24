package com.lazylite.mod.utils.pagingreq;

/**
 * Created by tiancheng :)
 */
public class Cache {

    int timeGranu;
    int timeValue;
    String savePath;
    //额外需要在缓存key url上追加的变化量key，
    // 比如服务下发时间戳，不需要拿出来对比麻烦，这个变化了，缓存自然就无效
    String additionalKey;

    public Cache(String savePath, int timeGranu, int timeValue) {
        this(savePath, timeGranu, timeValue, null);
    }

    public Cache(String savePath, int timeGranu, int timeValue, String additionalKey) {
        this.timeGranu = timeGranu;
        this.timeValue = timeValue;
        this.savePath = savePath;
        this.additionalKey = additionalKey;
    }
}