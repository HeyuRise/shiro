package com.pcbwx.shiro.utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.common.trust.MyX509TrustManager;
import com.pcbwx.shiro.enums.ConfigEnum;

/**
 * 公众平台通用接口工具类
* 
 * @author 王海龙
 * @date 2013-08-09
 */
public class DingUtil {
	private static final Logger logger = Logger.getLogger(DingUtil.class);

	//	通过code获取钉用户身份
	private final static String GET_USER_INFO_URL = "https://oapi.dingtalk.com/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE";
	// 通过userid获取钉用户信息
	private final static String GET_USER_DETAIL_URL = "https://oapi.dingtalk.com/user/get?access_token=ACCESS_TOKEN&userid=USERID";
	
	public static void main(String[] argv) {
		// {"noncestr":"abcdefg","timestamp":"1475914353","url":"http://test.dianziren.cn/pcbmis/index.html",
		// "jsapi_ticket":"07kTE5zbtcWew00VClBrhVTlmMiVpKjZYInnJ5bWuxdEiUBXrjp5JzfHlgAXQHgABU0eUggFJOMNzzOj8tLcwq"}
		String ticket = "07kTE5zbtcWew00VClBrhVTlmMiVpKjZYInnJ5bWuxdEiUBXrjp5JzfHlgAXQHgABU0eUggFJOMNzzOj8tLcwq";
		String nonceStr = "abcdefg";
		long timeStamp = 1475914353;
		String url = "http://test.dianziren.cn/pcbmis/index.html";
		
		String sign = null;
		try {
			sign = DingUtil.sign(ticket, nonceStr, timeStamp, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sign=" + sign);
	}
	
	public static String dingUserId2tbUserId(String dingUserId) {
		return dingUserId;
	}
	
	public static String sign(String jsapi_ticket, String noncestr, long timestamp, String url) throws Exception {
		StringBuilder content = new StringBuilder();
		content.append("jsapi_ticket=").append(jsapi_ticket)
		.append("&noncestr=").append(noncestr)
		.append("&timestamp=").append(String.valueOf(timestamp))
        .append("&url=").append(url);
        
		logger.info("待签名信息：" + content.toString());	

//		String[] arr = new String[] { jsapi_ticket, noncestr, String.valueOf(timestamp), url};
//		Arrays.sort(arr);
//		String str = assemble(arr);
//		List keyArray = sort(noncestr,timestamp,jsapi_ticket,url);
		
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行 sha1 加密
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
//		tmpStr = sha1(content.toString());
		return tmpStr;
    }
	 
	public static String sha1(String data) throws NoSuchAlgorithmException {	
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for(int i = 0; i < bits.length; i++){
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}
	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}

//    private static String bytesToHex(byte[] hash) {
//        Formatter formatter = new Formatter();
//        for (byte b : hash) {
//            formatter.format("%02x", b);
//        }
//        String result = formatter.toString();
//        formatter.close();
//        return result;
//   }
    
    /**
     * 获取钉用户的身份
     * @param account
     * @param json
     * @return
     */
    public static JSONObject getUserInfo(String accessToken, String code){
//    	String url = ConfigProperties.getProperty(ConfigEnum.GET_USER_INFO_URL);
    	String url = DingUtil.GET_USER_INFO_URL;
    	url = url.replaceAll("ACCESS_TOKEN", accessToken);
    	url = url.replaceAll("CODE", code);
    	logger.info("获取钉用户身份：" + url);
    	String result = httpRequest(url, "GET", null);
    	logger.info("获取钉用户身份结果：" + result);
    	return JSONObject.parseObject(result);
    }
    /**
     * 获取钉用户的信息
     * @param account
     * @param json
     * @return
     */
    public static JSONObject getUserDetail(String accessToken, String userId) {
//    	String url = ConfigProperties.getProperty(ConfigEnum.GET_USER_DETAIL_URL);
    	String url = DingUtil.GET_USER_DETAIL_URL;
    	url = url.replaceAll("ACCESS_TOKEN", accessToken);
    	url = url.replaceAll("USERID", userId);
    	logger.info("获取钉用户信息：" + url);
    	String result = httpRequest(url, "GET", null);
    	logger.info("获取钉用户信息结果：" + result);
    	return JSONObject.parseObject(result);
    }
    
    /**
     * 发起https请求并获取结果
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return String(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
    	System.out.println(outputStr);
        StringBuffer buffer = new StringBuffer();
        try {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new MyX509TrustManager() };
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();

                URL url = new URL(requestUrl);
                HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
                httpUrlConn.setSSLSocketFactory(ssf);

                httpUrlConn.setDoOutput(true);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                // 设置请求方式（GET/POST）
                httpUrlConn.setRequestMethod(requestMethod);

                if ("GET".equalsIgnoreCase(requestMethod))
                        httpUrlConn.connect();

                // 当有数据需要提交时
                if (null != outputStr) {
                        OutputStream outputStream = httpUrlConn.getOutputStream();
                        // 注意编码格式，防止中文乱码
                        outputStream.write(outputStr.getBytes("UTF-8"));
                        outputStream.close();
                }

                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                inputStream = null;
                httpUrlConn.disconnect();
        } catch (ConnectException ce) {
        	ce.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        logger.info(buffer.toString());
        return buffer.toString();
    }

}