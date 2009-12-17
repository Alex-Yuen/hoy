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

	public DeskCanvas(MIDlet midlet) {
		super(midlet);
		setFullScreenMode(true);
		run = true;
		state = 0;

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
		default:
			break;
		}

	}

	private void setState(byte state) {
		this.state = state;
	}

	protected void keyPressed(int keyCode) {
		if(state==3){ //draw, again or not?
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
		for (byte i = 0; i < cards.length; i++) {
			cards[i] = i;
		}
		permute(cards);

		// 发牌
		byte m = 0;
		for (byte i = 0; i < hands.length; i++) {
			for (byte j = 0; j < hands[i].length-3; j++) {
				hands[i][j] = cards[m++];
			}
		}

		// 底牌
		for (byte i = 0; i < hidden.length; i++) {
			hidden[i] = cards[m++];
		}

		// TODO 显示牌，排序
		for(byte i = 0; i < hands.length; i++){
			sort(hands[i]);
		}
		
		turnIndex = lastWinner; //设置当前玩家
		setState((byte) 2);
	}

	private void permute(byte[] array) {
		for (byte i = 1; i < array.length; i++) {
			swap(array, i, AI.random.nextInt(i));
		}
	}

	private void swap(byte[] array, int indexA, int indexB) {
		byte temp = array[indexA];
		array[indexA] = array[indexB];
		array[indexB] = temp;
	}
	
	private void sort(byte[] array){
		
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
		switch(turnIndex){
		case 0:
		case 2:
			AI.play(hands[turnIndex]);
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			setState((byte) 7);
			break;
		case 1:
			// TODO
			// 显示手指，提示play
			// 点击之后
			// setState((byte) 7);
			break;
		default:
			break;
		}
	}
}
