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
	private TextReceive read;// 读取线程

	private ClientUI userUI;

	public TextTransmit(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
		read = new TextReceive();
	}

	//开启TCP
	public void start() {
		// 初始化数据流
		try {
			socket = new Socket(user.serverIP,user.textPort);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"文本数据流异常！","异常",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		// 添加发送按钮的监听器
		sendEvent();
		// 开启读取文本线程
		read.setReadStream(in);
		read.setUI(userUI);
		read.start();
	}
	
	/**
	 * 关闭文本通信端口(TCP)
	 */
	public  void close()
	{		
		try {
			read.close();
			socket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"关闭通信异常！","异常",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * 实现控制器监听器
	 */
	private void sendEvent() {
		// 按钮发送事件
		userUI.sender.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				send();
			}
		});
	} 
	
	/**
	 * 向服务器发送数据
	 */
	private void send() {
		try {
			String str = userUI.getSendText();
			
			if (!str.equals("") && str != null) {
				out.writeUTF(str);
			}
			userUI.setSendText(null);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(userUI,"发送数据异常!","异常",JOptionPane.WARNING_MESSAGE);
		}
	}
}
