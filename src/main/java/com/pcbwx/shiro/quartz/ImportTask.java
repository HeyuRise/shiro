package com.pcbwx.shiro.quartz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcbwx.shiro.bean.syn.ContactSyn;
import com.pcbwx.shiro.bean.syn.ContactSynItem;
import com.pcbwx.shiro.bean.syn.MailnoSyn;
import com.pcbwx.shiro.bean.syn.RecipientsAddressSyn;
import com.pcbwx.shiro.bean.syn.RecipientsSyn;
import com.pcbwx.shiro.bean.syn.SenderAddressSyn;
import com.pcbwx.shiro.bean.syn.SenderSyn;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.dao.ExpressMapper;
import com.pcbwx.shiro.dao.RecipientsAddressMapper;
import com.pcbwx.shiro.dao.RecipientsMapper;
import com.pcbwx.shiro.dao.RoutePushMapper;
import com.pcbwx.shiro.dao.SenderAddressMapper;
import com.pcbwx.shiro.dao.SenderMapper;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.model.Recipients;
import com.pcbwx.shiro.model.RecipientsAddress;
import com.pcbwx.shiro.model.RoutePush;
import com.pcbwx.shiro.model.Sender;
import com.pcbwx.shiro.model.SenderAddress;
import com.pcbwx.shiro.service.ConfigService;
import com.pcbwx.shiro.utils.BASE64MD5Util;
import com.pcbwx.shiro.utils.DateCalcUtil;
import com.pcbwx.shiro.utils.DateTimeUtil;
import com.pcbwx.shiro.utils.JsonUtil;

public class ImportTask {
	private static final Logger logger = LogManager.getLogger(ImportTask.class);

	// 匹配14位数字
	private String reg = "[0-9]{14}";
	private final Pattern pattern = Pattern.compile(reg);
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private ExpressMapper expressMapper;
	@Autowired
	private RecipientsMapper recipientsMapper;
	@Autowired
	private RecipientsAddressMapper recipientsAddressMapper;
	@Autowired
	private SenderMapper senderMapper;
	@Autowired
	private SenderAddressMapper senderAddressMapper;
	@Autowired
	private RoutePushMapper routePushMapper;

