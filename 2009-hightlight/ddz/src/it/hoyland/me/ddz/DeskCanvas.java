package it.hoyland.me.ddz;

import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import it.hoyland.me.core.HLCanvas;

public class DeskCanvas extends HLCanvas {

	private byte state;
	private boolean run;
	private byte[][] hands = new byte[3][20]; // ������е���
	private byte[] hidden = new byte[3]; // ������
	private byte lord = 1; //0: �ϼ�; 1:����; 2:�¼�
	private byte lastWinner = 1; // ��һ��Ӯ��
	private byte[] bet = new byte[3]; // Ѻע
	private byte turnIndex = 1; // ��ʾ��ǰ���

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
		case 0:// ��ʼ��
			init(g);
			break;
		case 1:// �µ��ƾ�
			newGame(g);
			break;
		case 2:// �з�
			bet(g);
			break;
		case 3:// ����
			draw(g);
			break;
		case 4:// �����з֣��趨�����������õ��ƣ��ŵ��ƻ���
			lord(g);
			break;
		case 5:// ����
			putDown(g);
		case 6:// ��ʼ����
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

	private void init(Graphics g) { // ��ʼ��
		g.setColor(31, 176, 31);
		g.fillRect(0, 0, getWidth(), getHeight());
		// TODO, ��ʾ�ϼҡ��¼ҡ�������Ϣ����ʾ���ŵ��ƣ����棩
		
		setState((byte) 1);
	}

	private void newGame(Graphics g) { // �µ��ƾ�
		// ϴ��
		byte[] cards = new byte[54];
		for (byte i = 0; i < cards.length; i++) {
			cards[i] = i;
		}
		permute(cards);

		// ����
		byte m = 0;
		for (byte i = 0; i < hands.length; i++) {
			for (byte j = 0; j < hands[i].length-3; j++) {
				hands[i][j] = cards[m++];
			}
		}

		// ����
		for (byte i = 0; i < hidden.length; i++) {
			hidden[i] = cards[m++];
		}

		// TODO ��ʾ�ƣ�����
		for(byte i = 0; i < hands.length; i++){
			sort(hands[i]);
		}
		
		turnIndex = lastWinner; //���õ�ǰ���
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

	private void bet(Graphics g) { // �з�
		int r = 0;
		switch(turnIndex){
			case 0:
			case 2:
				r = AI.random.nextInt(4) - 1;
				bet[turnIndex] = (byte)r;
				say(r+""); // ˵��
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					e.printStackTrace();
				}
				checkBet(r);
				break;
			case 1:
				// TODO
				// ��ʾString: 1��, 2��, 3��, ����
				// ���֮��
				// checkBet(r);
				break;
			default:
				break;
		}
	}
	
	private void say(String info){ //˵��
		//TODO ��ʾ�Ի���
		info(":"+info);
	}
	
	private void checkBet(int r) {
		if(r!=3){
			if(bet[0]==-1&&bet[1]==-1&&bet[2]==-1){//������߶���-1��������
				setState((byte) 3);
			}else{
				nextTurn();
			}
		}else{
			// ��������
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
		// ��ñ��
		// ��ʾ�Ƶ�����
		// ���Ƽӵ���������
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
				// ��ʾ��ָ����ʾputdown
				// ���֮��
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
			// ��ʾ��ָ����ʾplay
			// ���֮��
			// setState((byte) 7);
			break;
		default:
			break;
		}
	}
}
