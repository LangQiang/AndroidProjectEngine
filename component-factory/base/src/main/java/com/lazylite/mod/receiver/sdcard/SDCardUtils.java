package com.lazylite.mod.receiver.sdcard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// by haiping
public class SDCardUtils {
	public static void init(Context context) {
		try {
			avaliable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		} catch (Exception e) {
			e.printStackTrace();
			avaliable = false;
		}		
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		try {
			context.registerReceiver(receiver, intentFilter);
		} catch (Exception e) {
		}
	}
	
	public static void release() {
		
	}
	
	public static boolean isAvailable() {
		return avaliable;
	}
	
	private static BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.isEmpty(action)) {
				boolean newAvaliable = avaliable;
				if (Intent.ACTION_MEDIA_MOUNTED.equals(action) || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
					try {
						newAvaliable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
					} catch (Exception e) {
						e.printStackTrace();
						newAvaliable = false;
					}
					
				}
				if (newAvaliable != avaliable) {
					avaliable = newAvaliable;
				}
			}
		}
	};

	private static volatile boolean	avaliable;
	
	/**
     * 内置
     */
    public final static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    public final  static String SDCARD_EXTERNAL = "external";

    public final static String SDCARD_USB = "usb";


    // API14以上包括14通过改方法获取 ，全盘扫描不需要关心是否挂载
    public static List<String> getSDCardInfo(Context context) {
    	if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return null;
		}
    	List<String> sdCardInfos = new ArrayList<String>();
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storagePathList != null && storagePathList.length > 0) {
            String mSDCardPath = storagePathList[0];
            sdCardInfos.add(mSDCardPath);
            if (storagePathList.length >= 2) {
                String externalDevPath = storagePathList[1];
                sdCardInfos.add(externalDevPath);
            }
            if(storagePathList.length >= 3){
            	 String externalDevPath = storagePathList[2];
                 sdCardInfos.add(externalDevPath);
            }
        }
        return sdCardInfos;
    
    }


    // API14以上包括14通过改方法获取 ，获取全的媒体目录，带是否挂载
    public static HashMap<File, Boolean> getSDCardState(Context context) {
    	if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return null;
		}
    	HashMap<File, Boolean> sdCardInfos = new HashMap<File, Boolean>();
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
            
            for (String path:storagePathList) {
            	File file = new File(path);
            	boolean isMounted = checkSDCardMount14(context,path);
            	if (isMounted) {
					sdCardInfos.put(file, true);
				}else {
					sdCardInfos.put(file, false);
				}
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sdCardInfos;
    }
    
    /**
     * @Description:判断SDCard是否挂载上,返回值为true证明挂载上了，否则未挂载
     * @Date 2013-11-12
     * @param context 上下文
     * @param mountPoint 挂载点
     */
    protected static boolean checkSDCardMount14(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager, mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
	private final static String STORAGE = "/storage";
	private final static String MNT = "/mnt";
	/**
	 * 返回扫描的根目录
	 * */
	public static File getScanRootFile(){
		File rootFile = new File(STORAGE);//4.2及以上有storage目录的使用storage
		if(!rootFile.exists()){
			rootFile = new File(MNT);//4.2以下没有storage的使用mnt
		}
		return rootFile;
	}
}
