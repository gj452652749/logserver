package com.hawker.remoteshell;

import java.io.IOException;

import com.hawker.flume.vo.RemoteHost;
import com.trilead.ssh2.Connection;

public class SSH2Facade {
	public Connection getConn(RemoteHost host) {
		Connection conn = new Connection(host.getIp(),host.getPort());
		try {
			conn.connect();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			boolean isValid=conn.authenticateWithPassword(host.getUsrname(), host.getPsword());
			if(!isValid) {
				conn.close();
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return conn;
	}

}
