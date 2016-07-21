package client.connect;

import java.net.InetAddress;
import java.util.Vector;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.swing.JOptionPane;

import client.UI.ClientUI;
import client.data.User;

/**
 * JMF��/��Ƶ����
 */
public class VedioTransmit {
	private User user;
	private ClientUI userUI;
	private int number = 2;

	private CaptureDeviceInfo videoDevice = null;
	private CaptureDeviceInfo audioDevice = null;
	private DataSource dataSources[];
	private DataSource videoDataLocal, audioDataLocal, playData;
	private DataSource videoDataSend, audioDataSend;

	private Processor videoProcessor, audioProcessor;
	private RTPManager videortpManager = null, audiortpManager = null;
	private SendStream videoRtpstream = null, audioRtpstream = null;

	private Player player;
	// ��ʱ����
	private DataSource tempSource = null;

	public VedioTransmit(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
		dataSources = new DataSource[number];
	}

	private void getDeviceInfo() {
		try {
			// �ٻ�ȡ��Ƶ�����豸�� CaptureDeviceInfo ����
			videoDevice = CaptureDeviceManager
					.getDevice("vfw:Microsoft WDM Image Capture (Win32):0");
			// �ڻ�ȡ��Ƶ�����豸�� CaptureDeviceInfo ����
			Vector<?> deviceList = CaptureDeviceManager
					.getDeviceList(new AudioFormat(AudioFormat.LINEAR, 8000, 8,
							1));
			audioDevice = (CaptureDeviceInfo) deviceList.elementAt(0);
		} catch (Exception e) {
			System.out.println("Device initializing failure!");
		}
	}

	// Ϊ��Ƶ�����豸����һ����Ƶ��������������ܴ����Ļ����˳�
	private void setVideoDataSource() throws Exception {
		tempSource = Manager.createDataSource(videoDevice.getLocator());
		videoDataLocal = Manager.createCloneableDataSource(tempSource);
		videoProcessor = Manager
				.createProcessor(((SourceCloneable) videoDataLocal)
						.createClone());
		videoProcessor.configure();

		// ����������ֱ���������
		while (true) {
			if (videoProcessor.getState() == Processor.Configured) {
				break;
			}
		}
		TrackControl[] track = videoProcessor.getTrackControls();
		videoProcessor.setContentDescriptor(new ContentDescriptor(
				ContentDescriptor.RAW_RTP));
		

		// �� track ���ó�������Ҫ�� track������Ҫ�� track ͳͳ disable ����
		boolean encodingOk = false;

		// �� track ���ó�������Ҫ�� track������Ҫ�� track ͳͳ disable ����
		for (int i = 0; i < track.length; i++) {
			if (!encodingOk && track[i] instanceof FormatControl) {
				if (((FormatControl) track[i]).setFormat(new VideoFormat(
						VideoFormat.JPEG_RTP)) == null) {
					track[i].setEnabled(false); // �������õ�(��VideoFormat.JPEG_RTP�����)�ŵ�ȫ����Ϊ�����ã�
				} else {
					encodingOk = true;
				}
			} else {
				// ���ǲ��ܽ��ŵ�����Ϊ .GSM ��ʽ����� disable ����
				track[i].setEnabled(false);
			}
		}

		videoProcessor.realize();
		// ����ֱ�������Ƶ��������ʵ�֣�
		while (true) {
			if (videoProcessor.getState() == Processor.Realized) {
				break;
			}
		}

		try {
			videoDataSend = videoProcessor.getDataOutput();
		} catch (NotRealizedError e) {
			System.exit(-1); // ���ܳɹ�������Ƶ����Դ�Ļ�����϶����в���ȥ��ֱ���˳�
		}
		System.out.println("videoDataSource is ready!");
	}
	
	// Ϊ��Ƶ�����豸����һ����Ƶ��������������ܴ������˳�����
	private void setAudioDataSource() throws Exception {
		tempSource = Manager.createDataSource(audioDevice.getLocator());
		audioDataLocal = Manager.createCloneableDataSource(tempSource);
		audioProcessor = Manager
				.createProcessor(((SourceCloneable) audioDataLocal)
						.createClone());
		audioProcessor.configure();

		// ����ֱ�������Ƶ�����������ã�
		while (true) {
			if (audioProcessor.getState() == Processor.Configured) {
				break;
			}
		}

		audioProcessor.setContentDescriptor(new ContentDescriptor(
				ContentDescriptor.RAW));
		TrackControl[] track = audioProcessor.getTrackControls();
		boolean encodingOk = false;

		for (int i = 0; i < track.length; i++) {
			if (!encodingOk && (track[i] instanceof FormatControl)) {
				if (((FormatControl) track[i]).setFormat(new AudioFormat(
						AudioFormat.ULAW_RTP, 8000, 8, 1)) == null) {
					track[i].setEnabled(false);
				} else {
					encodingOk = true;
				}
			} else {
				track[i].setEnabled(false);
			}
		}
		audioProcessor.realize();
		while (true) {
			if (audioProcessor.getState() == Processor.Realized) {
				break;
			}
		}
		try {
			audioDataSend = audioProcessor.getDataOutput();
		} catch (NotRealizedError e) {
			System.exit(-1);
		}
		System.out.println("audioDataSource is ready!");
	}

	// ��ʼ������Ƶ����Ƶ���ݣ�
	private void transmitStart() {
		try {
			videortpManager = RTPManager.newInstance();
			audiortpManager = RTPManager.newInstance();
			InetAddress ipAddress = InetAddress.getByName(user.groupIP);

			SessionAddress localAddress1 = new SessionAddress(InetAddress.getLocalHost(),
					user.videoPort);
			SessionAddress localAddress2 = new SessionAddress(InetAddress.getLocalHost(),
					user.videoPort + 2);

			videortpManager.initialize(localAddress1);
			audiortpManager.initialize(localAddress2);

			SessionAddress targetAddress1 = new SessionAddress(ipAddress,
					user.videoPort);
			SessionAddress targetAddress2 = new SessionAddress(ipAddress,
					user.videoPort + 2);

			videortpManager.addTarget(targetAddress1);
			audiortpManager.addTarget(targetAddress2);

			videoRtpstream = videortpManager.createSendStream(videoDataSend, 0);
			audioRtpstream = audiortpManager.createSendStream(audioDataSend, 0);
			
			videoProcessor.start();
			audioProcessor.start();
			
			videoRtpstream.start();
			audioRtpstream.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void play() {
		try {
			dataSources[0] = videoDataLocal;
			dataSources[1] = audioDataLocal;

			playData = Manager.createMergingDataSource(dataSources);

			player = javax.media.Manager.createPlayer(videoDataLocal);
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

	public void close() {
		if (player != null) {
			player.close();
			player = null;
		}
		if (videortpManager != null) {
			videortpManager.removeTargets("Closing session from RTPReceive");
			videortpManager.dispose(); // �ر�RTP�Ự������
			videortpManager = null;
		}

		if (audiortpManager != null) {
			audiortpManager.removeTargets("Closing session from RTPReceive");
			audiortpManager.dispose(); // �ر�RTP�Ự������
			audiortpManager = null;
		}
	}

	public void start() {
		try {
			getDeviceInfo();
			setVideoDataSource();
			setAudioDataSource();
			
			transmitStart();


			play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}