package com.pcbwx.shiro.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pcbwx.shiro.bean.OperateAddress;
import com.pcbwx.shiro.common.ConfigProperties;

public class SFUtil {
	
	// 匹配数字
	private static String reg = "([0-9])+";
	private static final Pattern pattern = Pattern.compile(reg);

	public static String getVerifyCode(String xml) {
		String data = xml + ConfigProperties.getCheckCode();
		byte[] md5 = md52(data);
		String ret = Base64.getEncoder().encodeToString(md5);
		return ret;
	}

	public static byte[] md52(String string) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(string.getBytes());
			byte[] m = md5.digest();// 加密
			return m;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static OperateAddress operateAddress(String address, Map<String, Map<String, Set<String>>> addressMap) {
		String province = null;
		String city = null;
		String county = null;
		String addressShort = null;
		String postCode = null;
		OperateAddress operateAddress = new OperateAddress();
		Integer postPla = address.lastIndexOf("邮编");
		if (postPla > 0) {
			String postCodeStr = address.substring(postPla);
			address = address.substring(0, postPla);
			if (address.endsWith("，")) {
				address = address.substring(0, address.length()-1);
			}
			Matcher postCodeMat = pattern.matcher(postCodeStr);
			if (postCodeMat.find()) {
				postCode = postCodeMat.group();
			}
			operateAddress.setPostCode(postCode);
		}
		boolean provinceOk = false;
		boolean cityOk = false;
		boolean countyOk = false;
		String head = address.substring(0, 2);
		if (head.equals("北京")) {
			province = "北京";
			city = "北京市";
			provinceOk = true;
			cityOk = true;
		} else if (head.equals("天津")) {
			province = "天津";
			city = "天津市";
			provinceOk = true;
			cityOk = true;
		} else if (head.equals("上海")) {
			province = "上海";
			city = "上海市";
			provinceOk = true;
			cityOk = true;
		} else if (head.equals("重庆")) {
			province = "重庆";
			city = "重庆市";
			provinceOk = true;
			cityOk = true;
		}
		if (provinceOk) {
			String headAfterCounty = null;
			if (address.contains(city)) {
				headAfterCounty = address.substring(city.length());
			} else {
				headAfterCounty = address.substring(city.length() - 1);
			}
			String headAfterCountyShort = headAfterCounty.substring(0, 2);
			Set<String> countySet = addressMap.get(province).get(city);
			for (String string : countySet) {
				if (string.contains(headAfterCountyShort)) {
					county = string;
					countyOk = true;
					if (headAfterCounty.contains(county)) {
						addressShort = headAfterCounty.substring(county.length());
					} else {
						addressShort = headAfterCounty.substring(county.length() - 1);
					}
					break;
				}
			}
			if (!countyOk) {
				addressShort = headAfterCounty;
			}
			operateAddress.setProvince(province);
			operateAddress.setCity(city);
			operateAddress.setCounty(county);
			operateAddress.setAddress(addressShort);
			return operateAddress;
		}

		for (String proStr : addressMap.keySet()) {
			if (proStr.contains(head)) {
				province = proStr;
				provinceOk = true;
				String headAfterCity = null;
				if (address.contains(proStr)) {
					headAfterCity = address.substring(proStr.length());
				} else {
					headAfterCity = address.substring(proStr.length() - 1);
				}
				String headafterCityShort = headAfterCity.substring(0, 2);
				Map<String, Set<String>> cityMap = addressMap.get(proStr);
				for (String cityStr : cityMap.keySet()) {
					if (cityStr.startsWith(headafterCityShort)) {
						city = cityStr;
						cityOk = true;
						String headAfterCounty = null;
						if (address.contains(city)) {
							headAfterCounty = headAfterCity.substring(city.length());
						} else {
							headAfterCounty = headAfterCity.substring(city.length() - 1);
						}
						String headAfterCountyShort = headAfterCounty.substring(0, 2);
						Set<String> countySet = addressMap.get(province).get(city);
						for (String string : countySet) {
							if (string.contains(headAfterCountyShort)) {
								county = string;
								countyOk = true;
								if (headAfterCounty.contains(county)) {
									addressShort = headAfterCounty.substring(county.length());
								} else {
									addressShort = headAfterCounty.substring(county.length() - 1);
								}
								break;
							}
						}
						if (!countyOk) {
							addressShort = headAfterCounty;
						}
						break;
					} else {
						Set<String> countySet = cityMap.get(cityStr);
						for (String string : countySet) {
							if (string.startsWith(headafterCityShort)) {
								city = cityStr;
								county = string;
								cityOk = true;
								countyOk = true;
								if (headAfterCity.contains(county)) {
									addressShort = headAfterCity.substring(county.length());
								} else {
									addressShort = headAfterCity.substring(county.length() - 1);
								}
								break;
							}
						}
						if (!countyOk) {
							addressShort = headAfterCity;
						}
					}
				}
				if (!cityOk) {
					addressShort = headAfterCity;
				}
				break;
			} else {
				Map<String, Set<String>> cityMap = addressMap.get(proStr);
				for (String cityStr : cityMap.keySet()) {
					if (cityStr.startsWith(head)) {
						province = proStr;
						city = cityStr;
						provinceOk = true;
						cityOk = true;
						String headAfterCounty = null;
						if (address.contains(city)) {
							headAfterCounty = address.substring(city.length());
						} else {
							headAfterCounty = address.substring(city.length() - 1);
						}
						String headAfterCountyShort = headAfterCounty.substring(0, 2);
						Set<String> countySet = addressMap.get(province).get(city);
						for (String string : countySet) {
							if (string.contains(headAfterCountyShort)) {
								county = string;
								countyOk = true;
								if (headAfterCounty.contains(county)) {
									addressShort = headAfterCounty.substring(county.length());
								} else {
									addressShort = headAfterCounty.substring(county.length() - 1);
								}
								break;
							}
						}
						if (!countyOk) {
							addressShort = headAfterCounty;
						}
						break;
					}
					if (!cityOk) {
						addressShort = address;
					}
				}
			}
		}
		operateAddress.setProvince(province);
		operateAddress.setCity(city);
		operateAddress.setCounty(county);
		operateAddress.setAddress(addressShort);
		return operateAddress;
	}
}
