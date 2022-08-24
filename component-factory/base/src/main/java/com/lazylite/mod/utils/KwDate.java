package com.lazylite.mod.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.lazylite.mod.log.LogMgr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


// by haiping
public class KwDate extends Date {

	private static final long serialVersionUID = 1L;

	// 李建衡：java的int是有符号的，以秒为单位，最多也就表示不到70年。再多会有溢出
	public static final int 
	T_SECOND 	= 1, 
	T_MINUTE 	= 60,
	T_HOUR 		= T_MINUTE * 60, 
	T_DAY 		= T_HOUR * 24, 
	T_WEEK 		= T_DAY * 7,
	T_MONTH 	= T_DAY * 30, 
	T_YEAR 		= T_DAY * 365;
	
	// 李建衡：java的int是有符号的，以毫秒为单位，最多也就表示不到一个月。再多会有溢出，因此使用long
	public static final long
	T_MS_SECOND = T_SECOND * 1000,
	T_MS_MINUTE = T_MINUTE * 1000,
	T_MS_HOUR 	= T_HOUR * 1000, 	
	T_MS_DAY 	= T_DAY * 1000, 	
	T_MS_WEEK 	= T_WEEK * 1000,	
	T_MS_MONTH 	= T_MONTH * 1000, 
	T_MS_YEAR 	= T_YEAR * 1000;

    //add by hongze
    public static final int COMPARE_FORMAT_ERROR = -1;
    public static final int COMPARE_BEFORE_TODAY = 0;
    public static final int COMPARE_TODAY = 1;
    public static final int COMPARE_AFTER_TODAY = 2;

	private SimpleDateFormat dateFormat = null;

	public KwDate() {
		super();
	}
	
	public KwDate(long tm) {
		super(tm);
	}

	// 从一个yyyy-MM-dd HH:mm:ss格式或者yyyy-MM-dd格式日期字符串构造
	public KwDate(final String strDate) {
		super();
		fromString(strDate);
	}

	// 增加second时间
	public final KwDate increase(final int seconds) {
		// 李建衡：如果增加的时间接近1个月时，会有溢出。因此类型要转成long
		setTime(getTime() + seconds * 1000L);
		return this;
	}

	// 增加多少年/月/天/时/分/秒时间
	public final KwDate increase(final int timeGranu, final int timeValue) {
		increase(timeGranu * timeValue);
		return this;
	}

	// 减去
	public final KwDate decrease(final int seconds) {
		setTime(getTime() - seconds * 1000L);
		return this;
	}

	// 减去多少年/月/天/时/分/秒时间
	public final KwDate decrease(final int timeGranu, final int timeValue) {
		decrease(timeGranu * timeValue);
		return this;
	}

	// 求this-other时间差，expectTimeGranu决定结果粒度，例如expectTimeGranu=T_HOUR，返回值的单位就是小时
	public final long sub(final Date other, final int expectTimeGranu) {
		return sub(this, other, expectTimeGranu);
	}

	// 求minuend-subtractor时间差，expectTimeGranu同上
	public static long sub(final Date minuend, final Date subtractor,
                           final int expectTimeGranu) {
		return (minuend.getTime() - subtractor.getTime()) / 1000
				/ expectTimeGranu;
	}

	// 按照传入字符串格式化
	public final String toFormatString(final String format) {
		if(dateFormat==null){
			dateFormat  = new SimpleDateFormat(format, Locale.CHINA);
		}else{
			dateFormat.applyPattern(format);
		}
		return dateFormat.format(this);
	}

	// 按照传入字符串格式加载
	public final boolean fromString(final String str, final String formatStr) {
		Date date;
		try {
			if(dateFormat==null){
				dateFormat  = new SimpleDateFormat(formatStr, Locale.CHINA);
			}else{
				dateFormat.applyPattern(formatStr);
			}
			date = dateFormat.parse(str);
		} catch (Exception e) {
			LogMgr.d("KwDate fromString", str);
			return false;
		}
		setTime(date.getTime());
		return true;
	}

	// 格式化字符串，带时间
	public final String toDateTimeString() {
		return toFormatString("yyyy-MM-dd HH:mm:ss");
	}

	// 格式化字符串，只有日期
	public final String toDateString() {
		return toFormatString("yyyy-MM-dd");
	}

	// 静态格式化字符串，只有时间
	public static final String toExString(String fmtStr, long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(fmtStr, Locale.CHINA);
		return dateFormat.format(date);
	}
	
	// 静态格式化字符串，只有时间
	public static final String toExDateTimeStringNoSecond(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		return dateFormat.format(date);
	}
	// 静态格式化字符串，只有时间
	public static final String toExDateTimeString(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return dateFormat.format(date);
	}

