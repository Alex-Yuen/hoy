package mobi.samov.client.game;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;


public class MusicPlayer implements PlayerListener{
	
	/* ������������ѭ�� */
	public final static int IMMENSITY = -1;

	/* ����������״̬ */
	public final static int UNREALIZED = 1; // δʵ��

	public final static int REALIZED = 2; // ʵ��

	public final static int PREFETCHED = 3; // Ԥȡ

	public final static int STARTED = 4; // ��ʼ

	public final static int CLOSED = 5; // �ر�
	
	Player MusicPlayer;
	
	VolumeControl control;

	boolean notActive;

	
	public MusicPlayer(){
		notActive = true;
	}
	
	public void load(String fileName) {
		try {
			InputStream inputstream = getClass().getResourceAsStream(fileName);
			if (inputstream == null) {
				System.out.println("Error happens: InputStream not found "
						+ fileName);
			}

			if (MusicPlayer == null) {
				 MusicPlayer = Manager.createPlayer(inputstream, "audio/midi");
//				MusicPlayer = Manager.createPlayer(inputstream, "audio/x-wav");
				// MusicPlayer = Manager.createPlayer(inputstream, "audio/basic");
				// MusicPlayer = Manager.createPlayer(inputstream, "audio/mpeg");
			} else {
				MusicPlayer.stop();
				MusicPlayer.close();
				MusicPlayer = null;
				 MusicPlayer = Manager.createPlayer(inputstream, "audio/midi");
//				MusicPlayer = Manager.createPlayer(inputstream, "audio/x-wav");
				// MusicPlayer = Manager.createPlayer(inputstream, "audio/basic");
				// MusicPlayer = Manager.createPlayer(inputstream, "audio/mpeg");
			}

			if (MusicPlayer == null) {
				System.out.println("Error handler happens: InputStream "
						+ fileName);
			} else {
				MusicPlayer.addPlayerListener(this);

				// "����"������
				MusicPlayer.realize();

				// Ԥȡ������
				MusicPlayer.prefetch();

				// ������������Ϊ���޴β���
				MusicPlayer.setLoopCount(-1);

				System.out.println("Realized Player: " + fileName);
			}
			// �Ӳ������л�ȡ�����ؼ�
			control = (VolumeControl) MusicPlayer.getControl("VolumeControl");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		} catch (MediaException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	/*  �ر�ָ�������� һ���رղ��ɻָ���ֻ�����¼���	 */
	public void destroyAt() {
		if (MusicPlayer != null) {
			MusicPlayer.close();
			MusicPlayer = null;
		}
	}

	/**
	 * �ر�ȫ�������� һ���رղ��ɻָ���ֻ�����¼���
	 */
	public void destroyAll() {
		if (MusicPlayer != null) {
			MusicPlayer.close();
			MusicPlayer = null;
		}
	}

	/**
	 * ��"ý�嵱ǰ���ŵ���ʱ��"��ʼ/��������
	 */
	public void play(int times) {
		if (MusicPlayer != null) {
			try {
				MusicPlayer.stop();
				MusicPlayer.setLoopCount(times);
				MusicPlayer.start();
			} catch (MediaException e) {
				e.printStackTrace();
				System.out.println(e);
			}
		}
	}

	/**
	 * ��ѯ��ǰ��������״̬
	 */
	public int getPlayState() {
		switch (MusicPlayer.getState()) {
		case Player.UNREALIZED:
			return UNREALIZED;

		case Player.REALIZED:
			return REALIZED;

		case Player.PREFETCHED:
			return PREFETCHED;

		case Player.STARTED:
			return STARTED;

		case Player.CLOSED:
			return CLOSED;
		default:
			return -1;
		}
	}

	/**
	 * ֹͣ��ǰ��������ע�⵱����stopʱ �������������Ƶ���ͷ
	 */
	public void stop() {
		if (MusicPlayer != null && MusicPlayer.getState() == Player.STARTED) {
			try {
				MusicPlayer.stop();
				System.out.println("The Player stoped here:"
						+ MusicPlayer.getMediaTime());
			} catch (MediaException e) {
				e.printStackTrace();
				System.out.println(e);
			}
		}
	}	


	

	public void playerUpdate(Player musicplayer, String event, Object eventData) {
		
		
	}
}
