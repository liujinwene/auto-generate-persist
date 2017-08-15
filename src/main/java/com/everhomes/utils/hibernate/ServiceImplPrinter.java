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
public class ServiceImplPrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;
	
	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"servicetemp/impl";
		File directory = new File(daoPath);
		directory.mkdirs();
		//写文件
		String filePathFormat = "%s/%sServiceImpl.java";
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(String.format(filePathFormat, daoPath, className))));

		writePackage(out, tableName);
		out.newLine();
		writeImport(out, tableName);
		out.newLine();
		writeClassHeaderAutowited(out, tableName);
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
		String className = TableConverter.getClassName(tableName);

		writeAddMethod(out, className);out.newLine();
		writeUpdateMethod(out, className);out.newLine();
		writeDeleteMethod(out, className);out.newLine();
		writeListByCdMethod(out, className);out.newLine();
		writeFindByCdMethod(out, className);out.newLine();
		writeFindCountByCdMethod(out, className);out.newLine();
		writeFindIdMethod(out, className);out.newLine();
	}

	private void writeFindIdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public %sResp find%sById(String id) throws Exception {";

		String a41 = "%s entity = %sDao.findById(id);";
		String a42 = "if(entity != null) {";
		String a43 = "return ObjectConvertUtil.convertForSampleType(entity, %sResp.class);";
		String a44 = "}";
		String a45 = "return null;";
		
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		
		out.write(TableConverter.appendSpacing(String.format(a41, className, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a42, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a43, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a44, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a45, className), 2));out.newLine();
		
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}

	private void writeFindCountByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public Integer findCount%sByCd(List%sByCdCmd cmd) throws Exception {";
		
		String a41 = "return %sDao.findCountByCd(cmd);";
		
		String a7 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		
		out.write(TableConverter.appendSpacing(String.format(a41, TableConverter.getHumpName(className, false)), 2));out.newLine();
		
		out.write(TableConverter.appendSpacing(a7, 1));out.newLine();
	}
	
	private void writeFindByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public %sResp find%sByCd(List%sByCdCmd cmd) throws Exception {";
		
		String a41 = "%s entity = %sDao.findByCd(cmd);";
		String a42 = "if(entity != null) {";
		String a43 = "return ObjectConvertUtil.convertForSampleType(entity, %sResp.class);";
		String a44 = "}";
		String a45 = "return null;";
		
		String a9 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className, className), 1));out.newLine();
		
		out.write(TableConverter.appendSpacing(String.format(a41, className, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a42, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a43, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a44, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a45, className), 2));out.newLine();
		
		out.write(TableConverter.appendSpacing(String.format(a9, className), 1));out.newLine();
	}


	private void writeListByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public List<%sResp> list%sByCd(List%sByCdCmd cmd) throws Exception {";
		
		String a41 = "List<%s> entitys = %sDao.listByCd(cmd);";
		String a42 = "List<%sResp> list = new ArrayList<%sResp>();";
		String a43 = "if(entitys != null && !entitys.isEmpty()) {";
		String a44 = "for(%s r : entitys) {";
		String a45 = "list.add(ObjectConvertUtil.convertForSampleType(r, %sResp.class));";
		String a46 = "}";
		String a47 = "}";
		String a48 = "return list;";
		
		String a8 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className, className), 1));out.newLine();
		
		out.write(TableConverter.appendSpacing(String.format(a41, className, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a42, className, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(a43, 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a44, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a45, className), 4));out.newLine();
		out.write(TableConverter.appendSpacing(a46, 3));out.newLine();
		out.write(TableConverter.appendSpacing(a47, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a48, 2));out.newLine();
		
		out.write(TableConverter.appendSpacing(a8, 1));out.newLine();
	}


	private void writeDeleteMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public void delete%s(%s entity) throws Exception {";
		String a4 = "%sDao.deleteObj(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeUpdateMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public void update%s(%s entity) throws Exception {";
		String a4 = "%sDao.updateObj(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeAddMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a3 = "public void add%s(%s entity) throws Exception {";
		String a4 = "%sDao.addObj(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, TableConverter.getHumpName(className, false)), 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public class %sServiceImpl implements I%sService {";
		out.write(String.format(classFormat, className, className, className));
		out.newLine();
	}
	
	private void writeClassHeaderAutowited(BufferedWriter out, String tableName) throws IOException {
		String entityStr = "@service";
		out.write(entityStr);out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.service.impl;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}
}
