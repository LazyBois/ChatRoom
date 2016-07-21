package client.connect;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import client.UI.ClientUI;
import client.data.User;

public class TextTransmit {

	private Socket socket;
	private User user;
	private DataInputStream in;
	private DataOutputStream out;
	private TextReceive read;// ��ȡ�߳�

	private ClientUI userUI;

	public TextTransmit(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
		read = new TextReceive();
	}

	//����TCP
	public void start() {
		// ��ʼ��������
		try {
			socket = new Socket(user.serverIP,user.textPort);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"�ı��������쳣��","�쳣",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		// ��ӷ��Ͱ�ť�ļ�����
		sendEvent();
		// ������ȡ�ı��߳�
		read.setReadStream(in);
		read.setUI(userUI);
		read.start();
	}
	
	/**
	 * �ر��ı�ͨ�Ŷ˿�(TCP)
	 */
	public  void close()
	{		
		try {
			read.close();
			socket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"�ر�ͨ���쳣��","�쳣",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * ʵ�ֿ�����������
	 */
	private void sendEvent() {
		// ��ť�����¼�
		userUI.sender.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				send();
			}
		});
	} 
	
	/**
	 * ���������������
	 */
	private void send() {
		try {
			String str = userUI.getSendText();
			
			if (!str.equals("") && str != null) {
				out.writeUTF(str);
			}
			userUI.setSendText(null);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"���������쳣!","�쳣",JOptionPane.WARNING_MESSAGE);
		}
	}
}
