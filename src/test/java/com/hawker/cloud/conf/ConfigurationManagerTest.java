package com.hawker.cloud.conf;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConfigurationManagerTest {
	@Test
	public void load() {
		ConfigurationManager confManager=new ConfigurationManager();
		confManager.loadPersistentConfTemplet();
	}
	@Test
	public void generatePersistentConf() {
		ConfigurationManager confManager=new ConfigurationManager();
		ConfBean bean=new ConfBean();
		bean.setId(1);
		bean.setRemoteDeployedDir("/usr/local/tmp");
		List<String> list=new ArrayList<String>();
		list.add("/usr/nginx.log");
		list.add("/usr/log4j.log");
		String result=confManager.generatePersistentConf("127.0.0.1",1,list);
		System.out.println(result);
	}

}
