package com.lazylite.mod.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lazylite.mod.App;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;
import com.lazylite.mod.log.LogMgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import timber.log.Timber;

// by haiping
public final class DeviceInfo {

	public static final String SEC_APP_CONFIG = "appconfig";
	public static final String KEY_APP_ANDROID_ID = "android_id";
	public static final String KEY_APP_RANDOM_DEVICE_ID = "random_device_id";
	public static final String KEY_APP_NEW_DEVICE_ID = "new_device_id";
	public static final String KEY_APP_DEVICE_ID = "device_id";

	//极光获取phone
	public static String sPhoneNum;
	// 手机IMEI号
	public static String DEVICE_ID;

	//Android ID
	private static String ANDROID_ID;

	// 屏幕宽度（像素）WelcomeActivity onResume之后才有值
	public static int WIDTH;

	// 屏幕高度（像素）WelcomeActivity onResume之后才有值
	public static int HEIGHT;

	// 屏幕密度（0.75 / 1.0 / 1.5）WelcomeActivity onResume之后才有值
	public static float DENSITY;

	// 屏幕密度DPI（120 / 160 / 240）WelcomeActivity onResume之后才有值
	public static int DENSITY_DPI;

	public static float SCALED_DENSITY;

	// 是否插入耳机
	public static boolean IS_EARPHONE;

	// 秀场直播是否是全屏
	public static boolean IS_SHOW_FULL_SCREEN;

	// 小米的MIUI系统版本
	private static int mMiuiVersion = -1;

	// 总内存(B为单位）
	private static long mTotalMem = -0L;
	//剩余内存（MB为单位）
	public static long AVAILABLE_MEM;
	//总ROM（MB为单位）
	public static long TOTAL_ROM;
	//剩余ROM（MB为单位）
	public static long AVAILABLE_ROM;

	// cpu频率(MHz为单位)
	private static int mCpuHz = 0;
	//是否支持neon指令集
	//public static boolean IS_NEON_SUPPORT = false;
	//运营商
	public static String OPERATOR;

	//机器型号
	public static final int UNKNOWN = 0;

	public static final int VIVO = 1;

	public static final int OPPO = 2;

	public static final int XIAOMI = 3;

	public static final int MEIZU = 4;

	public static final int SAMSUNG = 5;

	public static final int HUAWEI = 6;

	private static int mRomType = -1;

	private static int isFlyme4Above = -1;//-1代表未知 ,0 false 1 true
	private static int isFlyme6Above = -1;
	private static int isAndroidMOrAbove = -1;

	private static String MODEL;//机型
	private static String HARDWARE;//手机平台（CPU类型）

	private static boolean isInited;

	private DeviceInfo() {

	}

	@SuppressWarnings("deprecation")
	public static void init() {
		if (isInited) {
			return;
		}
		Context context = App.getInstance().getApplicationContext();

		//初始化基本的设备信息
		getDeviceId();
		//初始化分辨率
		initScreenInfo(context);

		try {
			AudioManager localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			IS_EARPHONE = localAudioManager.isWiredHeadsetOn();
		} catch (Exception e) {
			e.printStackTrace();
			IS_EARPHONE = false;
		}
		if (!IS_EARPHONE) {
			IS_EARPHONE = hasBluetoothAudioDevice();
		}
		AVAILABLE_MEM = getAvailMemory(App.getInstance().getApplicationContext());
		getRomSize();
//		OPERATOR = KwFlowUtils.getSimCardValue(App.getInstance().getApplicationContext());
		isInited = true;
	}

	@SuppressLint("NewApi")
	private static boolean hasBluetoothAudioDevice() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			boolean a2dp = false, headset = false;
			try {
				a2dp = adapter.getProfileConnectionState(BluetoothProfile.A2DP) != BluetoothProfile.STATE_DISCONNECTED;
				headset = adapter.getProfileConnectionState(BluetoothProfile.HEADSET) != BluetoothProfile.STATE_DISCONNECTED;
			} catch (Throwable e) {
			}

