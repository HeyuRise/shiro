package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.OperateAddress;
import com.pcbwx.shiro.bean.response.ResponseRoute;
import com.pcbwx.shiro.dao.AddressMapper;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.model.Address;
import com.pcbwx.shiro.service.AccountService;
import com.pcbwx.shiro.service.OrderService;
import com.pcbwx.shiro.service.RouteService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.service.SystemService;
import com.pcbwx.shiro.service.UserService;
import com.pcbwx.shiro.service.UtilService;
import com.pcbwx.shiro.utils.SFUtil;
import com.pcbwx.shiro.utils.StringUtil;

@Controller
@RequestMapping("/util")
@Api(tags = "工具类")
public class UtilController {

	@Autowired
	private UserService userService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private SupportService supportService;
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private AddressMapper addressMapper;
	
	@RequestMapping( value = {"/userRoleMap"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("角色id和名称下拉框")
	public Map<String, Object> getRoles(HttpServletRequest request){
		return userService.getUserRoles();
	}
	
	@RequestMapping(value = { "/enableExpressCompany" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("快递公司下拉框")
	public Map<String, Object> getExpressCompany() {
		return systemService.getEnableExpressCompany();
	}
	
	@RequestMapping(value = {"/enableProduct"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("业务类型下拉框")
	public Map<String, Object> getEnableProduct(){
		return systemService.getEnableProduct();
	}
	
	@RequestMapping(value = {"/enableService"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取可用增值服务")
	public Map<String, Object> getEnable(HttpServletRequest request){
		return systemService.getEnableService();
	}
	
	@RequestMapping( value = {"/province"} , method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("省下拉框")
	public Map<String, Object> getProvinces(){
		return orderService.getProvinces();
	}
	
	@RequestMapping( value = {"/city"} , method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("市下拉框")
	public Map<String, Object> getCitys(HttpServletRequest request,
			@RequestParam("p_text") String province){
		if (province == null) {
			return null;
		}
		return orderService.getCitys(province);
	}
	
	@RequestMapping( value = {"/county"} , method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("县/区下拉框")
	public Map<String, Object> getCountys(HttpServletRequest request,
			@RequestParam("p_text") String province,
			@RequestParam("c_text") String city){
		if (province == null || city == null) {
			return null;
		}
		return orderService.getCountys(province, city);
	}
	
	@RequestMapping( value = {"/status"} , method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("状态下拉框")
	public Map<String, Object> getStatus(){
		return systemService.getStatus();
	}
	
	/**
	 * 生成code图片
	 * @param height 高
	 * @param width  宽
	 * @param format 图片类型，默认png
	 * @param type	   生成码的类型，默认为二维码，1为code128
	 * @param text   文本
	 */
	@RequestMapping(value = { "/image" }, method = RequestMethod.GET)
	@ResponseBody
	public void getQRcode(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam( value = "height" ,required = false) Integer height,
			@RequestParam( value = "width" ,required = false) Integer width,
			@RequestParam( value = "format" ,required = false) String format,
			@RequestParam( value = "type" ,required = false) Integer type,
			@RequestParam("text") String text) {
		ByteArrayOutputStream baos = accountService.trans(height, width, format, type, text);
		try {
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取物流信息
	 * @param request
	 * @param type 默认为1,按运单号查;2为按订单号查
	 * @param mailno 订单号或运单号	 
	 */
	@RequestMapping( value = {"/route"} , method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getExpressRoute(HttpServletRequest request, 
			@RequestParam(value = "type", required = false) Integer type, 
			@RequestParam("mailno") String mailno){
		Map<String, Object> response = new HashMap<String, Object>();
		if (mailno == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "信息填写不完整");
			return response;
		}
		if (type == null) {
			type = 1;
		}
		List<ResponseRoute> responseRoutes = routeService.goforExpressRoute(type, mailno);
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		response.put("expressRoute", responseRoutes);
		return response;
	}
	
	/**
	 * 导出全国行政区列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "/addressExport" }, method = RequestMethod.GET)
	@ResponseBody
	public void address(HttpServletRequest request, HttpServletResponse response){
		XSSFWorkbook workbook = accountService.outputAddressXlsx();
		String fileName = "全国行政区列表.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			OutputStream stream = response.getOutputStream();
			workbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析地址
	 * @param request
	 * @param address
	 * @return
	 */
	@RequestMapping(value = { "/address" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> operateAddress(HttpServletRequest request,
			@RequestParam("address") String address){
		Map<String, Object> response = new HashMap<String, Object>();
		List<Address> records = addressMapper.selectByCondition(null, null, null);
		Map<String, Map<String, Set<String>>> addressMap = supportService.getAddressMap(records);
		OperateAddress operateAddress = null;
		try {
			operateAddress = SFUtil.operateAddress(address, addressMap);
		} catch (Exception e) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "解析失败");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		response.put("address", operateAddress);
		return response;
	}
	
	/**
	 * 获取运费重量等信息
	 * @param mailno 运单号
	 * @return
	 */
	@RequestMapping( value = { "/mailnoInfo" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getMailInfo(HttpServletRequest request,
			@RequestParam("orderId") String orderId){
		Map<String, Object> response = new HashMap<String, Object>();
		if (StringUtil.isEmpty(orderId)) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", ErrorCodeEnum.SYSTEM_ERROR.getDescr());
			return response;
		}
		return utilService.getMailNo(orderId);
	}

}
