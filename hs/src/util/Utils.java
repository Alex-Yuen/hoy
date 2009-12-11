package util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import msg.Message;
import util.numgen.NumGenerator;
import wox.serial.Easy;

public class Utils {

	public static final String NULL_ID = "0";

	public static final String ALL_ID = "*";

	private static NumGenerator numGenerator;

	// 时间日期
	public static java.util.Date sqlTimestamp2Date(java.sql.Timestamp timestamp) {
		if (timestamp == null)
			return null;
		return new java.util.Date(timestamp.getTime());
	}

	public static java.util.Date sqlTime2Date(java.sql.Time time) {
		if (time == null)
			return null;
		return new java.util.Date(time.getTime());
	}

	public static java.util.Date sqlDate2Date(java.sql.Date date) {
		if (date == null)
			return null;
		return new java.util.Date(date.getTime());
	}

	public static String padString(String str, int width, char padChar) {
		if (str.length() < width) {
			char[] paddingArray = new char[width - str.length()];
			Arrays.fill(paddingArray, padChar);
			String padding = String.valueOf(paddingArray);
			return padding + str;
		} else {
			return str;
		}
	}

	public static String date2DateString(Date dateTime) {
		if (dateTime == null)
			return "";
		return new SimpleDateFormat("yyyy-MM-dd").format(dateTime);
	}

	public static Date dateString2Date(String str) {
		if (str == null)
			return null;
		return new SimpleDateFormat("yyyy-MM-dd").parse(str, new ParsePosition(
				0));
	}

	/*
	 * format Date to string: yyyymmddhhnnss
	 */
	public static String date2String(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		NumberFormat formatter = NumberFormat.getIntegerInstance();
		formatter.setGroupingUsed(false);
		String str;
		formatter.setMinimumIntegerDigits(4);
		str = formatter.format(calendar.get(Calendar.YEAR));
		formatter.setMinimumIntegerDigits(2);
		str += formatter.format(calendar.get(Calendar.MONTH) + 1)
				+ formatter.format(calendar.get(Calendar.DAY_OF_MONTH))
				+ formatter.format(calendar.get(Calendar.HOUR_OF_DAY))
				+ formatter.format(calendar.get(Calendar.MINUTE))
				+ formatter.format(calendar.get(Calendar.SECOND));

		return str;
	}

	/*
	 * convert string to Date: yyyymmddhhnnss
	 */
	public static Date string2Date(String str) {
		Calendar calendar = new GregorianCalendar(Integer.parseInt(str
				.substring(0, 4)), Integer.parseInt(str.substring(4, 6)) - 1,
				Integer.parseInt(str.substring(6, 8)), Integer.parseInt(str
						.substring(8, 10)), Integer.parseInt(str.substring(10,
						12)), Integer.parseInt(str.substring(12, 14)));
		return new Date(calendar.getTimeInMillis());
	}

	/**
	 * format yyyy/MM/dd HH:mm:ss
	 * 
	 * @param str
	 * @return null if parse error
	 */
	public static Date dateTimeString2Date(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		return format.parse(str, pos);
	}

