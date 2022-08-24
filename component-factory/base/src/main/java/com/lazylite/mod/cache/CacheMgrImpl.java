package com.lazylite.mod.cache;

import android.text.TextUtils;

import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.KwDate;
import com.lazylite.mod.utils.KwDirs;
import com.lazylite.mod.utils.KwFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


// by haiping
public final class CacheMgrImpl {
	private static String TAG = "CacheMgrImpl";
	private static String pathPrefBase = KwDirs.getDir(KwDirs.CACHE);
	private static String timeFormatString = "_yyyy_MM_dd_HH_mm_ss";
	private static String cacheFileExt = ".dat";
	private static String delayDeleteFileExt = ".delay";
	private static String[] cacheFileFilter = { cacheFileExt };
	private static String[] delayDeleteFileFilter = { delayDeleteFileExt };

	public void cache(final String category, final int timeGranu, final int timeValue, final String key,
			final String data) {
		try {
			cache(category, timeGranu, timeValue, key, data.getBytes());
		} catch (OutOfMemoryError e) {
		}
	}

	public void cache(final String category, final int timeGranu, final int timeValue, final String key,
			final byte[] data) {
		KwFileUtils.mkdir(pathPrefBase + category);

		File oldFile = getFileByKey(category, key);
		if (oldFile != null && KwFileUtils.isExist(oldFile.getPath())) {
			safeDelete(oldFile.getPath());
		}
		String filePath = createSavePath(category, key, timeGranu, timeValue);

		FileOutputStream fos;
		File f = new File(filePath);
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			try {
				fos.write(data);
			} finally {
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			safeDelete(filePath);
		}
	}

	public String cacheFile(final String category, final int timeGranu, final int timeValue, final String key,
			final String strFile) {
		KwFileUtils.mkdir(pathPrefBase + category);
		String filePath = createSavePath(category, key, timeGranu, timeValue);
		
		if(filePath.equals(strFile))
			return filePath;
		File oldFile = getFileByKey(category, key);
		
		KwFileUtils.fileCopy(strFile, filePath);
		
		if (oldFile != null && KwFileUtils.isExist(oldFile.getPath())) {
			safeDelete(oldFile.getPath());
		}
		return filePath;
	}

	public String read(final String category, final String key) {
		byte[] buffer = readBytes(category, key);
		if (buffer == null) {
			return null;
		}
		try {
			return new String(buffer);
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	public byte[] readBytes(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null || !KwFileUtils.isExist(f.getPath())) {
			return null;
		}

		FileInputStream fis;
		byte[] buffer = null;
		try {
			fis = new FileInputStream(f.getPath());
			try {
				int len = fis.available();
				buffer = new byte[len];
				fis.read(buffer);
			} finally {
				fis.close();
			}
		} catch (Throwable e) { // new byte有可能是OOM异常，要用Throwable跟IOException一起捕获
			e.printStackTrace();
			return null;
		}

		return buffer;
	}

	public String getFile(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null || !KwFileUtils.isExist(f.getPath())) {
			return null;
		}

		return f.getPath();
	}

	public Boolean isOutOfTime(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null || !KwFileUtils.isExist(f.getPath())) {
			return true;
		}

