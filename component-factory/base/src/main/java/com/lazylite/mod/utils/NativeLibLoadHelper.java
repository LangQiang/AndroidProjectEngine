package com.lazylite.mod.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

// by haiping
public final class NativeLibLoadHelper {

	public static synchronized boolean load(Context context, final String libName) {
		boolean ret = false;
		StringBuilder info = new StringBuilder();
		if (android.os.Build.CPU_ABI.equals("armeabi-v7a")) {
			ret = innerLoad(context, libName + "V7", false, info);
		}
		if (!ret) {
			ret = innerLoad(context, libName, true, info);
		}
		return ret;
	}

	// 只尝试加载libs目录下的libName.so和libNameV7.so
	// 此函数绝对绝对不能抛异常,优先尝试加载V5的
	public static synchronized boolean simpleLoad(final String libName) {
		boolean ret = false;

		try {
			ret = classicLoad(libName);
			if (!ret) {
				ret = classicLoad(libName + "V7");
			}
		} catch (Throwable e) {
			ret = false;
		}

		return ret;
	}

	/**
	 * 只尝试加载libs目录下的libName.so和libNameV7.so
	 * @param libName
	 * @param isFirstV7 true,优先加载V7的
	 * @return
	 */
	public static synchronized boolean simpleLoad(final String libName, boolean isFirstV7) {
		if (isFirstV7) {
			boolean ret = false;
			try {
				//直接加载V7
				ret = classicLoad(libName + "V7");
				Log.i("simpleLoad",libName + "V7"+" load "+ret);
				if (!ret) {
					ret = classicLoad(libName);
					Log.i("simpleLoad",libName+" load "+ret);
				}
			} catch (Throwable e) {
				ret = false;
			}
			return ret;
		}else {
			return simpleLoad(libName);
		}
	}

	public static boolean innerLoad(Context context, final String libName, final boolean fromServer, StringBuilder info) {
		if (libs.contains(libName)) {
			return true;
		}
		boolean success = true;
		do {
			if (classicLoad(libName, info)) {
				break;
			}
			if (loadFromPath(libName, info)) {
				break;
			}
			if (loadFromFiles(context, libName, info)) {
				break;
			}
			if (loadFromSDCard(libName, info)) {
				break;
			}
//			if (fromServer && loadFromServer(libName, info)) {
//				break;
//			}
			success = false; // 泪流满面，建议提示用户把手机扔了吧
		} while (false);

		if (success) {
			libs.add(libName);
		}
		return success;
	}

	private static boolean classicLoad(final String name) {
		try {
			System.loadLibrary(name);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	private static boolean classicLoad(final String name, StringBuilder retInfo) {
		try {
			System.loadLibrary(name);
		} catch (Throwable e) {
			retInfo.append("\n").append(name).append("classicLoad failed:\n");
			return false;
		}
		return true;
	}

	private static boolean loadFromPath(final String name, StringBuilder retInfo) {

		return true;
	}

	public static boolean loadFromAssertsLibs(Context context, final String name) {
		String libName = "lib" + name + ".so";
		try {
			String tar = context.getFilesDir().getAbsolutePath() + File.separator + libName;
			File file = new File(tar);
			if (!file.exists()) {
				InputStream iStream = context.getAssets().open("libs/lib" + name);
				if (iStream==null) {
					return false;
				}
				byte[] buffer = new byte[4096];
				BufferedInputStream bis = new BufferedInputStream(iStream);
				FileOutputStream fos = new FileOutputStream(tar);
				try {
					int len = 0;
					while ((len = bis.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
				} finally {
					bis.close();
					fos.close();
				}
			}
			System.load(tar);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	private static boolean loadFromFiles(Context context, final String name, StringBuilder retInfo) {
		String libName = "lib" + name + ".so";
		try {
			String tar = context.getFilesDir().getAbsolutePath() + File.separator + libName;
			InputStream iStream = context.getAssets().open("libs/lib" + name);
			if (iStream==null) {
				return false;
			}
			byte[] buffer = new byte[4096];
			BufferedInputStream bis = new BufferedInputStream(iStream);
			FileOutputStream fos = new FileOutputStream(tar);
			try {
				int len = 0;
				while ((len = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			} finally {
				bis.close();
				fos.close();
			}
			retInfo.append("\nassert read success");
			System.load(tar);
		} catch (Throwable e) {
			retInfo.append("\nload from assert failed:\n").append(e);
			return false;
		}
		return true;
	}

	private static boolean loadFromSDCard(final String name, StringBuilder retInfo) {
		return true;
	}

	private static boolean loadFromServer(final String name, StringBuilder retInfo) {

		return true;
	}

	private static Set<String> libs = new HashSet<String>();
}
