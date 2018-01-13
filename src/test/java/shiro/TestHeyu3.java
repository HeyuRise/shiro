package shiro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import com.pcbwx.shiro.utils.HttpUtil;

public class TestHeyu3 {

	public static void main(String[] args) throws MessagingException {

	}

	public static void routPush() {
		String repsonse = "";
		try {
			String a = URLEncoder
					.encode("<Request service=\"RoutePushService\" lang=\"zh-CN\"><Body><WaybillRoute id=\"5348054\" mailno=\"444001020861\" orderid=\"dev170930001\" acceptTime=\"2017-09-26 12:36:36\" acceptAddress=\"苏州市\" remark=\"已签收\" opCode=\"80\"/></Body></Request>",
							"utf-8");
			repsonse = HttpUtil.postBody(
					"http://localhost:8080/shiro/push/route",
					"application/x-www-form-urlencoded;charset=UTF-8", a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(repsonse);
	}

	public static void receive() throws MessagingException {
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol", "pop3"); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.smtp.host", "pop3.pcbwx.com.cn"); // 发件人的邮箱的
		Session session = Session.getInstance(props);
		session.setDebug(false); // 设置为debug模式, 可以查看详细的发送 log
		Store store = session.getStore("pop3");
		store.connect("pop3.pcbwx.com.cn", "sun_hy2242@pcbwx.com.cn", "Heyu1061000745");
		// 获取邮件服务器的收件箱
		Folder folder = store.getFolder("INBOX");
		// 以只读权限打开收件箱
		folder.open(Folder.READ_WRITE);
		Message[] msgs = folder.getMessages();
		for (Message message : msgs) {
			try {
				mailReceiver(message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	
	private static void mailReceiver(Message msg)throws Exception{  
        // 发件人信息  
        Address[] froms = msg.getFrom();  
        if(froms != null) {  
            //System.out.println("发件人信息:" + froms[0]);  
            InternetAddress addr = (InternetAddress)froms[0];  
            System.out.println("发件人地址:" + addr.getAddress());  
            System.out.println("发件人显示名:" + addr.getPersonal());  
        }  
        System.out.println("邮件主题:" + msg.getSubject());  
        // getContent() 是获取包裹内容, Part相当于外包装  
        Object o = msg.getContent();  
        //if(o instanceof Multipart) {  
            Multipart multipart = (Multipart) o ;  
            reMultipart(multipart);  
//        } else if (o instanceof Part){  
//            Part part = (Part) o;   
//            rePart(part);  
//        } else {  
//            System.out.println("类型" + msg.getContentType());  
//            System.out.println("内容" + msg.getContent());  
//        }  
    }  
	
	/** 
     * @param multipart // 接卸包裹（含所有邮件内容(包裹+正文+附件)） 
     * @throws Exception 
     */  
    private static void reMultipart(Multipart multipart) throws Exception {  
        //System.out.println("邮件共有" + multipart.getCount() + "部分组成");  
        // 依次处理各个部分  
        for (int j = 0, n = multipart.getCount(); j < n; j++) {  
            //System.out.println("处理第" + j + "部分");  
            Part part = multipart.getBodyPart(j);//解包, 取出 MultiPart的各个部分, 每部分可能是邮件内容,  
            // 也可能是另一个小包裹(MultipPart)  
            // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative  
            if (part.getContent() instanceof Multipart) {  
                Multipart p = (Multipart) part.getContent();// 转成小包裹  
                //递归迭代  
                reMultipart(p);  
            } else {  
                rePart(part);  
            }  
         }  
    }  
      
    /** 
     * @param part 解析内容 
     * @throws Exception 
     */  
    private static void rePart(Part part) throws MessagingException,  
            UnsupportedEncodingException, IOException, FileNotFoundException {  
        if (part.getDisposition() != null) {  
            System.out.println("发现附件: " +  MimeUtility.decodeText(part.getFileName()));  
            System.out.println("内容类型: " + MimeUtility.decodeText(part.getContentType()));  
            System.out.println("附件内容:" + part.getContent());  
            InputStream in = part.getInputStream();// 打开附件的输入流  
            // 读取附件字节并存储到文件中  
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s; // 依次循环，至到读的值为空
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
            sb.append(s);
            }
            in.close();  
            reader.close();
            System.out.println(sb.toString());
        } else {  
            if(part.getContentType().startsWith("text/plain")) {  
                System.out.println("文本内容：" + part.getContent());  
            } else {  
                //System.out.println("HTML内容：" + part.getContent());  
            }  
        }  
    }  
}
