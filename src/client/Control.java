package client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import client.UI.ClientUI;
import client.connect.TextTransmit;
import client.connect.VedioReceive;
import client.connect.VedioTransmit;
import client.data.User;

public class Control {
	public Map<User, VedioReceive> clients;
	public ClientUI userUI;
	protected User user;
	public TextTransmit textThread;
	public VedioTransmit vedioSend;
	public InteractThread interact;// �����߳�

	public Control(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
		clients = new HashMap<User, VedioReceive>();
	}

	public void initClients() {
		userUI.addListModel(user.name);// ��ӵ������û�
		userUI.setNumberMember(1);
		interact = new InteractThread(user, this);// ʵ��������ͨ��
		String str = user.name + "@ADD";
		interact.sendMessage(str);// ���ͱ��û���Ϣ
		interact.start();// ��������ͨ��
	}

	public void start() {
		initClients();
		textStart();

		// ������Ƶ
		userUI.connect.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				videoSendStart();				
			}
		});
		// �ر���Ƶ����
		userUI.close.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				videoSendKill();
			}
		});
	}

	public void kill() {
		String message = user.name + "@REMOVE@" + user.ID;
		interact.sendMessage(message);
		videoSendKill();
		textKill();
		userUI.validate();
	}

	public void textStart() {
		textThread = new TextTransmit(user, userUI);
		textThread.start();
	}

	public void textKill() {
		textThread.close();
		textThread = null;
	}

	public void videoSendStart() {
		try {
			vedioSend = new VedioTransmit(user, userUI);
			vedioSend.start();
			String str = user.name + "@VedioStart@"+user.ID;
			interact.sendMessage(str);// ���ͱ��û���Ϣ
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void videoSendKill() {
		if (vedioSend != null) {
			vedioSend.close();
		}
	}
}
