/*    */ package cn.sendsms.jdsmsserver.web;

import java.util.Date;

/*    */ 
/*    */ public class BackupRecord
/*    */ {
/*    */   private long id;
/*    */ 
			private boolean machine;
			private int state;
			private String fileName;
			private long fileSize;
			private Date backupTime;
			private String memo;

/*    */   public long getId()
/*    */   {
/* 13 */     return this.id;
/*    */   }
/*    */   public void setId(long id) {
/* 16 */     this.id = id;
/*    */   }
public boolean isMachine() {
	return machine;
}
public void setMachine(boolean machine) {
	this.machine = machine;
}
public int getState() {
	return state;
}
public void setState(int state) {
	this.state = state;
}
public long getFileSize() {
	return fileSize;
}
public void setFileSize(long fileSize) {
	this.fileSize = fileSize;
}
public Date getBackupTime() {
	return backupTime;
}
public void setBackupTime(Date backupTime) {
	this.backupTime = backupTime;
}
public String getMemo() {
	return memo;
}
public void setMemo(String memo) {
	this.memo = memo;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}


/*    */ }

/* Location:           C:\Users\Administrator\Desktop\jdsmsserver-3.6.1.jar
 * Qualified Name:     cn.sendsms.jdsmsserver.web.OutMessage
 * JD-Core Version:    0.6.1
 */