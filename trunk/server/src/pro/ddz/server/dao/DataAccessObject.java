package pro.ddz.server.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import pro.ddz.server.model.User;

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
		User user = new User();
		return user;
	}
}
