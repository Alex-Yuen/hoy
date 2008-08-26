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
	
	public User quickRegister(){
		User user = null;
		try{
			user = new User();
			String dig = Utility.getRndDigitals(Utility.LENGTH);
			String pwd = Utility.getRndDigitals(Utility.LENGTH);
			user.setUserName(dig);
			user.setNickName(dig);
			user.setPassword(pwd);
			user.setScore(0);
			user.setSexual((new Random().nextInt(2)==1)?true:false);
			user.setCreatTime(Calendar.getInstance().getTime());
			
			Connection conn = datasource.getConnection();
			CallableStatement cStmt = conn.prepareCall("{? = call pro_quick_register[?, ?, ?]}");
			cStmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			cStmt.setString(1, user.getUserName());
			cStmt.setString(2, user.getPassword());
			cStmt.setBoolean(3, user.isSexual());
			
			cStmt.executeUpdate();
			user.setId(cStmt.getInt(1));
		}catch(Exception e){
			user = null;
		}
		return user;
	}
}
