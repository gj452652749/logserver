package com.hawker.flume;

import com.hawker.flume.pojo.RemoteHost;

/**
 * 负责将flume agent部署到宿主机
 * @author gaojun
 *
 */
public class RemoteDeployer {
	String flumeAgentDir;
	/**
	 * 将agent部署到远程服务器，并启动agent
	 * @param remoteIp
	 * @param usrName
	 * @param psword
	 * @param remoteDir
	 */
	public void deploy(String remoteIp, String usrName, String psword, String remoteDir) {
		//拷贝agent到远程服务器
		//远程启动服务
	}
	/**
	 * 从存储层拿到id对应的远程主机及部署路径
	 * @param id
	 * @return json格式
	 */
	public String getDeployedDirById(int id) {
		String remoteDir= "{\"ip\":\"\",\"\":\"\"}";
		return "";
	}
	/**
	 * 更新flume配置文件(配置文件存储到zk上)
	 */
	public void updateConf(RemoteHost host,String confDir,String sources) {
		String remoteDir="";
	}
}
