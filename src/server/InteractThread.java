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
 * ������Ϊһ�������࣬�������ݿͻ������������������Ϣ ���������ˣ�
 */
public class InteractThread extends Thread {
	private String IP;
	private InetAddress group;
	private int port;
	private MulticastSocket socket;
	private boolean flag;
	public boolean userInit = false;

	private int videoPort;
	// �û����
	public Socket socketUser;
	private int max = 4;// �����������
	private String[] groupIP;// ÿ���û��������鲥��ַ
	private boolean[] visited;// ����groupIP�ı������
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
			// socket.setLoopbackMode(true);// ��ֹ���Ļ���
			socket.joinGroup(group);
		} catch (IOException e) {
			System.out.println("�鲥�����쳣");
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
	 * �������ݰ�
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
	 * �������ݰ�
	 */
	public String receiveMessage() {
		byte data[] = new byte[8192];
		DatagramPacket packet = new DatagramPacket(data, data.length, group,
				port);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			System.out.println("���������쳣��");
		}
		String message = new String(packet.getData(), 0, packet.getLength());
		return message;
	}

	/**
	 * ������յ�������
	 * 
	 * @param message
	 *            ��ʽ��who(server/user)@����@other
	 */
	private synchronized void dealMessage(String message) {
		try {
			String[] messageArray = message.split("@");
			// �û����͵���Ϣ
			if (!messageArray[0].equals("Server")) {
				if (messageArray[1].equals("ADD"))// ĳ�û�����
				{
					getGroupIP(messageArray[0]);// ������Ϊ messageArray[0] �û�������Ϣ
					userInit = true;
				} else if (messageArray[1].equals("REMOVE")) {
					server.removeUser(messageArray[0],
							Integer.parseInt(messageArray[2]));
				}
			}
		} catch (Exception e) {
			System.out.println("���ݽ����쳣��");
			e.printStackTrace();
		}
	}

	private synchronized void getGroupIP(String userName) {
		int indexGroup = 0;
		while (true) {
			// �ҵ�δʹ�õ��鲥��ַ

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
