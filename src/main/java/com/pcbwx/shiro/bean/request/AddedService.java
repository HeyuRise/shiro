package com.pcbwx.shiro.bean.request;

import java.util.List;

public class AddedService {
	public String name;
	public List<String> value;
	public List<String> appearValue;
	public String toXml(){
		if (name == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append("<AddedService");
        sb.append(" name='").append(name).append("' ");
        if (value != null && !value.isEmpty()) {
        	Integer count = 0;
        	for (String string : value) {
				if (count == 0) {
					sb.append(" value='").append(string).append("' ");
				}else {
					sb.append(" value").append(count).append("='").append(string).append("' ");
				}
        		count++;
			}
		}
        sb.append(">");
        sb.append("</AddedService>");
        return sb.toString();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getValue() {
		return value;
	}
	public void setValue(List<String> value) {
		this.value = value;
	}
	public List<String> getAppearValue() {
		return appearValue;
	}
	public void setAppearValue(List<String> appearValue) {
		this.appearValue = appearValue;
	}
	public String toValue(){
		if (value == null || value.isEmpty()) {
			return null;
		}
		String valueStr = null;
		for (String string : value) {
			if (valueStr == null) {
				valueStr = string;
			}else {
				valueStr = valueStr + "," + string;
			}
		}
		return valueStr;
	}
	public String toAppearValue(){
		if (appearValue == null || value.isEmpty()) {
			return null;
		}
		String valueStr = null;
		for (String string : appearValue) {
			if (valueStr == null) {
				valueStr = string;
			}else {
				valueStr = valueStr + "," + string;
			}
		}
		return valueStr;
	}
}
