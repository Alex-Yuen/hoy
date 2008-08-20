package pro.ddz.server.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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
}
