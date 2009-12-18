/*
 * GameTimer.java
 *
 * Created on 2007年3月24日, 上午8:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.util.TimerTask;

/**
 * NumberSliderPuzzle的计时器操作与执行所用的类
 *
 * @author  Hideki Yonekawa
 * @version 1.0
 */
class GameTimer extends TimerTask {
	/** 储存GameCanvas的变量 */
	private GameCanvas			gameCanvas;

	GameTimer(GameCanvas gameCanvas) {
		this.gameCanvas = gameCanvas;
	}

	/** 被Timer调用的方法 */
	public void run() {
		//调用GameCanvas的addSec方法
		gameCanvas.SubSec();
	}
} 
