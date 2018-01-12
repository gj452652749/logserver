package com.hawker.flume.consts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ConfigConsts {

	@Value("${agent.sinks.kafka1.brokerList}")
	String brokerList;

	public String getBrokerList() {
		return brokerList;
	}
	
}
