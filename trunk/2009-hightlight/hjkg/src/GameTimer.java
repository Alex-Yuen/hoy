/*
 * GameTimer.java
 *
 * Created on 2007��3��24��, ����8:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.util.TimerTask;

/**
 * NumberSliderPuzzle�ļ�ʱ��������ִ�����õ���
 *
 * @author  Hideki Yonekawa
 * @version 1.0
 */
class GameTimer extends TimerTask {
	/** ����GameCanvas�ı��� */
	private GameCanvas			gameCanvas;

	GameTimer(GameCanvas gameCanvas) {
		this.gameCanvas = gameCanvas;
	}

	/** ��Timer���õķ��� */
	public void run() {
		//����GameCanvas��addSec����
		gameCanvas.SubSec();
	}
} 
