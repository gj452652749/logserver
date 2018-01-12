package com.hawker.cloud.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.hawker.flume.consts.ConfigConsts;

/**
 * flume集中化配置管理器
 * 
 * @author gaojun
 *
 */
@Component
public class ConfigurationManager {
	protected final static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	@Value("${agent.sinks.kafka1.brokerList}")
	String kafkaBrokerList;
	@Autowired
	ConfigConsts appConfig;
	public void addConf() {

	}

	/**
	 * 加载持久化配置模板
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Properties loadPersistentConfTemplet() {
		try {
			// String flumeagent_module;
			Properties flumeagent_module = new Properties();
			Resource resource = new ClassPathResource("taildir2kafka_template.properties"); 
//			flumeagent_module
//					.load(new FileInputStream(ResourceUtils.getFile("classpath:taildir2kafka_template.properties")));
			flumeagent_module
			.load(resource.getInputStream());
			Enumeration emr = flumeagent_module.propertyNames();// 得到配置文件的名字
			while (emr.hasMoreElements()) {
				String strKey = (String) emr.nextElement();
				String strValue = flumeagent_module.getProperty(strKey);
				System.out.println(strKey + "=" + strValue);
			}
			// 服务运行环境对应的kafka
			flumeagent_module.put("agent.sinks.kafka1.brokerList", kafkaBrokerList);
			return flumeagent_module;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除所有source
	 */
	public void removeAllSource(Properties properties) {
		Enumeration emr = properties.propertyNames();// 得到配置文件的名字
		while (emr.hasMoreElements()) {
			String strKey = ((String) emr.nextElement()).trim();
			if ((strKey.startsWith("agent.sources.sources1.filegroups"))
					|| (strKey.startsWith("agent.sources.sources1.headers")))
				properties.remove(strKey);
		}
	}

	/**
	 * 生成所有source
	 */
	public void generateAllSource( Properties properties, String host,List<String> sourceList) {
		StringBuilder groups = new StringBuilder();
		for (int i = 0; i <= sourceList.size() - 1; i++) {
			String source = sourceList.get(i);
			groups.append("s").append(i);
			if (i != (sourceList.size() - 1))
				groups.append(" ");
			String sourceKey = "agent.sources.sources1.filegroups.s" + i;
			properties.put(sourceKey, source);
			String sourceHeaderTopicKey = "agent.sources.sources1.headers.s" + i + ".topic";
			if(source.contains("/")){
				source=source.replaceAll("/","_");
			}
			String topic = host + source;
			properties.put(sourceHeaderTopicKey, topic);
		}
		properties.put("agent.sources.sources1.filegroups", groups.toString());
		properties.put("agent.sinks.kafka1.brokerList", appConfig.getBrokerList());
	}

	/**
	 * 更新所有source
	 */
	public void updateAllSource( Properties properties,String host, List<String> sourceList) {
		removeAllSource(properties);
		generateAllSource(properties, host, sourceList);
	}

	/**
	 * 生成持久化配置,返回生产的文件路径
	 */
	public String generatePersistentConf(String host,int id,List<String> distinctFileList) {
		Properties flumeagent_module_tmp = loadPersistentConfTemplet();
//		if (null != flumeagent_module_tmp) {
//			StringBuilder newCommand = new StringBuilder();
//			String file;
//			for (int i = 0; i <= confBean.files.size() - 1; i++) {
//				file = confBean.files.get(i);
//				newCommand.append("tail -F").append(file);
//				if (i != (confBean.files.size() - 1))
//					newCommand.append(" ");
//			}
//			flumeagent_module_tmp.put("a1.sources.r1.command", newCommand.toString());
//		}
		updateAllSource(flumeagent_module_tmp,host,distinctFileList);
		// String
		// classpath="classpath:"+confBean.getId()+"_flumeagent_module.properties";
		String filePath =  "dtbus_flumeagent_module.properties";
		try {
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
			flumeagent_module_tmp.store(new FileOutputStream(file), "just a test");
			return file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
