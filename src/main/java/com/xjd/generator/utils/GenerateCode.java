package com.xjd.generator.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class GenerateCode {
	
	// 需要要替换文件里的变量值
	public static Map<String, String> replaceMaps = new HashMap<String, String>();
	
	// 获取项目位置
	public static String getProjectPosition() {
		return ConfigRead.properties.getProperty("project.position", System.getProperty("user.dir"));
	}
	
	// 获取根包位置
	public static String getRootPackage(Boolean byPoint, Boolean realPath) {
		if(null == byPoint) {
			byPoint = true;
		}
		if(null == realPath) {
			realPath = false;
		}
		String rootPackage = ConfigRead.properties.getProperty("root.package");
		if(StringUtils.isNotBlank(rootPackage) && !byPoint) {
			rootPackage = rootPackage.replaceAll("\\.", "\\\\");
		}
		if(realPath) {
			String separator = byPoint ? "." :File.separator;
			rootPackage = getProjectPosition() + separator + rootPackage;
		}
		return rootPackage;
	}
	
	// 获取实体类包位置
	public static String getEntityPackage(Boolean byPoint, Boolean realPath) {
		if(null == byPoint) {
			byPoint = true;
		}
		if(null == realPath) {
			realPath = false;
		}
		String entityPackage = ConfigRead.properties.getProperty("entity.package", "pojo");
		if(StringUtils.isNotBlank(entityPackage) && !byPoint) {
			entityPackage = entityPackage.replaceAll("\\.", File.separator);
		}
		if(realPath) {
			String separator = byPoint ? "." :File.separator;
			entityPackage = getRootPackage(byPoint, true) + separator + entityPackage;
		}
		return entityPackage;
	}
	
	public static void generateFile(String readFilePath, String writeFilePath) {
		if(StringUtils.isNotBlank(readFilePath)) {
			InputStreamReader isr = null;
			BufferedReader br = null;
			OutputStreamWriter osw = null;
	        BufferedWriter bw = null;
			try {
				// 默认的时间格式
				SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
				
				// 输入流
				FileInputStream fis = new FileInputStream(readFilePath);
		        isr = new InputStreamReader(fis, "UTF-8");
		        br = new BufferedReader(isr);
		        
		        // 输出流
		        FileOutputStream fos = new FileOutputStream(new File(writeFilePath));
		        osw = new OutputStreamWriter(fos, "UTF-8");
		        bw = new BufferedWriter(osw);
		        
		        StringBuilder sb = new StringBuilder();
		        String line = "";
		        while ((line = br.readLine())!=null) {
		        	// 处理需要替换的字符
		        	if(StringUtils.isNotBlank(line)) {
		        		try {
		        			line = line.replaceAll("\\$\\{className\\}", replaceMaps.get("className"));
			        		line = line.replaceAll("\\$\\{author\\}", replaceMaps.get("author"));
			        		line = line.replaceAll("\\$\\{date\\}", dateSdf.format(new Date()));
			        		line = line.replaceAll("\\$\\{time\\}", timeSdf.format(new Date()));
						} catch (Exception e) {
							e.printStackTrace();
						}
		        	}
		            sb.append(line + "\n");
		        }
		        bw.write(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(null != br) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(null != isr) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		        if(null != bw) {
		        	try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
		        if(null != osw) {
		        	try {
						osw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
			}
		}
	}
	
	/**
	 * 通过内容生成文件
	 * @param content
	 * @param outputPath
	 */
	public static void generateFile(List<String> contents, String outputPath) {
		OutputStreamWriter osw = null;
        BufferedWriter bw = null;
		try {
			// 生成文件
			Utils.createDirAndFile(outputPath);
			// 输出流
	        FileOutputStream fos = new FileOutputStream(new File(outputPath));
	        osw = new OutputStreamWriter(fos, "UTF-8");
	        bw = new BufferedWriter(osw);
	        StringBuilder sb = new StringBuilder();
	        if(null != contents && 0 < contents.size()) {
	        	for(String content : contents) {
	        		sb.append(content + "\n");
	        	}
	        }
	        bw.write(sb.toString());
		} catch (Exception e) {
			System.err.println("通过内容生成文件方法出现问题！");
			e.printStackTrace();
		} finally {
	        if(null != bw) {
	        	try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        if(null != osw) {
	        	try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
	}
	
	// 生成实体类
	public static void generateEntity(String tableName, List<ColumnInfo> cis) {
		try {
			if(null != cis && 0 < cis.size()) {
				List<String> entityCodes = new ArrayList<String>();
				// 是否为时间类型的属性添加时间注解，若添加，则使用默认格式
				boolean addDateAnno = false; 
				if(StringUtils.isNotBlank(ConfigRead.properties.getProperty("need.add.date.anno")) 
						&& "true".equals(ConfigRead.properties.getProperty("need.add.date.anno"))) {
					addDateAnno = true;
				}
				// 第一行：包名
				entityCodes.add("package " + getRootPackage(true, false) + "." + getEntityPackage(true, false) + ";");
				// 空行
				entityCodes.add("");
				// 类型引用的位置下标
				int idx = 2;
				entityCodes.add("");
				// 声明行
				entityCodes.add("public class " + tableName + " {");
				entityCodes.add("");
				
				// 需要引用的类集合
				Map<String, String> imports = new TreeMap<String, String>();
				
				// 属性
				for(int i = 0;i < cis.size();i ++) {
					ColumnInfo ci = cis.get(i);
					if(null != ci) {
						// 基本类型之外的类型需要引用
						if(null != ci.getType()) {
							if("Date" == ci.getType()) {
								imports.put("Date", "import java.util.Date;");
								// 特别的，若想让时间输入输出有时间格式，则添加注解
								if(addDateAnno) {
									imports.put("DateTimeFormat", "import org.springframework.format.annotation.DateTimeFormat;");
									imports.put("JsonFormat", "import com.fasterxml.jackson.annotation.JsonFormat;");
									entityCodes.add("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
									entityCodes.add("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
								}
							}
						}
						// 写入属性注释
						if(StringUtils.isNotBlank(ci.getComment())) {
							entityCodes.add("    /** " + ci.getComment() + " **/");
						}
						// 写入属性
						entityCodes.add("    private " + ci.getType() + " " + Utils.underlineToHump(ci.getName(), null) + ";");
						entityCodes.add("");
					}
				}
				
				entityCodes.add("");
				
				// 生成类的无参构造函数
				entityCodes.add("    public " + tableName + "() {");
				entityCodes.add("        super();");
				entityCodes.add("    }");
				
				entityCodes.add("");
				
				// 生成类的全参构造函数
				StringBuilder args = new StringBuilder("    public " + tableName + "(");
				for(int i = 0;i < cis.size();i ++) {
					ColumnInfo ci = cis.get(i);
					if(null != ci) {
						if((i + 1) == cis.size()) {
							// 最后一个不接逗号
							args.append(ci.getType() + " " + Utils.underlineToHump(ci.getName(), null));
						} else {
							args.append(ci.getType() + " " + Utils.underlineToHump(ci.getName(), null) + ", ");
						}
					}
				}
				args.append(") {");
				entityCodes.add(args.toString());
				for(int i = 0;i < cis.size();i ++) {
					ColumnInfo ci = cis.get(i);
					if(null != ci) {
						entityCodes.add("        this." + Utils.underlineToHump(ci.getName(), null) + " = " + Utils.underlineToHump(ci.getName(), null) + ";");
					}
				}
				entityCodes.add("    }");
				
				entityCodes.add("");
				// 属性的getter和setter方法
				for(int i = 0;i < cis.size();i ++) {
					ColumnInfo ci = cis.get(i);
					if(null != ci) {
						// 写入属性的getter方法
						entityCodes.add("    public " + ci.getType() + " get" + Utils.underlineToHump(ci.getName(), true) + "() {");
						entityCodes.add("        return " + Utils.underlineToHump(ci.getName(), null) + ";");
						entityCodes.add("    }");
						entityCodes.add("");
						// 写入属性的setter方法
						entityCodes.add("    public void set" + Utils.underlineToHump(ci.getName(), true) + "(" + ci.getType() + " " + Utils.underlineToHump(ci.getName(), null) + ") {");
						entityCodes.add("        this." + Utils.underlineToHump(ci.getName(), null) + " = " + Utils.underlineToHump(ci.getName(), null) + ";");
						entityCodes.add("    }");
						entityCodes.add("");
					}
				}
				
				// 引用
				for(Map.Entry<String, String> entry : imports.entrySet()) {
					entityCodes.add(idx ++, entry.getValue());
				}
				
				entityCodes.add("");
				// 结束行
				entityCodes.add("}");
				
				// 生成文件
				generateFile(entityCodes, getEntityPackage(false, true) + File.separator + tableName + ".java");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
