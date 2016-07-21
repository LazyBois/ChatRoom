package client.connect;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;
import javax.media.protocol.DataSource;
import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;

import client.UI.ClientUI;
/**
 * ���ز����¼�������
*/
public class MyRTPListener implements ReceiveStreamListener, ControllerListener {
	private Player player;
	private JFrame jf;
	private JPanel panel;
	private ClientUI userUI;
	private int userID;
	public MyRTPListener(Player player,ClientUI userUI,int userID) {
		this.player = player;
		this.userUI = userUI;
		this.userID = userID;
	}
	/**
	 * RTP�¼�����
	 */
	public synchronized void update(ReceiveStreamEvent evt) {
		if (evt instanceof NewReceiveStreamEvent) {
			ReceiveStream stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
			DataSource dataSource = stream.getDataSource();
			try {
				Player player = javax.media.Manager.createPlayer(dataSource);
				player.addControllerListener(this);
				player.realize();
			} catch (NoPlayerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * ʵ�ּ������ӿ��еķ���.�˷�����֪ͨӦ�ó���Բ��������¼�������Ӧ
	 */
	public synchronized void controllerUpdate(ControllerEvent e) {
		if (e instanceof javax.media.RealizeCompleteEvent) {
			Component comp;
			// �õ��������Ŀ�������,����������ʾ��Ƶ������
			if ((comp = player.getVisualComponent()) != null) {
				// �����������ӵ�������
				userUI.addVideo(comp,userID);
			}
		} 
	}
}
