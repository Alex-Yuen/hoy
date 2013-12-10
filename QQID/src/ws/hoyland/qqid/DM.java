package ws.hoyland.qqid;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface DM extends Library {
	DM INSTANCE = (DM) Native.loadLibrary("lib\\UUWiseHelper", DM.class);

	public int uu_reportError(int id);

	public int uu_setTimeOut(int nTimeOut);

	public void uu_setSoftInfoA(int softId, String softKey);

	public int uu_loginA(String UserName, String passWord);

	public int uu_getScoreA(String UserName, String passWord);

	public int uu_recognizeByCodeTypeAndBytesA(byte[] picContent, int piclen,
			int codeType, byte[] returnResult);

	public void uu_getResultA(int nCodeID, String pCodeResult);
}