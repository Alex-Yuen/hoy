package ws.hoyland.je;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.lucene.util.UnicodeUtil;

import ws.hoyland.util.Converts;
import jeasy.analysis.MMAnalyzer;

public class JE {

	public static String separatedTerm(String text) throws IOException {
		MMAnalyzer analyzer = new MMAnalyzer();
		// Reader reader = new
		// FileReader(JE.class.getResource("dict.hld").getFile());// 添加分词库
		// MMAnalyzer.addDictionary(reader);

		// MMAnalyzer.addWord("迈克尔雷第"); //添加分词短语

		// System.out.println(MMAnalyzer.contains("迈克尔雷第"));
		// System.out.println(MMAnalyzer.contains("官员星"));
		// System.out.println(MMAnalyzer.size());
		// if(reader!=null){
		// reader.close();
		// }
		String str = analyzer.segment(text, "|");
		return str;
	}

	public static String printBytesExcept(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bs.length - 1; i++) {
			sb.append(bs[i] + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String getUnicodeBytes(String s) {
		try {
			StringBuffer out = new StringBuffer("");
			byte[] bytes = s.getBytes("unicode");
			for (int i = 2; i < bytes.length - 1; i += 2) {
//				out.append("\\u");
				String str = Integer.toHexString(bytes[i + 1] & 0xff);
				for (int j = str.length(); j < 2; j++) {
					out.append("0");
				}
				String str1 = Integer.toHexString(bytes[i] & 0xff);
				out.append(str);
				out.append(str1);
			}
			return out.toString().toUpperCase();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String bytesToBinaryString(byte[] bs){
		String result = "";
		for(int i=0;i<bs.length;i++){
			String x = "";
			byte b = bs[i];
			for(int m=0;m<8;m++){
				String s = ((b>>m&0x1)>0)?"1":"0";
				x = s+x;
			}
			
			result += x;
		}
		return result;
	}
	
	public static void main(String[] args) { // 测试
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			MMAnalyzer analyzer = new SuperMMAnalyzer(bos);

			// instance.
			String text = "新闻中心搜狐网站搜狐";
			text = "他飞快地跑了";
			String s = analyzer.segment(text, "|");
			/**
			 * String s = separatedTerm("据路透社报道，迈克尔雷第印度尼西亚社会事务部一官员星期二(29日)表示，" +
			 * "日惹市附近当地时间27日晨5时53分发生的里氏6.2级地震已经造成至少5427人死亡，" +
			 * "20000余人受伤，近20万人无家可归。");
			 **/
			System.out.println(s);
//			System.out.println(getUnicodeBytes("新"));
			byte[] bs = Converts.hexStringToByte(getUnicodeBytes(text));
//			System.out.println(bs[0]);
//			System.out.println(bs[1]);
//			System.out.println(bytesToBinaryString(new byte[]{(byte)0xb0, 0x65}));
			System.out.println(bs.length);
			System.out.println(bytesToBinaryString(bs));
			//0110010110110000 his 65b0
//			//1011000001100101 mine b065
			
//			System.out.println(Converts.bytesToHexString(bs));
//			System.out.println(Integer.toBinaryString(bs[0]));
			// System.out.println(bos.toByteArray().length);
			System.out.println(printBytesExcept(bos.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
