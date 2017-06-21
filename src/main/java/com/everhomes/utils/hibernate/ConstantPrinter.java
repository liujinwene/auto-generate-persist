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
public class ConstantPrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;

	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		if(!TableConverter.hasColumn(columns, "status")) {
			return;
		}
		
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"constant";
		File directory = new File(daoPath);
		directory.mkdirs();
		//写文件
		String filePathFormat = "%s/%sStatus.java";
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(String.format(filePathFormat, daoPath, className))));

		writePackage(out, tableName);
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
		writeDefaultValue(out);out.newLine();
		writeDefaultField(out);out.newLine();
		writeConstructor(out, tableName);out.newLine();
		writeFromCodeMethod(out, tableName);out.newLine();
		writeGetCodeMethod(out);out.newLine();
		writeGetMsgMethod(out);out.newLine();
	}

	private void writeGetMsgMethod(BufferedWriter out) throws IOException {
		String a1 = "public String getMsg() {";
		String a2 = "return msg;";
		String a3 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 1));out.newLine();
	}

	private void writeGetCodeMethod(BufferedWriter out) throws IOException {
		String a1 = "public byte getCode() {";
		String a2 = "return code;";
		String a3 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 1));out.newLine();
	}

	private void writeFromCodeMethod(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String a1 = "public %sStatus fromCode(Byte code) {";
		String a2 = "if(code == null) {";
		String a3 = "return null;";
		String a4 = "}";
		String a5 = "for(%sStatus r : %sStatus.values()) {";
		String a6 = "if(r.code == code.byteValue()) {";
		String a7 = "return r;";
		String a8 = "}";
		String a9 = "}";
		String a10 = "return null;";
		String a11 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a7, className), 4));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a8, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a9, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a10, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a11, className), 1));out.newLine();
	}

	private void writeConstructor(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String a1 = "private %sStatus(byte code, String msg) {";
		String a2 = "this.code = code;";
		String a3 = "this.msg = msg;";
		String a4 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 1));out.newLine();
	}

	private void writeDefaultField(BufferedWriter out) throws IOException {
		String a1 = "private byte code;";
		String a2 = "private String msg;";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
	}

	private void writeDefaultValue(BufferedWriter out) throws IOException {
		String a1 = "IN_ACTIVE((byte)0, \"未启动\"),";
		String a2 = "WAIT_CONFIRM((byte)1, \"待确认\"),";
		String a3 = "ACTIVE((byte)2, \"启用\");";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 1));out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public enum %sStatus {";
		out.write(String.format(classFormat, className));
		out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.constant;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}
}
