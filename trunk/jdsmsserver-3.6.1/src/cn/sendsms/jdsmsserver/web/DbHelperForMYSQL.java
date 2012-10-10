/*     */ package cn.sendsms.jdsmsserver.web;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
import java.util.Properties;
/*     */ 
/*     */ public class DbHelperForMYSQL extends DbHelper
/*     */ {
/*     */   public DbHelperForMYSQL(Properties props, String dbid)
/*     */   {
/*  15 */     this.props = props;
/*  16 */     this.dbid = dbid;
/*     */   }
/*     */ 
/*     */   public Page waitForSendMsgList(int pageIndex)
/*     */     throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException
/*     */   {
/*  22 */     Class.forName(getProperty("driver")).newInstance();
/*  23 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
/*  24 */     Statement stmt = conn.createStatement();
/*  25 */     String sql = "select * from " + getProperty("tables.sms_out") + " where status in ('U','Q','Z') order by create_date,priority desc limit %begin% , %size%";
/*  26 */     Page page = new Page();
/*  27 */     page.setPageIndex(pageIndex);
/*  28 */     sql = sql.replaceAll("%begin%", String.valueOf((page.getPageIndex() - 1) * page.getPageSize()));
/*  29 */     sql = sql.replaceAll("%size%", String.valueOf(page.getPageSize()));
/*     */ 
/*  31 */     ResultSet rs = stmt.executeQuery(sql);
/*  32 */     ArrayList list = page.getData();
/*  33 */     while (rs.next()) {
/*  34 */       OutMessage msg = new OutMessage();
/*  35 */       msg.setId(rs.getLong("id"));
/*  36 */       msg.setContent(rs.getString("text"));
/*  37 */       msg.setCreateTime(DateFormat(rs.getTimestamp("create_date")));
/*  38 */       msg.setGateway_id(rs.getString("gateway_id"));
/*  39 */       msg.setRecipient(rs.getString("recipient"));
/*  40 */       msg.setStatus((char)rs.getCharacterStream("status").read());
/*  41 */       msg.setType((char)rs.getCharacterStream("type").read());
/*  42 */       msg.setWap_url(rs.getString("wap_url"));
/*  43 */       list.add(msg);
/*     */     }
/*     */ 
/*  46 */     String sql1 = "select count(1) as toatlnum from " + getProperty("tables.sms_out") + " where status in ('U','Q','Z')";
/*  47 */     rs = stmt.executeQuery(sql1);
/*  48 */     if (rs.next()) {
/*  49 */       page.setTotalNum(rs.getLong(1));
/*  50 */       if (page.getTotalNum() % page.getPageSize() == 0L)
/*  51 */         page.setPageNum((int)(page.getTotalNum() / page.getPageSize()));
/*  52 */       else page.setPageNum((int)(page.getTotalNum() / page.getPageSize() + 1L));
/*     */     }
/*  54 */     stmt.close();
/*  55 */     conn.close();
/*  56 */     return page;
/*     */   }
/*     */ 
/*     */   public void cancelWaitMsg()
/*     */     throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
/*     */   {
/*  62 */     Class.forName(getProperty("driver")).newInstance();
/*  63 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
/*  64 */     Statement stmt = conn.createStatement();
/*  65 */     String sql = "update " + getProperty("tables.sms_out") + " set status = 'Z' where status in ('U','Q')";
/*  66 */     stmt.executeUpdate(sql);
/*  67 */     stmt.close();
/*  68 */     conn.close();
/*     */   }
/*     */ 
/*     */   public void deleteCanceledMsg()
/*     */     throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
/*     */   {
/*  74 */     Class.forName(getProperty("driver")).newInstance();
/*  75 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
/*  76 */     Statement stmt = conn.createStatement();
/*  77 */     String sql = "delete from " + getProperty("tables.sms_out") + " where status in ('Z')";
/*  78 */     stmt.executeUpdate(sql);
/*  79 */     stmt.close();
/*  80 */     conn.close();
/*     */   }
/*     */ 
/*     */   public Page findMsgByCondition(Condition condition)
/*     */     throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
/*     */   {
/*  86 */     Class.forName(getProperty("driver")).newInstance();
/*  87 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
/*  88 */     Statement stmt = conn.createStatement();
/*     */ 
/*  90 */     StringBuffer sql = new StringBuffer();
/*  91 */     sql.append("select * from ");
/*  92 */     if (condition.getType() == 3)
/*  93 */       sql.append(getProperty("tables.sms_in"));
/*     */     else {
/*  95 */       sql.append(getProperty("tables.sms_out"));
/*     */     }
/*  97 */     if (condition.getType() != 3) {
/*  98 */       sql.append(" where status = ");
/*  99 */       if (condition.getType() == 1)
/* 100 */         sql.append("'S'");
/*     */       else
/* 102 */         sql.append("'F'");
/*     */     } else {
/* 104 */       sql.append(" where type = 'I'");
/*     */     }
/*     */ 
/* 107 */     StringBuffer countSql = new StringBuffer(sql.toString());
/* 108 */     int index = countSql.indexOf("*");
/* 109 */     countSql.replace(index, index + 1, "count(1) as totalnum");
/*     */ 
/* 111 */     sql.append(" order by ");
/* 112 */     if (condition.getType() != 3)
/* 113 */       sql.append("create_date ");
/*     */     else
/* 115 */       sql.append("receive_date ");
/* 116 */     if (condition.getOrder() == 0)
/* 117 */       sql.append("desc");
/*     */     else {
/* 119 */       sql.append("asc");
/*     */     }
/* 121 */     Page page = new Page();
/* 122 */     page.setPageIndex(condition.getPageIndex());
/*     */ 
/* 124 */     sql.append(" limit " + (condition.getPageIndex() - 1) * page.getPageSize() + "," + page.getPageSize());
/* 125 */     ArrayList list = page.getData();
/* 126 */     ResultSet rs = stmt.executeQuery(sql.toString());
/* 127 */     while (rs.next()) {
/* 128 */       if (condition.getType() != 3) {
/* 129 */         OutMessage msg = new OutMessage();
/* 130 */         msg.setId(rs.getLong("id"));
/* 131 */         msg.setContent(rs.getString("text"));
/* 132 */         msg.setCreateTime(DateFormat(rs.getTimestamp("sent_date")));
/* 133 */         msg.setGateway_id(rs.getString("gateway_id"));
/* 134 */         msg.setRecipient(rs.getString("recipient"));
/* 135 */         msg.setStatus((char)rs.getCharacterStream("status").read());
/* 136 */         msg.setType((char)rs.getCharacterStream("type").read());
/* 137 */         msg.setWap_url(rs.getString("wap_url"));
/* 138 */         list.add(msg);
/*     */       } else {
/* 140 */         InMessage msg = new InMessage();
/* 141 */         msg.setId(rs.getLong("id"));
/* 142 */         msg.setContent(rs.getString("text"));
/* 143 */         msg.setGateway_id(rs.getString("gateway_id"));
/* 144 */         msg.setType((char)rs.getCharacterStream("type").read());
/* 145 */         msg.setMsgDate(DateFormat(rs.getTimestamp("message_date")));
/* 146 */         msg.setRecvDate(DateFormat(rs.getTimestamp("receive_date")));
/* 147 */         msg.setOriginator(rs.getString("originator"));
/* 148 */         list.add(msg);
/*     */       }
/*     */     }
/* 151 */     rs = stmt.executeQuery(countSql.toString());
/* 152 */     if (rs.next()) {
/* 153 */       page.setTotalNum(rs.getLong(1));
/* 154 */       if (page.getTotalNum() % page.getPageSize() == 0L)
/* 155 */         page.setPageNum((int)(page.getTotalNum() / page.getPageSize()));
/* 156 */       else page.setPageNum((int)(page.getTotalNum() / page.getPageSize() + 1L));
/*     */     }
/* 158 */     stmt.close();
/* 159 */     conn.close();
/* 160 */     return page;
/*     */   }
/*     */
@Override
public boolean[] getSwitchStatus() throws InstantiationException,
		IllegalAccessException, ClassNotFoundException, SQLException {
	/*  74 */     Class.forName(getProperty("driver")).newInstance();
	/*  75 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
	/*  76 */     Statement stmt = conn.createStatement();

	String sql = "select * from smsserver_switch order by id desc limit 1";
	 ResultSet rs = stmt.executeQuery(sql.toString());
	if (rs.next()) {

			boolean[] rt = new boolean[2];
			rt[0] = rs.getBoolean("master");
			rt[1] = rs.getBoolean("slaver");
			rs.close();
			stmt.close();
			conn.close();
			return rt;
	 }else{
		 return null;
	 }
}
@Override
public Page getSNBList(Condition condition) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
	/*  86 */     Class.forName(getProperty("driver")).newInstance();
	/*  87 */     Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
	/*  88 */     Statement stmt = conn.createStatement();
	/*     */ 
	/*  90 */     StringBuffer sql = new StringBuffer();
	/*  91 */     sql.append("select * from ");
	/*  92 */     if (condition.getType() == 0)
	/*  93 */       sql.append(getProperty("tables.sms_switch"));
	/*     */     else {
	/*  95 */       sql.append(getProperty("tables.sms_backup"));
	/*     */     }
	/**
	     if (condition.getType() != 3) {
       sql.append(" where status = ");
       if (condition.getType() == 1)
         sql.append("'S'");
       else
        sql.append("'F'");
	     } else {
	       sql.append(" where type = 'I'");
	    }
	**/
	/*     */ 
	/* 107 */     StringBuffer countSql = new StringBuffer(sql.toString());
	/* 108 */     int index = countSql.indexOf("*");
	/* 109 */     countSql.replace(index, index + 1, "count(1) as totalnum");
	/*     */ 
	/* 111 */     sql.append(" order by ");
	/* 112 */     if (condition.getType() == 0)
	/* 113 */       sql.append("switch_time ");
	/*     */     else
	/* 115 */       sql.append("backup_time ");
	
	/* 116 */     if (condition.getOrder() == 0)
	/* 117 */       sql.append("desc");
	/*     */     else {
	/* 119 */       sql.append("asc");
	/*     */     }
	/* 121 */     Page page = new Page();
	/* 122 */     page.setPageIndex(condition.getPageIndex());
	/*     */ 
	/* 124 */     sql.append(" limit " + (condition.getPageIndex() - 1) * page.getPageSize() + "," + page.getPageSize());
	/* 125 */     ArrayList list = page.getData();
	/* 126 */     ResultSet rs = stmt.executeQuery(sql.toString());
	/* 127 */     while (rs.next()) {
	/* 128 */       if (condition.getType() == 0) {
	/* 129 */         SwitRecord rec = new SwitRecord();
	/* 130 */         rec.setId(rs.getLong("id"));
					  rec.setMaster(rs.getBoolean("master"));
					  rec.setSlaver(rs.getBoolean("slaver"));
					  rec.setMemo(rs.getString("memo"));
					  rec.setSwitTime(rs.getDate("switch_time"));
	/* 138 */         list.add(rec);
	/*     */       } else {
	/* 140 */         BackupRecord rec = new BackupRecord();
	/* 141 */         rec.setId(rs.getLong("id"));
					  rec.setMachine(rs.getBoolean("machine"));
					  rec.setMemo(rs.getString("memo"));
					  rec.setState(rs.getInt("state"));
					  rec.setFileSize(rs.getLong("file_size"));
					  rec.setBackupTime(rs.getDate("backup_time"));
	/* 148 */         list.add(rec);
	/*     */       }
	/*     */     }
	/* 151 */     rs = stmt.executeQuery(countSql.toString());
	/* 152 */     if (rs.next()) {
	/* 153 */       page.setTotalNum(rs.getLong(1));
	/* 154 */       if (page.getTotalNum() % page.getPageSize() == 0L)
	/* 155 */         page.setPageNum((int)(page.getTotalNum() / page.getPageSize()));
	/* 156 */       else page.setPageNum((int)(page.getTotalNum() / page.getPageSize() + 1L));
	/*     */     }
	/* 158 */     stmt.close();
	/* 159 */     conn.close();
	/* 160 */     return page;
} 


}
/* Location:           C:\Users\Administrator\Desktop\jdsmsserver-3.6.1.jar
 * Qualified Name:     cn.sendsms.jdsmsserver.web.DbHelperForMYSQL
 * JD-Core Version:    0.6.0
 */