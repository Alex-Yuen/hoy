package ws.hoyland.sm.service;

public interface ProxyServiceMBean {
	public String getProxies();
	public void setProxies(String proxies);
	public void register(int port);
	public void unRegister(int port);
	public void notifyReload();
	public void doReload();
}
