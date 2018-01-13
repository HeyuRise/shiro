package com.pcbwx.shiro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pcbwx.shiro.common.ConfigProperties;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * 类名：https/https报文发送处理类 功能：https/https报文发送处理 版本：1.0 日期：2012-10-11 作者：中国银联UPMP团队
 * 版权：中国银联 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己的需要，按照技术文档编写,并非一定要使用该代码。该代码仅供参考。
 * */
public class HttpUtil {
	private static final Logger logger = LogManager.getLogger(FtpUtil.class);

	public static String encoding;

	private static final HttpConnectionManager connectionManager;

	private static final HttpClient client;
	
	private static OkHttpClient okHttpClient = new OkHttpClient();

	static {

		HttpConnectionManagerParams params = loadHttpConfFromFile();

		connectionManager = new MultiThreadedHttpConnectionManager();

		connectionManager.setParams(params);

		client = new HttpClient(connectionManager);
	}

	private static HttpConnectionManagerParams loadHttpConfFromFile() {
		// Properties p = new Properties();
		// try {
		// p.load(HttpUtil.class.getResourceAsStream(HttpUtil.class.getSimpleName().toLowerCase()
		// + ".properties"));
		// } catch (IOException e) {
		// }

		// encoding = p.getProperty("http.content.encoding");

		encoding = new String("utf-8");

		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setConnectionTimeout(15000);
		params.setSoTimeout(30000);
		params.setStaleCheckingEnabled(true);
		params.setTcpNoDelay(true);
		params.setDefaultMaxConnectionsPerHost(100);
		params.setMaxTotalConnections(1000);
		params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

		// params.setConnectionTimeout(Integer.parseInt(p.getProperty("http.connection.timeout")));
		// params.setSoTimeout(Integer.parseInt(p.getProperty("http.so.timeout")));
		// params.setStaleCheckingEnabled(Boolean.parseBoolean(p.getProperty("http.stale.check.enabled")));
		// params.setTcpNoDelay(Boolean.parseBoolean(p.getProperty("http.tcp.no.delay")));
		// params.setDefaultMaxConnectionsPerHost(Integer.parseInt(p.getProperty("http.default.max.connections.per.host")));
		// params.setMaxTotalConnections(Integer.parseInt(p.getProperty("http.max.total.connections")));
		// params.setParameter(HttpMethodParams.RETRY_HANDLER, new
		// DefaultHttpMethodRetryHandler(0, false));

		return params;
	}

	 /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String urlNameString = url + "?" + param;
    	return sendGet(urlNameString);
    }
    public static String sendGet(String url) {
        String urlNameString = url;
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
        	logger.error("发送GET请求出现异常！>> " + url);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }
    
	public static String post(String url, String encoding, String content) {
		try {
			byte[] resp = post(url, content.getBytes(encoding));
			if (null == resp)
				return null;
			return new String(resp, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String post(String url, String content) {
		return post(url, encoding, content);
	}

	public static byte[] post(String url, byte[] content) {
		try {
			byte[] ret = post(url, new ByteArrayRequestEntity(content));
			return ret;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] post(String url, RequestEntity requestEntity) throws Exception {

		PostMethod method = new PostMethod(url);
		method.addRequestHeader("Connection", "Keep-Alive");
		method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
		method.setRequestEntity(requestEntity);
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("发送POST请求出现异常！>> " + url);
				return null;
			}
			return method.getResponseBody();

		} catch (SocketTimeoutException e) {
			logger.error("发送POST请求出现异常！>> " + url);
			return null;
		} finally {
			method.releaseConnection();
		}
	}
	
	public static String postJson(String urlPath, String json) throws IOException {
    	return postBody(urlPath, "application/json", json);
    }
	/**
	 * postBody形式发送数据
	 * @param urlPath 对方地址
	 * @param data 要传送的数据
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static String postBody(String urlPath, String contentType, String data) throws IOException {
		String result = null;
		try{
			// Configure and open a connection to the site you will send the request
			URL url = new URL(urlPath);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			// 设置doOutput属性为true表示将使用此urlConnection写入数据 
			urlConnection.setDoOutput(true);
			// 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型 
			urlConnection.setRequestProperty("content-type", contentType);
			// 得到请求的输出流对象  
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
			// 把数据写入请求的Body
			out.write(data);
			out.flush();
			out.close();

			// 从服务器读取响应 
			InputStream inputStream = urlConnection.getInputStream();
			String encoding = urlConnection.getContentEncoding();
			result = IOUtils.toString(inputStream, encoding);
		}catch(IOException e){
			logger.error("发送POST请求出现异常！>> " + urlPath);
			e.printStackTrace();
		}
		return result;
	}
	
	public static String postXML(String xml) {
		okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
		okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
		okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
		String res = null;
		try {
			RequestBody requestBody = new FormEncodingBuilder().add("xml", xml)
					.add("verifyCode", SFUtil.getVerifyCode(xml)).build();
			Request request = new Request.Builder()
					.url(ConfigProperties.getPostPath()).post(requestBody).build();
			Response response = okHttpClient.newCall(request).execute();
			res = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		logger.info(res);
		return res;
	}
}
