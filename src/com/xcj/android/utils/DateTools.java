package com.xcj.android.utils;

import android.text.format.DateFormat;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期助手
 * 完成一些简单的日期格式化操作
 * @author EwinLive
 * @version 1.0
 */
public class DateTools {
	private static String format = "yyyy-MM-dd kk:mm:ss";
	private static final String TAG = "DateTools";

	/**
	 * 返回默认的日期格式
	 * @return
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * 按默认格式返回当前日期
	 * @return
	 */
	public static String getNow(){
		return DateFormat.format(format, System.currentTimeMillis()).toString();
	}
	
	/**
	 * 按用户指定格式返回当前日期
	 * @param user_format 日期格式
	 * @return
	 */
	public static String getNow(String user_format){
		return DateFormat.format(user_format, System.currentTimeMillis()).toString();
	}
	
	/**
	 * 按默认格式返回指定日期
	 * @param date 长整型的时间值(long)
	 * @return
	 */
	public static String getDate(long date){
		return DateFormat.format(format, date).toString();
	}
	
	/**
	 * 按默认格式返回指定日期
	 * @param date 
	 * @return
	 */
	public static String getDate(Date date){
		return DateFormat.format(format, date).toString();
	}
	
	/**
	 * 使用预设格式将字符串转为Date
	 */
	public static Date parse(String strDate) {
		return parse(strDate, format);
	}
	
	/**
	 * 使用参数Format将字符串转为Date
	 */
	public static Date parse(String strDate, String user_format) {
		SimpleDateFormat df = new SimpleDateFormat(user_format);
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			for(StackTraceElement ste : e.getStackTrace()){
				Log.e(TAG, ste.toString());
			}
			return null;
		}
	}
	
	/**
	 * 按用户指定格式返回指定日期
	 * @param date 长整型的时间值(long)
	 * @param user_format 日期格式
	 * @return
	 */
	public static String getDate(long date, String user_format){
		return DateFormat.format(user_format, date).toString();
	}
	
	/**
	 * 按用户指定格式返回指定日期
	 * @param date
	 * @param user_format 日期格式
	 * @return
	 */
	public static String getDate(Date date, String user_format){
		return DateFormat.format(user_format, date).toString();
	}
}
