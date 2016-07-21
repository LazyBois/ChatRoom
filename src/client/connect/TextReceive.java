package client.connect;

import java.io.DataInputStream;
import java.io.IOException;

import client.UI.ClientUI;

public class TextReceive extends Thread{
	private DataInputStream in = null;
	private ClientUI userUI;
	private boolean flag = true;
	public void setReadStream(DataInputStream in)
	{
		this.in = in;
	}
	
	public void setUI(ClientUI userUI)
	{
		this.userUI = userUI;
	}
	public void run()
	{
		while(flag)
		{
			try {
				String str = in.readUTF().trim();
				userUI.setReceiveText("\n"+str);
			} catch (IOException e) {
				userUI.setReceiveText("\n\t与服务器断开连接！");
				break;
			}
		}
	}
	public void close()
	{
		flag = false;
	}
}
