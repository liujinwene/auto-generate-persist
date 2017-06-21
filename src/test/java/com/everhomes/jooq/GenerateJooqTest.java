package com.everhomes.jooq;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.everhomes.CoreServerApp;
import com.everhomes.utils.jooq.GenereateJooqUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoreServerApp.class)
@WebAppConfiguration
public class GenerateJooqTest {
	@Autowired
	private GenereateJooqUtil generateJooqUtil;
	
	@Test
	public void test() throws Exception {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("configuration");
		tableNames.add("order");
		tableNames.add("rechange_good");
		tableNames.add("retreat_good");
		tableNames.add("shop");
		
		generateJooqUtil.generatePersist(tableNames);
	}
}
