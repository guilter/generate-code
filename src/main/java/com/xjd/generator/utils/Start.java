package com.xjd.generator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Start {
	
	public static void start() {
		// 获取配置信息
		ConfigRead.readConfig("C:\\Program Files\\eclipse\\workspace\\generate-code\\src\\main\\resources\\generatorConfig.properties");
		List<String> tableNames = new ArrayList<String>();
		String tableName = ConfigRead.properties.getProperty("table.name");
		if(StringUtils.isNotBlank(tableName)) {
			//用逗号将字符串分开，得到字符串数组
		    String[] strs = tableName.split(",");
		    //将字符串数组转换成集合list
		    tableNames = Arrays.asList(strs);
		}
		// 获取数据
		Map<String, List<ColumnInfo>> tableInfos = Connection.getTableColumns(tableNames);
		for(Map.Entry<String, List<ColumnInfo>> entry : tableInfos.entrySet()) {
			GenerateCode.generateEntity(Utils.preToUpper(entry.getKey()), entry.getValue());
		}
		System.out.println("执行完毕！");
	}
	
}
