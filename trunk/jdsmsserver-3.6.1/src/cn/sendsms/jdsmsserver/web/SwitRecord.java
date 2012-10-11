/*    */ package cn.sendsms.jdsmsserver.web;

import java.util.Date;

/*    */ 
/*    */ public class SwitRecord
/*    */ {
/*    */   private long id;
/*    */   private byte master;
			private byte slaver;
			private String mip;
			private String sip;
			private String memo;
			private Date switTime;
			
/*    */   public long getId()
/*    */   {
/* 13 */     return this.id;
/*    */   }
/*    */   public void setId(long id) {
/* 16 */     this.id = id;
/*    */   }

public void setMaster(byte master) {
	this.master = master;
}
public void setSlaver(byte slaver) {
	this.slaver = slaver;
}
public String getMemo() {
	return memo;
}
public void setMemo(String memo) {
	this.memo = memo;
}
public Date getSwitTime() {
	return switTime;
}
public void setSwitTime(Date switTime) {
	this.switTime = switTime;
}
public String getMip() {
	return mip;
}
public void setMip(String mip) {
	this.mip = mip;
}
public String getSip() {
	return sip;
}
public void setSip(String sip) {
	this.sip = sip;
}
public byte getMaster() {
	return master;
}
public byte getSlaver() {
	return slaver;
}

/*    */ }

/* Location:           C:\Users\Administrator\Desktop\jdsmsserver-3.6.1.jar
 * Qualified Name:     cn.sendsms.jdsmsserver.web.OutMessage
 * JD-Core Version:    0.6.1
 */