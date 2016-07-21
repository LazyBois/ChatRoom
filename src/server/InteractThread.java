package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import server.data.User;

/**
 * 此类作为一个交互类，用来传递客户端与服务器的特殊信息 （服务器端）
 */
public class InteractThread extends Thread {
	private String IP;
	private InetAddress group;
	private int port;
	private MulticastSocket socket;
	private boolean flag;
	public boolean userInit = false;

	private int videoPort;
	// 用户相关
	public Socket socketUser;
	private int max = 4;// 最大在线人数
	private String[] groupIP;// 每个用户都有其组播地址
	private boolean[] visited;// 对于groupIP的标记数组
	private User tempUser;

	private ServerThread server;

	public InteractThread(String IP, int port, int videoPort, String[] groupIP,
			boolean[] visited, Socket socket, ServerThread server) {
		this.IP = IP;
		this.port = port;
		this.videoPort = videoPort;
		this.groupIP = groupIP;
		this.visited = visited;
		this.socketUser = socket;
		this.server = server;
		init();
	}

	public void run() {
		flag = true;
		while (flag) {
			String str = receiveMessage();
			dealMessage(str);
		}
	}

	private void init() {
		try {
			group = InetAddress.getByName(IP);
			socket = new MulticastSocket(port);
			socket.setTimeToLive(225);
			// socket.setLoopbackMode(true);// 禁止报文回送
			socket.joinGroup(group);
		} catch (IOException e) {
			System.out.println("组播开启异常");
		}
	}

	public void kill() {
		flag = false;
		socket.close();
	}

	public User getUser() {
		return (User) tempUser.clone();
	}

	/**
	 * 发送数据包
	 */
	public void sendMessage(String str) {
		byte data[] = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, group,
				port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接收数据包
	 */
	public String receiveMessage() {
		byte data[] = new byte[8192];
		DatagramPacket packet = new DatagramPacket(data, data.length, group,
				port);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			System.out.println("接收数据异常！");
		}
		String message = new String(packet.getData(), 0, packet.getLength());
		return message;
	}

	/**
	 * 处理接收到的数据
	 * 
	 * @param message
	 *            格式：who(server/user)@类型@other
	 */
	private synchronized void dealMessage(String message) {
		try {
			String[] messageArray = message.split("@");
			// 用户发送的信息
			if (!messageArray[0].equals("Server")) {
				if (messageArray[1].equals("ADD"))// 某用户上线
				{
					getGroupIP(messageArray[0]);// 给名字为 messageArray[0] 用户分配信息
					userInit = true;
				} else if (messageArray[1].equals("REMOVE")) {
					server.removeUser(messageArray[0],
							Integer.parseInt(messageArray[2]));
				}
			}
		} catch (Exception e) {
			System.out.println("数据交互异常！");
			e.printStackTrace();
		}
	}

	private synchronized void getGroupIP(String userName) {
		int indexGroup = 0;
		while (true) {
			// 找到未使用的组播地址

			if (!visited[indexGroup]) {
				User user = new User(userName, groupIP[indexGroup], videoPort,
						indexGroup);
				tempUser = user;
				visited[indexGroup] = true;
				break;
			}
			indexGroup++;
			indexGroup = indexGroup % max;
		}
	}
}
