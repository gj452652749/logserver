package com.hawker.flume.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hawker.cloud.conf.ConfigurationManager;
import com.hawker.flume.pojo.DtFlumeApplyBean;
import com.hawker.flume.pojo.RemoteHost;
import com.hawker.remoteshell.SSHShell;

@Service
public class LogService {
	protected final static Logger logger = LoggerFactory.getLogger(LogService.class);
	@Autowired
	SSHShell sshShell;
	@Autowired
	ConfigurationManager confManager;
	public String apply() {
		return null;
	}
	public boolean volidate(RemoteHost host) {
		return sshShell.loginAuthentication(host);
	}
	public String listFiles(RemoteHost host, String remoteDir) {
		return sshShell.listFiles(host, remoteDir);
	}
	public boolean remoteDeploy(String localDir, RemoteHost host, String remoteDir) {
		String chomd777Cmd="chomd 777 "+remoteDir+"/flume-ng";
		String startupCmd=remoteDir+"/bin/flume-ng  agent --conf "+remoteDir+"/conf  "
				+ "--conf-file "+remoteDir+"/conf/dtbus_flumeagent_module.properties  "
						+ "--name agent  -Dflume.root.logger=INFO,console";
		logger.info("执行远程部署命令："+startupCmd);
		boolean isSuccess=sshShell.remoteDeploy(localDir, host, remoteDir, chomd777Cmd,startupCmd,56781,30000);
		return isSuccess;
	}
	/**
	 * 更新flume配置文件(配置文件存储到zk上)
	 */
	public boolean updateConf(DtFlumeApplyBean bean,String remoteDeployDir,List<String> distinctFileList) {
		String generateConfPath=confManager.generatePersistentConf(bean.getIp(),bean.getId(),distinctFileList);
		return sshShell.uploadFile2Remote(generateConfPath,new RemoteHost(bean.getIp()
				,bean.getPort(),bean.getUsrname(),bean.getPsword())
				,remoteDeployDir+"/conf");
	}
}
