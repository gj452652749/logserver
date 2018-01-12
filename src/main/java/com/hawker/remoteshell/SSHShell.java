package com.hawker.remoteshell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hawker.flume.vo.RemoteHost;
import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.SFTPv3DirectoryEntry;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;
import com.trilead.ssh2.util.IOUtils;

@Component
public class SSHShell {
	protected final static Logger logger = LoggerFactory.getLogger(SSHShell.class);
	private static String DEFAULTCHART = "UTF-8";
	SSH2Facade ssh2Client = new SSH2Facade();

	/**
	 * 将本地文件拷贝到远程
	 */
	private void scpFile2Remote(SCPClient scpClient, String localFile, String remoteDir) {
		logger.info("开始拷贝文件到:" + remoteDir);
		try {
			scpClient.put(localFile, remoteDir,"0777");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 新建远程目录
	 * 
	 * @param scpClient
	 * @param localFile
	 * @param remoteDir
	 */
	private void mkRemoteDir(Connection conn, String remoteDir) {
		logger.info("开始新建目录文件:" + remoteDir);
		String shellCmd = "mkdir -m 777 -p " + remoteDir;
		// 此处需要阻塞创建
		execShellWithStdout(conn, shellCmd);
	}

	private String getRelativePathUri(String rootLocalPathStr, String dirStr) {
		int i = 0;
		for (; i <= (rootLocalPathStr.length() - 1); i++) {
			if (rootLocalPathStr.charAt(i) != dirStr.charAt(i))
				break;
		}
		return dirStr.substring(i);
	}

	private void recursiveScp(Connection conn, SCPClient scpClient, String localDir, String remoteRootDir) {
		Path rootLocalPath = Paths.get(localDir);
		try {
			Files.walkFileTree(rootLocalPath, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					// return super.preVisitDirectory(dir, attrs);
					String relativePath = getRelativePathUri(rootLocalPath.toString(), dir.toString());
					String remoteDir = remoteRootDir + relativePath.replace("\\", "/");
					mkRemoteDir(conn, remoteDir);
					// System.out.println("正在访问：" + dir + "目录"+relativePath);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					// return super.visitFile(file, attrs);
					String relativePath = getRelativePathUri(rootLocalPath.toString(), (file.getParent()).toString());
					String remoteDir = remoteRootDir + relativePath.replace("\\", "/");
					// System.out.println("\t正在访问" + file + "文件"+relativePath);
					scpFile2Remote(scpClient, file.toString(), remoteDir);
					return FileVisitResult.CONTINUE; // 没找到继续找
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean uploadFile2Remote(String localFilePath, RemoteHost host, String remoteDir) {
		logger.info("更新文件到远程:" + host.getIp()+"/"+remoteDir);
		Connection conn = ssh2Client.getConn(host);
		if (null == conn) {
			logger.warn("更新文件主机验证失败！");
			return false;
		}
		SCPClient scpClient = null;
		try {
			scpClient = conn.createSCPClient();
			scpFile2Remote(scpClient, localFilePath, remoteDir);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}

	/**
	 * 将本地文件拷贝到远程
	 */
	public void scpDir2Remote(String localDir, RemoteHost host, String remoteDir) {
		Connection conn = ssh2Client.getConn(host);
		if (null == conn)
			return;
		SCPClient scpClient = null;
		SFTPv3Client client = null;
		try {
			scpClient = conn.createSCPClient();
			client = new SFTPv3Client(conn);
			logger.info("开始拷贝目录...");
			// 递归遍历目录及文件，拷贝到远程服务器对应目录
			mkRemoteDir(conn, remoteDir);
			recursiveScp(conn, scpClient, localDir, remoteDir);
			// scpClient.put("c:\\workplace\\tmp\\log.txt", "/usr/local/tmp/");
			logger.info("拷贝目录完成！");
			Vector<SFTPv3DirectoryEntry> files = client.ls("/usr/local/tmp");
			for (SFTPv3DirectoryEntry item : files) {
				System.out.println("文件名称: " + item.filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
			conn.close();
		}
	}

	/**
	 * 获取远程目录的子目录和文件
	 */
	public String listFiles(RemoteHost host, String remoteDir) {
		String result = null;
		Connection conn = ssh2Client.getConn(host);
		if (null == conn)
			return null;
//		String shell = "ls " + remoteDir;
		String shell="find "+remoteDir+" -maxdepth 1 -type d  -printf \"%f\\n\"";
		result = execShellWithStdout(conn, shell);
		conn.close();
		return result;
	}

	/**
	 * 登陆鉴权
	 */
	public boolean loginAuthentication(RemoteHost host) {
		Connection conn = ssh2Client.getConn(host);
		if (null == conn) {
			return false;
		}
		return true;
	}

	/**
	 * 执行shell，返回是否成功
	 * 
	 * @param conn
	 * @param shellCmd
	 * @return
	 */
	private Boolean execShell(Connection conn, String shellCmd) {
		StringBuilder result = new StringBuilder();
		Session session = null;
		try {
			// 一个session只能执行一次shell
			session = conn.openSession();
			session.execCommand(shellCmd);
			session.getStdout();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	public boolean isServiceExists(Connection conn, int port) {
		//通过验证端口是否开启判断服务是否已存在
		String executeInfo;
		executeInfo=execShellWithStdout(conn, "lsof -i:"+port);
		if(!StringUtils.isEmpty(executeInfo)) {
			logger.warn("服务已存在！"+executeInfo);
			return true;
		}
		return false;
	}
	/**
	 * 执行shell，返回是否成功
	 * 
	 * @param conn
	 * @param shellCmd
	 * @return
	 */
	private Boolean execStartupShell(Connection conn, String shellCmd, int port, long timeout) {
		//通过验证端口是否开启判断服务是否已存在
		String executeInfo;
//		executeInfo=execShellWithStdout(conn, "lsof -i:"+port);
//		if(!StringUtils.isEmpty(executeInfo)) {
//			logger.warn("服务已存在！"+executeInfo);
//			return true;
//		}
		StringBuilder result = new StringBuilder();
		Session session = null;
		try {
			// 一个session只能执行一次shell
			session = conn.openSession();
			session.execCommand(shellCmd);
			long begin = System.currentTimeMillis();
			while ((System.currentTimeMillis() - begin) <= timeout) {
				try {
					Thread.sleep(10000);// 暂停10s再关闭shell
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//通过验证端口是否开启判断脚本是否成功运行
				executeInfo=execShellWithStdout(conn, "lsof -i:"+port);
				if(!StringUtils.isEmpty(executeInfo)) {
					logger.info("启动成功："+executeInfo);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return false;
	}

	/**
	 * 执行shell，返回执行信息
	 * 
	 * @param conn
	 * @param shellCmd
	 * @return
	 */
	private String execShellWithStdout(Connection conn, String shellCmd) {
		//StringBuilder result = new StringBuilder();
		String results = "";
		Session session = null;
		try {
			// 一个session只能执行一次shell
			session = conn.openSession();
			session.execCommand(shellCmd);
			InputStream stdout = new StreamGobbler(session.getStdout());
			try {
				// 阻塞等待流传输过来，限定30k
				session.waitForCondition(ChannelCondition.EOF, 30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buffer = new byte[100];
			// 循环从控制台输出流中读取数据
			while (stdout.available() > 0) {
				int le = stdout.read(buffer);
				if (le > 0) {
					results += new String(buffer, 0, le);
				}
			}
			//System.out.println(results);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
		return results;
	}

	private String processStdout(InputStream in, String charset) {
		InputStream stdout = new StreamGobbler(in);
		StringBuffer buffer = new StringBuffer();
		;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
				//System.out.println(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public String execShell(RemoteHost host, String shellCmd) {
		// StringBuilder result = new StringBuilder();
		String result = "";
		Connection conn = ssh2Client.getConn(host);
		Session session = null;
		try {
			// 一个session只能执行一次shell
			session = conn.openSession();
			session.execCommand(shellCmd);
			// try {
			// session.waitForCondition(ChannelCondition.EOF, 30000);
			// } catch (InterruptedException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			try {
				Thread.sleep(10000);// 暂停10s再关闭shell
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
			conn.close();
		}
		return result;
	}

	public String execShellWithStdout(RemoteHost host, String shellCmd) {
		// StringBuilder result = new StringBuilder();
		String result = "";
		Connection conn = ssh2Client.getConn(host);
		Session session = null;
		try {
			// 一个session只能执行一次shell
			session = conn.openSession();
			session.execCommand(shellCmd);
			result = processStdout(session.getStdout(), DEFAULTCHART);
			// 如果为得到标准输出为空，说明脚本执行出错了
			if (StringUtils.isEmpty(result)) {
				result = processStdout(session.getStderr(), DEFAULTCHART);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
			conn.close();
		}
		return result;
	}

	public String execShellByPTYWithStdout(RemoteHost host, String shellCmd) {
		Connection conn = ssh2Client.getConn(host);
		StringBuilder sb = new StringBuilder();
		Session session = null;
		try {
			session = conn.openSession();
			session.requestPTY("vt100", 80, 24, 640, 480, null);
			session.startShell();
			// session.execCommand(shellCmd);
			// 模拟控制台输出
			InputStream stdout = new StreamGobbler(session.getStdout());
			// 模拟命令输入
			PrintWriter out = new PrintWriter(session.getStdin());

			out.println(shellCmd);
			out.flush();
			try {
				// 阻塞等待流传输过来，限定30k，读写同线程时用
				session.waitForCondition(ChannelCondition.EOF, 30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			out.print("\r\n");
			out.flush();
			try {
				// 阻塞等待流传输过来，限定30k
				session.waitForCondition(ChannelCondition.EOF, 30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long start = System.currentTimeMillis();
			logger.info("执行完毕！"+shellCmd);
			byte[] buffer = new byte[100];
			String results = "";
			// 循环从控制台输出流中读取数据
			while (stdout.available() > 0) {
				int le = stdout.read(buffer);
				if (le > 0) {
					results += new String(buffer, 0, le);
				}
			}
			System.out.println(results);
			IOUtils.closeQuietly(out);
			session.getStdin().close();
			IOUtils.closeQuietly(stdout);// 必须关闭才能让消费子线程结束
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {

			if (session != null) {
				session.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return sb.toString();
	}

	/**
	 * 拷贝部署文件到远程，并执行远程启动命令
	 * 
	 * @param localDir
	 * @param host
	 * @param remoteDir
	 * @param shellCmds
	 * @return
	 */
	public Boolean remoteDeploy(String localDir, RemoteHost host, String remoteDir, String[] shellCmds) {
		// scpDir2Remote(localDir, host, remoteDir);
		Connection conn = ssh2Client.getConn(host);
		boolean isSuccess = true;
		for (String cmd : shellCmds) {
			isSuccess = execShell(conn, cmd);
			if (!isSuccess)
				return false;
		}
		conn.close();
		return true;
	}

	/**
	 * 通过监听端口的方式判断服务是否启动
	 * @param localDir
	 * @param host
	 * @param remoteDir
	 * @param chmodCmd
	 * @param startupCmd
	 * @return
	 */
	public Boolean remoteDeploy(String localDir, RemoteHost host, String remoteDir, String chmodCmd,
			String startupCmd,int port,int timeout) {
		Connection conn = ssh2Client.getConn(host);
		//判斷服務是否已存在
		boolean isServiceExists=isServiceExists(conn, port);
		if(isServiceExists)
			return true;
		scpDir2Remote(localDir, host, remoteDir);
		//boolean isSuccess = true;
		//isSuccess = execShell(conn, chmodCmd);
		execStartupShell(conn, startupCmd, port, timeout);
		conn.close();
		return true;
	}

	public void syncRemoteDeploy(String localDir, RemoteHost host, String remoteDir, String[] shellCmds) {
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Connection conn = ssh2Client.getConn(host);
				boolean isSuccess = true;
				execShellWithStdout(conn, shellCmds[1]);
				conn.close();
			}

		});
	}
}
