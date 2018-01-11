package com.pcbwx.shiro.enums;

public enum TaskClockEnum {
	// ====cad====
	MAILNO_SYNCHRONIZE("mailno_synchronize", "同步运单号"),
	DEFAULT("default", "");
		
	private String code;
	private String descr;
		
	private TaskClockEnum(String code, String descr) {
		this.code = code;
		this.descr = descr;
	}
		
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
}
