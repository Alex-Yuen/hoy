package ws.hoyland.st.df;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import ws.hoyland.st.DataSource;

public class DefaultSource implements DataSource {

	private Queue<List<String>> data;
	
	public DefaultSource(){
		this.data = new LinkedList<List<String>>();
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/SZ160706.TXT")));
			String line = null;
			List<String> t = null;
			while((line=reader.readLine())!=null){
				t = new ArrayList<String>();
				for(String c : line.split("\t")){
					t.add(c);
				}

				this.data.add(t);
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
