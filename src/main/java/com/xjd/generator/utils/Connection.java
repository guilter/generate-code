package com.xjd.generator.utils;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据库连接与读取数据
 */
public class Connection {
	
	// 数据库连接
	private static java.sql.Connection connection = null;
	
	// 查询的sql语句
	private static final String queryColumnSql = "select column_name as name, data_type as `type`, column_key as is_primary_key, column_comment as comment "
			+ "from information_schema.columns where table_name = '${tableName}' order by ordinal_position";
	
	// 获得数据库连接
	public static java.sql.Connection getConnection() {
		if(null == connection) {
			try {
				String driver = "com.mysql.cj.jdbc.Driver";
				String url = ConfigRead.properties.getProperty("db.url");
				String username = ConfigRead.properties.getProperty("db.username");
				String password = ConfigRead.properties.getProperty("db.password");
				if(StringUtils.isNotBlank(driver) && StringUtils.isNotBlank(url) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
					// 加载驱动程序
					Class.forName(driver);
					// 连接数据库
					connection = DriverManager.getConnection(url, username, password);
					if(!connection.isClosed()) {
						System.out.println("数据库连接成功！");
					}
				} else {
					System.err.println("数据库参数有误或为空！");
				}
			} catch (Exception e) {
				System.err.println("获取数据源出现错误！");
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	// 关闭数据库连接
	public static void closeConnection() {
		if(null != connection) {
			try {
				connection.close();
			} catch (Exception e) {
				System.err.println("关闭数据源出现错误！");
				e.printStackTrace();
			}
		}
	}
	
	// 获取表字段数据
	public static Map<String, List<ColumnInfo>> getTableColumns(List<String> tableNames) {
		Map<String, List<ColumnInfo>> tableColumns = new HashMap<String, List<ColumnInfo>>();
		if(null != tableNames && 0 < tableNames.size()) {
			try {
				if(null == connection) {
					getConnection();
				}
				for(int i = 0;i < tableNames.size(); i ++) {
					String tableName = tableNames.get(i);
					if(StringUtils.isBlank(tableName)) {
						continue;
					}
					List<ColumnInfo> cis = new ArrayList<ColumnInfo>();
					String sql = queryColumnSql.replace("${tableName}", tableNames.get(i));
					// 创建statement对象，用来执行sql语句
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery(sql);
					ColumnInfo ci = null;
					while(rs.next()) {
						ci = new ColumnInfo();
						// 属性名称要转一下
						ci.setName(rs.getString("name"));
						ci.setIsPrimaryKey(StringUtils.isNotBlank(rs.getString("is_primary_key")));
						ci.setComment(rs.getString("comment"));
						String type = rs.getString("type").toLowerCase();
						if(ColumnInfo.typeMap.containsKey(type)) {
							ci.setType(ColumnInfo.typeMap.get(type));
						} else {
							throw new Exception("字段'" + rs.getString("name") + "'的类型'" + type + "'没有找到java对应的类型。");
						}
						cis.add(ci);
					}
					tableColumns.put(tableName, cis);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}
		return tableColumns;
	}
	
}
