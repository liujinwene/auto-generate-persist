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
public class ServiceJooqPrinter {
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
		String filePathFormat = "%s/%sService.java";
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(String.format(filePathFormat, daoPath, className))));

		writePackage(out, tableName);
		out.newLine();
		out.newLine();
		writeClassHeader(out, tableName);
		out.newLine();
		writeClassBottom(out);

		out.flush();
		out.close();
	}
	

	private void writeClassBottom(BufferedWriter out) throws IOException {
		out.write("}");
		out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public interface %sService {";
		out.write(String.format(classFormat, className, className));
		out.newLine();
	}
	
	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.service;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}


}