		KwDate date = getExpireDate(f.getPath());
		KwDate now = new KwDate();
		return date.before(now);
	}

	public KwDate getExpireDate(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null || !KwFileUtils.isExist(f.getPath())) {
			return null;
		}
		return getExpireDate(f.getPath());
	}

	public boolean isExist(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null) {
			return false;
		}
		return KwFileUtils.isExist(f.getPath());
	}

	public void delete(final String category, final String key) {
		File f = getFileByKey(category, key);
		if (f == null) {
			return;
		}
		if (KwFileUtils.isExist(f.getPath())) {
			safeDelete(f.getPath());
		}
	}

	public void cleanOutOfDate() {
		ArrayList<File> dirs = KwFileUtils.getDirs(pathPrefBase);
		for (File dir : dirs) {
			File[] all = KwFileUtils.getFiles(dir.getPath(), cacheFileFilter);

			if (all != null) {
				for (File f : all) {
					String path = f.getPath();
					KwDate date = getExpireDate(path);
					int totalTime = getTotalTimeSecond(path);
//					if (totalTime < KwDate.T_DAY * 30) {
//						totalTime = KwDate.T_DAY * 30;
//					}
					date.increase(totalTime);
					if (date.before(new KwDate())) {
						safeDelete(f.getPath());
					}
				}
			}
		}
	}


	public void cleanCategory(final String category) {
		String path = pathPrefBase;
		if (category != null && !TextUtils.isEmpty(category)) {
			path += category;
		}
		safeDelete(path);
	}

	public long getCategorySize(final String category) {
		return KwFileUtils.getDirSize(pathPrefBase + category);
	}
	
	public long getDirectorySize(final String directory) {
		return KwFileUtils.getDirSize(directory);
	}

	public void cleanDelayDeletFiles() {
		ArrayList<File> dirs = KwFileUtils.getDirs(pathPrefBase);
		for (File dir : dirs) {
			File[] all = KwFileUtils.getFiles(dir.getPath(), delayDeleteFileFilter);

			// 李建衡：防止因为权限、io失败等原因造成all为null时，引起的崩溃
			if (all != null) {
				for (File f : all) {
					f.delete(); // 这里再删不掉就没招了OMG
				}
			}
		}
	}

	// category/hashcode.totaltime.expire.dat
	@SuppressWarnings("deprecation")
	private String createSavePath(final String category, final String key, final int timeGranu, final int timeValue) {
		KwDate date = new KwDate();
		date.increase(timeGranu, timeValue);
		switch (timeGranu) {
		case KwDate.T_YEAR:
			date.setMonth(0);
		case KwDate.T_MONTH:
			date.setDate(0);
		case KwDate.T_DAY:
			date.setHours(0);
		case KwDate.T_HOUR:
			date.setMinutes(0);
		case KwDate.T_MINUTE:
			date.setSeconds(0);
		default:
			break;
		}
		
		StringBuilder builder = new StringBuilder(pathPrefBase);
		builder.append(category).append(File.separator);
		builder.append(key.hashCode()).append(".").append(date.sub(new KwDate(),KwDate.T_SECOND));
		builder.append(date.toFormatString(timeFormatString)).append(cacheFileExt);
		return builder.toString();
	}

	private File getFileByKey(final String category, final String key) {
		File[] files = KwFileUtils.getFilesByRegex(pathPrefBase + category, createFilePattern(key), null);
		if (files != null && files.length > 0) {
			return files[0];
		}
		return null;
	}

	// hashcode.totaltime.expire.dat
	private String createFilePattern(final String key) {
		return "\\Q" + key.hashCode() + "\\E\\.\\d.+_\\d{4}(_\\d{2}){5}+\\" + cacheFileExt;
	}

	private KwDate getExpireDate(final String filePathString) {
		String strTimeString = filePathString.substring(filePathString.length() - timeFormatString.length()
				- cacheFileExt.length(), filePathString.length() - cacheFileExt.length());
		KwDate date = new KwDate();
		date.fromString(strTimeString, timeFormatString);
		return date;
	}

	private int getTotalTimeSecond(final String filePathString) {
		String strTimeString = filePathString.substring(0, filePathString.length() - timeFormatString.length()
				- cacheFileExt.length());
		String totalTimeStr = KwFileUtils.getFileExtension(strTimeString); // 罪过
		int totalTime = 0;
		if(false == TextUtils.isEmpty(totalTimeStr)&&TextUtils.isDigitsOnly(totalTimeStr)){
			try {
				totalTime = Integer.parseInt(totalTimeStr);
			} catch (Exception e) {
				LogMgr.e(TAG, e);
			}
		}
		return totalTime;
	}

	private KwDate getSaveDate(final String filePathString) {
		KwDate expireDate = getExpireDate(filePathString);
		expireDate.decrease(getTotalTimeSecond(filePathString));
		return expireDate;
	}

	private Random rand = new Random(System.currentTimeMillis());

	private void safeDelete(final String path) {
		KwFileUtils.deleteFile(path);
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = KwFileUtils.getFiles(path, cacheFileFilter);
				if (files == null) {
					return;
				}
				for (File f : files) {
					safeDelete(f.getPath());
				}
			} else {
				String currentPath = KwFileUtils.getFilePath(path) + File.separator;
				String fileName = rand.nextInt() + delayDeleteFileExt;
				while (KwFileUtils.isExist(currentPath + fileName)) {
					fileName = rand.nextInt() + fileName;
				}
				file.renameTo(new File(fileName));// 改名都失败的话。。。让Linux情何以堪
			}
		}
	}

	//根据key和url精确匹配
	public File getFileByKeyWithUrl(String category,String key,String url) {
		File[] files=KwFileUtils.getFilesByRegex(pathPrefBase+category, key.hashCode()+"_"+url.hashCode()+"_\\d{4}.+\\"+cacheFileExt,null);
		if(files!=null && files.length>0) {
			return files[0];
		}
		return null;
	}
	//根据key模糊匹配
	public File[] getFilesByKey(String category,String key,boolean isFuzzy){
		File[] files = null;
		if (!isFuzzy) { //精确匹配，考虑时间的
			files=KwFileUtils.getFilesByRegex(pathPrefBase+category, key.hashCode()+"_\\d{4}.+\\"+cacheFileExt,null);
		}else { //模糊匹配，后面带url的
			files=KwFileUtils.getFilesByRegex(pathPrefBase+category, key.hashCode()+".+\\"+cacheFileExt,null);
		}
		return files;
	}
	//组合key和url创建保持的缓存文件名
	private String createSavePathWithUrl(String category,String key,String url,int timeGranu,int timeValue) {
		KwDate date=new KwDate();
		date.increase(timeGranu, timeValue);
		
		String hashKey=key.hashCode()+"_"+url.hashCode()+date.toFormatString(timeFormatString)+cacheFileExt;
		return pathPrefBase+category+File.separator+hashKey;
	}
	
	public String cacheWithUrl(String category,int timeGranu,int timeValue,String key,String url,String data) {
		return cacheWithUrl(category, timeGranu, timeValue, key, url, data.getBytes());
	}
	
	public String cacheWithUrl(String category,int timeGranu,int timeValue,String key,String url,final byte[] data) {
		KwFileUtils.mkdir(pathPrefBase+category);
		
		File oldFile=getFileByKeyWithUrl(category,key,url);
		if(oldFile!=null && KwFileUtils.isExist(oldFile.getPath())) {
			KwFileUtils.deleteFile(oldFile.getPath());
		}
		String filePath=createSavePathWithUrl(category,key,url, timeGranu, timeValue);
		
		FileOutputStream fos;
		File f=new File(filePath);
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return filePath;
	}
	public boolean isOutOfTime(String category,File f) {
		if(f==null || !KwFileUtils.isExist(f.getPath())) return true;
		
		KwDate date=getFileDate(f.getPath());
		return date.before(new KwDate());
	}
	private KwDate getFileDate(String filePathString) {
		String strTimeString = filePathString.substring(filePathString.length()
				- timeFormatString.length() - cacheFileExt.length(),
				filePathString.length() - cacheFileExt.length());
		KwDate date=new KwDate();
		date.fromString(strTimeString, timeFormatString);
		return date;
	}
}
