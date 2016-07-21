package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.data.User;

//�������߳�
public class ServerThread extends Thread {
	// �������˿�
	private int textPort;
	private int videoPort;
	// �û����
	public int max;// �����������
	private String[] groupIP;// ÿ���û��������鲥��ַ
	public boolean[] visited;// ����groupIP�ı������
	private Map<User, ClientThread> clients;

	private ServerSocket serverSocket = null;
	private boolean flag;// �߳̿�����رձ��
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
			System.out.println("ServerSocket�˿��쳣��");
		}
	}

	/**
	 * Ϊÿ���û������������߳� ������û�
	 */
	public void run() {
		flag = true;
		interact.start();

		while (flag) {
			try {
				Socket socket = serverSocket.accept();
				// �Ѵ���������
				if (clients.size() == max) {
					DataOutputStream out = new DataOutputStream(
							socket.getOutputStream());
					out.writeUTF("���������Ѵ����ޣ�");
					out.flush();
					out.close();
					socket.close();
					continue;
				}
				// �����ڴ˴� �ȴ�User��Ϣ��ʼ�����
				while (true) {
					if (interact.userInit) {
						addUser(socket);
						interact.userInit = false;
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("���ӿͻ����쳣��");
			}
		}
	}

	/**
	 * ��ֹ���߳�
	 */
	public void kill() {
		clear();
		flag = false;
	}

	/**
	 * ������������û�
	 */
	private void clear() {

	}

	/**
	 * ���һ�����û�
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
		client.start();// �����Դ˿ͻ��˷�����߳�
		clients.put(user, client);
	}

	/**
	 * ����û�user
	 * 
	 * @param user
	 */
	public void removeUser(String user, int ID) {
		Iterator<User> iter = clients.keySet().iterator();
		while (iter.hasNext()) {
			User key = iter.next();
			if (key.equals(new User(user, ID))) {
				visited[key.ID] = false;// �黹�鲥��ַ
				clients.get(key).kill();// ��ֹ����
				clients.remove(key);// �Ƴ���¼
				break;
			}
		}
	}
}
