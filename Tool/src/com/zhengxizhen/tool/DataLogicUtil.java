package com.zhengxizhen.tool;

/**
 * @Title Tool
 * @package com.zhengxz_lib.tool
 * @Description 数据逻辑判断工具类
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.0
 * */
public class DataLogicUtil {

	/**
	 * 判断是否是纯数字
	 * */
	public static boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

}
