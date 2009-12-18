package it.hoyland.me.ddz;

import java.util.Random;

public class AI {
	public static Random random = new Random();

	public static void putDown(byte[] bs) {
		// TODO Auto-generated method stub
		if(bs.length!=20){
			return;
		}else{
			// Ëæ»ú
		}
	}

	/*
	 * ´òÅÆ
	 */
	public static int play(byte[] bs, byte[] hands, byte lastPlayCount, boolean b) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void permute(byte[] cards, byte[][] hands, byte[] hidden) {

		for (byte i = 0; i < cards.length; i++) {
			cards[i] = i;
		}
		permute(cards);

		// ·¢ÅÆ
		byte m = 0;
		for (byte i = 0; i < hands.length; i++) {
			for (byte j = 0; j < hands[i].length-3; j++) {
				hands[i][j] = cards[m++];
			}
		}

		// µ×ÅÆ
		for (byte i = 0; i < hidden.length; i++) {
			hidden[i] = cards[m++];
		}
		
	}

	public static void sort(byte[] bs) {
		// TODO Auto-generated method stub
		
	}
	
	private static void permute(byte[] array) {
		for (byte i = 1; i < array.length; i++) {
			swap(array, i, AI.random.nextInt(i));
		}
	}

	private static void swap(byte[] array, int indexA, int indexB) {
		byte temp = array[indexA];
		array[indexA] = array[indexB];
		array[indexB] = temp;
	}

	

}
