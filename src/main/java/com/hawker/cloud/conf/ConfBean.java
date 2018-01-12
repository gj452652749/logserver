package com.hawker.cloud.conf;

import java.util.List;

public class ConfBean {
	int id;
	String remoteDeployedDir;// agent部署的远程路径
	String files;// 需要监听的文件集合

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRemoteDeployedDir() {
		return remoteDeployedDir;
	}

	public void setRemoteDeployedDir(String remoteDeployedDir) {
		this.remoteDeployedDir = remoteDeployedDir;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	

}