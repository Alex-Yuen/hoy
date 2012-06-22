package ws.hoyland.rstest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RS2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//int k = 0;
		try{
			long start = System.currentTimeMillis();

			List<Record> rsx = new ArrayList<Record>();
			Connection conn = null;
			Statement stmt = null;
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rs", "root", "Hoy133");			
		    stmt = conn.createStatement();
		    
		    ResultSet rs = stmt.executeQuery("select * from record");
			
		    Record rd = null;
		    
		    while(rs.next()){
				rd = new Record();
				rd.setId(rs.getLong("id"));
				rd.setAge(rs.getLong("age"));
				rd.setFamily(rs.getLong("family"));
				rd.setNation(rs.getLong("nation"));
				rd.setZone(rs.getLong("zone"));
		    	rsx.add(rd);
		    }
		    System.out.println(System.currentTimeMillis()-start);
		    
//			Record rd = null;
			Random rnd = new Random();
//			for(int i=0; i<200000; i++){
//				rd = new Record();
//				rd.setId(i);
//				rd.setAge(rnd.nextInt(100)+1);
//				rd.setFamily(rnd.nextInt(100)+1);
//				rd.setNation(rnd.nextInt(100)+1);
//				rd.setZone(rnd.nextInt(100)+1);
//				stmt.executeUpdate("insert into record (age, family, nation, zone) value("+rd.getAge()+", "+rd.getFamily()+", "+rd.getNation()+", "+rd.getZone()+")");
//				k++;
//				//rs.add(rd);
//			}
			

		
			//search
			final Record rdx = new Record();
			rdx.setAge(rnd.nextInt(100)+1);
			rdx.setFamily(rnd.nextInt(100)+1);
	//		rdx.setNation(rnd.nextInt()+1);
	//		rdx.setZone(rnd.nextInt()+1);
			int i = 0;
			for(Record rdn : rsx){
	//			if(rdx.getAge()!=0){
	//				if(rdn.getAge()!=rdx.getAge())
	//					continue;
	//			}
	//			if(rdx.getFamily()!=0){
	//				if(rdn.getFamily()!=rdx.getFamily())
	//					continue;
	//			}
	//			if(rdx.getNation()!=0){
	//				if(rdn.getNation()!=rdx.getNation())
	//					continue;
	//			}
	//			if(rdx.getZone()!=0){
	//				if(rdn.getZone()!=rdx.getZone())
	//					continue;
	//			}
	//			i++;
				if((rdx.getAge() == 0 || rdn.getAge() == rdx.getAge()) && (rdx.getFamily() == 0 || rdn.getFamily() == rdx.getFamily()) && (rdx.getNation() == 0 || rdn.getNation() == rdx.getNation()) && (rdx.getZone() == 0 || rdn.getZone() == rdx.getZone())){
					i++;
				}
			}
			
				
			System.out.println(System.currentTimeMillis()-start);
			System.out.println("count="+i);

		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("k="+k);
	}

}
