package com.pcbwx.shiro.common;

import java.util.ArrayList;
import java.util.List;

import com.pcbwx.shiro.utils.JsonUtil;

public class Params {
	private List<ParamEntity> params;

	public List<ParamEntity> getParams() {
		return params;
	}

	public void setParams(List<ParamEntity> params) {
		this.params = params;
	}
	
	public static void main(String[] args) {
		List<ParamEntity> paramList = new ArrayList<ParamEntity>();
		
		ParamEntity pe = new ParamEntity();
		pe.setName("contact_no");
		pe.setValue("T10001");		
		paramList.add(pe);
		
		pe = new ParamEntity();
		pe.setName("open_id");
		pe.setValue("123456");			
		paramList.add(pe);
		
		Params params = new Params();
		params.setParams(paramList);
		
		String jsonStr = JsonUtil.obj2json(params);
		System.out.println(jsonStr);
		
		Params ps = (Params) JsonUtil.json2obj(jsonStr, Params.class);
		for (ParamEntity p : ps.getParams()) {
			System.out.println(p.getName() + "," + p.getValue());
		}
	}
}
