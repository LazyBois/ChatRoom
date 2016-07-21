package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Iterator;

import javax.swing.JOptionPane;

import client.connect.VedioReceive;
import client.data.User;

/**
 * 此类作为一个交互类，用来传递客户端与服务器的特殊信息 （客户端）
 */
public class InteractThread extends Thread {
	private User user;
	private String IP;
	private InetAddress group;
	private int port;
	private MulticastSocket socket;
	private Control control;
	private boolean flag;

	public InteractThread(User user, Control control) {
		this.IP = user.publicIP;
		this.port = user.publicPort;
		this.user = user;
		this.control = control;
		init();
	}

	private void init() {
		try {
			group = InetAddress.getByName(IP);
			socket = new MulticastSocket(port);
			socket.setTimeToLive(225);
			// socket.setLoopbackMode(true);// 禁止报文回送
			socket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		flag = true;
		while (flag) {
			receiveMessage();
		}
	}

	public void kill() {
		flag = false;
		socket.close();
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
	private void receiveMessage() {
		byte data[] = new byte[8192];
		DatagramPacket packet = new DatagramPacket(data, data.length, group,
				port);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData(), 0, packet.getLength());
		dealMessage(message);
	}

	/**
	 * 处理接收到的数据
	 * 
	 * @param message
	 *            格式：who(server/user)@类型@other
	 */
	private void dealMessage(String message) {
		try {
			String[] messageArray = message.split("@");
			if(messageArray != null)
			{
				if(messageArray[0].equals("Server"))
				{
					if(messageArray[1].equals("ADD"))
					{
						if(messageArray[2].equals(user.name))
						{
							user.groupIP = messageArray[3];
							user.ID = Integer.parseInt(messageArray[4]);
						}
						else
						{
							User userAdd = new User(messageArray[2]);
							userAdd.groupIP = messageArray[3];
							userAdd.videoPort = user.videoPort;
							userAdd.ID = Integer.parseInt(messageArray[4]);
							addUser(userAdd);
						}
					}
					else if(messageArray[1].equals("INIT") && messageArray[2].equals(user.name))
					{
						User userAdd = new User(messageArray[3]);
						userAdd.groupIP = messageArray[4];
						userAdd.videoPort = user.videoPort;
						userAdd.ID = Integer.parseInt(messageArray[5]);
						addUser(userAdd);
					}
				}
				else
				{
					if(!messageArray[0].equals(user.name) && messageArray[1].equals("REMOVE"))
					{
						removeUser(messageArray[0],Integer.parseInt(messageArray[2]));
					}
					else if(!messageArray[0].equals(user.name) && messageArray[1].equals("VedioStart"))
					{
						User user = new User(messageArray[0]);
						user.ID = Integer.parseInt(messageArray[2]);
						
						VedioReceive receive = control.clients.get(user);
						if(receive != null)
						{
							System.out.println("videoStart"+user.name);
							receive.start();							
						}
						else
						{
							System.out.println("receive is null");
						}
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(control.userUI, "数据交互异常！", "异常",
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	//为此用户初始化一个新的线程
	private synchronized void addUser(User userAdd)
	{
		VedioReceive receive = null;
		receive = new VedioReceive(userAdd,
				control.userUI);
//		receive.start();
		control.clients.put(userAdd, receive);
		control.userUI.addListModel(userAdd.name);
		control.userUI.setNumberMember(control.clients.size()+1);//此处+1是因为还要计算本人
	}
	
	private synchronized void removeUser(String user,int ID)
	{
		User u1 = new User(user,ID);	
		if(control.clients.containsKey(u1))
		{
			control.clients.remove(new User(user,ID));			
			control.userUI.removeListModel(user);		
			control.userUI.setNumberMember(control.clients.size()+1);//此处+1是因为还要计算本人			
		}
		else
		{
			System.out.println("REMOVE Error!name:"+user);
		}
	}
}
