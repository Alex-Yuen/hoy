package ws.hoyland.sm.service;

public class ProxyService implements ProxyServiceMBean {
	private String proxies;

	public String getProxies() {
		// TODO Auto-generated method stub
		this.proxies = "127.0.0.1:8080\r\n"; 
		return this.proxies;
	}

	public void setProxies(String proxies) {
		this.proxies = proxies;
		
	}

}
