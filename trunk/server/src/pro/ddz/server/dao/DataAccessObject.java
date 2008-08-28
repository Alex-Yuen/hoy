package pro.ddz.server.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Random;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import pro.ddz.server.model.User;
import pro.ddz.server.util.Utility;

public class DataAccessObject {
	private static DataSource datasource;
	
	public DataAccessObject() {
		if (datasource == null) {
			try {
				Context initCtx = new InitialContext();
				Context ctx = (Context) initCtx.lookup("java:comp/env");
				datasource = (DataSource) ctx.lookup("jdbc/ddzsource");
			} catch (Exception e) {
				datasource = null;
			}
		}
	}
	
	//¿ìËÙ×¢²á
	public User quickRegister(){
		User user = null;
		Connection conn = null;
		CallableStatement cStmt = null;
		try{
			user = new User();
			String dig = Utility.getRndDigitals(Utility.LENGTH);
			String pwd = Utility.getRndDigitals(Utility.LENGTH);
			System.out.println(dig+pwd);
			user.setUserName(dig);
			user.setNickName(dig);
			user.setPassword(pwd);
			user.setScore(0);
			user.setSexual((new Random().nextInt(2)==1)?true:false);
			user.setCreatTime(Calendar.getInstance().getTime());
			
			conn = datasource.getConnection();
			cStmt = conn.prepareCall("{call pro_quick_register(?, ?, ?, ?)}");
			cStmt.setString(1, user.getUserName());
			cStmt.setString(2, user.getPassword());
			cStmt.setBoolean(3, user.isSexual());
			cStmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			
			cStmt.execute();
			user.setId(cStmt.getInt(4));
		}catch(Exception e){
			e.printStackTrace();
			user = null;
		}finally{
			try{
				cStmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return user;
	}
	
	//µÇÂ¼
	public User login(String userName, String password){
		User user = null;
		Connection conn = null;
		CallableStatement cStmt = null;
		try{
			user = new User();
			user.setUserName(userName);
			user.setPassword("*"+password);
			conn = datasource.getConnection();
			cStmt = conn.prepareCall("{call pro_login(?, ?, ?)}");
			cStmt.setString(1, userName);
			cStmt.setString(2, password);
			cStmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			
			cStmt.execute();
			user.setId(cStmt.getInt(3));
		}catch(Exception e){
			e.printStackTrace();
			user = null;
		}finally{
			try{
				cStmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return user;
	}
}
