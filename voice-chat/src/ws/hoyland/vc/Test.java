package ws.hoyland.vc;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Mixer.Info[] infos = AudioSystem.getMixerInfo();
			for(int i=0;i<infos.length;i++){
				//System.out.println(infos[i].getName()+"="+infos[i].getDescription());

				Mixer mixer = AudioSystem.getMixer(infos[i]);
				Line[] ls = mixer.getSourceLines();
				for(int x=0;x<ls.length;x++){
					System.out.println(ls[x]);
				}
			}
			

			//Mixer.Info[] infos = AudioSystem.getMixerInfo();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
