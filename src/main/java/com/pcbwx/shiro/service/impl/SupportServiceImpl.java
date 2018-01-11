package com.pcbwx.shiro.service.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.pcbwx.shiro.bean.response.AddressResponse;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.dao.AddressMapper;
import com.pcbwx.shiro.dao.ConfigMapper;
import com.pcbwx.shiro.dao.DictionaryMapper;
import com.pcbwx.shiro.dao.ExpressProductMapper;
import com.pcbwx.shiro.dao.MenuMapper;
import com.pcbwx.shiro.dao.RoleAuthMapper;
import com.pcbwx.shiro.dao.ServiceMapper;
import com.pcbwx.shiro.dao.UserAuthMapper;
import com.pcbwx.shiro.dao.UserRoleMapper;
import com.pcbwx.shiro.dao.UserRoleRelationMapper;
import com.pcbwx.shiro.enums.ClickEnum;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.model.Address;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.ExpressProduct;
import com.pcbwx.shiro.model.Menu;
import com.pcbwx.shiro.model.RecordUtils;
import com.pcbwx.shiro.model.RoleAuth;
import com.pcbwx.shiro.model.UserAuth;
import com.pcbwx.shiro.model.UserRole;
import com.pcbwx.shiro.model.UserRoleRelation;
import com.pcbwx.shiro.service.ConfigService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.utils.DataUtil;
import com.pcbwx.shiro.utils.DateTimeUtil;
import com.pcbwx.shiro.utils.HttpUtil;

/**
 * 日志接口实现类
 * 
 * @author 王海龙
 *
 */
@Service("supportService")
public class SupportServiceImpl implements SupportService {

	private static Logger logger = Logger.getLogger(SupportServiceImpl.class);

	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private DictionaryMapper dictionaryMapper;
	
//	@Autowired
//	private LogMapper logMapper; // 日志DAO接口

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private UserRoleRelationMapper userRoleRelationMapper;
	@Autowired
	private RoleAuthMapper roleAuthMapper;
	@Autowired
	private UserAuthMapper userAuthMapper;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private ExpressProductMapper expressProductMapper;
	@Autowired
	private ServiceMapper serviceMapper;
	
	@PostConstruct
	public void reloadCacheInfo() {
		logger.info("启动载入缓存...");
		
		configService.setUtilRecord(ConfigEnum.LAST_RELOAD_TIME, new Date(), "");
		reloadDictionary();
		reloadUserRole();
		reloadUserRoleRelation();
		reloadUserAuth();
		reloadRoleAuth();
		reloadAddress();
		reloadMenu();
		reloadProduct();
		reloadService();
	}

	public void reloadDictionary() {
		List<Dictionary> dictionarys = dictionaryMapper.load();
		cacheService.reloadDictionary(dictionarys);
	}

	private void reloadUserRole(){
		List<UserRole> records = userRoleMapper.load();
		logger.info(records.size() + "");
		cacheService.reloadUserRole(records);
	}
	private void reloadUserRoleRelation(){
		List<UserRoleRelation> records = userRoleRelationMapper.load();
		cacheService.reloadUserRoleRelation(records);
	}

	private void reloadUserAuth(){
		List<UserAuth> userAuths = userAuthMapper.load();
		cacheService.reloadUserAuth(userAuths);
	}

	private void reloadRoleAuth(){
		List<RoleAuth> roleAuths = roleAuthMapper.load();
		cacheService.reloadRoleAuth(roleAuths);
	}
	
	private void reloadAddress(){
		List<Address> addresses = addressMapper.selectByCondition(null, null, null);
		cacheService.reloadAddress(addresses);
	}
	
	private void reloadMenu(){
		List<Menu> menus = menuMapper.load();
		cacheService.reloadMenu(menus);
	}
	
	private void reloadProduct(){
		List<ExpressProduct> products = expressProductMapper.selectByProductName(null);
		cacheService.reloadExpressProduct(products);
	}
	
	private void reloadService(){
		List<com.pcbwx.shiro.model.Service> services = serviceMapper.selectByCondition(null);
		cacheService.reloadService(services);
	}
	
	@Override
	public synchronized boolean doReloadCache() {
		logger.info("载入...");
		Date lastDate = configService.getUtilDate(ConfigEnum.LAST_RELOAD_TIME);
		Date now = new Date();
		
		Date lastRecordTime = null;
		boolean haveReload = false;
		lastRecordTime = dictionaryMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadDictionary();
			haveReload = true;
		}
		
