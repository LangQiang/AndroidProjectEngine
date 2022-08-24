package com.lazylite.mod.receiver.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.threadpool.KwThreadPool;

// by haiping
public class NetworkStateUtil extends BroadcastReceiver {

	private static Context sContext;

	static long l;
	private static final String TAG = "NetworkStateUtil";

	public static void init(final Context ctx) {
		if (attached) {
			return;
		}
		sContext = ctx.getApplicationContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		try {
			ctx.registerReceiver(instance, filter);
			attached = true;
		} catch (Exception ignore) {
		}
		getNetworkInfo(ctx);
	}

	public static void release() {
		if (attached && sContext != null) {
			try { // 说好了不到处try的，安卓你咋了
				sContext.unregisterReceiver(instance);
			} catch (Exception ignore) {
			}
			attached=false;
		}
	}

	public static boolean isAvailable() {
		if (isNetworkAvaliable) {
			return true;
		}
		final long timeNow = System.currentTimeMillis();
		if (timeNow - l > 20000) {//最多20秒刷一次
			l = timeNow;
			KwThreadPool.runThread(KwThreadPool.JobType.IMMEDIATELY, new Runnable() {
				@Override
				public void run() {
					if (sContext != null) {
						getNetworkInfo(sContext);
					}
				}
			});
		}
		return isNetworkAvaliable;
	}

	public static boolean isWifi() {
		return isAvailable() && isWifiAvaliable;
	}

	public static boolean isMobile() {
		return isAvailable() && !isWifi();
	}

	public static boolean is3GOr4G() {
		boolean flag = (getNetWorkType() == TYPE_3G || getNetWorkType() == TYPE_4G);
		return isAvailable() && flag;
	}
	
	public static boolean is3G() {
		return isAvailable() && getNetWorkType() == TYPE_3G;
	}
	
	public static boolean is4G() {
		return isAvailable() && getNetWorkType() == TYPE_4G;
	}

	public static boolean isWifiOr3GOr4G() {
		return isWifi() || is3GOr4G();
	}

	public static final int	TYPE_UNKNOWN	= 0, TYPE_2G = 1, TYPE_3G = 2, TYPE_4G = 3, TYPE_5G = 4;

	public static int getNetWorkType() {
		return networkTypeID;
	}

	// "WIFI"、"2G"、"3G"、"4G"、"UNKNOWN"
	public static String getNetworkTypeName() {
		return networkTypeName;
	}

	public static final int	OPERATOR_UNKNOWN	= 0, OPERATOR_CMCC = 1, OPERATOR_CUCC = 2, OPERATOR_CT = 3, OPERATOR_NR = 4, OPERATOR_LTE_CA = 5;

	public static int getOperatorType() {
		return networkOperatorID;
	}

	public static String getAccessPoint() { // 老版本里搞来的诡异函数，推送需要
		return accessPointName;
	}
	
	// 有网，非wifi，且仅在wifi下联网开关打开，返回true，否则返回false
	public static boolean isOnlyWifiConnect() {
		if (!isNetworkAvaliable) {
			return false;
		}
		if (isWifiAvaliable) {
			return false;
		}
		return false;
	}

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		KwThreadPool.runThread(KwThreadPool.JobType.NORMAL, new Runnable() {
			@Override
			public void run() {
				boolean lastIsNetworkAvaliable = isNetworkAvaliable;
				boolean lastIsWifiAvaliable = isWifiAvaliable;
				getNetworkInfo(context);
				if (lastIsNetworkAvaliable != isNetworkAvaliable
						|| lastIsWifiAvaliable != isWifiAvaliable) {
					MessageManager.getInstance().asyncNotify(INetworkObserver.EVENT_ID,
							new MessageManager.Caller<INetworkObserver>() {
								public void call() {
									ob.onNetworkChanged(
											isNetworkAvaliable, isWifiAvaliable);
								}
							});
				}
			}
		});
	}

	public static void getNetworkInfo(final Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return;
		}

		NetworkInfo[] info = null;
		try {
			info = connectivity.getAllNetworkInfo();
		} catch (Exception ignore) {
		}
		if (info != null) {
			isNetworkAvaliable = false;
			isWifiAvaliable = false;
			
			for (int i = 0; i < info.length; i++) {
				if (info[i].isConnected()) {
					isNetworkAvaliable = true;
					accessPointName = info[i].getTypeName();
					if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
						isWifiAvaliable = true;
						networkTypeName = "WIFI";
					} else if (info[i].getType() == ConnectivityManager.TYPE_MOBILE) {
						int typeID = info[i].getSubtype();
						if (typeID < NETWORK_TYPES.length) {
							accessPointName = info[i].getExtraInfo();
							networkTypeID = NETWORK_TYPES[typeID][0];
							networkOperatorID = NETWORK_TYPES[typeID][1];
							networkTypeName = TYPE_NAME[networkTypeID];
						} else {
							networkTypeID = TYPE_3G;
							networkTypeName = "3G";
						}
					} else {
						networkTypeName = "UNKNOWN";
					}
					break;
				}
			}
		}
	}

	private static final String[]	TYPE_NAME		= { "UNKNOWN", "2G", "3G", "4G" , "5G"};

	private static final int[][]	NETWORK_TYPES	= {
													/* 0 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 1 */{ TYPE_2G, OPERATOR_CUCC },
													/* 2 */{ TYPE_2G, OPERATOR_CMCC },
													/* 3 */{ TYPE_3G, OPERATOR_CUCC },
													/* 4 */{ TYPE_2G, OPERATOR_CT },
													/* 5 */{ TYPE_3G, OPERATOR_CT },
													/* 6 */{ TYPE_3G, OPERATOR_CT },
													/* 7 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 8 */{ TYPE_3G, OPERATOR_CUCC },
													/* 9 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 10 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 11 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 12 */{ TYPE_3G, OPERATOR_CT },
													/* 13 */{ TYPE_4G/* 其实是TYPE_LTE */, OPERATOR_UNKNOWN },
													/* 14 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 15 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 16 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 17 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 18 */{ TYPE_UNKNOWN, OPERATOR_UNKNOWN },
													/* 19 */{TYPE_5G, OPERATOR_LTE_CA }, //可能不是5g
													/* 20 */{ TYPE_5G, OPERATOR_NR },
													};

	private static NetworkStateUtil	instance		= new NetworkStateUtil();
	private static boolean			attached;
	private static volatile boolean	isNetworkAvaliable;
	private static volatile boolean	isWifiAvaliable;
	private static volatile int		networkTypeID;
	private static volatile String networkTypeName	= "UNKNOWN";
	private static volatile int		networkOperatorID;
	private static volatile String accessPointName	= "None";

}