	public static String date2DateTimeString(Date dateTime) {
		if (dateTime == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(dateTime);
	}

	public static Date webDateTimeString2Date(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		return format.parse(str, pos);
	}

	public static String date2WebDateTimeString(Date dateTime) {
		if (dateTime == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(dateTime);
	}

	public static boolean isValidWebDateTime(String str) {
		return null != webDateTimeString2Date(str);
	}

	// object to xml
	public static String objToXml(Object obj) {
		return Easy.toStr(obj);
	}

	// xml to object
	public static Object xmlToObj(String xml) {
		if (isNullString(xml))
			return null;
		return Easy.fromStr(xml);

	}
	
	// object to xml
	public static String objToJson(Object obj) {
		return Easy.toStr(obj);
	}

	// xml to object
	public static Object jsonToObj(String xml) {
		if (isNullString(xml))
			return null;
		return Easy.fromStr(xml);

	}

	// generate UUID
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}

	// string related
	public static String unnull(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	public static boolean isNullString(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static String bracket(String str) {
		return "(" + str + ")";
	}

	public static String quote(String str) {
		return "'" + str + "'";
	}

	public static String doublQuote(String str) {
		return "\"" + str + "\"";
	}

	public static boolean isNumber(String str) {
		String regex = "[+-]?\\d*[.]?[\\d]+";
		return str.matches(regex);
	}

	public static String join(List<String> list, String splitter) {
		if (list == null)
			return "";
		String str = "";
		String sp = "";
		for (String s : list) {
			str += sp + s;
			sp = splitter;
		}
		return str;
	}

	public static String sortString(String str) {
		char[] chars = str.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}

	// generate sequence number
	public static int genNumber(String... name) {
		return numGenerator.get(name);
	}

	public static String genNumberString(String... name) {
		return numGenerator.getString(name);
	}

	public static void setNumGenerator(NumGenerator numGenerator) {
		Utils.numGenerator = numGenerator;
	}

	// compare BigDecimal
	public static boolean gt(BigDecimal left, BigDecimal right) {
		if (left == null || right == null)
			return false;
		return left.compareTo(right) > 0;
	}

	public static boolean lt(BigDecimal left, BigDecimal right) {
		if (left == null || right == null)
			return false;
		return left.compareTo(right) < 0;
	}

	public static boolean ge(BigDecimal left, BigDecimal right) {
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		return left.compareTo(right) >= 0;
	}

	public static boolean le(BigDecimal left, BigDecimal right) {
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		return left.compareTo(right) <= 0;
	}

	public static boolean eq(BigDecimal left, BigDecimal right) {
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		return left.compareTo(right) == 0;
	}

	public static boolean eqAmount(BigDecimal left, BigDecimal right) {
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		return left.subtract(right).abs().compareTo(new BigDecimal("0.01")) <= 0;
	}

	public static String bigDecimal2String(BigDecimal value) {
		if (value == null)
			return "";
		return value.toPlainString();
	}

	public static BigDecimal string2BigDecimal(String str) {
		if (isNullString(str))
			return null;
		return new BigDecimal(str);
	}

	// encrypt password
	public static String encryptPassword(String password) {
		return hashEncrypt(password, "SHA");
	}

	public static String hashEncrypt(String string, String encryptType) {
		try {
			MessageDigest md = MessageDigest.getInstance(encryptType);
			byte[] result = md.digest(string.getBytes());
			return bytes2HexString(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SysException("encrypt fail " + e.getMessage());
		}

	}

	public static byte[] hexString2Bytes(String str) {
		if (str == null || str.length() == 0)
			return new byte[0];

		if (str.length() % 2 != 0)
			throw new IllegalArgumentException(
					"string length should be times of 2");

		byte[] bytes = new byte[str.length() / 2];
		int i = 0;
		while (i < bytes.length) {
			Short s = Short.parseShort(str.substring(i * 2, (i + 1) * 2), 16);
			bytes[i] = s.byteValue();
			i++;
		}

		return bytes;
	}

	public static String bytes2HexString(byte in[]) {

		byte ch = 0x00;

		int i = 0;

		if (in == null || in.length <= 0)

			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F" };

		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {

			// Strip off high nibble
			ch = (byte) (in[i] & 0xF0);

			// shift the bits down
			ch = (byte) (ch >>> 4);

			// must do this is high order bit is on!
			ch = (byte) (ch & 0x0F);

			// convert the nibble to a String
			out.append(pseudo[(int) ch]);

			// Strip off low nibble
			ch = (byte) (in[i] & 0x0F);

			// convert the nibble to a String
			out.append(pseudo[(int) ch]);

			i++;

		}

		String rslt = new String(out);

		return rslt;
	}

	// 计算全保、全打的“反”数
	public static int fan(String betContent) {
		int[] a = new int[10];

		for (int i = 0; i < 4; ++i) {
			int d = Integer.valueOf(betContent.charAt(i));
			a[d]++;
		}

		int count = 0;
		for (int i = 0; i < a.length; ++i) {
			if (a[i] != 0)
				count++;
		}

		//  4个号码各不相同（1234）：24种组合，即24反
		if (count == 4)
			return 24;

		//  仅有两个号码相同（1134）：12反
		if (count == 3)
			return 12;

		if (count == 2) {
			for (int i = 0; i < a.length; ++i) {
				//  两组两个号码形同（1144）：6反
				if (a[i] == 2)
					return 6;
				//  三个号码相同（1114）：4反
				if (a[i] == 1 || a[i] == 3)
					return 4;
			}
		}

		//  四个号码都相同（1111）：1反
		return 1;
	}

	/**
	 * 转换integer 到 String
	 * @param i
	 * @return
	 */
    public static String Integer2String(Integer i){
    	if(i==null){
    		return "";
    	}else{
    		return i.toString();
    	}
    }
	
    /**
	 * 转换String 到 integer
	 * @param i
	 * @return
	 */
    public static Integer String2Integer(String i){
    	if(i==null||i.length()==0||i.equals("null")){
    		return 0;
    	}else{
    		return Integer.parseInt(i);
    	}
    }
	
    /**
	 * 转换Long 到 String
	 * @param i
	 * @return
	 */
    public static String Long2String(Long i){
    	if(i==null){
    		return "";
    	}else{
    		return i.toString();
    	}
    }
	
    /**
	 * 转换String 到 Long
	 * @param i
	 * @return
	 */
    public static Long String2Long(String i){
    	if(i==null||i.length()==0||i=="null"){
    		return 0l;
    	}else{
    		return Long.parseLong(i);
    	}
    }
	
	
    /**
	 * 转换integer 到 String
	 * @param i
	 * @return
	 */
    public static String Short2String(Short i){
    	if(i==null){
    		return "";
    	}else{
    		return i.toString();
    	}
    }
	
    /**
	 * 转换String 到 integer
	 * @param i
	 * @return
	 */
    public static Short String2Short(String i){
    	if(i==null||i.length()==0||i.equals("null")){
    		return 0;
    	}else{
    		return Short.parseShort(i);
    	}
    }
	
   
    /**
	 * 转换object 到 String
	 * @param i
	 * @return
	 */
    public static String toString(Object i){
    	if(i==null){
    		return "";
    	}else{
    		return i.toString();
    	}
    }
    
    /**
	 * 转换object 到 String
	 * @param i
	 * @return
	 */
    public static Timestamp String2Timestamp(String i){
    	if(i==null||i.length()==0||i.equals("null")){
    		return null;
    	}else{
    		return Timestamp.valueOf(i);
    	}
    }

    /**
	 *  检查字符串不能为“”或NULL
	 *  
	 * @param str
	 */
	public static boolean checkStrNotNull(String str){
		if (str == null) {
			// throw new RetObjException(Message.INVALID_DATA);
			return false;
		} 
		
		str = str.trim();
		if (str.equals("")) {
			return false;
		}
		return true;
	}

    
}
