package com.bclould.tea.utils;

import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * 字符串为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || "".equals(str)){
			return true;
		}
		return false;
	}
	
	/**
	 * 字符串不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		if(str != null && !"".equals(str)){
			return true;
		}
		return false;
	}
	/***
	 * 
	 * 字符串是不是数字
	 * 
	 * @param str
	 * @return
	 */
	
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();    
	 }

	public static int StringToInt(String str){
		String intStr = str;
		if(StringUtils.isEmpty(intStr)){
			return 0;
		}
		if(intStr.contains(".")){
			intStr = intStr.substring(0,intStr.indexOf("."));
		}
		if (!StringUtils.isNumeric(intStr)) {
			return 0;
		}
		return Integer.parseInt(intStr);
	}

}
