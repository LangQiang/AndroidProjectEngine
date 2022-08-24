package com.lazylite.mod.cache.fixcache;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockPool {

    public static final int TYPE_READ = 1;

    public static final int TYPE_WRITE = 2;

    private static final int PERMANENT_SIZE = 3;

    private List<LockEntity> lockEntries = new LinkedList<>();

    public void unlock(LockEntity lockEntity, int lockType) {
        execute(lockEntity, lockType == TYPE_READ, false);
    }

    @NonNull
    private LockEntity getLock(String fileName) {
        LockEntity lockEntity = find(fileName);
        if (lockEntity == null) {
            lockEntity = new LockEntity();
            lockEntity.fileName = fileName;
            lockEntity.readWriteLock = new ReentrantReadWriteLock();
            lockEntries.add(lockEntity);
        } else {
            lockEntity.fileName = fileName;
        }

        return lockEntity;
    }

    private LockEntity find(String fileName) {
        LockEntity idle = null;
        LockEntity inUse = null;
        for (LockEntity lockEntity : lockEntries) {
            if (lockEntity.fileName.equals(fileName)) {
                inUse = lockEntity;
                break;
            } else if (lockEntity.isIdle()) {
                idle = lockEntity;
            }
        }
        return inUse != null ? inUse : idle;
    }

    public LockEntity obtainAndLock(String file, int lockType) {
        LockEntity lock;
        synchronized (this) {
            lock = getLock(file);
            lock.useCount ++;
        }

        execute(lock, lockType == TYPE_READ, true);

        return lock;
    }

    private void execute(LockEntity lockEntity, boolean isRead, boolean isLock) {

        Lock lock = isRead ? lockEntity.readWriteLock.readLock() : lockEntity.readWriteLock.writeLock();

        if (isLock) {
            lock.lock();
        } else {
            lock.unlock();
            synchronized (this) {
                lockEntity.useCount --;
                shrinkMap(lockEntity);
            }
        }
    }

    private void shrinkMap(LockEntity lockEntity) {
        if (lockEntity.useCount != 0 || lockEntries.size() <= PERMANENT_SIZE) {
            return;
        }
        lockEntries.remove(lockEntity);
    }

}
