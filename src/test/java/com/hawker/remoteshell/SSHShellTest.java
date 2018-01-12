package com.hawker.remoteshell;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.hawker.flume.vo.RemoteHost;

public class SSHShellTest {

	@Test
	public void scpRecursive() {
		SSHShell shell = new SSHShell();
		// shell.recursiveScp(null,null,
		// "C:\\workplace\\server\\apache-flume-1.7.0-bin\\bin", "/usr/local/tmp");
	}

	@Test
	public void scpDir2Remote() {
		SSHShell shell = new SSHShell();
		shell.scpDir2Remote("C:\\workplace\\server\\apache-flume-1.7.0-bin\\bin",
				new RemoteHost("172.18.6.8", 22, "root", "12345678.com"), "/usr/local/tmp");
	}

	@Test
	public void uploadFile2Remote() {
		SSHShell shell = new SSHShell();
		shell.uploadFile2Remote("C:\\workplace\\project\\branch\\logserver\\1_flumeagent_module.properties",
				new RemoteHost("10.0.31.145", 22, "root", "hello@gold1234"), "/usr/local/apache-flume-1.7.0-bin/conf");
	}

	@Test
	public void uploadFile() {
		SSHShell shell = new SSHShell();
		shell.uploadFile2Remote("C:\\Log\\lva_setupnet_20170728131915_0.log",
				new RemoteHost("172.18.2.6", 22, "test", "B@test000"), "/usr/tmp/apache-flume-1.7.0-bin/read");
	}

	@Test
	public void remoteDeploy() {
		SSHShell shell = new SSHShell();
		String remoteDir = "/usr/local/tmp/flume";
		String chomd777Cmd = "chmod 777 " + remoteDir + "/bin/flume-ng";
		// String startupCmd="nohup "+remoteDir+"/bin/flume-ng agent --conf
		// "+remoteDir+"/conf "
		// + "--conf-file "+remoteDir+"/conf/filesink.conf "
		// + "--name a1 -Dflume.root.logger=INFO,console > nohup1.out &";
		String startupCmd = "nohup /usr/local/apache-flume-1.7.0-bin/bin/flume-ng  agent --conf /usr/local/apache-flume-1.7.0-bin/conf  --conf-file /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf  --name a1  -Dflume.root.logger=INFO,console > nohup.out &";
		// String startupCmd="pwd";
		System.out.println("改变文件权限：" + chomd777Cmd);
		System.out.println("执行远程部署命令：" + startupCmd);
		boolean isSuccess = shell.remoteDeploy("C:\\workplace\\tmp\\linux\\apache-flume-1.7.0-bin",
				new RemoteHost("172.18.6.8", 22, "root", "12345678.com"), remoteDir,
				new String[] { chomd777Cmd, startupCmd });
		System.out.println(isSuccess);
	}

	@Test
	public void remoteDeploy2() {
		SSHShell shell = new SSHShell();
		String remoteDir = "/usr/local/tmp/flume";
		String chomd777Cmd = "chmod 777 " + remoteDir + "/bin/flume-ng";
		// String startupCmd="nohup "+remoteDir+"/bin/flume-ng agent --conf
		// "+remoteDir+"/conf "
		// + "--conf-file "+remoteDir+"/conf/filesink.conf "
		// + "--name a1 -Dflume.root.logger=INFO,console > nohup1.out &";
		String startupCmd = "/usr/local/apache-flume-1.7.0-bin/bin/flume-ng  agent --conf /usr/local/apache-flume-1.7.0-bin/conf  --conf-file /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf  --name a1  -Dflume.root.logger=INFO,console > nohup.out";
		// String startupCmd="pwd";
		System.out.println("改变文件权限：" + chomd777Cmd);
		System.out.println("执行远程部署命令：" + startupCmd);
		boolean isSuccess = shell.remoteDeploy("C:\\workplace\\tmp\\linux\\apache-flume-1.7.0-bin",
				new RemoteHost("172.18.6.8", 22, "root", "12345678.com"), remoteDir, chomd777Cmd, startupCmd, 56781,
				30000);
		System.out.println(isSuccess);
	}

	@Test
	public void executeShell() {
		SSHShell shell = new SSHShell();
		/// usr/local/apache-flume-1.7.0-bin/bin/flume-ng agent --conf
		/// /usr/local/apache-flume-1.7.0-bin/conf --conf-file
		/// /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf --name a1
		/// -Dflume.root.logger=INFO,console > nohup.out
		String result = shell.execShell(new RemoteHost("172.18.6.8", 22, "root", "12345678.com"),
				"/usr/local/apache-flume-1.7.0-bin/bin/flume-ng  agent --conf /usr/local/apache-flume-1.7.0-bin/conf  --conf-file /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf  --name a1  -Dflume.root.logger=INFO,console > nohup.out");
		System.out.println(result);
	}

	@Test
	public void executePTYShell() {
		SSHShell shell = new SSHShell();
		/// usr/local/apache-flume-1.7.0-bin/bin/flume-ng agent --conf
		/// /usr/local/apache-flume-1.7.0-bin/conf --conf-file
		/// /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf --name a1
		/// -Dflume.root.logger=INFO,console > nohup.out
		String result = shell.execShellByPTYWithStdout(new RemoteHost("172.18.6.8", 22, "root", "12345678.com"),
				"nohup /usr/local/apache-flume-1.7.0-bin/bin/flume-ng  agent --conf /usr/local/apache-flume-1.7.0-bin/conf  --conf-file /usr/local/apache-flume-1.7.0-bin/conf/filesink.conf  --name a1  -Dflume.root.logger=INFO,console > nohup.out &\r\n");
		System.out.println(result);
	}

	/**
	 * 鉴权
	 */
	@Test
	public void loginAuthentication() {
		SSHShell shell = new SSHShell();
		Boolean bo = shell.loginAuthentication(new RemoteHost("172.18.6.8", 22, "root", "12345678.com"));
		assert bo;
		bo = shell.loginAuthentication(new RemoteHost("172.18.6.8", 22, "root", "12345678"));
		assert bo : "鉴权失败";
	}

	@Test
	public void getListFiles() {
		SSHShell shell = new SSHShell();
		String files = shell.listFiles(new RemoteHost("172.18.6.8", 22, "root", "12345678.com"), "/usr/local/tmp0");
		JSONArray jsonArray = new JSONArray();
		String[] eles = files.split("\n");
		for (String ele : eles) {
			jsonArray.add(ele);
		}
		System.out.println(files);
		System.out.println(jsonArray.toString());
	}
}