		lastRecordTime = userRoleMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadUserRole();
			haveReload = true;
		}
		
		lastRecordTime = userRoleRelationMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadUserRoleRelation();
			haveReload = true;
		}
		
		lastRecordTime = roleAuthMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadRoleAuth();
			haveReload = true;
		}
		
		lastRecordTime = userAuthMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadUserAuth();
			haveReload = true;
		}
		
		lastRecordTime = menuMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadMenu();
			haveReload = true;
		}
		
		lastRecordTime = addressMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadAddress();
			haveReload = true;
		}
		
		lastRecordTime = expressProductMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadProduct();
			haveReload = true;
		}
		
		lastRecordTime = serviceMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			reloadService();
			haveReload = true;
		}
		
		//--------------config----------------
		lastRecordTime = configMapper.selectLastRecordTime();
		if (lastDate == null || (lastRecordTime != null && lastDate.before(lastRecordTime))) {
			configService.reloadConfig();
			haveReload = true;
		}
		//-------------记录时间
		if (haveReload) {
			configService.setUtilRecord(ConfigEnum.LAST_RELOAD_TIME, now, "");			
		}
		
		return true;
	}

	public static String getHostIp() {
		String SERVER_IP = null;
		/*int i = 0;*/
		try {
			@SuppressWarnings("rawtypes")
			Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			loop:while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
//				System.out.println("进入外层while....");
				while (inetAddresses.hasMoreElements()) {
					ip = inetAddresses.nextElement();
					/*i++;
					System.out.println("第" + i + "个ip:" + ip);
					System.out.println("第" + i + "个SERVER_IP:" + ip.getHostAddress());
					System.out.println("第" + i + "个ip.isSiteLocalAddress():" + ip.isSiteLocalAddress());
					System.out.println("第" + i + "个ip.isLoopbackAddress():" + ip.isLoopbackAddress());*/
					if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {
						SERVER_IP = ip.getHostAddress();
						break loop;
					} else {
						ip = null;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return SERVER_IP;
	}
	
	/**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     * @param request
     * @return ip
     */
   public static String getLocalIp(HttpServletRequest request) {
       String remoteAddr = request.getRemoteAddr();
       String forwarded = request.getHeader("X-Forwarded-For");
       String realIp = request.getHeader("X-Real-IP");

       String ip = null;
       if (realIp == null) {
           if (forwarded == null) {
               ip = remoteAddr;
           } else {
               ip = remoteAddr + "/" + forwarded.split(",")[0];
           }
       } else {
           if (realIp.equals(forwarded)) {
               ip = realIp;
           } else {
               if(forwarded != null){
                   forwarded = forwarded.split(",")[0];
               }
               ip = realIp + "/" + forwarded;
           }
       }
       return ip;
   }

	@Override
	public String getQueryRouteXML(Integer type, String mailno) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Request service='RouteService' lang='zh-CN'>");
		sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
		sb.append("<Body>");
		sb.append("<RouteRequest tracking_type='").append(type)
				.append("' tracking_number='").append(mailno).append("'/>");
		sb.append("</Body>");
		sb.append("</Request>");
		return sb.toString();
	}

	@Override
	public synchronized String getOrderId() {
		Date now = new Date();
		RecordUtils recordUtils = configService.getUtilRecord(ConfigEnum.ORDER_ID);
		String dateTime = DateTimeUtil.date2dateTimeStr(now, "yyMMdd");
		Integer a = 1;
		String orderId = null;
		String orderIdHead = ConfigProperties.getOrderIdHead();
		if (orderIdHead == null) {
			orderIdHead = "dev";
		}
		if (recordUtils == null) {
			orderId = orderIdHead + dateTime + "00" + a; 
			recordUtils = new RecordUtils();
		}else {
			Date theDateLastTime = recordUtils.getValueTime();
			if (now.before(theDateLastTime)) {
				a = recordUtils.getValueInt();
				a = a + 1;
				if (a < 100) {
					if (a < 10) {
						orderId = orderIdHead + dateTime + "00" + a; 
					}else {
						orderId = orderIdHead + dateTime + "0" + a; 
					}
				}else {
					orderId = orderIdHead + dateTime + a; 
				}
			}else{
				orderId = orderIdHead + dateTime + "00" + a; 
			}
		}
		Date todayLateTime = DateTimeUtil.getTheDayLastTime(now);
		configService.setUtilRecord(ConfigEnum.ORDER_ID, orderId, a, todayLateTime, null);
		return orderId;
	}

	@Override
	public String getOkXML(String service) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Response service=\"" + service + "\"" + ">");
		sb.append("<Head>OK</Head>");
		sb.append("</Response>");
		return sb.toString();
	}

	@Override
	public String getErrorXML(String service) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Response service=\"" + service + "\"" + ">");
		sb.append("<Head>ERR</Head>");
		sb.append("<ERROR code=\"4001\">系统发生数据错误或运行时异常</ERROR>");
		sb.append("</Response>");
		return sb.toString();
	}

	@Override
	public void reloadInternetAddress() {
		Date now = new Date();
		List<Address> records = addressMapper.selectByCondition(null, null, null);
		Map<String, Map<String, Set<Address>>> proCityMap = reloadAddressDetail(records);
		for (String province : proCityMap.keySet()) {
			String[] provinces = province.split("\\(");
			String shortProvince = provinces[0];
			shortProvince = convertStr(shortProvince);
			String proResponse = null;
			try {
				proResponse = HttpUtil.postBody(ConfigProperties.getAddressUrl(), "application/x-www-form-urlencoded;charset=UTF-8", "shengji=" + province);
			} catch (IOException e) {
				LogContext.error("行政区请求失败", "请求" + province + "的市列表失败");
				continue;
			}
			List<AddressResponse> citys = new ArrayList<AddressResponse>();
			@SuppressWarnings("serial")
			Type typeCity = new TypeToken<List<AddressResponse>>(){}.getType();
			Gson gsonCity = new Gson();
			citys = gsonCity.fromJson(proResponse, typeCity);
			if (citys == null || citys.isEmpty()) {
				continue;
			}
			Map<String, Set<Address>> cityMap = proCityMap.get(province);
			if (cityMap == null || cityMap.isEmpty()) {
				continue;
			}
			Map<String, String> cityResMap = new HashMap<String, String>(); 
			for (AddressResponse addressResponse : citys) {
				String resDiji = addressResponse.getDiji();
				// 去除掉行政区名字中的☆
				if (resDiji.contains("☆")) {
					resDiji = resDiji.substring(0, resDiji.length()-1);
				}
				cityResMap.put(resDiji, resDiji);
			}
			/*
			 * 检查本地数据库是否有已经被撤销的市行政区，
			 * 如果有的话，就把该行政区下的所有县区设为不可用
			 */
			for (String cityCache : cityMap.keySet()) {
				String city = cityResMap.get(cityCache);
				if (city != null) {
					continue;
				}
				Set<Address> citySet = cityMap.get(cityCache);
				if (citySet == null || citySet.isEmpty()) {
					continue;
				}
				LogContext.warn("市级行政区变动", cityCache + "撤销");
				for (Address address : citySet) {
					address.setEnable(0);
					addressMapper.updateByPrimaryKeySelective(address);
				}
			}
			for (AddressResponse city : citys) {
				String cityTrue = city.getDiji();
				// 去除掉行政区名字中的☆
				if (cityTrue.contains("☆")) {
					cityTrue = cityTrue.substring(0, cityTrue.length()-1);
				}
				Set<Address> countySet = proCityMap.get(province).get(cityTrue);
				String cityResponse = null;
				try {
					cityResponse = HttpUtil.postBody(ConfigProperties.getAddressUrl(), "application/x-www-form-urlencoded;charset=UTF-8", "shengji=" + province + "&diji=" + city.getDiji());
				} catch (IOException e) {
					LogContext.error("行政区请求失败", "请求" + province + cityTrue + "的县区列表失败");
					continue;
				}
				List<AddressResponse> countys = new ArrayList<AddressResponse>();
				@SuppressWarnings("serial")
				Type typeCounty = new TypeToken<List<AddressResponse>>(){}.getType();
				Gson gsonCounty = new Gson();
				countys = gsonCounty.fromJson(cityResponse, typeCounty);
				/*
				 * 如果请求中的该市下没有县区,并且本体数据库
				 */
				if (countys == null || countys.isEmpty()) {
					if (countySet == null || countySet.isEmpty()) {
						Address addressNew = new Address();
						addressNew.setCity(cityTrue);
						addressNew.setCounty(cityTrue);
						addressNew.setCreatTime(now);
						addressNew.setEnable(1);
						addressNew.setProvince(shortProvince);
						addressNew.setProvinceFullName(province);
						addressNew.setUpdateTime(now);
						addressMapper.insertSelective(addressNew);
					}else if (countySet.size() > 1) {
						for (Address address : countySet) {
							address.setEnable(0);
							addressMapper.updateByPrimaryKeySelective(address);
						}
						Address addressNew = new Address();
						addressNew.setCity(cityTrue);
						addressNew.setCounty(cityTrue);
						addressNew.setCreatTime(now);
						addressNew.setEnable(1);
						addressNew.setProvince(shortProvince);
						addressNew.setProvinceFullName(province);
						addressNew.setUpdateTime(now);
						addressMapper.insertSelective(addressNew);
					}
					continue;
				}
				Map<String, String> countyMap = new HashMap<String, String>(); 
				for (AddressResponse addressResponse : countys) {
					String xianji = addressResponse.getXianji();
					// 去除掉行政区名字中的☆
					if (xianji.contains("☆")) {
						xianji = xianji.substring(0, xianji.length()-1);
					}
					countyMap.put(xianji, xianji);
				}
				/*
				 * 检查是否有新的市行政区，如果有的话，
				 * 把该行政区下的所有县区都加入数据库
				 */
				if (countySet == null || countySet.isEmpty()) {
					LogContext.warn("市级行政区变动", "新增" + cityTrue);
					for (String county : countyMap.keySet()) {
						Address addressNew = new Address();
						addressNew.setCity(cityTrue);
						addressNew.setCounty(county);
						addressNew.setCreatTime(now);
						addressNew.setEnable(1);
						addressNew.setProvince(shortProvince);
						addressNew.setProvinceFullName(province);
						addressNew.setUpdateTime(now);
						addressMapper.insertSelective(addressNew);
					}
					continue;
				}
				Map<String, Address> addMap = null;
				try {
					addMap = DataUtil.set2map(countySet, Address.class, "county");
				} catch (NoSuchMethodException | SecurityException e) {
					continue;
				}
				/*
				 * 查看是否有撤销的县区
				 */
				for (String county : addMap.keySet()) {
					String equ = countyMap.get(county);
					if (equ == null) {
						Address unableAddress = addMap.get(county);
						unableAddress.setEnable(0);
						addressMapper.updateByPrimaryKeySelective(unableAddress);
						continue;
					}
				}
				/*
				 * 查看是否有新增的县区
				 */
				for (String  county : countyMap.keySet()) {
					Address address = addMap.get(county);
					if (address == null) {
						Address addressNew = new Address();
						addressNew.setCity(cityTrue);
						addressNew.setCounty(county);
						addressNew.setCreatTime(now);
						addressNew.setEnable(1);
						addressNew.setProvince(shortProvince);
						addressNew.setProvinceFullName(province);
						addressNew.setUpdateTime(now);
						addressMapper.insertSelective(addressNew);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 把数据库省市区加入内存
	 * @param records
	 * @return
	 */
	public Map<String, Map<String, Set<Address>>> reloadAddressDetail(List<Address> records){
		Set<String> provinceSet = new HashSet<String>();
		Map<String, Map<String, Set<Address>>> proCityMap = new HashMap<String, Map<String, Set<Address>>>();
		for (Address address : records) {
			provinceSet.add(address.getProvinceFullName());
		}
		for (String province : provinceSet) {
			Map<String, Set<Address>> cityCounty = new HashMap<String, Set<Address>>();
			Set<String> citys = new HashSet<String>();
			for (Address address : records) {
				if (province.equals(address.getProvinceFullName())) {
					citys.add(address.getCity());
				}
			}
			for (String city : citys) {
				Set<Address> countys = new HashSet<Address>();
				for (Address address : records) {
					if (province.equals(address.getProvinceFullName()) && city.equals(address.getCity())) {
						countys.add(address);
					}
				}
				cityCounty.put(city, countys);
			}
			proCityMap.put(province, cityCounty);
		}
		return proCityMap;
	}
	
	@Override
	public Map<String, Map<String, Set<String>>> getAddressMap(List<Address> records){
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
	
	/**
	 * 把“市”字去掉
	 * @param province
	 * @return
	 */
	public String convertStr(String province){
		if (province.startsWith("北京")) {
			return "北京";
		}else if (province.startsWith("天津")) {
			return "天津";
		}else if (province.startsWith("重庆")) {
			return "重庆";
		}else if (province.startsWith("上海")) {
			return "上海";
		}else {
			return province;
		}
	}
	
	@Override
	public Set<Integer> getUserAuths(String account){
		List<UserRole> userRoles = cacheService.getUserRole(account);
		if (userRoles == null || userRoles.size() == 0) {
			return null;
		}
		Set<Integer> allAuthId = new HashSet<Integer>();
		List<RoleAuth> roleAuths = new ArrayList<RoleAuth>();
		Map<Integer, RoleAuth> role = cacheService.getRoleAuth();
		for (Integer roleAuthId : role.keySet()) {
			roleAuths.add(role.get(roleAuthId));
		}
		for (UserRole userRole : userRoles) {
			for (RoleAuth roleAuth : roleAuths) {
				if (userRole.getId() == roleAuth.getRoleId()) {
					allAuthId.add(roleAuth.getAuthId());
				}
			}
		}
		return allAuthId;
	}

	@Override
	public Integer ebableClick(String account, ClickEnum clickEnum) {
		Set<Integer> authIds = this.getUserAuths(account);
		Integer click = ErrorCodeEnum.SUCCESS.getCode();
		// 查看用户是否有操作权限
		Dictionary dictionary = cacheService.getDictionary(DictionaryEnum.BUTTON, clickEnum.getCode());
		if (dictionary != null) {
			Integer authId = dictionary.getParamInt1();
			if (authId != null) {
				if (!authIds.contains(authId)) {
					click = ErrorCodeEnum.SYSTEM_ERROR.getCode();
				}
			}
		}
		return click;
	}
}

