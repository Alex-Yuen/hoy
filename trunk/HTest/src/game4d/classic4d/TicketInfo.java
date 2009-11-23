package game4d.classic4d;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import util.Utils;

public class TicketInfo {

	private int pageId;

	// 页日期 20090901, 投注时由AppServer填写
	// private String pageDate;

	// 会员Id, 投注时由AppServer填写
	// private String memberId;

	// 页号, 投注时由AppServer填写
	// private int pageNo;

	// ticketInfoId, 投注时由AppServer填写
	private int ticketInfoId;

	// 开彩公司
	private Vector drawCompanies;

	// 投注彩期数
	private int drawCount;

	// 投注号码
	private String number;

	// 玩法投注额
	// B: Big
	// S: Small
	// 4A: 4A
	// A: A
	// ABC: ABC
	// 2A: 2A
	// 2C: 2C
	private Hashtable betting;

	public TicketInfo(int pageId, int ticketInfoId, Vector drawCompanies,
			int drawCounts, String number, Hashtable betting) {
		super();
		this.pageId = pageId;
		this.ticketInfoId = ticketInfoId;
		this.drawCompanies = drawCompanies;
		this.drawCount = drawCounts;
		this.number = number;
		this.betting = betting;
	}

	public TicketInfo() {
		super();
	}

	public Vector getDrawCompanies() {
		return drawCompanies;
	}

	public String getDrawCompaniesStr() {
		return Utils.join(drawCompanies, ",");
	}

	public void setDrawCompanies(Vector drawCompanies) {
		this.drawCompanies = drawCompanies;
	}

	public int getDrawCount() {
		return drawCount;
	}

	public void setDrawCount(int drawCounts) {
		this.drawCount = drawCounts;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Hashtable getBetting() {
		return betting;
	}

	public String getBettingStr() {
		Vector list = new Vector();
		Enumeration en = betting.elements();
		while(en.hasMoreElements()){
			String key = (String)en.nextElement();
			list.addElement(key + "/" + betting.get(key));
		}
		
		return Utils.join(list, ",");
	}

	public void setBetting(Hashtable betting) {
		this.betting = betting;
	}

	public int getTicketInfoId() {
		return ticketInfoId;
	}

	public void setTicketInfoId(int ticketInfoId) {
		this.ticketInfoId = ticketInfoId;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

}
