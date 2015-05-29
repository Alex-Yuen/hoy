package ws.hoyland.captcha.database.util;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.DriverManager;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class DBfactory {

	private Connection conn;
	private static DBfactory db;

	public static DBfactory getDBfactory() {

		if (db == null)
			db = new DBfactory();
		return db;

	}

	private DBfactory() {

		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:myDatabase;create=true";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(connectionURL);
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public String getValuebyDouble(double dble) {

		ArrayList<Object[]> list = new ArrayList<Object[]>();
		try {
			Statement stmt2 = conn.createStatement();

			ResultSet rs = stmt2
					.executeQuery("select t.value , t.vdouble from vauledouble t where t.vdouble<"
							+ (dble + 0.005)
							+ " and t.vdouble>"
							+ (dble - 0.005) + "");

			while (rs.next()) {
				list.add(new Object[] { rs.getString(1), rs.getString(2) });

			}

			rs.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return list.size() > 0 ? list.get(0)[0].toString() : null;

	}

	public void fillLIstModel(DefaultTableModel dbf) {

		try {
			Statement stmt2 = conn.createStatement();

			ResultSet rs = stmt2.executeQuery("select * from Tsamplevalue");

			while (rs.next()) {
				dbf.addRow(new Object[] { rs.getString(1),
						new Object[] { rs.getString(2), rs.getString(3) },
						rs.getString(3) });
			}

			rs.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void delSampleandvalue(String id) {

		try {
			Statement stmt2 = conn.createStatement();

			stmt2.execute("delete from tsamplevalue where id='" + id + "'");

			stmt2.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public void updateSampleValue(String id, String value) {

		try {
			Statement stmt2 = conn.createStatement();

			stmt2.execute("update tsamplevalue set value='" + value
					+ "' where id='" + id + "'");

			stmt2.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// 获得样本
	public Object[] getSampleandvalue() {
		ArrayList<String> listsample = new ArrayList<String>();
		ArrayList<String> listvalue = new ArrayList<String>();
		try {
			Statement stmt2 = conn.createStatement();

			ResultSet rs = stmt2
					.executeQuery("select t.sample,vdouble from Tsamplevalue t , vauledouble t1 where t1.value=t.value  ");

			while (rs.next()) {
				listsample.add(rs.getString(1));
				listvalue.add(rs.getString(2));
			}

			rs.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
		double listsampled[][] = new double[listsample.size()][81];
		double listvalued[][] = new double[listsample.size()][1];
		for (int i = 0; i < listsample.size(); i++) {
			String[] str = listsample.get(i).split(",");
			for (int j = 0; j < 81; j++) {
				listsampled[i][j] = Double.valueOf(str[j]);
			}

			listvalued[i][0] = Double.valueOf(listvalue.get(i));

		}

		return new Object[] { listsampled, listvalued };

	}

	// 获得预设的结果
	public double[][] getdesiredvalue() {
		return new double[1][1];

	}

	public void insertTsamplevalue(String sample, String value, String id) {
		try {
			PreparedStatement psInsert = conn
					.prepareStatement("insert into Tsamplevalue values(?,?,?)");
			psInsert.setString(1, sample);
			psInsert.setString(2, value);
			psInsert.setString(3, id);
			psInsert.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
