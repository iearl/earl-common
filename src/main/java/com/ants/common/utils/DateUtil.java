package com.ants.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
* @ClassName: DateUtil 
* @Description: 日期工具类 
* @author magw
* @version V1.0   
* @date 2017年12月17日 下午12:58:33 
*
 */
public class DateUtil {
	public static final String format="yyyy-MM-dd HH:mm:ss";
	public static final String formatSimp="yyyy-MM-dd";
	public static String formatDate(Date date){
		return formatDate(date,format);
	}
	/**
	 * 日期对象转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date,String format){
		String result="";
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		if(date!=null){
			result=sdf.format(date);
		}
		return result;
	}
	
	/**
	 * 字符串转日期对象
	 * @param str
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static Date formatString(String str,String format) {
		if(StringUtil.isEmpty(str)){
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date formatString(String str){
		return formatString(str,formatSimp);
	}
	
	public static String getCurrentDateStr()throws Exception{
		
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
		return sdf.format(date);
	}

		public static void main(String[] args) {
			System.out.println(formatDate(new Date()));
		}
	
}
