package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.data.User;

//服务器线程
public class ServerThread extends Thread {
	// 服务器端口
	private int textPort;
	private int videoPort;
	// 用户相关
	public int max;// 最大在线人数
	private String[] groupIP;// 每个用户都有其组播地址
	public boolean[] visited;// 对于groupIP的标记数组
	private Map<User, ClientThread> clients;

	private ServerSocket serverSocket = null;
	private boolean flag;// 线程开启与关闭标记
	private InteractThread interact;

	Socket socket = null;

	public ServerThread(int textPort, int videoPort, int audioPort,
			String[] groupIP, boolean[] visited, int max) {

		this.textPort = textPort;
		this.videoPort = videoPort;
		this.groupIP = groupIP;
		this.visited = visited;
		this.max = max;
		init();
	}

	public void init() {
		clients = new HashMap<User, ClientThread>();
		interact = new InteractThread("239.255.6.1", 10000, videoPort, groupIP,
				visited, socket, this);
		try {
			serverSocket = new ServerSocket(textPort);
		} catch (IOException e) {
			System.out.println("ServerSocket端口异常！");
		}
	}

	/**
	 * 为每个用户开启单独的线程 添加新用户
	 */
	public void run() {
		flag = true;
		interact.start();

		while (flag) {
			try {
				Socket socket = serverSocket.accept();
				// 已达人数上限
				if (clients.size() == max) {
					DataOutputStream out = new DataOutputStream(
							socket.getOutputStream());
					out.writeUTF("在线人数已达上限！");
					out.flush();
					out.close();
					socket.close();
					continue;
				}
				// 阻塞在此处 等待User信息初始化完成
				while (true) {
					if (interact.userInit) {
						addUser(socket);
						interact.userInit = false;
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("连接客户端异常！");
			}
		}
	}

	/**
	 * 终止此线程
	 */
	public void kill() {
		clear();
		flag = false;
	}

	/**
	 * 清除所有在线用户
	 */
	private void clear() {

	}

	/**
	 * 添加一个新用户
	 * 
	 * @param socket
	 */
	private void addUser(Socket socket) {
		User user = interact.getUser();
		String allMessage = "Server@ADD@" + user.getName() + "@"
				+ user.getGroupIP() + "@" + user.ID;
		interact.sendMessage(allMessage);
		Iterator<User> iter = clients.keySet().iterator();
		while (iter.hasNext()) {
			User key = iter.next();
			try {
				allMessage = "Server@INIT@" + user.getName() + "@" + key.getName()
						+ "@" + key.getGroupIP() + "@" + key.ID;
				interact.sendMessage(allMessage);
				
//				if(clients.get(key).socket.getKeepAlive())
//				{
//					allMessage = "Server@INIT@" + user.getName() + "@" + key.getName()
//							+ "@" + key.getGroupIP() + "@" + key.ID;
//					interact.sendMessage(allMessage);				
//				}
//				else
//				{
//					clients.remove(key);
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ClientThread client = new ClientThread(user, socket, clients);
		client.start();// 开启对此客户端服务的线程
		clients.put(user, client);
	}

	/**
	 * 清除用户user
	 * 
	 * @param user
	 */
	public void removeUser(String user, int ID) {
		Iterator<User> iter = clients.keySet().iterator();
		while (iter.hasNext()) {
			User key = iter.next();
			if (key.equals(new User(user, ID))) {
				visited[key.ID] = false;// 归还组播地址
				clients.get(key).kill();// 终止进程
				clients.remove(key);// 移除记录
				break;
			}
		}
	}
}
