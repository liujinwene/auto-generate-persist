package com.everhomes.utils.hibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.everhomes.utils.ColumnObject;

@Component
public class CmdPrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;

	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"cmd";
		File directory = new File(daoPath);
		directory.mkdirs();
		//写文件
		String filePathFormat = "%s/List%sByCdCmd.java";
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(String.format(filePathFormat, daoPath, className))));

		writePackage(out, tableName);
		out.newLine();
		writeImport(out);
		out.newLine();
		writeClassHeader(out, tableName);
		writeClassBody(out, tableName, columns);
		writeClassBottom(out);

		out.flush();
		out.close();
	}
	
	private void writeClassBottom(BufferedWriter out) throws IOException {
		out.write("}");
		out.newLine();
	}

	private void writeClassBody(BufferedWriter out, String tableName, List<ColumnObject> columns) throws IOException {
		String sortsFieldName = "sorts";
		String sortsFieldType = "List<ReSortCmd>";
		if(columns != null && !columns.isEmpty()) {
			for(ColumnObject column : columns) {
				writeField(
						out,
						TableConverter.getHumpName(column.getName(), false),
						TableConverter.getJavaDataType(column.getDataType())
						);
			}
			writeField(out, sortsFieldName, sortsFieldType);
			out.newLine();
			for(ColumnObject column : columns) {
				writeFieldMethod(
						out,
						column.getName(),
						TableConverter.getHumpName(column.getName(), false),
						TableConverter.getJavaDataType(column.getDataType())
						);
			}
			writeFieldMethod(out, sortsFieldName, sortsFieldName, sortsFieldType);
		}
	}
	
	private void writeFieldMethod(BufferedWriter out, String columnName, String humpName, String javaDataType) throws IOException {
		writeGetMethod(out, columnName, humpName, javaDataType);
		writeSetMethod(out, humpName, javaDataType);
		out.newLine();
	}
	
	private void writeSetMethod(BufferedWriter out, String humpName, String javaDataType) throws IOException {
		String methodHeaderFormat = "public void set%s(%s %s) {";
		String methodBodyFormat = "this.%s = %s;";
		String methodBottom = "}";
		out.write(TableConverter.appendSpacing(String.format(
				methodHeaderFormat,
				TableConverter.getHumpName(humpName, true),
				javaDataType,
				humpName
				), 1)
				);
		out.newLine();
		out.write(TableConverter.appendSpacing(String.format(methodBodyFormat, humpName, humpName), 2));
		out.newLine();
		out.write(TableConverter.appendSpacing(methodBottom, 1));
		out.newLine();

	}

	private void writeGetMethod(BufferedWriter out, String columnName, String humpName, String javaDataType) throws IOException {
		String methodHeaderFormat = "public %s get%s() {";
		String methodBodyFormat = "return %s;";
		String methodBottom = "}";

		out.write(TableConverter.appendSpacing(String.format(
				methodHeaderFormat,
				javaDataType,
				TableConverter.getHumpName(humpName, true)
				), 1)
				);
		out.newLine();
		out.write(TableConverter.appendSpacing(String.format(methodBodyFormat, humpName), 2));
		out.newLine();
		out.write(TableConverter.appendSpacing(methodBottom, 1));
		out.newLine();
	}

	private void writeField(BufferedWriter out, String humpName, String javaDataType) throws IOException {
		String fieldNameFormat = "private %s %s;";
		out.write(TableConverter.appendSpacing(String.format(fieldNameFormat, javaDataType, humpName), 1));
		out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public class List%sByCdCmd  extends PageCommand {";
		out.write(String.format(classFormat, className));
		out.newLine();
	}

	private void writeImport(BufferedWriter out) throws IOException {
		String a0 = "import java.util.Date;";
		String a1 = "import java.math.BigDecimal;";
		String a2 = "import java.util.List;";
		String a3 = "import %s.common.command.PageCommand;";
		String a4 = "import %s.common.command.ReSortCmd;";

		out.write(a0);out.newLine();
		out.write(a1);out.newLine();
		out.write(a2);out.newLine();out.newLine();
		out.write(String.format(a3, defaultPackage));out.newLine();
		out.write(String.format(a4, defaultPackage));out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.cmd;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}

}
