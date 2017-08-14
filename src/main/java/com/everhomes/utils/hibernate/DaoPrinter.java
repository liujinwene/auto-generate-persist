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
public class DaoPrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;

	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"dao";
		File directory = new File(daoPath);
		directory.mkdirs();
		//写文件
		String filePathFormat = "%s/I%sDao.java";
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
		
		String a1 = "void addObj(%s entity);";
		String a2 = "void updateObj(%s entity);";
		String a3 = "void deleteObj(%s entity);";
		String a4 = "List<%s> listByCd(List%sByCdCmd cmd);";
		String a41 = "%s findByCd(List%sByCdCmd cmd);";
		String a5 = "Integer findCountByCd(List%sByCdCmd cmd);";
		String a6 = "%s findById(String id);";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a41, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 1));out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public interface I%sDao extends IHibernateBaseDao<%s> {";
		out.write(String.format(classFormat, className, className));
		out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String a1 = "import java.util.List;";
		String a2 = "import %s.basecomponent.orm.hibernate.IHibernateBaseDao;";
		String a3 = "import %s.%s.cmd.List%sByCdCmd;";
		String a4 = "import %s.%s.po.%s;";

		out.write(String.format(a1, defaultPackage));out.newLine();out.newLine();
		out.write(String.format(a2, defaultPackage));out.newLine();
		out.write(String.format(a3, defaultPackage, componentPackage, className));out.newLine();
		out.write(String.format(a4, defaultPackage, componentPackage, className));out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.dao;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}

}
