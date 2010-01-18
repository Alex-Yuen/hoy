package net.fxsprit.test;

import net.fxsprit.util.IdGenerator;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IdGenerator IDGENERATOR = new IdGenerator();
		for(int i=0;i<50;i++)
		System.out.println(IDGENERATOR.generateId(8));
	}

}
