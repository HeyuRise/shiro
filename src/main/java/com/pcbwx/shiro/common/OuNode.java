package com.pcbwx.shiro.common;

import java.util.ArrayList;
import java.util.List;

/**
 * ldap联系人结构节点
 * 
 * @author 王海龙
 *
 */
public class OuNode {
	
	private Integer id;
	private String nodeName;
	private List<String> childs = new ArrayList<String>();
	
	
	public OuNode() {
		
	}
	
	public OuNode(int id, String nodeName, List<String> childs) {
		super();
		this.id = id;
		this.nodeName = nodeName;
		this.childs = childs;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public List<String> getChilds() {
		return childs;
	}
	public void setChilds(List<String> childs) {
		this.childs = childs;
	}
	
	@Override
	public String toString() {
		return "OuNode [id=" + id + ", nodeName=" + nodeName + ", childs="
				+ childs + "]";
	}
	
	
	
}
