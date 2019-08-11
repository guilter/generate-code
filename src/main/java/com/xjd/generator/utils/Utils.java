package com.xjd.generator.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Utils {
	
	// 创建文件夹和文件
	public static void createDirAndFile(String filePath) {
		try {
			File file = new File(filePath);
			if(!file.exists()) {
				// 判断路径中是否有文件名
				if(-1 < filePath.indexOf(".")) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				} else {
					file.mkdirs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 获取变量名
	public static Set<String> getVariableName(String str, String start, String end) {
		Set<String> result = new HashSet<String>();
		String regex = start + "(.*?)" + end;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()) {
			String key = matcher.group(1);
			if(!key.contains(start) && !key.contains(end)) {
				result.add(key);
			}
		}
		return result;
	}
	
	// 下划线命名转驼峰命名
	public static String underlineToHump(String param, Boolean preToUpper){
		StringBuilder result = new StringBuilder();
		if(-1 == param.indexOf("_")) {
			result.append(param);
		} else {
			String str[] = param.split("_");
			for(String s : str){
				if(0 == result.length()){
					result.append(s.toLowerCase());
				} else {
					result.append(s.substring(0, 1).toUpperCase());
					result.append(s.substring(1).toLowerCase());
				}
			}
		}
		if(null != preToUpper && preToUpper) {
			param = result.toString();
			result = new StringBuilder(preToUpper(param));
		}
		return result.toString();
	}
	
	// 首字母大写
	public static String preToUpper(String param) {
		if(StringUtils.isNotBlank(param)) {
			param = param.substring(0, 1).toUpperCase() + param.substring(1);
		}
		return param;
	}
	
}
