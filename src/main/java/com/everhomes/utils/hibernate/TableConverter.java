package com.everhomes.utils.hibernate;

import java.util.List;

import com.everhomes.utils.ColumnObject;

public class TableConverter {
	public static final String downLineChar = "_";
	public static final String defaultSpacing = "    ";
	
	public static String appendSpacing(String value, Integer count) {
		if(count != null && count > 0) {
			while(count > 0) {
				value = defaultSpacing + value;
				count--;
			}
		}
		return value;
	}
	
	
	public static String ignoreTableNamePrefix(String tableName) {
		return tableName.substring(tableName.indexOf('_')+1);
	}
	
	public static String getComponentPackage(String tableName) {
		return ignoreTableNamePrefix(tableName).split("_")[0];
	}
	
	public static String getClassName(String tableName) {
		return getHumpName(ignoreTableNamePrefix(tableName), true);
	}
	
	public static String getJavaDataType(String dataType) {
		switch (dataType.toUpperCase()) {
		case "VARCHAR":
		case "TEXT":
			return "String";
		case "TINYINT":
			return "Byte";
		case "INT":
		case "INTEGER":
			return "Integer";
		case "BIGINT":
			return "Long";
		case "DOUBLE":
			return "Double";
		case "DECIMAL":
			return "BigDecimal";
		case "DATETIME":
			return "Date";
		default:
			throw new RuntimeException("dataType not found");
		}
	}

	public static String getHumpName(String name, Boolean firstUpperCase) {
		int downLineIndex = name.indexOf(downLineChar);
		while(downLineIndex != -1) {
			String prefixStr = name.substring(0, downLineIndex);
			String middleChar = name.substring(downLineIndex+1, downLineIndex+2).toUpperCase();
			String suffixStr = name.substring(downLineIndex+2, name.length());
			name = prefixStr+middleChar+suffixStr;
			//循环
			downLineIndex = name.indexOf(downLineChar);
		}
		if(firstUpperCase != null && firstUpperCase) {
			return name.substring(0,1).toUpperCase()+name.substring(1);
		}
		return name;
	}
	
	public static boolean hasColumn(List<ColumnObject> columns, String name) {
		if(columns != null && !columns.isEmpty()) {
			for(ColumnObject column : columns) {
				if(column.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
}
