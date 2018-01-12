package com.hawker.flume.vo;

public class DtFlumeApplyBean {
	int id;
	String ip;
	int port=22;
	String usrname;
	String psword;
	String files=null;
	String topics=null;
	String kafkaHosts;
	public DtFlumeApplyBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DtFlumeApplyBean(String ip, int port, String usrname, String psword, String files) {
		super();
		this.ip = ip;
		this.port = port;
		this.usrname = usrname;
		this.psword = psword;
		this.files = files;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsrname() {
		return usrname;
	}

	public void setUsrname(String usrname) {
		this.usrname = usrname;
	}

	public String getPsword() {
		return psword;
	}

	public void setPsword(String psword) {
		this.psword = psword;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getKafkaHosts() {
		return kafkaHosts;
	}

	public void setKafkaHosts(String kafkaHosts) {
		this.kafkaHosts = kafkaHosts;
	}

}
