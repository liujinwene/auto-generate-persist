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
public class PoJooqPrinter {
	@Value("${jooq_default_package}")
	private String defaultPackage;
	@Value("${jooq_root_path}")
	private String rootPath;
	
	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String poPath = componentPath+File.separator+"po";
		File directory = new File(poPath);
		directory.mkdirs();
		//写文件
		String filePath = poPath+File.separator+className+".java";

		BufferedWriter out = new BufferedWriter(new FileWriter(new File(filePath)));

		writePackage(out, TableConverter.getComponentPackage(tableName));
		out.newLine();
		writeImport(out);
		out.newLine();
		writeClassHeader(out, tableName);
		writeClassBody(out, columns);
		writeClassBottom(out);

		out.flush();
		out.close();
	}

	private void writeClassBody(BufferedWriter out, List<ColumnObject> columns) throws IOException {
		if(columns != null && !columns.isEmpty()) {
			for(ColumnObject column : columns) {
				writeField(
						out,
						TableConverter.getHumpName(column.getName(), false),
						TableConverter.getJavaDataType(column.getDataType())
						);
			}
			out.newLine();
			for(ColumnObject column : columns) {
				writeFieldMethod(
						out,
						column.getName(),
						TableConverter.getHumpName(column.getName(), false),
						TableConverter.getJavaDataType(column.getDataType())
						);
			}
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

	private void writeClassBottom(BufferedWriter out) throws IOException {
		out.write("}");
		out.newLine();
	}

	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		String classFormat = "public class %s {";
		out.write(String.format(classFormat, className));
		out.newLine();
	}

	private void writeImport(BufferedWriter out) throws IOException {
		String a2 = "import java.util.Date;";
		out.write(a2);out.newLine();
	}

	private void writePackage(BufferedWriter out, String componentPackage) throws IOException {
		String packageFormat = "package %s.%s.po;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}

}
