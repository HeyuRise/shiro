package shiro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.pcbwx.shiro.bean.request.Order;
import com.pcbwx.shiro.bean.request.RouteSearch;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.utils.SFUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


public class TestHeyu {
	
	private static OkHttpClient client = new OkHttpClient();
	public static final int TYPE_MAILNO = 1;
	public static final int TYPE_ORDERNO = 2;
	
	public static void main(String[] args){

	}
	
	public static String getFile() throws IOException{
		StringBuffer sb = new StringBuffer();
		FileReader fileReader = new FileReader("D:/My Documents/ChinaMainland.Json");
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		while ((line = reader.readLine()) != null){
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}
	
	public static void route(){
		RouteSearch route = new RouteSearch(1, "444838431155", 1);
		String response = route(route);
		System.out.println(response);
	}

	private static String post(String xml) {
		String res = null;
		System.out.println(xml);
		System.out.println(SFUtil.getVerifyCode(xml));
		try {
			RequestBody requestBody = new FormEncodingBuilder().add("xml", xml)
					.add("verifyCode", SFUtil.getVerifyCode(xml)).build();
			Request request = new Request.Builder().url(ConfigProperties.getPostPath())
					.post(requestBody).build();
			Response response = client.newCall(request).execute();
			res = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String order(Order order) {
		return post(order.toXml());
	}
	
	public static String route(RouteSearch route){
		return post(route.toXML());
	}
	
	public static String query(int type, String mailno) {
		return post(getQueryXml(type, mailno));
	}

	public static String query(String mailno) {
		return post(getQueryXml(TYPE_MAILNO, mailno));
	}

	private static String getQueryXml(int type, String mailno) {
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
}