	public boolean contactSyn() {
		logger.info("同步通讯录开始");
		List<ContactSyn> contactSyns = null;
		try {
			contactSyns = receive();
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		if (contactSyns.isEmpty()) {
			logger.info("同步通讯录结束");
			return true;
		}
		boolean isSuccess = operateContact(contactSyns);
		if (!isSuccess) {
			LogContext.error("同步失败", "同步通讯录失败");
		}
		logger.info("同步通讯录结束");
		return true;
	}

	public boolean mailnoSyn() {
		Date now = new Date();
		Date date = DateCalcUtil.subTime(now, Calendar.HOUR, 11);
		String dayTime = DateTimeUtil.date2dateStr(date);
		List<RoutePush> routePushs = routePushMapper.selectByCreateTime(dayTime);
		if (routePushs == null || routePushs.isEmpty()) {
			logger.info("无可同步运单");
			return true;
		}
		Set<Integer> expressIds = new HashSet<Integer>();
		for (RoutePush routePush : routePushs) {
			expressIds.add(routePush.getExpressId());
		}
		if (expressIds.isEmpty()) {
			logger.info("无可同步运单");
			return true;
		}
		List<Express> expresses = expressMapper.selectSynMailno(expressIds);
		if (expresses == null || expresses.isEmpty()) {
			logger.info("无可同步运单");
			return true;
		}
		List<MailnoSyn> mailnos = new ArrayList<MailnoSyn>();
		for (Express express : expresses) {
			MailnoSyn mailno = new MailnoSyn();
			String mailnoChild = express.getMailNoChild();
			String mail = express.getMailNo()
					+ (mailnoChild == null ? "" : ("," + mailnoChild));
			mailno.setMail_no(mail);
			mailno.setOrder_id(express.getInnerOrderId());
			mailno.setCreate_time(DateTimeUtil.date2dateTimeStr(express
					.getCreateTime()));
			mailnos.add(mailno);
		}

		String json = JsonUtil.obj2json(mailnos);
		String md5json = BASE64MD5Util.md5(json);
		String fileName = "express-"
				+ DateTimeUtil.date2dateTimeStr(now, "yyyyMMddHHmmss") + "-"
				+ md5json;
		mkPathDir();
		String filePath = ConfigProperties.getMailnoFilePath() + fileName + ".txt";
		File file = new File(filePath);
		Writer output = null;
		try {
			output = new OutputStreamWriter(new FileOutputStream(file,true), "UTF-8");
			output.write(json);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean isSuccess = send(fileName, filePath);
		if (!isSuccess) {
			return false;
		}
		return true;
	}

	public boolean send(String fileName, String filePath) {
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol",
				ConfigProperties.getSendProtocol()); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.smtp.host", ConfigProperties.getSendHost()); // 发件人的邮箱的
		//阿里云ecs禁用了25端口，所以使用80端口
		props.setProperty("mail.smtp.port", "80"); 
		props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
		Session session = Session.getDefaultInstance(props);
		session.setDebug(false); // 设置为debug模式, 可以查看详细的发送 log
		MimeMessage message = createMimeMessage(session, fileName, filePath);
		if (message == null) {
			return false;
		}
		try {
			Transport transport = session.getTransport();
			transport.connect(ConfigProperties.getMailAccount(),
					ConfigProperties.getMailPaddword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public MimeMessage createMimeMessage(Session session,
			String fileName, String filePath) {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(ConfigProperties
					.getMailAccount(), "顺丰面单电子化系统", "UTF-8"));
			message.setRecipient(MimeMessage.RecipientType.TO,
					new InternetAddress(ConfigProperties.getMailTargetMail(),
							ConfigProperties.getMailTargetMail(), "UTF-8"));
			String subject = "（转内网：" + ConfigProperties.getInnerMail() + "）"
					+ fileName;
			message.setSubject(subject, "UTF-8");
			message.addHeader("charset", "UTF-8");

			/* 添加正文内容 */
			Multipart multipart = new MimeMultipart();
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setText(fileName);

			contentPart.setHeader("Content-Type", "text/html; charset=UTF-8");
			multipart.addBodyPart(contentPart);

			/* 添加附件 */
			MimeBodyPart fileBody = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath);
			fileBody.setDataHandler(new DataHandler(source));
			fileBody.setFileName(fileName + ".txt");
			multipart.addBodyPart(fileBody);

			message.setContent(multipart);
			message.setSentDate(new Date());
			message.saveChanges();
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (MessagingException e) {
			return null;
		}
		return message;
	}

	public List<ContactSyn> receive() throws MessagingException {
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol", ConfigProperties.getReceiveProtocol()); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.smtp.host", ConfigProperties.getReceiveHost()); // 发件人的邮箱的
		Session session = Session.getInstance(props);
		session.setDebug(false); // 设置为debug模式, 可以查看详细的发送 log
		Store store = session.getStore(ConfigProperties.getReceiveProtocol());
		store.connect(ConfigProperties.getReceiveHost(), ConfigProperties.getMailAccount(),ConfigProperties.getMailPaddword());
		// 获取邮件服务器的收件箱
		Folder folder = store.getFolder("INBOX");
		// 以只读权限打开收件箱
		folder.open(Folder.READ_WRITE);
		List<ContactSyn> contactSyns = new ArrayList<ContactSyn>();
		Message[] msgs = folder.getMessages();
		Date lastTime = configService.getUtilDate(ConfigEnum.LAST_CONTACT_SYN_TIME);
		for (Message message : msgs) {
			String subject = message.getSubject();
			logger.info(subject);
			message.setFlag(Flag.DELETED, true);
			if (!subject.startsWith("person")) {
				continue;
			}
			ContactSyn contactSyn = new ContactSyn();
			Matcher matcher = pattern.matcher(subject);
			if (matcher.find()) {
				String dateString = matcher.group();
				Date date = DateTimeUtil.Str2dateTime(dateString, "yyyyMMddHHmmss");
				if (date == null) {
					LogContext.error("标题格式错误", "通讯录同步邮件标题格式错误");
					continue;
				}
				if (lastTime != null && !lastTime.before(date)) {
					continue;
				}
				contactSyn.setDate(date);
			}else {
				LogContext.error("标题格式错误", "通讯录同步邮件标题格式错误");
				continue;
			}
			try {
				String txt = mailReceiver(message);
				logger.info(txt);
				if (txt != null) {
					ContactSynItem contactSynItem = (ContactSynItem) JsonUtil.json2obj(txt, ContactSynItem.class);
					if (contactSynItem == null) {
						LogContext.error("邮件解析错误", "通讯录同步邮件解析错误");
						continue;
					}
					contactSyn.setContactSynItem(contactSynItem);
				}
			} catch (Exception e) {
				LogContext.error("邮件解析错误", "通讯录同步邮件解析错误");
				continue;
			}
			contactSyns.add(contactSyn);
		}
		receiveOver(folder, store);
		return contactSyns;
	}
	/**
	 * 清理资源，关闭连接
	 * 
	 * @return
	 */
	private boolean receiveOver(Folder folder, Store store) {
		if (null != folder) {
			try {
				folder.close(true);
			} catch (MessagingException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (null != store) {
			try {
				store.close();
			} catch (MessagingException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	private String mailReceiver(Message msg) throws Exception {
		// 发件人信息
		Address[] froms = msg.getFrom();
		if (froms != null) {
			InternetAddress addr = (InternetAddress) froms[0];
			logger.info("发件人地址:" + addr.getAddress());
			logger.info("发件人显示名:" + addr.getPersonal());
		}
		String subject = msg.getSubject();
		// getContent() 是获取包裹内容, Part相当于外包装
		Object o = msg.getContent();
		if (o instanceof Multipart) {
			Multipart multipart = (Multipart) o;
			return reMultipart(multipart, subject);
		} else if (o instanceof Part) {
			Part part = (Part) o;
			return rePart(part, subject);
		}
		return null;
	}

	/**
	 * @param multipart
	 * // 接卸包裹（含所有邮件内容(包裹+正文+附件)）
	 * @throws Exception
	 */
	private String reMultipart(Multipart multipart, String subject) throws Exception {
		Integer n = multipart.getCount();
		for (int i = 0; i < n; i++) {
			Part part = multipart.getBodyPart(i);
			// 解包, 取出 MultiPart的各个部分,每部分可能是邮件内容,
			// 也可能是另一个小包裹(MultipPart)
			// 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
			if (part.getContent() instanceof Multipart) {
				Multipart p = (Multipart) part.getContent();// 转成小包裹
				// 递归迭代
				String txt = reMultipart(p, subject);
				if (txt == null) {
					continue;
				}
				return txt;
			} else {
				String txt = rePart(part, subject);
				if (txt == null) {
					continue;
				}
				return txt;
			}
		}
		return null;
	}

	/**
	 * 解析附件内容，把附件存到本体，返回附件内容
	 * @param part
	 * @param subject
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String rePart(Part part, String subject) throws MessagingException,
			UnsupportedEncodingException, IOException, FileNotFoundException {
		if (part.getDisposition() != null) {
			InputStream in = part.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String pathHead = ConfigProperties.getMailnoFilePath();
			if (pathHead == null) {
				pathHead = "";
			}else {
				mkPathDir();
			}
			File file = new File(pathHead + part.getFileName());
			FileWriter fileWriter = new FileWriter(file);
			String s; // 依次循环，至到读的值为空
			StringBuilder sb = new StringBuilder();
			while ((s = reader.readLine()) != null) {
				sb.append(s);
				fileWriter.write(s);
			}
			fileWriter.flush();
			fileWriter.close();
			in.close();
			return sb.toString();
		} else {
			return null;
		}
	}

	private boolean operateContact(List<ContactSyn> contactSyns){
		Integer size = contactSyns.size();
		// 把邮件附件内容按时间戳由小到大排列
		if (size != 1) {
			for (int i = 0; i < size - 1; i++) {
				for (int j = 0; j < size - i - 1; j++) {
					if (contactSyns.get(j).getDate().after(contactSyns.get(j + 1).getDate())) {
						ContactSyn contactSyn = contactSyns.get(j);
						ContactSyn contactSyn2 = contactSyns.get(j + 1);
						contactSyns.set(j, contactSyn2);
						contactSyns.set(j + 1, contactSyn);
					}
				}
			}
		}
		Date now = new Date();
		Date lastDate = null;
		for (ContactSyn contactSyn : contactSyns) {
			lastDate = contactSyn.getDate();
			logger.info(lastDate);
			ContactSynItem contactSynItem = contactSyn.getContactSynItem();
			if (contactSynItem == null) {
				continue;
			}
			RecipientsSyn recipients = contactSynItem.getRecipients();
			if (recipients != null && recipients.getCode() != null) {
				operateRecipients(recipients, now);
			}
			SenderSyn sender = contactSynItem.getSender();
			if (sender != null && sender.getCode() != null) {
				operateSender(sender, now);
			}
			List<SenderAddressSyn> senderAddress = contactSynItem.getSender_address();
			if (senderAddress != null && !senderAddress.isEmpty()) {
				operateSenderAddress(senderAddress, now);
			}
		}
		if (lastDate != null) {
			configService.setUtilRecord(ConfigEnum.LAST_CONTACT_SYN_TIME, lastDate, null);
		}
		return true;
	}
	
	/**
	 * 更新收件人信息和地址
	 * @param recipients
	 * @param now
	 */
	private void operateRecipients(RecipientsSyn recipients, Date now){
		String receiveCode = recipients.getCode();
		// 删除该收件人全部地址
		recipientsAddressMapper.deleteByRecipientsCode(receiveCode);
		Recipients recipientsModel =  recipientsMapper.selectByInnerCodeNoStatus(receiveCode);
		String tel = recipients.getTel();
		String mobile = recipients.getMobile();
		// 去掉空格
		if (tel != null) {
			tel = tel.replaceAll(" ", "");
		}
		if (mobile != null) {
			mobile = mobile.replaceAll(" ", "");
		}
		if (recipientsModel == null) {
			recipientsModel = new Recipients();
			recipientsModel.setCreateTime(now);
			recipientsModel.setCompany(recipients.getCompany());
			recipientsModel.setInnerCode(receiveCode);
			String innerUpdateTimeStr = recipients.getUpdate_time();
			Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
			recipientsModel.setInnnerUpdateTime(innerUpdateTime);
			recipientsModel.setMobile(mobile);
			recipientsModel.setName(recipients.getName());
			recipientsModel.setSendCompany(recipients.getSender_company());
			recipientsModel.setStatus(recipients.getEnable());
			recipientsModel.setTelephone(tel);
			recipientsMapper.insertSelective(recipientsModel);
		}else {
			recipientsModel.setCompany(recipients.getCompany());
			recipientsModel.setInnerCode(receiveCode);
			String innerUpdateTimeStr = recipients.getUpdate_time();
			Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
			recipientsModel.setInnnerUpdateTime(innerUpdateTime);
			recipientsModel.setMobile(mobile);
			recipientsModel.setName(recipients.getName());
			recipientsModel.setSendCompany(recipients.getSender_company());
			recipientsModel.setStatus(recipients.getEnable());
			recipientsModel.setTelephone(tel);
			recipientsMapper.updateByPrimaryKeySelective(recipientsModel);
		}
		if (recipients.getEnable() == 0) {
			return;
		}
		List<RecipientsAddressSyn> address = recipients.getAddress();
		if (address != null && !address.isEmpty()) {
			for (RecipientsAddressSyn addressSyn : address) {
				RecipientsAddress recipientsAddress = new RecipientsAddress();
				recipientsAddress.setAddress(addressSyn.getDetail());
				recipientsAddress.setCity(addressSyn.getCity());
				recipientsAddress.setCreateTime(now);
				recipientsAddress.setDistrict(addressSyn.getCounty());
				recipientsAddress.setInnerId(addressSyn.getId());
				String innerUpdateTimeStr = addressSyn.getUpdate_time();
				Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
				recipientsAddress.setInnerUpdateTime(innerUpdateTime);
				recipientsAddress.setProvince(addressSyn.getProvince());
				recipientsAddress.setRecipientsCode(receiveCode);
				recipientsAddress.setStatus(1);
				recipientsAddressMapper.insertSelective(recipientsAddress);
			}
		}
	}
	
	/**
	 * 更新发件人信息
	 * @param senderSyn
	 * @param now
	 */
	private void operateSender(SenderSyn senderSyn, Date now){
		String senderCode = senderSyn.getCode();
		Sender sender = senderMapper.selectByInnerCodeNoStatus(senderCode);
		String tel = senderSyn.getTel();
		String mobile = senderSyn.getMobile();
		// 去掉空格
		if (tel != null) {
			tel = tel.replaceAll(" ", "");
		}
		if (mobile != null) {
			mobile = mobile.replaceAll(" ", "");
		}
		if (sender == null) {
			sender = new Sender();
			sender.setCreateTime(now);
			String innerUpdateTimeStr = senderSyn.getUpdate_time();
			Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
			sender.setInnerUpdateTime(innerUpdateTime);
			sender.setInnerCode(senderCode);
			sender.setMobile(mobile);
			sender.setName(senderSyn.getName());
			sender.setStatus(senderSyn.getEnable());
			sender.setTelephone(tel);
			senderMapper.insertSelective(sender);
		}else{
			String innerUpdateTimeStr = senderSyn.getUpdate_time();
			Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
			sender.setInnerUpdateTime(innerUpdateTime);
			sender.setInnerCode(senderCode);
			sender.setMobile(mobile);
			sender.setName(senderSyn.getName());
			sender.setStatus(senderSyn.getEnable());
			sender.setTelephone(tel);
			senderMapper.updateByPrimaryKeySelective(sender);
		}
	}
	
	/**
	 * 更新发件地址，全部删除后在插入
	 * @param senderAddressSyns
	 * @param now
	 */
	private void operateSenderAddress(List<SenderAddressSyn> senderAddressSyns, Date now){
		senderAddressMapper.deleteAll();
		for (SenderAddressSyn senderAddressSyn : senderAddressSyns) {
			Integer innerId = senderAddressSyn.getId();
			SenderAddress senderAddress = new SenderAddress();
			senderAddress = new SenderAddress();
			senderAddress.setCreateTime(now);
			senderAddress.setAddress(senderAddressSyn.getDetail());
			senderAddress.setCity(senderAddressSyn.getCity());
			senderAddress.setDistrict(senderAddressSyn.getCounty());
			senderAddress.setInnerId(innerId);
			senderAddress.setProvince(senderAddressSyn.getProvince());
			String innerUpdateTimeStr = senderAddressSyn.getUpdate_time();
			Date innerUpdateTime = DateTimeUtil.dateTimeStr2date(innerUpdateTimeStr);
			senderAddress.setInnerUpdateTime(innerUpdateTime);
			senderAddress.setStatus(senderAddressSyn.getEnable());
			senderAddressMapper.insertSelective(senderAddress);
		}
	}

	/**
	 * 查看文件夹是否存在，如果不存在的话就新建
	 */
	private void mkPathDir(){
		File filedir = new File(ConfigProperties.getMailnoFilePath());
		if (!filedir.exists()) {
			filedir.mkdirs();
		}
	}
}
