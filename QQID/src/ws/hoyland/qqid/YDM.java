package ws.hoyland.qqid;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface YDM extends Library {
	YDM INSTANCE = (YDM) Native.loadLibrary("lib\\yundamaAPI", YDM.class);

	public void YDM_SetAppInfo(int nAppId, String lpAppKey);

	public int YDM_Login(String lpUserName, String lpPassWord);

	public int YDM_DecodeByPath(String lpFilePath, int nCodeType,
			StringBuilder pCodeResult);

	public int YDM_UploadByPath(String lpFilePath, int nCodeType);

	public int YDM_EasyDecodeByPath(String lpUserName, String lpPassWord,
			int nAppId, String lpAppKey, String lpFilePath, int nCodeType,
			int nTimeOut, StringBuilder pCodeResult);

	public int YDM_DecodeByBytes(byte[] lpBuffer, int nNumberOfBytesToRead,
			int nCodeType, byte[] resultByte);//StringBuilder pCodeResult //byte[] resultByte //String rsb

	public int YDM_UploadByBytes(byte[] lpBuffer, int nNumberOfBytesToRead,
			int nCodeType);

	public int YDM_EasyDecodeByBytes(String lpUserName, String lpPassWord,
			int nAppId, String lpAppKey, byte[] lpBuffer,
			int nNumberOfBytesToRead, int nCodeType, int nTimeOut,
			StringBuilder pCodeResult);

	public int YDM_GetResult(int nCaptchaId, StringBuilder pCodeResult);

	public int YDM_Report(int nCaptchaId, boolean bCorrect);

	public int YDM_EasyReport(String lpUserName, String lpPassWord, int nAppId,
			String lpAppKey, int nCaptchaId, boolean bCorrect);

	public int YDM_GetBalance(String lpUserName, String lpPassWord);

	public int YDM_EasyGetBalance(String lpUserName, String lpPassWord,
			int nAppId, String lpAppKey);

	public int YDM_SetTimeOut(int nTimeOut);

	public int YDM_Reg(String lpUserName, String lpPassWord, String lpEmail,
			String lpMobile, String lpQQUin);

	public int YDM_EasyReg(int nAppId, String lpAppKey, String lpUserName,
			String lpPassWord, String lpEmail, String lpMobile, String lpQQUin);

	public int YDM_Pay(String lpUserName, String lpPassWord, String lpCard);

	public int YDM_EasyPay(String lpUserName, String lpPassWord, long nAppId,
			String lpAppKey, String lpCard);

	/**
	public int uu_reportError(int id);

	public int uu_setTimeOut(int nTimeOut);

	public void uu_setSoftInfoA(int softId, String softKey);

	public int uu_loginA(String UserName, String passWord);

	public int uu_getScoreA(String UserName, String passWord);

	public int uu_recognizeByCodeTypeAndBytesA(byte[] picContent, int piclen,
			int codeType, byte[] returnResult);

	public void uu_getResultA(int nCodeID, String pCodeResult);
	**/
}