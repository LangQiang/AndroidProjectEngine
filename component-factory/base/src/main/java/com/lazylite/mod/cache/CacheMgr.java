package com.lazylite.mod.cache;

import android.os.Handler;

import com.lazylite.mod.cache.fixcache.LockEntity;
import com.lazylite.mod.cache.fixcache.LockPool;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.threadpool.KwThreadPool;
import com.lazylite.mod.utils.KwDebug;
import com.lazylite.mod.utils.KwDirs;

import java.io.File;

// by海平
// 注意，category只允许用大小写字母和数字
public final class CacheMgr {

	private static final int TYPE_STR = 0;
	private static final int TYPE_BYTE_ARRAY = 1;
	private static final int TYPE_STR_FILE = 2;
	private static final int TYPE_DELETE = 3;
	private static final int TYPE_OUT_OF_TIME = 4;

	private CacheMgrImpl	mgr			= new CacheMgrImpl();

	private LockPool lockPool;

	private CacheMgr() {
		lockPool = new LockPool();
	}

	private static class Inner {
		private static final CacheMgr INSTANCE = new CacheMgr();
	}

	public static CacheMgr getInstance() {
		return Inner.INSTANCE;
	}

	// 缓存String，重名覆盖。timeGranu为时间粒度
	// 比如:timeGranu=T_HOUR，timeValue=3，缓存耗时间就是3小时
	// 注意，粒度同时也是精确度
	// 比如：1 T_DAY可能是24小时，也可能只有一秒钟，即日期变更一天就算一天，从23：59变为00：00就是一天
	// 其它粒度也一样
	// 所以必要的情况下要用精确的粒度，比如24 T_HOUR
	public void cache(final String category, final int timeGranu,
			final int timeValue, final String key, final String data) {
		write(category, timeGranu, timeValue, key, TYPE_STR, data);
	}

	// 缓存byte数组
	public void cache(final String category, final int timeGranu,
			final int timeValue, final String key, final byte[] data) {
		write(category, timeGranu, timeValue, key, TYPE_BYTE_ARRAY, data);
	}

	// 缓存文件，把strFile文件拷贝到缓存里
	public void cacheFile(final String category, final int timeGranu,
			final int timeValue, final String key, final String strFile) {
		write(category, timeGranu, timeValue, key, TYPE_STR_FILE, strFile);
	}

	public void delete(final String category, final String key) {
		write(category, 0, 0, key, TYPE_DELETE, null);
	}

	private void write(final String category, final int timeGranu,
					   final int timeValue, final String key, int type, Object data) {

		LockEntity lockEntity = lockPool.obtainAndLock(category + key, LockPool.TYPE_WRITE);

		try {

			if (type == TYPE_STR) {
				mgr.cache(category, timeGranu, timeValue, key, (String) data);
			} else if (type == TYPE_BYTE_ARRAY) {
				mgr.cache(category, timeGranu, timeValue, key, (byte[])data);
			} else if (type == TYPE_STR_FILE) {
				mgr.cacheFile(category, timeGranu, timeValue, key, (String)data);
			} else if (type == TYPE_DELETE) {
				mgr.delete(category, key);
			}

		} finally {
			lockPool.unlock(lockEntity, LockPool.TYPE_WRITE);
		}

	}

	// 读缓存String如果不存在，返回null（超期但没有被清理的时候正常返回内容）
	public String read(final String category, final String key) {
		return read(category, key, TYPE_STR);
	}

	// 读取缓存byte[]如果不存在，返回null（超期但没有被清理的时候正常返回内容）
	public byte[] readBytes(final String category, final String key) {
		return read(category, key, TYPE_BYTE_ARRAY);
	}

	// 得到缓存文件路径，如果不存在，返回null（超期但没有被清理的时候正常返回文件路径）
	public String getFile(final String category, final String key) {
		return read(category, key, TYPE_STR_FILE);
	}

	// 是否过期，不存在的话也认为是过期
	public boolean isOutOfTime(final String category, final String key) {
		Boolean ret = read(category, key, TYPE_OUT_OF_TIME);
		return ret == null ? true : ret;
	}

	@SuppressWarnings("unchecked")
	private <T> T read(final String category, final String key, int type) {

		LockEntity lockEntity = lockPool.obtainAndLock(category + key, LockPool.TYPE_READ);

		try {
			if (type == TYPE_STR) {
				return (T) mgr.read(category, key);
			} else if (type == TYPE_BYTE_ARRAY) {
				return (T) mgr.readBytes(category, key);
			} else if (type == TYPE_STR_FILE) {
				return (T) mgr.getFile(category, key);
			} else if (type == TYPE_OUT_OF_TIME) {
				return (T) mgr.isOutOfTime(category, key);
			}
			return null;

		} finally {
			lockPool.unlock(lockEntity, LockPool.TYPE_READ);
		}
	}


	// 遍历所有缓存，彻底删除过期内容
	public void cleanOutOfDate() { //理论上也有同步问题，概率很小
		LogMgr.d("CacheMgr", "[cleanCategory] clean all category");
		mgr.cleanOutOfDate();
	}

	// 不管是否过期，均清理
	public void cleanCategory(final String category) { //理论上也有同步问题，概率很小
		LogMgr.d("CacheMgr", "[cleanCategory] clean " + category);
		mgr.cleanCategory(category);
	}

	public interface GetCategorySizeListener {
		void onGetCategorySizeListener(final String[] categories, final long[] sizes);
	}

	// 传空获取整个缓存所有分类总大小
	public void asyncGetCategorySize(final String[] categories, final GetCategorySizeListener listener,
			final Handler tarHandler) {
		KwDebug.classicAssert(listener != null);
		KwThreadPool.runThread(KwThreadPool.JobType.IMMEDIATELY, new Runnable() {
			@Override
			public void run() {
				long[] result = new long[categories.length];
				for (int i = 0; i < categories.length; ++i) {
					if(categories[i] != null && categories[i].equals(KwDirs.getDir(KwDirs.AUTODOWN_CACHE))){
						result[i] = mgr.getDirectorySize(KwDirs.getDir(KwDirs.AUTODOWN_CACHE));
					} else {
					    result[i] = mgr.getCategorySize(categories[i]);
					}
				}
				final long[] sizes=result;
				MessageManager.getInstance().asyncRunTargetHandler(tarHandler, new MessageManager.Runner() {
					@Override
					public void call() {
						listener.onGetCategorySizeListener(categories, sizes);
					}
				});
			}
		});
	}

	public void cleanDelayDeletFile() {
		mgr.cleanDelayDeletFiles();
	}

	public synchronized void cacheWithUrl(String category,int timeGranu,int timeValue,String key,String url,String data) {
		mgr.cacheWithUrl(category,timeGranu, timeValue, key,url, data);
	}
	//根据key值模糊匹配出所有的文件
	public synchronized File[] getFiles(String category,String key,boolean isFuzzy) {
		return mgr.getFilesByKey(category,key,isFuzzy);
	}
	// 缓存byte数组
	public synchronized String cacheFileWithUrl(final String category, final int timeGranu,
				final int timeValue, final String key,String url, final byte[] data) {
		return mgr.cacheWithUrl(category,timeGranu, timeValue, key,url, data);
	}
	//根据key和url精确查找
	public synchronized File getFileWithUrl(String category,String key,String url) {
		return mgr.getFileByKeyWithUrl(category,key,url);
	} 
	
	//是否过期，不存在的话也认为是过期
	public synchronized boolean isOutOfTime(String category,File file) {
		return mgr.isOutOfTime(category,file);
	} 

}
