package com.lazylite.mod.cache.fixcache;

import java.util.concurrent.locks.ReadWriteLock;

public class LockEntity {

    ReadWriteLock readWriteLock;

    int useCount;

    String fileName;

    boolean isIdle() {
        return useCount <= 0;
    }
}
