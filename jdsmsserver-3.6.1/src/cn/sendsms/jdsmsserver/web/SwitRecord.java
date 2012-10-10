/*    */ package cn.sendsms.jdsmsserver.web;

import java.util.Date;

/*    */ 
/*    */ public class SwitRecord
/*    */ {
/*    */   private long id;
/*    */   private boolean master;
			private boolean slaver;
			
			private String memo;
			private Date switTime;
			
/*    */   public long getId()
/*    */   {
/* 13 */     return this.id;
/*    */   }
/*    */   public void setId(long id) {
/* 16 */     this.id = id;
/*    */   }

public boolean isMaster() {
	return master;
}
public void setMaster(boolean master) {
	this.master = master;
}
public boolean isSlaver() {
	return slaver;
}
public void setSlaver(boolean slaver) {
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

/*    */ }

/* Location:           C:\Users\Administrator\Desktop\jdsmsserver-3.6.1.jar
 * Qualified Name:     cn.sendsms.jdsmsserver.web.OutMessage
 * JD-Core Version:    0.6.1
 */