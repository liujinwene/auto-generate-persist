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
public class DaoImplPrinter {
	@Value("${default_package}")
	private String defaultPackage;
	@Value("${root_path}")
	private String rootPath;
	
	public void print(String tableName, List<ColumnObject> columns) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		String className = TableConverter.getClassName(tableName);

		String componentPath = rootPath+File.separator+componentPackage;
		//新建目录
		String daoPath = componentPath+File.separator+"dao/impl";
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

		writeAddMethod(out, className);out.newLine();
		writeUpdateMethod(out, className);out.newLine();
		writeDeleteMethod(out, className);out.newLine();
		writeListByCdMethod(out, className);out.newLine();
		writeFindByCdMethod(out, className);out.newLine();
		writeFindCountByCdMethod(out, className);out.newLine();
		writeFindIdMethod(out, className);out.newLine();
		writerPageByMethod(out, className);out.newLine();
		writeAddConditionMethod(out, className, columns);out.newLine();
		writeOrderByMethod(out, className, columns);out.newLine();
	}

	private void writeOrderByMethod(BufferedWriter out, String className, List<ColumnObject> columns) throws IOException {
		String a1 = "private void orderBy(Criteria criteria, List%sByCdCmd cmd) {";
		String a2 = "if(cmd.getSorts() != null && !cmd.getSorts().isEmpty()) {";
		String a3 = "for(ReSortCmd sort : cmd.getSorts()) {";
		String a4 = "if(StringUtils.isNotBlank(sort.getSortField()) && StringUtils.isNotBlank(sort.getSortType())) {";
		String a5 = "}";
		String a6 = "}";
		String a7 = "}";
		String a8 = "}";
		
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		if(!TableConverter.hasColumn(columns, "defalut_order")) {
			out.write(TableConverter.appendSpacing(String.format(a2, className), 2));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a3, className), 3));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a4, className), 4));out.newLine();
			writeOrderByBody(out, className, columns);
			out.write(TableConverter.appendSpacing(String.format(a5, className), 4));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a6, className), 3));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a7, className), 2));out.newLine();
		}
		out.write(TableConverter.appendSpacing(String.format(a8, className), 1));out.newLine();
	}


	private void writeOrderByBody(BufferedWriter out, String className, List<ColumnObject> columns) throws IOException {
		String columnName = "default_order";
		writeColumnOrderBy(out, columnName);
	}


	private void writeColumnOrderBy(BufferedWriter out, String columnName) throws IOException {
		String fieldName = TableConverter.getHumpName(columnName, false);
		
		String a1 = "if(sort.getSortField().equals(\"%s\")) {";
		String a2 = "if(sort.getSortType().equals(\"asc\")) {";
		String a3 = "criteria.addOrder(Order.asc(\"%s\"));";
		String a4 = "} else {";
		String a5 = "criteria.addOrder(Order.desc(\"%s\"));";
		String a6 = "}";
		String a7 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, fieldName), 5));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, fieldName), 6));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, fieldName), 7));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, fieldName), 6));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, fieldName), 7));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, fieldName), 6));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a7, fieldName), 5));out.newLine();
		out.newLine();
	}


	private void writeAddConditionMethod(BufferedWriter out, String className, List<ColumnObject> columns) throws IOException {
		String header = "private void addCondition(Criteria criteria, List%sByCdCmd cmd) {";
		String bottom = "}";
		
		out.write(TableConverter.appendSpacing(String.format(header,  className), 1));out.newLine();
		//write body
		if(columns != null && !columns.isEmpty()) {
			for(ColumnObject column : columns) {
				writeCondition(out, column.getName(), column.getDataType());
			}
		}
		out.write(TableConverter.appendSpacing(bottom, 1));out.newLine();
		
	}

	private void writeCondition(BufferedWriter out, String columnName, String javaDataType) throws IOException {
		String fieldName = TableConverter.getHumpName(columnName, false);
		String firstUpperFieldName = TableConverter.getHumpName(columnName, true);
		if(javaDataType.equals("String")) {
			String a1 = "if(StringUtils.isNotBlank(cmd.get%s())) {";
			String a2 = "criteria.add(Restrictions.eq(\"%s\", cmd.get%s()));";
			String a3 = "}";
			out.write(TableConverter.appendSpacing(String.format(a1, firstUpperFieldName), 2));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a2, fieldName, firstUpperFieldName), 3));out.newLine();
			out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		} else {
			String a1 = "if(cmd.get%s() != null) {";
			String a2 = "criteria.add(Restrictions.eq(\"%s\", cmd.get%s()));";
			String a3 = "}";
			out.write(TableConverter.appendSpacing(String.format(a1, firstUpperFieldName), 2));out.newLine();
			out.write(TableConverter.appendSpacing(String.format(a2, fieldName, firstUpperFieldName), 3));out.newLine();
			out.write(TableConverter.appendSpacing(a3, 2));out.newLine();
		}
	}

	private void writerPageByMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "private void pageBy(Criteria criteria, List%sByCdCmd cmd) {";
		String a2 = "if(cmd.getPageSize() != null){";
		String a3 = "criteria.setMaxResults(cmd.getPageSize());";
		String a4 = "}";
		String a5 = "if(cmd.getOffset() != null){";
		String a6 = "criteria.setFirstResult(cmd.getOffset());";
		String a7 = "}";
		String a8 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a3, 3));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a6, 3));out.newLine();
		out.write(TableConverter.appendSpacing(a7, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a8, 1));out.newLine();
	}


	private void writeFindIdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional(readOnly = true)";
		String a3 = "public %s findById(String id) {";
		String a4 = "return get(\"id\", id);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeFindCountByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional(readOnly = true)";
		String a3 = "public Integer findCountByCd(List%sByCdCmd cmd) {";
		String a4 = "Criteria criteria = createCriteria();";
		String a5 = "addCondition(criteria, cmd);";
		String a6 = "return getRowCount(criteria);";
		String a7 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a6, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a7, 1));out.newLine();
	}
	
	private void writeFindByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional(readOnly = true)";
		String a3 = "public %s findByCd(List%sByCdCmd cmd) {";
		String a4 = "List<%s> list = listByCd(cmd);";
		String a5 = "if(list != null && !list.isEmpty()) {";
		String a6 = "return list.get(0);";
		String a7 = "}";
		String a8 = "return null;";
		String a9 = "}";
		
		out.write(TableConverter.appendSpacing(String.format(a1, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a2, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a4, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a5, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a6, className), 3));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a7, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a8, className), 2));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a9, className), 1));out.newLine();
	}


	private void writeListByCdMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional(readOnly = true)";
		String a3 = "public List<%s> listByCd(List%sByCdCmd cmd) {";
		String a4 = "Criteria criteria = createCriteria();";
		String a5 = "addCondition(criteria, cmd);";
		String a52 = "orderBy(criteria, cmd);";
		String a6 = "pageBy(criteria, cmd);";
		String a7 = "return criteria.list();";
		String a8 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a52, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a6, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a7, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a8, 1));out.newLine();
	}


	private void writeDeleteMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional";
		String a3 = "public void deleteObj(%s entity) {";
		String a4 = "delete(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeUpdateMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional";
		String a3 = "public void updateObj(%s entity) {";
		String a4 = "update(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeAddMethod(BufferedWriter out, String className) throws IOException {
		String a1 = "@Override";
		String a2 = "@Transactional";
		String a3 = "public void addObj(%s entity) {";
		String a4 = "save(entity);";
		String a5 = "}";
		
		out.write(TableConverter.appendSpacing(a1, 1));out.newLine();
		out.write(TableConverter.appendSpacing(a2, 1));out.newLine();
		out.write(TableConverter.appendSpacing(String.format(a3, className), 1));out.newLine();
		out.write(TableConverter.appendSpacing(a4, 2));out.newLine();
		out.write(TableConverter.appendSpacing(a5, 1));out.newLine();
	}


	private void writeClassHeader(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		
		String classFormat = "public class %sDaoImpl extends HibernateBaseDao<%s> implements I%sDao {";
		out.write(String.format(classFormat, className, className, className));
		out.newLine();
	}
	
	private void writeClassHeaderAutowited(BufferedWriter out, String tableName) throws IOException {
		String entityStr = "@Repository";
		out.write(entityStr);out.newLine();
	}

	private void writeImport(BufferedWriter out, String tableName) throws IOException {
		String className = TableConverter.getClassName(tableName);
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String a1 = "import java.util.List;";
		
		String a21 = "import org.apache.commons.lang.StringUtils;";
		String a2 = "import org.hibernate.Criteria;";
		String a22 = "import org.hibernate.criterion.Order;";
		String a4 = "import org.hibernate.criterion.Restrictions;";
		String a5 = "import org.springframework.stereotype.Repository;";
		String a6 = "import org.springframework.transaction.annotation.Transactional;";
		
		String a7 = "import %s.basecomponent.orm.hibernate.impl.HibernateBaseDao;";
		String a10 = "import %s.%s.cmd.List%sByCdCmd;";
		String a11 = "import %s.%s.dao.I%sDao;";
		String a12 = "import %s.%s.po.%s;";
		String a13 = "import %s.common.command.ReSortCmd;";

		out.write(a1);out.newLine();out.newLine();
		
		out.write(a21);out.newLine();
		out.write(a2);out.newLine();
		out.write(a22);out.newLine();
		out.write(a4);out.newLine();
		out.write(a5);out.newLine();
		out.write(a6);out.newLine();out.newLine();
		
		out.write(String.format(a7, defaultPackage));out.newLine();
		out.write(String.format(a10, defaultPackage, componentPackage, className));out.newLine();
		out.write(String.format(a11, defaultPackage, componentPackage, className));out.newLine();
		out.write(String.format(a12, defaultPackage, componentPackage, className));out.newLine();
		out.write(String.format(a13, defaultPackage));out.newLine();
	}

	private void writePackage(BufferedWriter out, String tableName) throws IOException {
		String componentPackage = TableConverter.getComponentPackage(tableName);
		
		String packageFormat = "package %s.%s.dao.impl;";
		out.write(String.format(packageFormat, defaultPackage, componentPackage));
		out.newLine();
	}

}
