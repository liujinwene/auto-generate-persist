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
public class ServicePrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;

	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"servicetemp";
		File directory = new File(daoPath);
		directory.mkdirs();
		//写文件
		String filePathFormat = "%s/I%sService.java";
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(String.format(filePathFormat, daoPath, className))));

		writePackage(out, tableName);
		out.newLine();
		writeImport(out, tableName);
		out.newLine();
		writeClassHeader(out, tableName);
		writeClassBody(out, tableName);
		writeClassBottom(out);

		out.flush();
		out.close();
	}
	
	private void writeClassBottom(BufferedWriter out) throws IOException {
		out.write("}");
		out.newLine();
	}

	private void writeClassBody(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String a0 = "//%s";
		String a1 = "void add%s(%s entity) throws Exception;";
		String a2 = "void update%s(%s entity) throws Exception;";
		String a3 = "void delete%s(%s entity) throws Exception;";
		String a4 = "List<%sResp> list%sByCd(List%sByCdCmd cmd) throws Exception;";
		String a41 = "%sResp find%sByCd(List%sByCdCmd cmd) throws Exception;";
		String a5 = "Integer findCount%sByCd(List%sByCdCmd cmd) throws Exception;";
		String a6 = "%sResp find%sById(String id) throws Exception;";
		
		out.write(TableConverter.appendSpacing(String.format(a0,  className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a1,className,  className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2,className,  className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3,className,  className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4,className,  className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a41,className,  className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5,className,  className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6,className,  className), 1));out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public interface I%sService {";
		out.write(String.format(classFormat, className, className));
		out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.service;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}
}
