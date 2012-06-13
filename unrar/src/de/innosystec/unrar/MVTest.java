package de.innosystec.unrar;

import java.io.File;

import de.innosystec.unrar.rarfile.FileHeader;

public class MVTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename="c:/testdata/test3.rar";
		File f = new File(filename);
		Archive a = null;
		boolean result = false;
		
		String[] pwds = {"123", "1sdfsdf11", "1234"};
		
		for(int i=0; i<pwds.length;i++){
			long start = System.currentTimeMillis();
			try {
				a = new Archive(f, pwds[i]);
				if(a!=null&&a.isPass()){
					result = true;
				}else{
					result = false;
				}
				
				if(!a.getMainHeader().isEncrypted()){
					//result = true;
					FileHeader fh = a.nextFileHeader();
					try{
						//while(fh!=null){
							a.extractFile(fh, null);							
							fh = a.nextFileHeader();							
						//}
						result = true;
					}catch(Exception e){
						//e.printStackTrace();
						result = false;
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
				result = false;
			}
			
			System.out.println("PWD["+i+"]:"+pwds[i]+"="+result+"/"+(System.currentTimeMillis()-start)+"ms");
		}
//		if(a!=null){
//			a.getMainHeader().print();
//			FileHeader fh = a.nextFileHeader();
//			while(fh!=null){	
//				try {
//					File out = new File("c:/testdata/"+fh.getFileNameString().trim());
//					//System.out.println(out.getAbsolutePath());
//					FileOutputStream os = new FileOutputStream(out);
//					a.extractFile(fh, os);
//					os.close();
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (RarException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				fh=a.nextFileHeader();
//			}
//		}
	}
}



