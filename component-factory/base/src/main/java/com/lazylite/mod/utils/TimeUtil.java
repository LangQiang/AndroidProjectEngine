package com.lazylite.mod.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	public static final String yyyyMMddHHMMSSFormat = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyyMMddHHMMFormat = "yyyy-MM-dd HH:mm";
	public static final String yyyyMMddFormat = "yyyy-MM-dd";
	public static final String yyyyMMddHHmmssNospaceFormat = "yyyyMMddHHmmss";
	public static final String yyyyPMMPddFormat = "yyyy.MM.dd";
	
	public static long getCurrentSecond() {
		Long tsLong = System.currentTimeMillis() / 1000;
		return tsLong;
	}
	
	public static long getTodayDayTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0); 
	    return calendar.getTimeInMillis();
	}

	public static long getYesterdayTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH,-1);
		return calendar.getTimeInMillis();
	}
	
	public static long getDayTime(int day){
		Calendar calendar = Calendar.getInstance();    
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, day);  //设置为1号,当前日期既为本月第一天 
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0); 
	    
	    return calendar.getTimeInMillis();
	}
	
	public static long getMonthFinalDayTime(){
		Calendar calendar = Calendar.getInstance();    
		calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0); 
	    return calendar.getTimeInMillis();
	}
	
	public static long getMonthFristDayTime(){
		Calendar calendar = Calendar.getInstance();  
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0); 
	    return calendar.getTime().getTime();
	}
	

	/**
	 * 获取当天日期 格式yyyy-MM-dd-HH-mm-ss
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat dataFormat = new SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss");
		String time = dataFormat.format(new Date());
		return time;
	}

	/**
	 * 获取当前时间指定格式的字符串
	 * @param format
	 * @return
	 */
	public static String getCurrentStringTime(String format){
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		String time = dataFormat.format(new Date());
		return time;
	}
	
	/**
	 * 将指定时间串格式化为指定格式
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static String formatDate(String dateString, String format) {
		try {
			SimpleDateFormat dataFormat = new SimpleDateFormat(format);
			return dataFormat.format(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String formatDate(Date date, String format) {
		try {
			SimpleDateFormat dataFormat = new SimpleDateFormat(format);
			return dataFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断一个String是不是时间
	 * @param str
	 * @return
     */
	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			// e.printStackTrace();
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}

	/**
	 * 获取时间差值
	 * 
	 * 差值天
	 * 
	 * @return
	 */
	public static long getTimeDiff(String leftTime, String rightTime) {
		long day = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date right = null;
		Date left = null;
		try {
			right = df.parse(rightTime);
		} catch (ParseException e) {
			right = new Date();
			e.printStackTrace();
		}
		try {
			left = df.parse(leftTime);
		} catch (ParseException e) {
			left = new Date();
			e.printStackTrace();
		}
		long l = right.getTime() - left.getTime();
		day = l / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 获取时间差值
	 *
	 * 差值毫秒  right - left
	 */
	public static long getTimeDiffMs(String leftTime, String rightTime,String formatStr) {
		if(TextUtils.isEmpty(formatStr)){
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		Date right = null;
		Date left = null;
		try {
			right = df.parse(rightTime);
		} catch (ParseException e) {
			right = new Date();
			e.printStackTrace();
		}
		try {
			left = df.parse(leftTime);
		} catch (ParseException e) {
			left = new Date();
			e.printStackTrace();
		}
		return right.getTime() - left.getTime();
	}

	/**
	 * 判断到期时间 现在 在vip的时候用了
	 * @param duration
	 * @param nowTime
	 * @return
	 */
	public static boolean isExpire(String duration, String nowTime){
		if(TextUtils.isEmpty(duration)){
			return false;
		}
		int sDay = 1, mDay = 0;
		if (!"0000-00-00 00:00:00".equals(duration)
				&& !"0000-00-00 00:00:00".equals(nowTime)) {
			try {
				sDay = Integer.parseInt(new KwDate(duration).toFormatString("yyyyMMdd"));
				mDay = Integer.parseInt(new KwDate(nowTime).toFormatString("yyyyMMdd"));
			} catch (Throwable e) {
				sDay = 1;
				mDay = 0;
			}
		}
		if (sDay > mDay) {
			return false;
		}
		return true;
	}

	

	/**
	 * 判断日期是否为今天，日期格式yyyy-MM-dd
	 */
	@SuppressWarnings("unused")
	public static boolean isDateToday(String date) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String currentTime = "";
		// 使用服务器系统时间
		// if (ExpressTime.SERVER_SYSTEM_TIME_NULL !=
		// ExpressTime.currentServerSystemTime) {
		// currentTime =
		// changeTimeStempToDate(ExpressTime.currentServerSystemTime);
		// } else {
		// currentTime = f.format(c.getTime());
		// }
		// LogUtil.d("TimeUtil isDateToday currentTime = " + currentTime +
		// " date = " + date);
		if (currentTime.equals(date)) {
			return true;
		}
		return false;
	}

	/**
	 * yyyy-MM-dd转换为MM月dd日
	 */
	public static String changeTimeFormat(String date) {
		SimpleDateFormat date0 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat date1 = new SimpleDateFormat("yyyy年MM月dd");
		String nowTime = "";
		try {
			nowTime = date1.format(date0.parse(date)).substring(5) + "日";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowTime;
	}

	/**
	 * 将时间戳转换成时间字符串
	 */
	public static String changeTimeStempToString(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
	
	/**
	 * 将时间戳转换成时间字符串
	 */
	public static String changeTimeStempToString(long time, String format) {
		Date date = new Date(time);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 将时间戳转换成日期符串
	 */
	public static String changeTimeStempToDate(int mill) {
		Date date = new Date(mill * 1000L);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 将字符串转换成时间戳
	 */
	public static int getTimeStemp(String time) {
		int timeStemp = -1;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			Date date = simpleDateFormat.parse(time);
			timeStemp = (int) (date.getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStemp;
	}
	
	/**
	 * 将字符串转换成时间戳
	 */
	public static int getDateTimeStemp(String time) {
		if(TextUtils.isEmpty(time)){
			return -1;
		}
		int timeStemp = -1;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					yyyyMMddHHMMFormat);
			Date date = simpleDateFormat.parse(time);
			timeStemp = (int) (date.getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStemp;
	}

	public static long dateToTime(String dateTime) {
		long time=0;
		// Date或者String转化为时间戳
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = format.parse(dateTime);
			time=date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 时间戳转换Date对象
	 */
	public static Date TimeLongToDate(long mill) {
		Date date = new Date(mill * 1000);
		return date;
	}

	/**
	 * 格式化日期
	 */
	public static String toString(Date date, String format) {
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
	
	
	/**
	 *判断某一时间戳到现在的时间 例如一分钟前，一天前
	 */
	public static String formatTimeStamp(Long timeStamp){
		long minute = (long) (1000*60);
		long hour = minute *60;
		long day = hour *24;
		long mouth = day*30;
		long year = mouth* 12;
		
		int  num = 0;
		String unit = null;
		long offsetSecons =System.currentTimeMillis() - 1000*timeStamp;
		if(offsetSecons<minute){
			return "刚刚";
		}
		else if(offsetSecons> minute && offsetSecons <hour){
			num = (int) (offsetSecons/minute);
			unit ="分钟";
		}else if(offsetSecons>= hour && offsetSecons < day){
			num = (int) (offsetSecons/hour);
			unit = "小时";
		}else if(offsetSecons >= day && offsetSecons < mouth){
			num = (int) (offsetSecons/day);
			unit = "天";
		} else if(offsetSecons >= mouth && offsetSecons <  year){
			num = (int) (offsetSecons/mouth);
			unit = "月";
		}else if(offsetSecons >= year){
			num = (int) (offsetSecons/year);
			unit = "年";
		}
		return  num+ unit + "前";
	}
	
	public static boolean isFirstMonthDay(){
		Calendar c = Calendar.getInstance();
		int today = c.get(c.DAY_OF_MONTH); // 得到本月的那一天
		if(today ==1){ // 然后判断是不是本月的第一天
			return true;
		}else{
			return false;
		}
	}

	public static String changeTimeFormatMdHm(String date) {
		SimpleDateFormat date0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat date1 = new SimpleDateFormat("MM.dd HH:mm");
		String nowTime = "";
		try {
			nowTime = date1.format(date0.parse(date));
		} catch (Exception e) {
			return date;
		}
		return nowTime;
	}

	/**
	 * 一秒的毫秒数
	 */
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	/**
	 * 一天的毫秒数
	 */
	public static final long DAY = 1000 * 60 * 60 * 24L;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 30;
	public static final long SEASON = MONTH * 3;
	public static final long YEAR = DAY * 365;

	/**
	 * 生成要显示的时间字符串
	 *
	 * @param milliSecs
	 * @return
	 */
	public static String toString(long milliSecs) {
		StringBuffer sb = null;
		sb = new StringBuffer();
		long second = milliSecs / 1000;
		long m = second / 60;
		sb.append(m < 10 ? "0" : "").append(m);
		sb.append(":");
		long s = second % 60;
		sb.append(s < 10 ? "0" : "").append(s);
		return sb.toString();
	}

	/**
	 * 生成要显示的时间字符串
	 * 大于1小时显示 HH:mm:ss
	 * 小于1小时显示 mm:ss
	 *
	 * @param milliSecs
	 * @return
	 */
	public static String formatTimeSpecial(long milliSecs) {
		long second = milliSecs / 1000;

		long h = second / 3600;
		long m = second % 3600 / 60;
		long s = second % 60;

		if (second < 3600) {
			return String.format(Locale.getDefault(), "%02d:%02d", m, s);
		} else {
			return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
		}
	}

	public static String formatTime(long milliSecs) {
		long second = milliSecs / 1000;

		long h = second / 3600;
		long m = second % 3600 / 60;
		long s = second % 60;

		return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
	}

	public static String format(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(time));
	}


	private static Time today = new Time();
	private static Time time = new Time(); //不能加"GMT+8"，按网上加了时间就不对了。

	/**
	 * 复杂格式化传入的时间值，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日
	 *
	 * @param hasTime：用于标示，除当天外，其它的时间格式中，是否包括时分字符
	 * @return
	 */
	public static String ExFormatDateTime(long msec, boolean hasTime) {
		today.setToNow();
		time.set(msec);  //%Y%m%dT%H%M
		if (time.year < today.year) { //去年的时间
			return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");
		} else if (time.year == today.year) { //今年时间
			if (time.month < today.month) {  //上一个月以前
				return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
			} else if (time.month == today.month) {  //当前月
				if (time.monthDay == today.monthDay) {  //当天时间
					return time.format("%H:%M");
				} else if ((time.monthDay + 1) == today.monthDay) { //昨天
					return hasTime ? "昨天 " + time.format("%H:%M") : "昨天";
				} else if ((time.monthDay + 1) < today.monthDay) {
					return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
				} else {
					return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
				}
			} else {
				return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
			}
		} else {
			return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");//此为未来时间，统一全显示格式
		}
	}

	/**
	 * 复杂格式化传入的时间值2，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日，比上一个函数增加钟显示功能了一小时内的分
	 *
	 * @param hasTime：用于标示，除当天外，其它的时间格式中，是否包括时分字符
	 * @return
	 */
	public static String ExFormatDateTime2(long msec, boolean hasTime) {
		today.setToNow();
		time.set(msec);  //%Y%m%dT%H%M
		if (time.year < today.year) { //去年的时间
			return time.format(hasTime ? "%Y.%m.%d %H:%M" : "%Y.%m.%d");
		} else if (time.year == today.year) { //今年时间
			if (time.month < today.month) {  //上一个月以前
				return time.format(hasTime ? "%m.%d %H:%M" : "%m.%d");
			} else if (time.month == today.month) {  //当前月
				if (time.monthDay == today.monthDay) {  //当天时间
					long minSpace = System.currentTimeMillis() - msec;
					if (minSpace < 3600000) { //如果是一小时以内的时间
						int minVal = (int) (minSpace / 60000);
						if (minVal < 1) {
							return "刚刚";
						} else {
							return minVal + "分钟前";
						}
					} else {
						return time.format("%H:%M");
					}
				} else if ((time.monthDay + 1) == today.monthDay) { //昨天
					return hasTime ? "昨天 " + time.format("%H:%M") : "昨天";
				} else if ((time.monthDay + 1) < today.monthDay) {
					return time.format(hasTime ? "%m.%d %H:%M" : "%m.%d");
				} else {
					return time.format(hasTime ? "%Y.%m.%d %H:%M" : "%Y.%m.%d"); //此为未来时间，统一全显示格式
				}
			} else {
				return time.format(hasTime ? "%Y.%m.%d %H:%M" : "%Y.%m.%d"); //此为未来时间，统一全显示格式
			}
		} else {
			return time.format(hasTime ? "%Y.%m.%d %H:%M" : "%Y.%m.%d");//此为未来时间，统一全显示格式
		}
	}

	/**
	 * 复杂格式化传入的时间值2，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日，比上一个函数增加钟显示功能了一小时内的分
	 *
	 * @param msec：毫秒值
	 * @return
	 */
	public static String ExFormatDateTime3(long msec) {
		today.setToNow();
		time.set(msec);

		if (time.year == today.year
				&& time.month == today.month
				&& time.monthDay == today.monthDay) {
			long minSpace = System.currentTimeMillis() - msec;
			if (minSpace < 3600000) { //如果是一小时以内的时间
				int minVal = (int) (minSpace / 60000);
				if (minVal < 5) {
					return "当前在线";
				} else {
					return minVal + "分钟前在线";
				}
			} else {
				return minSpace / 3600000 + "小时前在线";
			}
		} else {
			return "1天前在线";
		}
	}

	public static String formatPlayTime(int time) {
		if (time <= 0) {
			return "00:00";
		}

		int sec, min;
		sec = (time / 1000) % 60;
		min = time / 60000;

		return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
	}

}
