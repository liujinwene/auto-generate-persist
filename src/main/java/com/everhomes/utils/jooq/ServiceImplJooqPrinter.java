package com.everhomes.utils.jooq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.everhomes.utils.ColumnObject;
import com.everhomes.utils.hibernate.TableConverter;

@Component
public class ServiceImplJooqPrinter {
	@Value("${jooq_default_package}")
	private String defaultPackage;
	@Value("${jooq_root_path}")
	private String rootPath;
	
	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"service";
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
		out.newLine();
		out.newLine();
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
		String lowerClassName = TableConverter.getHumpName(tableName, false);
		tableName = TableConverter.ignoreTableNamePrefix(tableName);

		String a1 = "private static final Logger LOGGER = LoggerFactory.getLogger(%sServiceImpl.class);";
		String a2 = "@Autowired";
		String a3 = "private %sDao %sDao;";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, lowerClassName), 1));out.newLine();
		
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public class %sServiceImpl implements %sService {";
		out.write(String.format(classFormat, className, className));
		out.newLine();
	}
	
	private void writeClassHeaderAutowited(BufferedWriter out, String tableName) throws IOException {
		String entityStr = "@Service";
		out.write(entityStr);out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String a1 = "import org.slf4j.Logger;";
		String a2 = "import org.slf4j.LoggerFactory;";
		String a3 = "import org.springframework.beans.factory.annotation.Autowired;";
		String a4 = "import org.springframework.stereotype.Service;";
		
		String a5 = "import %s.%s.dao.%sDao;";

		out.write(a1);out.newLine();
		out.write(a2);out.newLine();
		out.write(a3);out.newLine();
		out.write(a4);out.newLine();
		out.newLine();
		
		out.write(String.format(a5, defaultPackage, componentPackage, className));out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.service;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}

}
