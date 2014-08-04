package ws.hoyland.six;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	private static int getFactorialSum(int n){ 
		if(n==1||n==0){   
			return 1;  
		}else{   
			return getFactorialSum(n-1)*n; 
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] sps = new int[]{
				26,19,7,31,42,30,37,37,12,49,36,4,38,42,20,35,8,36,8,6,8,44,25,2,26,15,45,36,9,5,12,10,43,22,21,19,47,14,30,34,14,1,35,5,49,5,24,23,7,39,35,10,47,14,32,23,40,2,4,22,20,31,11,2,1,3,25,34,38,36,13,46,45,20,45,24,4,26,20,2,26,41,31,19,39,44,20,31,44,11,20,24,42,33,4,42,48,37,16,39,34,40,4,38,31,15,48,47,34,26,26,38,24,19,21,10,14,28,4,7,26,48,35,7,15,36,48,23,44,30,41,17,14,21,45,24,21,26,7,4,24,4,31,21,45,41,18,10,38,10,49,23,14,19,
				6,27,42,10,1,41,27,40,16,2,2,28,40,41,13,14,7,16,44,19,3,1,46,46,47,46,16,16,20,21,48,30,31,36,17,42,29,20,41,30,8,35,40,17,33,19,32,17,11,8,3,24,15,44,20,9,32,17,14,49,41,27,18,30,26,6,1,10,24,43,11,14,37,44,24,2,49,19,49,27,13,24,31,33,42,24,22,22,22,42,7,37,23,34,43,16,35,28,45,37,42,41,38,47,27,5,12,6,34,25,47,5,46,27,6,12,2,27,27,13,41,37,18,8,32,46,27,15,17,18,48,35,19,23,14,35,8,42,9,34,27,14,37,10,40,10,23,7,36,11,12,12,
				3,40,43,4,38,10,2,37,48,37,20,48,46,9,21,19,17,12,16,30,45,40,5,44,49,29,30,10,22,28,21,9,20,20,15,13,29,43,32,11,9,22,12,10,28,45,28,35,38,47,3,36,5,22,6,30,48,31,8,35,4,23,10,8,17,18,38,35,22,46,2,39,38,42,19,38,13,27,37,30,15,29,36,35,20,22,21,10,34,30,36,31,24,34,48,10,7,12,1,40,2,30,6,42,29,17,32,16,22,25,42,13,6,38,13,5,47,33,46,23,13,14,40,47,44,49,47,10,43,49,48,33,35,29,35,21,5,32,14,29,5,31,25,1,34,31,32,5,2,24,41,5,1,38,
				10,2,30,12,47,14,1,31,41,15,49,47,7,47,43,48,25,8,15,17,37,22,47,13,5,32,43,34,12,6,15,37,41,34,38,8,18,42,14,44,6,41,25,4,23,2,45,14,38,22,3,8,35,41,4,32,40,33,41,34,7,46,38,37,37,9,6,32,9,24,28,39,47,24,1,20,44,37,3,43,27,3,29,13,31,32,5,1,7,12,10,48,16,28,8,45,48,5,27,45,38,38,3,29,37,43,43,42,39,40,47,28,32,13,9,16,35,4,40,26,6,21,28,1,19,2,41,42,43,25,31,21,30,13,31,13,10,6,47,4,48,38,24,14,48,34,49,2,14,23,40,45,
				39,7,48,7,13,11,26,1,24,5,28,32,22,13,37,32,47,18,34,7,15,21,17,37,5,2,28,4,12,18,8,34,27,11,43,17,5,4,25,39,2,18,48,9,48,6,19,9,36,14,33,36,13,22,27,17,34,36,6,38,27,23,7,2,30,28,5,1,44,39,30,39,22,17,14,13,17,49,8,42,25,44,34,20,30,21,49,42,48,42,9,31,44,23,1,36,46,33,20,16,36,17,37,6,17,36,33,49,41,31,19,34,34,4,15,20,30,20,38,12,49,18,26,30,40,17,21,36,20,21,16,46,32,48,36,4,13,18,9,14,21,29,20,16,27,29,47,27,39,6,5,14,
				21,43,46,25,17,21,33,38,44,7,6,18,45,37,39,14,19,30,11,35,45,14,5,44,34,22,38,40,27,30,27,27,14,4,6,36,11,25,3,31,6,25,13,27,42,3,14,16,46,14,30,25,30,48,5,8,7,7,6,19,49,1,35,31,15,6,27,40,15,21,7,48,36,28,21,20,47
		};
		
		List<Integer> ct = new ArrayList<Integer>();
		for(int i=0;i<49;i++){
			ct.add(0);
		}
		List<Integer> ctx = new ArrayList<Integer>();
		for(int i=0;i<49;i++){
			ctx.add(0);
		}
		Map<Integer, Double> p = new HashMap<Integer, Double>();
		for(int i=0;i<49;i++){
			p.put(i, 0d);
		}
		
		System.out.println(ct.size()+"/"+ctx.size());
		float r = 0.00f;
		int n = 0;
		int position = 0;
		
		for(int idx=200;idx<sps.length;idx++){
			//init
			for(int i=0;i<ct.size();i++){
				ct.set(i, 0);
			}
			
			for(int i=0;i<ctx.size();i++){
				ctx.set(i, 0);
			}			
			
			for(int i=0;i<49;i++){
				p.put(i, 0d);
			}
			
			r = 0.00f;
			n = 0;
			position = 0;
			//caculate all
			for(int t=idx-200;t<idx;t++){
				ct.set(sps[t]-1, ct.get(sps[t]-1)+1);
			}
			//caculate current
			for(int t=idx-10;t<idx;t++){
				ctx.set(sps[t]-1, ctx.get(sps[t]-1)+1);
			}
			//System.out.println(ct.size());

			r = 1f/49f;
			for(int i=0;i<p.size();i++){
				//r = ct.get(i)/200f;
				n = ctx.get(i) + 1;
				//System.out.println(r+"="+n);
				p.put(i, Math.pow(Math.E, (-10*r))*Math.pow(10*r, n)/getFactorialSum(n));
			}
			

			List<Map.Entry<Integer, Double>> px =
					new ArrayList<Map.Entry<Integer, Double>>(p.entrySet());

			Collections.sort(px, new Comparator<Map.Entry<Integer, Double>>(){

				@Override
				public int compare(Map.Entry<Integer, Double> d0, Map.Entry<Integer, Double> d1) {
					return d1.getValue().compareTo(d0.getValue());
//					if(arg0.doubleValue()>arg1.doubleValue())
//					{
//						System.out.println("XXXXXXXXXXXXXXXX");
//						return 1;
//					}else{
//						System.out.println("YYYYYYYYYYYYYYYY");
//						return 0;
//					}
				}
				
			});
			
			for(int i=0;i<px.size();i++){
				//System.out.println(px.get(i).getKey());
				if(px.get(i).getKey()+1==sps[idx]){
					position = i;
					break;
				}
			}
			System.out.println(position);
			//sps[t]
			//break;
		}
	}

}
