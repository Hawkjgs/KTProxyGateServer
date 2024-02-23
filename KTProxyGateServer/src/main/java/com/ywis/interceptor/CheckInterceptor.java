package com.ywis.interceptor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import sm.comm.SFileUtil;
import sm.comm.SMEnv;
import sm.comm.SMLicense;
import sm.comm.StringUtil;
 

@RestController
public class CheckInterceptor extends HandlerInterceptorAdapter {
	
	@SuppressWarnings("unused")
	private  Logger log = LoggerFactory.getLogger(CheckInterceptor.class);
	//Logger log = Logger.getLogger(this.getClass());
	
 

	@Value("${license.key:null}")
	private String LICENSE_KEY;
	
	
	@Value("${server.mac:null}")
	private String SERVER_MAC;
	
	@Value("${license.product:null}")
	private String PRODUCT_KEY;
	

	@Value("${server.ip:null}")
	private String SERVER_IP_ADDR;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String path = request.getContextPath();
		ModelAndView mv = new ModelAndView();
		request.setAttribute("contextPath", path);
		request.setAttribute("title_icon_1", "<span class=\"td_mini_hide\"> <i class='glyphicon glyphicon-record'></i>");
		
	 	String userCallURL = request.getRequestURI();
		HttpSession session = request.getSession();
		
		String IPADDR = request.getRemoteAddr();
		 
		Map  searchMap = new HashMap();
		ModelMap model = new ModelMap();
		
		HashMap ipMap = new HashMap();
		ipMap.put("IP_ADDR", IPADDR);
		request.setAttribute("IPADDR", IPADDR);
		
		if((userCallURL.indexOf(".css") >=0) 
		|| (userCallURL.indexOf(".js") >=0)
		
		|| (userCallURL.indexOf(".ico") >=0)
		|| (userCallURL.indexOf(".eot") >=0)
		|| (userCallURL.indexOf(".svg") >=0)
		|| (userCallURL.indexOf(".ttf") >=0)
		|| (userCallURL.indexOf(".woff") >=0)
		|| (userCallURL.indexOf(".woff2") >=0)
		|| (userCallURL.indexOf(".map") >=0)
		|| (userCallURL.indexOf(".jpg") >=0)
		|| (userCallURL.indexOf(".png") >=0)
		|| (userCallURL.indexOf(".jpeg") >=0)
		|| (userCallURL.indexOf(".gif") >=0)
		|| (( userCallURL.indexOf(".html") >=0) &&  !(userCallURL.indexOf("swagger-ui/index.html") >=0)) 
		|| (userCallURL.indexOf("/api-docs") >=0)
		|| (userCallURL.indexOf("/ktgateway/") >=0)
		|| (userCallURL.indexOf("/test.do") >=0)
		|| (userCallURL.indexOf("denied") >=0)
		|| (userCallURL.indexOf("Ajax") >=0) 
		|| (userCallURL.indexOf("MyPhoto") >=0) 
		|| (userCallURL.indexOf("/.well-known") >=0) 
		|| (userCallURL.indexOf("/licence.do") >=0) 
		|| (userCallURL.indexOf("/denied.do") >=0) 
		|| (userCallURL.indexOf("/license_reg.do") >=0) 
		//|| (userCallURL.indexOf("/swagger-ui/") >=0)
		|| (userCallURL.indexOf("/agentView.do") >=0)
		|| (userCallURL.indexOf("/swagger") >=0) 
	 
		)  {
			return true;
		}
		
		
		String keyRead= SFileUtil.readFile(LICENSE_KEY) ;
		
		StringBuffer buf = new StringBuffer();
		
		if(!SMLicense.MakeLicCheck(PRODUCT_KEY, keyRead, SERVER_IP_ADDR)) {
			response.sendRedirect(path+"/denied.do");
			return false;
		}
	    
		
		if(userCallURL.indexOf("login.do") >=0 || (userCallURL.indexOf("login_proc") >=0) ) {
			return true;
		}
		
		Map userInfo  = (Map) session.getAttribute("APIUserInfo");
		
		if ( userInfo == null || (""+userInfo.get("MEM_ID")).isEmpty()){
			response.sendRedirect(path+"/login.do");
			return false;
		}
		 
		return true;
 

	}

	    

}