			return a2dp || headset;
		}else{
			return false;
		}
	}



	//必须申请权限：Manifest.permission.READ_PHONE_STATE
	//初始化基本的设备信息
	public static void initDeviceInfo(Context context) {
		initScreenInfo(context);
		getDeviceId();
	}

	public static String getDeviceId() {
		if (TextUtils.isEmpty(AppInfo.INSTALL_SOURCE) || TextUtils.isEmpty(AppInfo.VERSION_NAME)) {
			//跨进程初始化
//			AppInfo.initParas();
		}

		if (!TextUtils.isEmpty(DEVICE_ID)) {
			return DEVICE_ID;
		}
		DEVICE_ID = getOnlyDeviceId();
		return DEVICE_ID;
	}

	public static String getAndroidId() {
//		if (!ConfMgr.getBoolValue(IConfDef.SEC_DEFAULT, IConfDef.KEY_PROTOCOL_DIALOG_IS_SHOWED, false) && App.isDebug()) {
//			throw new RuntimeException("不能在用户点击同意隐私协议弹窗之前获取敏感信息【AndroidId】");
//		}
		if (!TextUtils.isEmpty(ANDROID_ID)) {
			return ANDROID_ID;
		}
		ANDROID_ID = ConfMgr.getStringValue(SEC_APP_CONFIG, KEY_APP_ANDROID_ID, null);
		if (!TextUtils.isEmpty(ANDROID_ID)) {
			LogMgr.i("getAndroidId", "ANDROID_ID ConfMgr:" + ANDROID_ID);
			return ANDROID_ID;
		}
		try {
			ANDROID_ID = Settings.System.getString(App.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
			LogMgr.i("getAndroidId", "ANDROID_ID System:" + ANDROID_ID);
		} catch (Exception e) {
		}

		ANDROID_ID = checkExceptionDeviceId(ANDROID_ID);
		ConfMgr.setStringValue(SEC_APP_CONFIG, KEY_APP_ANDROID_ID, ANDROID_ID, false);

		return ANDROID_ID;
	}

	public static String getRandomDeviceId() {
		String newDeviceId = ConfMgr.getStringValue(SEC_APP_CONFIG, KEY_APP_RANDOM_DEVICE_ID, null);
		if (TextUtils.isEmpty(newDeviceId)) {
			newDeviceId = randCreateDeviceId();
			ConfMgr.setStringValue(SEC_APP_CONFIG, KEY_APP_RANDOM_DEVICE_ID, newDeviceId, false);
		}
		return newDeviceId;
	}

	public static String getOnlyDeviceId() {
		//优先NEWDEVICEID，此部分功能服务端已经屏蔽了
		String newDeviceId = ConfMgr.getStringValue(SEC_APP_CONFIG, KEY_APP_NEW_DEVICE_ID, null);
		if (!TextUtils.isEmpty(newDeviceId)) {
			return newDeviceId;
		}

		//获取本地保存的设备id，防止vivo这类的手机做次数限制而发生变化 by xudong.wang
		String deviceId = readDeviceId();
		if (!TextUtils.isEmpty(deviceId)) {
			return deviceId;
		}

		try { //首次启动时候进行初始化，正常只会走一次
			deviceId = getDeviceIdCompat();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//异常处理,包括检测mac地址
		deviceId = checkExceptionDeviceId(deviceId);

		//保存到本地配置，供后续使用
		saveDeviceId(deviceId);
		return deviceId;
	}

	/**
	 * 适配Android Q，Q上使用AndroidId代替DeviceId
	 */
	private static String getDeviceIdCompat() {
		Context context = App.getInstance().getApplicationContext();
		String deviceId = "";
		try {
			/*if (Permission.checkSelfPermission(App.getInstance(),new String[]{TransformText.READ_PHONE_STATE})) {
				deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			}*/
			if (TextUtils.isEmpty(deviceId)) {
				deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
		} catch (Exception ignored) {
		}
		return deviceId;
	}


	public static void initScreenInfo(Activity activity) {
		if (activity == null) return;
		try {
			DisplayMetrics dm = new DisplayMetrics();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
			} else {
				activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			}
			WIDTH = Math.min(dm.widthPixels, dm.heightPixels);
			HEIGHT = Math.max(dm.widthPixels, dm.heightPixels);
			DENSITY = dm.density;
			LogMgr.d("DENSITY", "initScreenInfo--" + DENSITY);
			DENSITY_DPI = dm.densityDpi;
			SCALED_DENSITY = dm.scaledDensity;
			LogMgr.d("DeviceInfo", "DENSITY:"+DENSITY+" WIDTH:"+WIDTH+" HEIGHT:"+HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getMiuiVersionCode(){
		try {
			//非小米系统不用读配置,返回0
			if (getRomType(App.getInstance()) != XIAOMI){
				return 0;
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
				String versionCodeName = getSystemProperty("ro.miui.ui.version.code");
				int versionCode = Integer.parseInt(versionCodeName);
				//实际的版本号比返回的少2，比如MIUI10 是 8
				return versionCode + 2;
			} else {
				String temp = getBuildProperty("ro.miui.ui.version.name", null);
				if (!TextUtils.isEmpty(temp)) {
					int versionNum=0;

						int length= temp.length();
						temp = temp.substring(1,length);
						versionNum = Integer.valueOf(temp);
						return versionNum;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean is360() {
		String temp = getBuildProperty("ro.build.uiversion", "");
		return !TextUtils.isEmpty(temp) && temp.contains("360");
	}

	public static String getBuildProperty(String key, String defaultValue) {
		String val = defaultValue;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
			val = getSystemProperty(key,defaultValue);  //使用反射FileNotFoundException: /system/build.prop (Permission denied)处理异常情况
		}else{
			Properties property = new Properties();
			try {
				property.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
				val = property.getProperty(key, defaultValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return val;
	}

	private static String getSystemProperty(String key, String defaultValue) {
		try {
			Class<?> clz = Class.forName("android.os.SystemProperties");
			Method get = clz.getMethod("get", String.class, String.class);
			return (String) get.invoke(clz, key, defaultValue);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static void initScreenInfo(Context context) {
		if (WIDTH == 0) {
			try {
				DisplayMetrics dm = new DisplayMetrics();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
				} else {
					((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
				}
				WIDTH = Math.min(dm.widthPixels, dm.heightPixels);
				HEIGHT = Math.max(dm.widthPixels, dm.heightPixels);
				DENSITY = dm.density;
				LogMgr.d("DENSITY", "initScreenInfo--" + DENSITY);
				DENSITY_DPI = dm.densityDpi;
				SCALED_DENSITY = dm.scaledDensity;
				LogMgr.d("DeviceInfo", "DENSITY:"+DENSITY+" WIDTH:"+WIDTH+" HEIGHT:"+HEIGHT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String randCreateDeviceId() {
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder();
		int i = 0;
		i = rand.nextInt(5);
		i = i == 0 ? 1 : i;
		i *= 10000;
		i = rand.nextInt(i) + i;
		sb.append(i);

		i = rand.nextInt(5) + 5;
		i *= 100000;
		i = rand.nextInt(i) + i;
		sb.append(i);
		return sb.toString();
	}

	private static String readDeviceId() {
		String id = ConfMgr.getStringValue(SEC_APP_CONFIG, KEY_APP_DEVICE_ID, null);
		if (TextUtils.isEmpty(id)) {
			String devIdPath = KwDirs.getDir(KwDirs.SETTING) + File.separator + "device_id.text";
			id = KwFileUtils.fileRead(devIdPath);
			if (!TextUtils.isEmpty(id)) {
				ConfMgr.setStringValue(SEC_APP_CONFIG, KEY_APP_DEVICE_ID, id, false);
			}
		}
		return id;
	}

	private static void saveDeviceId(String id) {
		//保存数据库
		ConfMgr.setStringValue(SEC_APP_CONFIG, KEY_APP_DEVICE_ID, id, false);
		//保存本地文件
		String devIdPath = KwDirs.getDir(KwDirs.SETTING) + File.separator + "device_id.text";
		KwFileUtils.fileWrite(devIdPath,id);
	}

	//对id异常的处理 by xudong.wang
	private static String checkExceptionDeviceId(String id){
		if (TextUtils.isEmpty(id) || "012345678912345".equals(id) || "111111111111111".equals(id) || "00000000".equals(id)
				|| "000000000000000".equals(id) ||"0000000000000000".equals(id) || "00:00:00:00:00:00".equals(id)
				|| "02:00:00:00:00:00".equals(id) || "Unknown".equalsIgnoreCase(id) || "9774d56d682e549c".equalsIgnoreCase(id)){
			//读取配置idiuxiu
			return randCreateDeviceId();
		}
		return id;
	}

	//总内存(B为单位）
	public static long getTotalMemory() {
		if (mTotalMem == 0L) {
			String[] arrayOfString;
			try {
				FileReader localFileReader = new FileReader("/proc/meminfo");
				BufferedReader localBufferedReader = new BufferedReader(
						localFileReader, 1024);
				try {
					String firstLine = localBufferedReader.readLine();
					if (firstLine == null) {
						mTotalMem = -1L;
					}else {
						arrayOfString = firstLine.split("\\s+");
						mTotalMem = (long) (Integer.valueOf(arrayOfString[1]).intValue()) * 1024;
					}
				} finally {
					localBufferedReader.close();
				}
			} catch (Throwable e) {
				mTotalMem = -1L;
			}
		}
		return mTotalMem;
	}

	private static long getAvailMemory(Context context){
		try {
			// 获取android当前可用内存大小
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
			am.getMemoryInfo(mi);
			return mi.availMem/(1024*1024);//MB单位
		} catch (Exception e) {

		}
		return  0;
	}

	private static void getRomSize() {
		try {
			//调用该类来获取磁盘信息（而getDataDirectory就是内部存储）
			final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
			long tcounts = statFs.getBlockCount();//总共的block数
			long counts = statFs.getAvailableBlocks() ; //获取可用的block数
			long size = statFs.getBlockSize(); //每格所占的大小，一般是4KB==
			AVAILABLE_ROM = counts * size / 1024 / 1024;//可用内部存储大小
			TOTAL_ROM = tcounts *size / 1024 / 1024; //内部存储总大小
		} catch (Exception e) {

		}
	}

	public static Set<BluetoothDevice> getBluetoothDevice() {
		BluetoothAdapter bluetoothAdapter;
		try {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
				return null;
			}
		} catch (SecurityException exception){
			//没有蓝牙权限
			return null;
		}
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		if(pairedDevices == null || pairedDevices.size() <= 0){
			return null;
		}
		return pairedDevices;
	}

	/**
	 * 是否是模拟器，
	 * 没有蓝牙或者没有光传感器
	 * @return
	 */
	public static boolean isSimulator() {
		return notHasBlueTooth() || notHasLightSensorManager();
	}


	/**
	 * 判断是否存在光传感器来判断是否为模拟器
	 * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
	 * @return true 为模拟器
	 */
	public static boolean notHasLightSensorManager() {
		SensorManager sensorManager = (SensorManager) App.getInstance().getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
		if (null == sensor8) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断蓝牙是否有效来判断是否为模拟器
	 *
	 * @return true 为模拟器
	 */
	public static boolean notHasBlueTooth() {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if (ba == null) {
			return true;
		} else {
			// 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
			String name = ba.getName();
			if (TextUtils.isEmpty(name)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Map<String,String> getBluetoothInfo() {
		//蓝牙设备信息
		String bluetoothName = "";
		String bluetoothType = "";
		Map<String,String> map = new HashMap<>();
		try {
			Set<BluetoothDevice> bluetoothDevices = DeviceInfo.getBluetoothDevice();
			if (bluetoothDevices != null) {
				int index = 0;
				for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
					if (index > 0) {
						bluetoothName += ";" + bluetoothDevice.getName();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
							bluetoothType += ";" +bluetoothDevice.getType();
						}
					}else {
						bluetoothName += bluetoothDevice.getName();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
							bluetoothType += bluetoothDevice.getType();
						}
					}
					index++;
				}
			}
		} catch (Exception e) {

		}
		//与播歌日志保持一致
		map.put("BLUETOOTH_NAME",bluetoothName);
		map.put("BLUETOOTH_TYPE",bluetoothType);
		return map;
	}
	
	/**
	 * 返回内存大小，以MB为单位
	 * @return
	 */
	public static long getTotalMemoryForMB(){
		return getTotalMemory() / 1024 /1024;
	}

	public static int getMaxCPUMHz() {
		if (mCpuHz == 0) {
			mCpuHz = getMaxCpuFreq();
		}
		return mCpuHz;
	}
	
	private static int getMaxCpuFreq() {
		// 获取cpu最大频率
	    int result = -1;
	    ProcessBuilder cmd;
		InputStream is = null;
	    try {
	    	for (int i = 0; i < getNumCores(); i++) {
	    		try {
					String[] args = { "/system/bin/cat",
							"/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq" };
					cmd = new ProcessBuilder(args);
					Process process = cmd.start();
					is = process.getInputStream();
					byte[] te = new byte[24];
					String cmdResult = "";
					while (is.read(te) != -1) {
						cmdResult += new String(te);
					}
					float core = Float.valueOf(cmdResult);
					core = core / 1000;  //把khz转为MHz
					result = Math.max(result, (int) core);
				} catch (Throwable throwable) {

				}
			}
	    } catch (Error | Exception e) {
	    } finally {
			IOUtils.closeIoStream(is);
		}
		return result;
	}
    
	
	public static int getNumCores() {// 获取cpu核心数
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            if (Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }

	    }
	    try {
	        File dir = new File("/sys/devices/system/cpu/");
	        File[] files = dir.listFiles(new CpuFilter());
	        return files.length;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 1;
	    }
	}

	/**
	 * 获取设备性能评级
	 * @return grade
	 * @see #GRADE_LOW
	 * @see #GRADE_MIDDLE
	 * @see #GRADE_HIGH
	 * */
	public static final int GRADE_UNKNOWN = -1;
	public static final int GRADE_LOW = 1;
	public static final int GRADE_MIDDLE = 3;
	public static final int GRADE_HIGH = 5;

	private static Integer sDeviceGrade = null;

	public static int getDevicePerformanceGrade() {

		if (sDeviceGrade == null) {
			float finalScore = devicePerformanceScore();
			if (finalScore == -1) {
				sDeviceGrade = GRADE_UNKNOWN;
			} else {
				if (finalScore < 2.5) {
					sDeviceGrade = GRADE_LOW;
				} else if (finalScore < 4) {
					sDeviceGrade = GRADE_MIDDLE;
				} else {
					sDeviceGrade = GRADE_HIGH;
				}
			}
		}

		return sDeviceGrade;
	}

	public static float devicePerformanceScore() {
		int maxCPUHz = DeviceInfo.getMaxCPUMHz();
		long ram = DeviceInfo.getTotalMemoryForMB();
		int CPUCoreNum = DeviceInfo.getNumCores();

		int cpuHzScore = Math.min(Math.max(maxCPUHz / 500 - 1, -1), 5);

		int ramScore = Math.min((int) ram / 2000, 5);

		int coreNumScore = Math.min(CPUCoreNum / 2 + 1, 5);

		Timber.e("cpuHzScore: " + cpuHzScore + " ramScore: " + ramScore + "coreNumScore: " + coreNumScore);

		if (cpuHzScore < 0 || ramScore < 0) {
			return -1;
		}

		return cpuHzScore * 0.4f + ramScore * 0.3f + coreNumScore * 0.3f;
	}


	/**
	 *
	 * [获取cpu类型和架构]
	 *
	 * @return
	 */
	public static boolean getCpuNeonSupport() {
		InputStream is = null;
		InputStreamReader ir = null;
		BufferedReader br = null;
		try {
			is = new FileInputStream("/proc/cpuinfo");
			ir = new InputStreamReader(is);
			br = new BufferedReader(ir);
			String nameFeatures = "Features";
			while (true) {
				String line = br.readLine();
				String[] pair = null;
				if (line == null) {
					break;
				}
				pair = line.split(":");
				if (pair.length != 2) {
					continue;
				}
				String key = pair[0].trim();
				String val = pair[1].trim();
				if (key.compareToIgnoreCase(nameFeatures) == 0) {
					if (val.contains("neon")) {
						//IS_NEON_SUPPORT = true;
						return true;
					}
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeIoStream(br);
			IOUtils.closeIoStream(ir);
			IOUtils.closeIoStream(is);
		}
		return false;
	}

	public static boolean isFlymeV4OrAbove() {
		String displayId = Build.DISPLAY;
		if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
			String[] displayIdArray = displayId.split(" ");
			for (String temp : displayIdArray) {
				//版本号4以上，形如4.x.
				if (temp.matches("^[4-9]\\.(\\d+\\.)+\\S*")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isFlymeV6OrAbove() {
		String displayId = Build.DISPLAY;
		if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
			String[] displayIdArray = displayId.split(" ");
			for (String temp : displayIdArray) {
				//版本号4以上，形如4.x.
				if (temp.matches("^[6-9]\\.(\\d+\\.)+\\S*")) {
					return true;
				}
			}
		}
		return false;
	}


	private static boolean internalIsAndroidMOrAbove() {
		if (Build.VERSION.SDK_INT >= 23) {
			return true;
		}
		return false;
	}

	public static boolean isAndroidMOrAbove(){
		if (isAndroidMOrAbove == -1){
			isAndroidMOrAbove = internalIsAndroidMOrAbove() ? 1 : 0;
		}
		return isAndroidMOrAbove == 1;
	}

	public static boolean isFlyme6Above(){
		if (isFlyme6Above == -1){
			isFlyme6Above = isFlymeV6OrAbove() ? 1 : 0;
		}
		return isFlyme6Above == 1;
	}

	public static boolean isFlyme4Above(){
		if (isFlyme4Above == -1){
			isFlyme4Above = isFlymeV4OrAbove() ? 1 : 0;
		}
		return isFlyme4Above == 1;
	}

	// 判断是否为V5系统
	public static boolean isV5System() {
		if (mMiuiVersion == -1) {
			mMiuiVersion = getMiuiVersionCode();
		}
		return mMiuiVersion >=5 ;
	}

	public static boolean isMiUi6Above(){
		if (mMiuiVersion == -1) {
			mMiuiVersion = getMiuiVersionCode();
		}
		return mMiuiVersion >= 6;
	}

	public static boolean isMiUi9Above(){
		if (mMiuiVersion == -1) {
			mMiuiVersion = getMiuiVersionCode();
		}
		return mMiuiVersion >= 9;
	}


	public static void initRom(Context context){
		int romType = getRomType(context);
		//状态栏用19一下不用初始化
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
			mMiuiVersion = 0;
			isFlyme6Above = 0;
			isFlyme4Above = 0;
			return;
		}
		if (romType == XIAOMI){
			mMiuiVersion = getMiuiVersionCode();
		} else {
			mMiuiVersion = 0;
		}
		if (romType == MEIZU){
			if (isFlyme6Above()){
				isFlyme4Above = 0;
			} else {
				isFlyme4Above();
			}
		} else {
			isFlyme6Above = 0;
			isFlyme4Above = 0;
		}
	}

	/**
	 * 判断手机型号
	 * @return
	 */
	public static int getRomType(Context context) {
		if (mRomType != -1){
			return mRomType;
		}
		//先判断桌面情况
		int type = checkIsHuaWei(context);
		if (type == HUAWEI) {
			mRomType = HUAWEI;
			return HUAWEI;
		}
		//然后判断rom情况
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(new File(Environment
					.getRootDirectory(), "build.prop")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xiaomiRomKey = property.getProperty("ro.miui.ui.version.name");
		if (xiaomiRomKey != null) {
			mRomType = XIAOMI;
			return XIAOMI;
		}
		String vivoRomKey1 = property.getProperty("ro.vivo.rom");
		String vivoRomKey2 = property.getProperty("ro.vivo.rom.version");
		if (vivoRomKey1 != null || vivoRomKey2 != null) {
			mRomType = VIVO;
			return VIVO;
		}
		String oppoRomKey = property.getProperty("ro.build.version.opporom");
		if (oppoRomKey != null) {
			mRomType = OPPO;
			return OPPO;
		}
		if (Build.DISPLAY.toLowerCase(Locale.getDefault()).contains("flyme")) {
			mRomType = MEIZU;
			return MEIZU;
		}
		//最后判断机器情况
		String brand = Build.BRAND.toLowerCase(Locale.getDefault());
		if (!TextUtils.isEmpty(brand)) {
			if (brand.contains("meizu")) {
				mRomType = MEIZU;
				return MEIZU;
			} else if (brand.contains("huawei") || brand.contains("honor")) {
				mRomType = HUAWEI;
				return HUAWEI;
			} else if (brand.contains("xiaomi")) {
				mRomType = XIAOMI;
				return XIAOMI;
			} else if (brand.contains("sam")) {
				mRomType = SAMSUNG;
				return SAMSUNG;
			} else if (brand.contains("vivo")) {
				mRomType = VIVO;
				return VIVO;
			} else if (brand.contains("oppo")) {
				mRomType = OPPO;
				return OPPO;
			}
		}
		mRomType = UNKNOWN;
		return UNKNOWN;
	}

	//只有已经接入了sdk 并且是相应机型才能返回 补充1：后期加了渠道判断 返回值 "huawei"代表允许华为支付 和字符串含义无关
	public static String additionalPayType() {
		int romType = getRomType(App.getInstance());
		if (romType == HUAWEI) {
			try {
				Class.forName("com.huawei.hms.iap.Iap");
				if (isHuaWeiChannel()) {
					return "huawei";
				}
			} catch (Exception e) {
				LogMgr.e("AdditionalPayType", "no sdk");
			}
		}
		return "";
	}

	private static byte isRedmiNote8Pro_miui10 = -1;
	public static boolean isRedmiNote8Pro_miui10() {
		try {
			if (-1 != isRedmiNote8Pro_miui10) {
				return isRedmiNote8Pro_miui10 == 1;
			}
			String model = Build.MODEL.toLowerCase(Locale.getDefault());
			model = model.replaceAll(" ", "");
			if ("RedmiNote8Pro".equalsIgnoreCase(model)) {
				isRedmiNote8Pro_miui10 = 0;
			}
			if (0 == isRedmiNote8Pro_miui10) {
				String versionCode = getSystemProperty("ro.miui.ui.version.name");
				if ("V10".equalsIgnoreCase(versionCode)) {
					isRedmiNote8Pro_miui10 = 1;
				}
			}
		} catch (Exception ignore) {
		}
		return isRedmiNote8Pro_miui10 == 1;
	}

	private static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ignore) {
				}
			}
		}
		return line;
	}

	/**
	 * 判断是否是华为桌面
	 * @return
	 */
	private static int checkIsHuaWei(Context context)
	{
		PackageManager manager=context.getPackageManager();
		try {
			PackageInfo info =manager.getPackageInfo("com.huawei.android.launcher",0);

			if(info.versionCode>0){
				return HUAWEI;
			}

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	/**
	 * 努比亚这个辣鸡手机创建了通知栏图片后不知道咋回收了,后面可能还有其他辣鸡手机
	 *
	 * @return
	 */
	public static boolean isNeedCopyNotificationBitmap() {
		return Build.VERSION.SDK_INT >= 23 && Build.MANUFACTURER.contains("nubia");
	}

	/*
		屏幕是否锁屏
		如果flag为true，表示有两种状态：a、屏幕是黑的 b、目前正处于解锁状态 。
		如果flag为false，表示目前未锁屏
	*/
	public static boolean isScreenLocked(Context context) {
		try{
			KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			return mKeyguardManager.inKeyguardRestrictedInputMode();
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}


	/** 获取手机型号 */
	public static String getModel(){
		if (TextUtils.isEmpty(MODEL)){
			MODEL = Build.MODEL;
		}
		return MODEL;
	}


	/**
	 * 获取CPU类型
	 * @return
	 */
	public static String getHardware(){
		if (TextUtils.isEmpty(HARDWARE)){
			HARDWARE = Build.HARDWARE;
		}
		return HARDWARE;
	}

	public static boolean isAndroidO() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
	}

	public static boolean isAndroidP() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
	}

	public static boolean isAndroidQ() {
		return Build.VERSION.SDK_INT >= 29;
	}

	public static boolean isOPPO() {
		return DeviceInfo.getRomType(App.getInstance()) == DeviceInfo.OPPO;
	}

	public static boolean isXiaoMi() {
		return DeviceInfo.getRomType(App.getInstance()) == DeviceInfo.XIAOMI;
	}

	public static boolean isHuaWei() {
		return DeviceInfo.getRomType(App.getInstance()) == DeviceInfo.HUAWEI;
	}

	public static boolean isViVo() {
		return DeviceInfo.getRomType(App.getInstance()) == DeviceInfo.VIVO;
	}

	public static boolean isSamsung() {
		return DeviceInfo.getRomType(App.getInstance()) == DeviceInfo.SAMSUNG;
	}

	//以后华为渠道可能还会有其他渠道名 要更新这个方法
	public static boolean isHuaWeiChannel() {
		String channel = TextUtils.isEmpty(AppInfo.INSTALL_CHANNEL) ? KwChannelInfoUtils.getChannel(App.getInstance()) : AppInfo.INSTALL_CHANNEL;
		if (channel != null && channel.contains("thuawei")) {
			return true;
		}
		return false;
	}

	public static boolean needIgnoreConnectionHeader() {
		return Build.VERSION.SDK_INT > 29 && "xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
	}

}
