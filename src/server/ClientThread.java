package server;

import java.net.*;
import java.util.Map;

import server.connect.TextThread;

import server.data.User;

//������Ϊÿ���ͻ�������һ���µ��߳�
public class ClientThread extends Thread {

	private User user;// ���߳�Ϊ���û�����
	private Map<User, ClientThread> clients;
	public Socket socket;
	private TextThread textThread;

	public ClientThread(User user, Socket socket,
			Map<User, ClientThread> clients) {
		this.user = user;
		this.clients = clients;
		this.socket = socket;
	}

	// �ֶκ���
	public User getUser() {
		return user;
	}

	public TextThread getTextThread() {
		return textThread;
	}

	public void textStart() {
		textThread = new TextThread(user, socket, clients);
		textThread.start();
	}

	public void run() {
		textStart();
	}

	public void kill() {
		textThread.kill();
	}
}
