package com.hawker.flume.vo;

public class RemoteHost {
	String ip;
	int port=22;
	String usrname;
	String psword;
	
	public RemoteHost() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RemoteHost(String ip, int port,String usrname, String psword) {
		super();
		this.ip = ip;
		this.port=port;
		this.usrname = usrname;
		this.psword = psword;
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
	

}
