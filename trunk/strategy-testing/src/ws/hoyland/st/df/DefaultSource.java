package ws.hoyland.st.df;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

import ws.hoyland.st.DataSource;

public class DefaultSource implements DataSource {

	private Queue<List<String>> data;
	
	@SuppressWarnings("unchecked")
	public DefaultSource(){
		this.data = new LinkedList<List<String>>();
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/SH000300.TXT")));
			String line = null;
			List<String> t = null;
			while((line=reader.readLine())!=null){
				t = new ArrayList<String>();
				for(String c : line.split("\t")){
					t.add(c);
				}

				this.data.add(t);
			}
			
			Object[] x = this.data.toArray();
			double[] cc = new double[this.data.size()];
			int i = 0;
			for (List<String> lx : this.data) {
				cc[i++] = Double.parseDouble(lx.get(4));
			}
			
			Core ta = new Core();
			MInteger mi_begin = new MInteger();
			MInteger mi_length = new MInteger();
			
			int period = 20;
			double[] ema = new double[cc.length];
			ta.ema(0, cc.length-1, cc, period, mi_begin, mi_length, ema);
			
			int m=0;
			for(;m<period;m++){
				((List<String>)x[m]).add(String.valueOf(0));
			}
			for(;m<cc.length;m++){
				((List<String>)x[m]).add(String.valueOf(ema[m-period]));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> fire() {
		// TODO Auto-generated method stub
		if(data.size()>0){
			return data.poll();
		}else{
			return null;
		}
	}


}
