package mobi.samov.client.game;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;


public class MusicPlayer implements PlayerListener{
	
	/* 播发次数无限循环 */
	public final static int IMMENSITY = -1;

	/* 播发器各个状态 */
	public final static int UNREALIZED = 1; // 未实现

	public final static int REALIZED = 2; // 实现

	public final static int PREFETCHED = 3; // 预取

	public final static int STARTED = 4; // 开始

	public final static int CLOSED = 5; // 关闭
	
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

				// "具现"播放器
				MusicPlayer.realize();

				// 预取播放器
				MusicPlayer.prefetch();

				// 将播放器设置为无限次播放
				MusicPlayer.setLoopCount(-1);

				System.out.println("Realized Player: " + fileName);
			}
			// 从播放器中获取音量控件
			control = (VolumeControl) MusicPlayer.getControl("VolumeControl");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		} catch (MediaException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	/*  关闭指定播放器 一旦关闭不可恢复，只能重新加载	 */
	public void destroyAt() {
		if (MusicPlayer != null) {
			MusicPlayer.close();
			MusicPlayer = null;
		}
	}

	/**
	 * 关闭全部播放器 一旦关闭不可恢复，只能重新加载
	 */
	public void destroyAll() {
		if (MusicPlayer != null) {
			MusicPlayer.close();
			MusicPlayer = null;
		}
	}

	/**
	 * 从"媒体当前播放到的时刻"开始/继续播放
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
	 * 查询当前播发器的状态
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
	 * 停止当前播放器，注意当调用stop时 播放器不会重绕到开头
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
