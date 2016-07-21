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
				rtpManager[i] = RTPManager.newInstance(); // Ϊÿһ��RTP�Ự����һ��RTP������
				rtpManager[i].addSessionListener(this); // ��ӻỰ����
				rtpManager[i].addReceiveStreamListener(this); // ��ӽ��յ����ݵļ���

				// �ر�ע��ĵط������ܽ����ص�ַ����Ϊ��127.0.0.1��������Ҫʵ�ʵ�ip��ַ����
				InetAddress ipAddress = InetAddress.getByName(user.groupIP);

				SessionAddress localAddress = new SessionAddress(ipAddress,
						user.videoPort + 2 * i);
				SessionAddress targetAddress = new SessionAddress(ipAddress,
						user.videoPort + 2 * i);

				rtpManager[i].initialize(localAddress); // �������Ự��ַ��RTP������
				BufferControl bc = (BufferControl) rtpManager[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null) {
					bc.setBufferLength(350); // ���û�������С��Ҳ����ʹ������ֵ��
				}

				rtpManager[i].addTarget(targetAddress); // ����Ŀ�ĻỰ��ַ
			} catch (Exception e) {
				System.err.println("initialize error");
				return false;
			}
		}

		return true;
	}

	public synchronized void update(ReceiveStreamEvent evt) {
		if (evt instanceof NewReceiveStreamEvent) { // ���յ��µ�������
			ReceiveStream stream = ((NewReceiveStreamEvent) evt).getReceiveStream(); // �õ���������
//			System.out.println("�ڼ��ν�����������" + count);
			dataSourceArray[count] = stream.getDataSource(); // �õ�����Դ
			count++;
			if (count == number) {
				play();
			}
		}
	}

	private void play() {
		try {
			dataSource = Manager.createMergingDataSource(dataSourceArray);
			// ����һ������������
			if (dataSource == null) {
				System.out.println("dsLocal == null");
			}
			player = javax.media.Manager.createPlayer(dataSource);
			// ����һ���������Ŀ���������������
			MyRTPListener gg = new MyRTPListener(player, userUI, user.ID);
			// ������������ע�������������
			player.addControllerListener(gg);
			// ��ʼ����
			player.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(userUI, "IOException��", "�쳣",
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
