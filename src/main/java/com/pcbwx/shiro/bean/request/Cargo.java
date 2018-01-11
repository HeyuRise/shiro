package com.pcbwx.shiro.bean.request;

public class Cargo {
	 /**
     * 货物名称 String(60)
     * 中文的话不超过20字
     */
    public String name;
    /**
     * 货物数量
     */
    public String count;
    /**
     * 重量
     */
    public String weight;

    public Cargo(String name) {
        this.name = name;
    }
    public Cargo() {
		super();
	}
	public String toXml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Cargo");
        if (name != null) {
        	sb.append(" name='").append(name).append("' ");
		}
        if (weight != null && !weight.equals("")) {
            sb.append(" weight='").append(weight).append("' ");
        }
        sb.append(">");
        sb.append("</Cargo>");
        return sb.toString();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
}
