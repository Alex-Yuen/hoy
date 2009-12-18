package it.hoyland.me.ddz;

import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import it.hoyland.me.core.HLCanvas;

public class DeskCanvas extends HLCanvas {

	private byte state;
	private boolean run;
	private byte[][] hands = new byte[3][20]; // 玩家手中的牌
	private byte[] hidden = new byte[3]; // 隐藏牌
	private byte lord = 1; //0: 上家; 1:本家; 2:下家
	private byte lastWinner = 1; // 上一局赢家
	private byte[] bet = new byte[3]; // 押注
	private byte turnIndex = 1; // 表示当前玩家
	private byte[] score = new byte[3];//分数
	private byte[] played = new byte[54]; // 存放已经打的牌
	private byte lastPlayCount; // 上一轮打出的牌(无打的不计)
	private byte lastPlayIndex = 1; // 最后一个打牌的人的位置

	public DeskCanvas(MIDlet midlet) {
		super(midlet);
		setFullScreenMode(true);
		run = true;
		state = 0;
		for(byte i=0; i<score.length; i++){
			score[i] = 100;
		}

	}

	public void run() {
		while (run) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			repaint();
		}
	}

	protected void paint(Graphics g) {

		switch (state) {
		case 0:// 初始化
			init(g);
			break;
		case 1:// 新的牌局
			newGame(g);
			break;
		case 2:// 叫分
			bet(g);
			break;
		case 3:// 流局
			draw(g);
			break;
		case 4:// 结束叫分，设定地主，进入拿底牌，放底牌环节
			lord(g);
			break;
		case 5:// 扣牌
			putDown(g);
		case 6:// 开始打牌
			play(g);
			break;
		case 7:// win
			win(g);
			break;
		default:
			break;
		}

	}

	private void setState(byte state) {
		this.state = state;
	}

	protected void keyPressed(int keyCode) {
		if(state==3||state==7){ //draw, again or not?
			boolean flag = true;
			if(flag){
				setState((byte)1);
			}else{
				((DDZMIDlet)midlet).goMain(); 
			}
		}
	}

	private void init(Graphics g) { // 初始化
		g.setColor(31, 176, 31);
		g.fillRect(0, 0, getWidth(), getHeight());
		// TODO, 显示上家、下家、本家信息，显示三张底牌（背面）
		
		setState((byte) 1);
	}

	private void newGame(Graphics g) { // 新的牌局
		// 洗牌
		byte[] cards = new byte[54];
		AI.permute(cards, hands, hidden);

		// TODO 排序, 显示牌
		for(byte i = 0; i < hands.length; i++){
			AI.sort(hands[i]);
		}
		
		turnIndex = lastWinner; //设置当前玩家
		setState((byte) 2);
	}

	private void bet(Graphics g) { // 叫分
		int r = 0;
		switch(turnIndex){
			case 0:
			case 2:
				r = AI.random.nextInt(4) - 1;
				bet[turnIndex] = (byte)r;
				say(r+""); // 说话
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					e.printStackTrace();
				}
				checkBet(r);
				break;
			case 1:
				// TODO
				// 显示String: 1分, 2分, 3分, 不叫
				// 点击之后
				// checkBet(r);
				break;
			default:
				break;
		}
	}
	
	private void say(String info){ //说话
		//TODO 显示对话框
		info(":"+info);
	}
	
	private void checkBet(int r) {
		if(r!=3){
			if(bet[0]==-1&&bet[1]==-1&&bet[2]==-1){//如果三者都是-1，则流局
				setState((byte) 3);
			}else{
				nextTurn();
			}
		}else{
			// 结束叫牌
			setState((byte) 4);
		}
		
	}
	
	private void nextTurn(){
		if(turnIndex==2){
			turnIndex = 0;
		}else{
			turnIndex += 1;
		}
	}

	private void draw(Graphics g) {
		info("draw, again or not? ");
	}
	
	private void lord(Graphics g){
		lord = turnIndex;
		//TODO 
		// 画帽子
		// 显示牌的正面
		// 把牌加到地主手上
		hands[lord][17] = hidden[0];
		hands[lord][18] = hidden[1];
		hands[lord][19] = hidden[2];
		
		setState((byte) 5);
	}
	
	private void putDown(Graphics g) {
		switch(turnIndex){
			case 0:
			case 2:
				AI.putDown(hands[turnIndex]);
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					e.printStackTrace();
				}
				setState((byte) 6);
				break;
			case 1:
				// TODO
				// 显示手指，提示putdown
				// 点击之后
				// setState((byte) 6);
				break;
			default:
				break;
		}
	}
	
	private void play(Graphics g) {
		int r = 0;
		switch(turnIndex){
		case 0:
		case 2:
			r = AI.play(played, hands[turnIndex], lastPlayCount, lastPlayIndex==turnIndex);
			// 显示新打的牌
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			checkPlay(r);
			break;
		case 1:
			// TODO
			// 显示手指，提示play
			// 点击之后
			// 显示新打的牌
			// checkPlay(r);
			break;
		default:
			break;
		}
	}
	
	private void checkPlay(int r){
		switch(r){
		case 0:// pass
		case 1:// play
			nextTurn();
			break;
		case 2:// win
			setState((byte) 7);
			break;
		default:
			break;
		}
	}
	
	private void win(Graphics g) {
		// 提示turnIndex win
		// 计分
		// 显示一个动态加分的过程
		// 提示是否继续
		// 转keypress进行判断
	}
}
