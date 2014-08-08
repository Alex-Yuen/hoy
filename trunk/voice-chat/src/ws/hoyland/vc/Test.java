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
//			Mixer.Info[] infos = AudioSystem.getMixerInfo();
//			for(int i=0;i<infos.length;i++){
//				System.out.println(infos[i].getName()+"="+infos[i].getDescription());
//
//				Mixer mixer = AudioSystem.getMixer(infos[i]);
//				System.out.println(mixer);
//				//System.out.println(mixer.getMaxLines(infos[i]));
//				Line[] ls = mixer.getSourceLines();
//				System.out.println(ls.length);
//				for(int x=0;x<ls.length;x++){
//					//System.out.println(ls[x]);
//				}
//			}
//			

			//Mixer.Info[] infos = AudioSystem.getMixerInfo();
			
			Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
			System.out.println("Mixers:");
			for (Mixer.Info minfo: minfoSet) {
			    System.out.println("   " + minfo.toString());

			    Mixer m = AudioSystem.getMixer(minfo);
			    System.out.println("    Mixer: " + m.toString());
			    System.out.println("      Source lines");
			    Line.Info[] slines = m.getSourceLineInfo();
			    for (Line.Info s: slines) {
				System.out.println("        " + s.toString());
			    }

			    Line.Info[] tlines = m.getTargetLineInfo();
			    System.out.println("      Target lines");
			    for (Line.Info t: tlines) {
				System.out.println("        " + t.toString());
			    }
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
