package com.everhomes.utils.hibernate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.everhomes.utils.ColumnObject;

@Component
public class GerenateHibernateUtil {
	@Value("${jdbc_driverClassName}")
	private String driverName;
	@Value("${jdbc_url}")
	private String url;
	@Value("${jdbc_username}")
	private String userName;
	@Value("${jdbc_password}")
	private String password;
	@Value("${table_schema}")
	private String tableSchema;

	@Autowired
	private PoPrinter poPrinter;
	@Autowired
	private DaoPrinter daoPrinter;
	@Autowired
	private DaoImplPrinter daoImplPrinter;
	@Autowired
	private ServicePrinter servicePrinter;
	@Autowired
	private ServiceImplPrinter serviceImplPrinter;
	@Autowired
	private CmdPrinter cmdPrinter;
	@Autowired
	private RespPrinter respPrinter;
	@Autowired
	private ConstantPrinter constantPrinter;

	
	public void generatePersist(List<String> tableNames) throws Exception {
		//connnect database
		Class.forName(driverName);
		Connection con = DriverManager.getConnection(url, userName, password);
		Statement state = con.createStatement();
		String sql = "select column_name,data_type from columns where table_name='%s' and table_schema='%s' order by ordinal_position";
		for(String tableName : tableNames) {
			//columns
			ResultSet result = state.executeQuery(String.format(sql, tableName, tableSchema));
			List<ColumnObject> columns = new ArrayList<ColumnObject>();
			while(result.next()) {
				ColumnObject column = new ColumnObject();
				column.setName(result.getString(1));
				column.setDataType(result.getString(2));
				columns.add(column);
			}
			genereatePo(tableName, columns);
			genereateDao(tableName, columns);
			genereateService(tableName, columns);
			genereateCmd(tableName, columns);
			generateResp(tableName, columns);
			generateConstant(tableName, columns);
			result.close();
		}
		state.close();
		con.close();
	}


	private void generateConstant(String tableName, List<ColumnObject> columns) throws IOException {
		constantPrinter.print(tableName, columns);
	}

	private void generateResp(String tableName, List<ColumnObject> columns) throws IOException {
		respPrinter.print(tableName, columns);
	}

	private void genereateCmd(String tableName, List<ColumnObject> columns) throws IOException {
		cmdPrinter.print(tableName, columns);
	}
	
	private void genereateService(String tableName, List<ColumnObject> columns) throws IOException {
		servicePrinter.print(tableName, columns);
		serviceImplPrinter.print(tableName, columns);
	}

	private void genereateDao(String tableName, List<ColumnObject> columns) throws Exception {
		daoPrinter.print(tableName, columns);
		daoImplPrinter.print(tableName, columns);
	}

	private void genereatePo(String tableName, List<ColumnObject> columns) throws Exception {
		poPrinter.print(tableName, columns);
	}

}
