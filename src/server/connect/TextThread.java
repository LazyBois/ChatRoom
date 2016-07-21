package server.connect;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import server.ClientThread;
import server.data.User;

/**
 * @author Lazy_Man
 *
 */
public class TextThread extends Thread{
	private boolean flag;
	private User user;
	private Map<User, ClientThread> clients;	
	private Socket socket;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	public TextThread(User user,Socket socket,Map<User, ClientThread> clients)
	{
		this.user = user;
		this.clients = clients;
		this.setSocket(socket);
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			System.out.println("文本数据流异常!");
		}
	}
	
	public DataInputStream getRead()
	{
		return in;
	}	
	public DataOutputStream getWrite()
	{
		return out;
	}
	public User getUser()
	{
		return user;
	}
	
	public void run()
	{
		String message = null;
		flag = true;
		while(flag)
		{
			try {
				message = in.readUTF().trim();
			} catch (IOException e) {
				return;
			}
			sendAll(user.getName()+":"+message);
		}
	}
	
	/**
	 * 终止此线程
	 */
	public void kill()
	{	
		flag = false;
	}
	
	/**
	 * 给所有用户发送信息
	 * @param message 要发送的信息
	 */
	public void sendAll(String message)
	{
		Iterator<Entry<User, ClientThread>> iter = clients.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<User, ClientThread> entry = (Map.Entry<User, ClientThread>) iter.next();
			ClientThread val = (ClientThread)entry.getValue();
			try {
				val.getTextThread().getWrite().writeUTF(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
