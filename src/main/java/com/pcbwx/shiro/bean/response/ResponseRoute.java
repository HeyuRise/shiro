package com.pcbwx.shiro.bean.response;

import java.util.List;

public class ResponseRoute {
	private String mailno;
	private List<Route> routes;
	public String getMailno() {
		return mailno;
	}
	public void setMailno(String mailno) {
		this.mailno = mailno;
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
}
