package com.everhomes.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.everhomes.CoreServerApp;
import com.everhomes.utils.hibernate.GerenateHibernateUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoreServerApp.class)
@WebAppConfiguration
public class GenerateHibernateTest {
	
	@Autowired
	private GerenateHibernateUtil gerenateHibernateUtil;
	
	@Test
	public void test() throws Exception {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("tbl_custom_layout");
		tableNames.add("tbl_custom_group");
		tableNames.add("tbl_custom_item");
		tableNames.add("tbl_commodity_group");
		tableNames.add("tbl_banner");
		tableNames.add("tbl_commodity_item");
		
		gerenateHibernateUtil.generatePersist(tableNames);
	}
}