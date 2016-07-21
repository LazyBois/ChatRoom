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
 * JMF音/视频发送
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
	// 临时数据
	private DataSource tempSource = null;

	public VedioTransmit(User user, ClientUI userUI) {
		this.user = user;
		this.userUI = userUI;
		dataSources = new DataSource[number];
	}

	private void getDeviceInfo() {
		try {
			// ①获取视频捕获设备的 CaptureDeviceInfo 对象！
			videoDevice = CaptureDeviceManager
					.getDevice("vfw:Microsoft WDM Image Capture (Win32):0");
			// ②获取音频捕获设备的 CaptureDeviceInfo 对象！
			Vector<?> deviceList = CaptureDeviceManager
					.getDeviceList(new AudioFormat(AudioFormat.LINEAR, 8000, 8,
							1));
			audioDevice = (CaptureDeviceInfo) deviceList.elementAt(0);
		} catch (Exception e) {
			System.out.println("Device initializing failure!");
		}
	}

	// 为视频捕获设备创建一个视频处理器，如果不能创建的话就退出
	private void setVideoDataSource() throws Exception {
		tempSource = Manager.createDataSource(videoDevice.getLocator());
		videoDataLocal = Manager.createCloneableDataSource(tempSource);
		videoProcessor = Manager
				.createProcessor(((SourceCloneable) videoDataLocal)
						.createClone());
		videoProcessor.configure();

		// 阻塞在这里直到配置完成
		while (true) {
			if (videoProcessor.getState() == Processor.Configured) {
				break;
			}
		}
		TrackControl[] track = videoProcessor.getTrackControls();
		videoProcessor.setContentDescriptor(new ContentDescriptor(
				ContentDescriptor.RAW_RTP));
		

		// 将 track 设置成我们想要的 track，不想要的 track 统统 disable 掉！
		boolean encodingOk = false;

		// 将 track 设置成我们想要的 track，不想要的 track 统统 disable 掉！
		for (int i = 0; i < track.length; i++) {
			if (!encodingOk && track[i] instanceof FormatControl) {
				if (((FormatControl) track[i]).setFormat(new VideoFormat(
						VideoFormat.JPEG_RTP)) == null) {
					track[i].setEnabled(false); // 将不可用的(除VideoFormat.JPEG_RTP以外的)磁道全部设为不可用！
				} else {
					encodingOk = true;
				}
			} else {
				// 我们不能将磁道设置为 .GSM 格式，因此 disable 它！
				track[i].setEnabled(false);
			}
		}

		videoProcessor.realize();
		// 阻塞直到完成视频处理器的实现！
		while (true) {
			if (videoProcessor.getState() == Processor.Realized) {
				break;
			}
		}

		try {
			videoDataSend = videoProcessor.getDataOutput();
		} catch (NotRealizedError e) {
			System.exit(-1); // 不能成功配置视频数据源的话下面肯定进行不下去，直接退出
		}
		System.out.println("videoDataSource is ready!");
	}
	
	// 为音频捕获设备创建一个音频处理器，如果不能创建就退出程序
	private void setAudioDataSource() throws Exception {
		tempSource = Manager.createDataSource(audioDevice.getLocator());
		audioDataLocal = Manager.createCloneableDataSource(tempSource);
		audioProcessor = Manager
				.createProcessor(((SourceCloneable) audioDataLocal)
						.createClone());
		audioProcessor.configure();

		// 阻塞直到完成音频处理器的配置！
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

	// 开始传送视频和音频数据！
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

	public void close() {
		if (player != null) {
			player.close();
			player = null;
		}
		if (videortpManager != null) {
			videortpManager.removeTargets("Closing session from RTPReceive");
			videortpManager.dispose(); // 关闭RTP会话管理器
			videortpManager = null;
		}

		if (audiortpManager != null) {
			audiortpManager.removeTargets("Closing session from RTPReceive");
			audiortpManager.dispose(); // 关闭RTP会话管理器
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