	// 静态格式化字符串，只有时间
	public static final String toExDateTimeString(long time, String pattern) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
		return dateFormat.format(date);
	}

	// 静态格式化字符串，只有时间
	public static final String toTimeStringNoSecond(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
		return dateFormat.format(date);
	}
	// 静态格式化字符串，只有时间
	public static  final String toTimeStringNoHour(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
		return dateFormat.format(date);
	}
		
	//静态 格式化字符串，只有时间
	public static final String toTimeStringNoSecondNoYear(long time) {
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
		return dateFormat.format(date);
	}
	
	// 格式化字符串，只有时间
	public final String getTimeStringNoSecond(long time) {
		if(dateFormat==null){
			dateFormat  = new SimpleDateFormat("HH:mm", Locale.CHINA);
		}else{
			dateFormat.applyPattern("HH:mm");
		}
		this.setTime(time);
		return dateFormat.format(this);
	}
	// 格式化字符串，只有时间
	public final String getTimeStringNoHour(long time) {
		if(dateFormat==null){
			dateFormat  = new SimpleDateFormat("mm:ss", Locale.CHINA);
		}else{
			dateFormat.applyPattern("mm:ss");
		}
		this.setTime(time);
		return dateFormat.format(this);		
	}
		
	// 格式化字符串，只有时间
	public final String getTimeStringNoSecondNoYear(long time) {
		if(dateFormat==null){
			dateFormat  = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
		}else{
			dateFormat.applyPattern("MM月dd日 HH:mm");
		}
		this.setTime(time);
		return dateFormat.format(this);	
	}
		
	// 从字符串加载，格式错误导致失败的话，保持当前状态不变，返回false
	public final boolean fromString(final String str) {
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		String formatStr = "yyyy-MM-dd";
		if (str.length() > 10) {
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		return fromString(str, formatStr);
	}
	
	public static long getDayStartTime(){
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime().getTime();
	}

    //add by hongze
    public String today() {
    	if(dateFormat==null){
			dateFormat  = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		}else{
			dateFormat.applyPattern("yyyyMMdd");
		}        
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    //add by hongze
    public void resetTime() {
        setTime(Calendar.getInstance().getTime().getTime());
    }
    //add by chengxiaohan 传入"yyyy-MM-dd";等格式
    public String today(String format) {
    	try {
        	if(dateFormat==null){
    			dateFormat  = new SimpleDateFormat(format, Locale.CHINA);
    		}else{
    			dateFormat.applyPattern(format);
    		}        
            return dateFormat.format(Calendar.getInstance().getTime());
		} catch (Exception e) {
		}
    	return "";
    }

    public int compareToToday(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return COMPARE_FORMAT_ERROR;
        }
        try {
            long limitDate = Long.parseLong(dateString);
            long today = Long.parseLong(today());
            if (limitDate < 1) {
                return COMPARE_FORMAT_ERROR;
            }
            if (today == limitDate) {
                return COMPARE_TODAY;
            } else if (today > limitDate) {
                return COMPARE_BEFORE_TODAY;
            } else {
                return COMPARE_AFTER_TODAY;
            }
        } catch (Exception e) {
            LogMgr.e("KwDate", e.toString());
            return COMPARE_FORMAT_ERROR;
        }
    }
    
    /*
     * 秀场添加
     */
    @SuppressLint({ "SimpleDateFormat", "UseValueOf" })
    public String timeSwitch(String string){
    	SimpleDateFormat format = new SimpleDateFormat( "HH:mm:ss" , Locale.CHINA);
    	 Long time=new Long(string);
    	 String d = format.format(time * 1000l);
		return d;
    }

    /**
     * 判断传入时间和现在的差值 
     * @param dateString 
     * @param days 相差的天数作比较
     * @return 
     */
    public boolean compareToDaysBefore(String dateString, int days) {
        if (TextUtils.isEmpty(dateString)) {
            return false;
        }
        try {
            long limitDate = Long.parseLong(dateString);
            long before = Long.parseLong(decrease(T_DAY, days).toFormatString("yyyyMMdd"));
            if (limitDate < 1) {
                return false;
            }
            if (before >= limitDate) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogMgr.e("KwDate", e.toString());
            return false;
        }
    }

	public String todayLine() {
		if(dateFormat==null){
			dateFormat  = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		}else{
			dateFormat.applyPattern("yyyy-MM-dd");
		}
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	public static long getTimeByField(int type, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		switch (type) {
			case Calendar.MILLISECOND:
				calendar.set(Calendar.MILLISECOND, value);
				break;
			case Calendar.SECOND:
				calendar.set(Calendar.SECOND, value);
				calendar.set(Calendar.MILLISECOND, 0);
				break;
			case Calendar.MINUTE:
				calendar.set(Calendar.MINUTE, value);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				break;
			case Calendar.HOUR_OF_DAY:
				calendar.set(Calendar.HOUR_OF_DAY, value);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				break;
		}
		return calendar.getTime().getTime();
	}
}
