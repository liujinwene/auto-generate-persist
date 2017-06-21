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
public class DaoImplJooqPrinter {
	@Value("${jooq_default_package}")
	private String defaultPackage;
	@Value("${jooq_root_path}")
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
		String filePathFormat = "%s/%sDaoImpl.java";
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
		tableName = TableConverter.ignoreTableNamePrefix(tableName);

		out.newLine();
		writeDslField(out);out.newLine();
		writeCreateMethod(out, className);out.newLine();
		writeUpdateMethod(out, className);out.newLine();
		writeDeleteMethod(out, className);out.newLine();
		writeListByCdMethod(out, tableName, className);out.newLine();
		writeFindByCdMethod(out, tableName, className);out.newLine();
		writeFindIdMethod(out, tableName, className);out.newLine();
		writeFindCountByCdMethod(out, tableName, className);out.newLine();
		writeAddConditionMethod(out, tableName, className, columns);out.newLine();
	}

	private void writeAddConditionMethod(BufferedWriter out, String tableName, String className, List<ColumnObject> columns) throws IOException {
		String a1 = "private void addCondition(SelectQuery<%sRecord> query, List%sByCdCmd cmd) {";
		String a2 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className, className), 1));out.newLine();
		if(columns != null && !columns.isEmpty()) {
			writeConditionBody(out, tableName, className, columns);
		}
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
	}


	private void writeConditionBody(BufferedWriter out, String tableName, String className, List<ColumnObject> columns) throws IOException {
		String a1 = "Condition condition = null;";
		String a2 = "query.addConditions(condition);";
		
		out.write(TableConverter.appendSpacing(a1, 2));out.newLine();
		for(ColumnObject column : columns) {
			writeFieldCondition(out, tableName, className, column);
		}
		out.write(TableConverter.appendSpacing(a2, 2));out.newLine();
	}


	private void writeFieldCondition(BufferedWriter out, String tableName, String className, ColumnObject column) throws IOException {
		
		
		String upperTableName = tableName.toUpperCase();
		String upperColumnName = column.getName().toUpperCase();
		String upperFieldName = TableConverter.getHumpName(column.getName(), true);
		
		String a1 = "if(cmd.get%s() != null) {";
		String a2 = "if(condition == null) {";
		String a3 = null;
		if(TableConverter.getJavaDataType(column.getDataType()).equals("Date")) {
			a3 = "condition = Tables.%s.%s.eq(new Timestamp(cmd.get%s().getTime()));";
		} else {
			a3 = "condition = Tables.%s.%s.eq(cmd.get%s());";
		}
		String a4 = "} else {";
		String a5 = null;
		if(TableConverter.getJavaDataType(column.getDataType()).equals("Date")) {
			a5 = "condition = condition.and(Tables.%s.%s.eq(new Timestamp(cmd.get%s().getTime())));";
		} else {
			a5 = "condition = condition.and(Tables.%s.%s.eq(cmd.get%s()));";
		}
		String a6 = "}";
		String a7 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, upperFieldName), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, upperTableName, upperColumnName, upperFieldName), 4));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, upperTableName, upperColumnName, upperFieldName), 4));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a7, className), 2));out.newLine();
	}


	private void writeFindCountByCdMethod(BufferedWriter out, String tableName, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public Integer findCountByCd(List%sByCdCmd cmd) {";
		String a3 = "SelectQuery<%sRecord>  query = dsl.selectQuery(Tables.%s);";
		String a4 = "addCondition(query, cmd);";
		String a5 = "return dsl.fetchCount(query);";
		String a6 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, tableName.toUpperCase()), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 1));out.newLine();
	}


	private void writeFindIdMethod(BufferedWriter out, String tableName, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public %sRecord findById(Long id) {";
		String a3 = "SelectQuery<%sRecord>  query = dsl.selectQuery(Tables.%s);";
		String a4 = "query.addConditions(Tables.%s.ID.eq(id));";
		String a5 = "return query.fetchOne();";
		String a6 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, tableName.toUpperCase()), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, tableName.toUpperCase()), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 1));out.newLine();
	}


	private void writeFindByCdMethod(BufferedWriter out, String tableName, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public %sRecord findByCd(List%sByCdCmd cmd) {";
		String a3 = "SelectQuery<%sRecord>  query = dsl.selectQuery(Tables.%s);";
		String a4 = "addCondition(query, cmd);";
		String a5 = "return query.fetchOne();";
		String a6 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, tableName.toUpperCase()), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 1));out.newLine();
	}


	private void writeListByCdMethod(BufferedWriter out, String tableName, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public List<%sRecord> listByCd(List%sByCdCmd cmd) {";
		String a3 = "SelectQuery<%sRecord>  query = dsl.selectQuery(Tables.%s);";
		String a4 = "addCondition(query, cmd);";
		String a5 = "return query.fetch();";
		String a6 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, tableName.toUpperCase()), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 1));out.newLine();
	}


	private void writeDeleteMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public void delete(%sRecord record) {";
		String a3 = "dsl.executeDelete(record);";
		String a4 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 1));out.newLine();
	}


	private void writeUpdateMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public void update(%sRecord record) {";
		String a3 = "dsl.executeUpdate(record);";
		String a4 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 1));out.newLine();
	}


	private void writeCreateMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "public void create(%sRecord record) {";
		String a3 = "dsl.executeInsert(record);";
		String a4 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 1));out.newLine();
	}


	private void writeDslField(BufferedWriter out) throws IOException {
		String a1 = "@Autowired";
		String a2 = "private DSLContext dsl;";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
	}


	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public class %sDaoImpl implements %sDao {";
		out.write(String.format(classFormat, className, className));
		out.newLine();
	}
	
	private void writeClassHeaderAutowited(BufferedWriter out, String tableName) throws IOException {
		String entityStr = "@Component";
		out.write(entityStr);out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String a0 = "import java.sql.Timestamp;";
		String a1 = "import java.util.List;";
		
		String a21 = "import org.jooq.Condition;";
		String a2 = "import org.jooq.DSLContext;";
		String a22 = "import org.jooq.SelectQuery;";
		String a4 = "import org.springframework.beans.factory.annotation.Autowired;";
		String a5 = "import org.springframework.stereotype.Component;";
		
		String a7 = "import %s.%s.cmd.List%sByCdCmd;";
		String a10 = "import %s.schema.Tables;";
		String a11 = "import %s.schema.tables.records.%sRecord;";

		out.write(a0);out.newLine();
		out.write(a1);out.newLine();
		out.newLine();
		
		out.write(a21);out.newLine();
		out.write(a2);out.newLine();
		out.write(a22);out.newLine();
		out.write(a4);out.newLine();
		out.write(a5);out.newLine();out.newLine();
		
		out.write(String.format(a7, defaultPackage, componentPackage, className));out.newLine();
		out.write(String.format(a10, defaultPackage));out.newLine();
		out.write(String.format(a11, defaultPackage, className));out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.dao;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}
}
