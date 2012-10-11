package cn.sendsms.jdsmsserver.web;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

// Referenced classes of package cn.sendsms.jdsmsserver.web:
//            Page, Condition

public abstract class DbHelper
{

    protected Properties props;
    protected String dbid;

    public DbHelper()
    {
    }

    public static DbHelper getDbHelper(Properties props, String dbid)
        throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        String classType1 = cn.sendsms.jdsmsserver.web.DbHelper.class.getName();
        String classType = (new StringBuilder(String.valueOf(classType1))).append("For").append(props.getProperty((new StringBuilder(String.valueOf(dbid))).append(".type").toString()).toUpperCase()).toString();
        Class c = Class.forName(classType);
        Constructor constructor = c.getConstructor(new Class[] {
            java.util.Properties.class, java.lang.String.class
        });
        DbHelper helper = (DbHelper)constructor.newInstance(new Object[] {
            props, dbid
        });
        return helper;
    }

    protected String getProperty(String key)
    {
        return props.getProperty((new StringBuilder(String.valueOf(dbid))).append(".").append(key).toString(), "");
    }

    public void insertMessage(boolean isPush, String mobiles, String content, String sendport, String pushUrl)
        throws Exception
    {
        Class.forName(getProperty("driver")).newInstance();
        Connection conn = DriverManager.getConnection(getProperty("url"), getProperty("username"), getProperty("password"));
        String sql = "insert into %s (type,recipient,text,wap_url,wap_expiry_date,wap_signal,create_da" +
"te,originator,encoding,status_report,flash_sms,dst_port,src_port,sent_date,ref_n" +
"o,priority,status,errors,gateway_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
"?)"
;
        sql = String.format(sql, new Object[] {
            getProperty("tables.sms_out")
        });
        PreparedStatement pstmt = conn.prepareStatement(sql);
        String ms[] = mobiles.split(" ");
        String as[];
        int j = (as = ms).length;
        for(int i = 0; i < j; i++)
        {
            String mobile = as[i];
            if(!isPush)
            {
                pstmt.setString(1, "O");
            } else
            {
                pstmt.setString(1, "W");
            }
            pstmt.setString(2, mobile);
            pstmt.setString(3, content);
            pstmt.setString(4, pushUrl);
            pstmt.setString(5, null);
            pstmt.setString(6, null);
            pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(8, "");
            pstmt.setString(9, "U");
            pstmt.setInt(10, 0);
            pstmt.setInt(11, 0);
            pstmt.setInt(12, -1);
            pstmt.setInt(13, -1);
            pstmt.setTimestamp(14, null);
            pstmt.setString(15, null);
            pstmt.setInt(16, 0);
            pstmt.setString(17, "U");
            pstmt.setInt(18, 0);
            pstmt.setString(19, sendport);
            System.out.println(sql);
            pstmt.executeUpdate();
        }

    }

    protected String DateFormat(Date time)
    {
        if(time == null)
        {
            return "";
        } else
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(time);
        }
    }

    public abstract Page waitForSendMsgList(int i)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException;

    public abstract void cancelWaitMsg()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException;

    public abstract void deleteCanceledMsg()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException;

    public abstract Page findMsgByCondition(Condition condition)
        throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException;
    
    public abstract byte[] getSwitchStatus()
    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException;
    
    public abstract Page getSNBList(Condition condition)
    	throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException;
}
