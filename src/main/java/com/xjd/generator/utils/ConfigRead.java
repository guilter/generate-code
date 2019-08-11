package com.xjd.generator.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * 读取配置文件
 */
public class ConfigRead {
	
	// 配置属性
	public static Properties properties = new Properties();
	
	public static void readConfig(String filePath) {
		if(StringUtils.isNotBlank(filePath)) {
			InputStream is = null;
			try {
				// 读取属性文件
				is = new BufferedInputStream(new FileInputStream(filePath));
				if(null != is) {
					// 加载属性列表
					properties.load(is);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(null != is) {
						is.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
}
