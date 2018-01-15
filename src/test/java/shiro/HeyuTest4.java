package shiro;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pcbwx.shiro.utils.BASE64MD5Util;
import com.pcbwx.shiro.utils.FileUtil;

public class HeyuTest4 {
	public static void main(String[] args) throws IOException {
		System.out.println(BASE64MD5Util.md5("123456"));
	}
	
	public static void operate(){
		StringBuffer sfSb = new StringBuffer();
		StringBuffer govSb = new StringBuffer();
		byte[] gov = FileUtil.readFile("d:/gov.txt");
		String govAddress = new String(gov);
		byte[] sf = FileUtil.readFile("d:/sf.txt");
		String sfAddress = new String(sf);
		List<Address> govAddresses = null;
		List<Address> sfAddresses = null;
		Gson g = new Gson();
		Type type = (Type) new TypeToken<List<Address>>() {}.getType();
		govAddresses = g.fromJson(govAddress, type);
		sfAddresses = g.fromJson(sfAddress, type);
		Map<String, Map<String, Set<String>>> govMap = new HashMap<String, Map<String, Set<String>>>();
		Map<String, Map<String, Set<String>>> sfMap = new HashMap<String, Map<String, Set<String>>>();
		govMap = getAddressMap(govAddresses);
		sfMap = getAddressMap(sfAddresses);
		for (Address address : sfAddresses) {
			String provice = address.getProvince();
			String city = address.getCity();
			String county = address.getCounty();
			Map<String, Set<String>> cityMap = govMap.get(provice);
			if (cityMap == null) {
				sfSb.append(provice + "\r\n");
				continue;
			}
			Set<String> countySet = cityMap.get(city);
			if (countySet == null) {
				sfSb.append(provice + "-------------" + city + "----------------" + county + "\r\n");
				continue;
			}
			boolean has = false;
			for (String string : countySet) {
				if (Objects.equals(string, county)) {
					has = true;
					break;
				}
			}
			if (has) {
				continue;
			}
			sfSb.append(provice + "-------------" + city + "----------------" + county + "\r\n");
		}
		for (Address address : govAddresses) {
			String provice = address.getProvince();
			String city = address.getCity();
			String county = address.getCounty();
			Map<String, Set<String>> cityMap = sfMap.get(provice);
			if (cityMap == null) {
				govSb.append(provice + "\r\n");
				continue;
			}
			Set<String> countySet = cityMap.get(city);
			if (countySet == null) {
				govSb.append(provice + "-------------" + city + "----------------" + county + "\r\n");
				continue;
			}
			boolean has = false;
			for (String string : countySet) {
				if (Objects.equals(string, county)) {
					has = true;
					break;
				}
			}
			if (has) {
				continue;
			}
			govSb.append(provice + "-------------" + city + "----------------" + county + "\r\n");
		}
		FileUtil.writeFile("d:/顺丰有而政府没有.txt", sfSb.toString().getBytes());
		FileUtil.writeFile("d:/政府有而顺丰没有.txt", govSb.toString().getBytes());
	}
	
	public static Map<String, Map<String, Set<String>>> getAddressMap(List<Address> records){
		Set<String> provinceSet = new HashSet<String>();
		Map<String, Map<String, Set<String>>> proCityMap = new HashMap<String, Map<String, Set<String>>>();
		for (Address address : records) {
			provinceSet.add(address.getProvince());
		}
		for (String province : provinceSet) {
			Map<String, Set<String>> cityCounty = new HashMap<String, Set<String>>();
			Set<String> citys = new HashSet<String>();
			for (Address address : records) {
				if (province.equals(address.getProvince())) {
					citys.add(address.getCity());
				}
			}
			for (String city : citys) {
				Set<String> countys = new HashSet<String>();
				for (Address address : records) {
					if (province.equals(address.getProvince()) && city.equals(address.getCity())) {
						countys.add(address.getCounty());
					}
				}
				cityCounty.put(city, countys);
			}
			proCityMap.put(province, cityCounty);
		}
		return proCityMap;
	}
}
