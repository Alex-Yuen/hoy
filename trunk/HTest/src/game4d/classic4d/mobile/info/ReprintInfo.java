package game4d.classic4d.mobile.info;


public class ReprintInfo extends BaseInfo{
	//c_表示客户端传来数据
	private String date;//日期
	private String page;//页数
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	
}
