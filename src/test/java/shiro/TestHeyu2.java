package shiro;

import java.io.IOException;
import java.net.URLEncoder;

import com.pcbwx.shiro.bean.request.Cargo;
import com.pcbwx.shiro.bean.request.Order;
import com.pcbwx.shiro.bean.request.RequestOrderSearch;
import com.pcbwx.shiro.bean.request.RequestOrderZD;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.utils.HttpUtil;

public class TestHeyu2 {

	public static final int TYPE_MAILNO = 1;
	public static final int TYPE_ORDERNO = 2;

	public static void main(String[] args) throws IOException {
		//test();
		address();
		//sendFilterOrder();
		//order();
		//date();
		//sendRoutePush();
		//order();
		//address();
		//money();
		//order();
	}
	
	public static void search() {
		RequestOrderSearch requestOrderSearch = new RequestOrderSearch(
				"wxtb1456789611");
		String response = HttpUtil.postXML(requestOrderSearch.toXml());
		System.out.println(response);
	}

	public static void order() {
		Order order = new Order();
		order.setOrderId("devdev171207001");
		order.setSendCompany("无锡同步");
		order.setSendContact("孙贺宇");
		order.setSendTel("17368352904");
		order.setSendProvince("江苏省");
		order.setSendCity("无锡市");
		order.setSendCounty("滨湖区");
		order.setSendAddress("华东大厦5002");
		order.setReceiverContact("孙贺宇");
		order.setReceiverTel("17368352904");
		order.setReceiverProvince("西藏自治区");
		order.setReceiverCity("林芝市");
		order.setReceiverCounty("波密县");
		order.setReceiverAddress("卧龙镇");
		order.setPayMethod(1);
		order.setExpressType(1);
		order.setParcelQuantity(1);
		Cargo cargo = new Cargo("苹果");
		order.setCargo(cargo);
//		List<AddedService> addedServices = new ArrayList<AddedService>();
//		AddedService addedService = new AddedService();
//		addedService.setName("URGENT");
//		addedServices.add(addedService);
//		addedService = new AddedService();
//		addedService.setName("PKFEE");
//		List<String> values = new ArrayList<String>();
//		values.add("1");
//		values.add("888888888");
//		addedService.setValue(values);
//		addedServices.add(addedService);
//		order.setAddedServices(addedServices);
		String xml = order.toXml();
		System.out.println(xml);
		String response = HttpUtil.postXML(xml);
		System.out.println(response);
	}

	public static void address() throws IOException {
		String province = "广东省(粤)";
		String response = HttpUtil.postBody(ConfigProperties.getAddressUrl(),
				"application/x-www-form-urlencoded;charset=UTF-8", "shengji=" + province + "&diji=广州市");
		System.out.println(response);
	}

	public static void sendRoutePush() throws IOException {
		String devPath = "http://test.dianziren.cn";
		//String testPath = "http://106.14.94.226";
		String push = "content=<?xml version='1.0' encoding='UTF-8'?><Request service=\"RoutePushService\" lang=\"zh-CN\"><Body><WaybillRoute id=\"18443493050\" mailno=\"601614687596\" orderid=\"wxtb171205006\" acceptTime=\"2017-10-30 20:30:17\" acceptAddress=\"无锡市\" remark=\"已签收\" opCode=\"80\"/></Body></Request>";
		//push = FileUtil.readFileString("e:/shiro.txt", "utf-8");
		System.out.println(push);
		String a = URLEncoder.encode(push, "utf-8");
		String repsonse = HttpUtil.postBody(
				devPath + "/shiro/push/route",
				"application/x-www-form-urlencoded;charset=UTF-8", a);
		System.out.println(repsonse);
	}

	public static void getOrderSearch() {
		RequestOrderSearch requestOrderSearch = new RequestOrderSearch(
				"TE2017081404");
		String response = HttpUtil.postXML(requestOrderSearch.toXml());
		System.out.println(response);
	}

	public static void orderZD() {
		RequestOrderZD requestOrderZD = new RequestOrderZD("", 2);
		String response = HttpUtil.postXML(requestOrderZD.toXml());
		System.out.println(response);
	}

	public static void money() {
		String money = " <Request service=\"QueryFreightService\" lang=\"zh-CN\"><Head>WXSTBDZ</Head><Body>   <QueryFreightObj source_province=\"北京\" source_city_name=\"北京市\"  source_code=\"\"   dest_province=\"广东省\"     dest_city_name=\"深圳市\"   dest_code=\"\"   meterage_weightqty=\"1.0\" express_type=\"1\"/>    </Body>  </Request>";
		String response = HttpUtil.postXML(money);
		System.out.println(response);
	}

	public static String query(String mailno) {
		return HttpUtil.postXML(getQueryXml(TYPE_MAILNO, mailno));
	}

	private static String getQueryXml(int type, String mailno) {
		StringBuilder sb = new StringBuilder();
		sb.append("<Request service='RouteService' lang='zh-CN'>");
		sb.append("<Head>").append(ConfigProperties.getApiCode())
				.append("</Head>");
		sb.append("<Body>");
		sb.append("<RouteRequest tracking_type='").append(type)
				.append("' tracking_number='").append(mailno).append("'/>");
		sb.append("</Body>");
		sb.append("</Request>");
		return sb.toString();
	}

	/**
	 * 将字符串转成unicode
	 * 
	 * @param str
	 *            待转字符串
	 * @return unicode字符串
	 */
	public static String convert(String str) {
		str = (str == null ? "" : str);
		String tmp;
		StringBuffer sb = new StringBuffer(1000);
		char c;
		int i, j;
		sb.setLength(0);
		for (i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			sb.append("\\u");
			j = (c >>> 8); // 取出高8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);
			j = (c & 0xFF); // 取出低8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);

		}
		return (new String(sb));
	}

	/**
	 * 将unicode 字符串
	 * 
	 * @param str
	 *            待转字符串
	 * @return 普通字符串
	 */
	public static String revert(String str) {
		str = (str == null ? "" : str);
		if (str.indexOf("\\u") == -1)// 如果不是unicode码则原样返回
			return str;
		StringBuffer sb = new StringBuffer(1000);
		for (int i = 0; i < str.length() - 6;) {
			String strTemp = str.substring(i, i + 6);
			String value = strTemp.substring(2);
			int c = 0;
			for (int j = 0; j < value.length(); j++) {
				char tempChar = value.charAt(j);
				int t = 0;
				switch (tempChar) {
				case 'a':
					t = 10;
					break;
				case 'b':
					t = 11;
					break;
				case 'c':
					t = 12;
					break;
				case 'd':
					t = 13;
					break;
				case 'e':
					t = 14;
					break;
				case 'f':
					t = 15;
					break;
				default:
					t = tempChar - 48;
					break;
				}
				c += t * ((int) Math.pow(16, (value.length() - j - 1)));
			}
			sb.append((char) c);
			i = i + 6;
		}
		return sb.toString();
	}
}
