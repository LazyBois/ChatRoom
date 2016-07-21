package client.connect;

import java.awt.Component;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;
import javax.swing.JOptionPane;

import client.UI.ClientUI;
import client.data.User;

public class VedioReceive extends Thread implements SessionListener,
		ReceiveStreamListener {
	public User user;
	private ClientUI userUI;

	private int count = 0;
	private int number = 2;
	private RTPManager rtpManager[] = null;
	private DataSource dataSourceArray[] = null;
	private DataSource dataSource = null;

	private Player player = null;

	public VedioReceive(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
	}

	public void run() {
		this.initialize();
	}

	protected boolean initialize() {

		dataSourceArray = new DataSource[number];
		rtpManager = new RTPManager[number];
		for (int i = 0; i < number; i++) {
			try {
				rtpManager[i] = RTPManager.newInstance(); // 为每一个RTP会话产生一个RTP管理器
				rtpManager[i].addSessionListener(this); // 添加会话监听
				rtpManager[i].addReceiveStreamListener(this); // 添加接收到数据的监听

				// 特别注意的地方：不能将本地地址设置为“127.0.0.1”，必须要实际的ip地址！！
				InetAddress ipAddress = InetAddress.getByName(user.groupIP);

				SessionAddress localAddress = new SessionAddress(ipAddress,
						user.videoPort + 2 * i);
				SessionAddress targetAddress = new SessionAddress(ipAddress,
						user.videoPort + 2 * i);

				rtpManager[i].initialize(localAddress); // 将本机会话地址给RTP管理器
				BufferControl bc = (BufferControl) rtpManager[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null) {
					bc.setBufferLength(350); // 设置缓冲区大小（也可以使用其他值）
				}

				rtpManager[i].addTarget(targetAddress); // 加入目的会话地址
			} catch (Exception e) {
				System.err.println("initialize error");
				return false;
			}
		}

		return true;
	}

	public synchronized void update(ReceiveStreamEvent evt) {
		if (evt instanceof NewReceiveStreamEvent) { // 接收到新的数据流
			ReceiveStream stream = ((NewReceiveStreamEvent) evt).getReceiveStream(); // 得到新数据流
//			System.out.println("第几次接受数据流：" + count);
			dataSourceArray[count] = stream.getDataSource(); // 得到数据源
			count++;
			if (count == number) {
				play();
			}
		}
	}

	private void play() {
		try {
			dataSource = Manager.createMergingDataSource(dataSourceArray);
			// 创建一个播放器对象
			if (dataSource == null) {
				System.out.println("dsLocal == null");
			}
			player = javax.media.Manager.createPlayer(dataSource);
			// 创建一个播放器的控制器监听器对象
			MyRTPListener gg = new MyRTPListener(player, userUI, user.ID);
			// 给播放器对象注册控制器监听器
			player.addControllerListener(gg);
			// 开始播放
			player.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(userUI, "IOException！", "异常",
					JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}
	}

	public synchronized void update(SessionEvent evt) {
		if (evt instanceof NewParticipantEvent) {
			Participant p = ((NewParticipantEvent) evt).getParticipant();
			System.err.println("  - A new participant had just joined: "
					+ p.getCNAME());
		}
	}
}
