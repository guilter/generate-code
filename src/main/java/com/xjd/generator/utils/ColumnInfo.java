package com.xjd.generator.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 表字段信息
 */
public class ColumnInfo {
	
	// 表字段类型对应成java类型
	public static Map<String, String> typeMap = new HashMap<String, String>();
	
	static {
		typeMap.put("tinyint", "Integer");
		typeMap.put("smallint", "Integer");
		typeMap.put("mediumint", "Integer");
		typeMap.put("bigint", "Long");
		typeMap.put("int", "Integer");
		typeMap.put("integer", "Integer");
		typeMap.put("double", "Double");
		typeMap.put("float", "Float");
		typeMap.put("decimal", "Double");
		typeMap.put("numeric", "Double");
		typeMap.put("char", "String");
		typeMap.put("varchar", "String");
		typeMap.put("date", "Date");
		typeMap.put("time", "Date");
		typeMap.put("datetime", "Date");
		typeMap.put("timestamp", "Long");
		typeMap.put("text", "String");
		typeMap.put("longtext", "String");
	}
	
	// 字段名称
	public String name;
	
	// 字段类型
	public String type;
	
	// 是否是主键
	public Boolean isPrimaryKey;
	
	// 注释
	public String comment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
