package com.ywis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;
import org.web3j.protocol.admin.Admin;
import org.web3j.utils.*;
import org.web3j.protocol.http.HttpService;
  

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import sm.comm.CommUtil;
import sm.comm.Seed256;

import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
public class LoginController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);


	@Value("${user.api.pwd:null}")
	private String USER_API_PWD;
	
	
	@Value("${user.admin.pwd:null}")
	private String USER_ADMIN_PWD;
	   
	 @RequestMapping(value={"/login.do","/"})
	    public ModelAndView login(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
	    	ModelAndView mv = new ModelAndView();
	    	HttpSession session = request.getSession();
			
	    	mv.addObject("error", searchMap.get("error"));
	    	Map loginInfo =(HashMap) session.getAttribute("APIUserInfo");
			
	    	if(loginInfo !=null) {
	    		String MEM_LVL =  CommUtil.NvlToBlank(loginInfo.get("MEM_LVL")) ;
	    		if("ADMIN".equals(MEM_LVL)) {
	    			mv.setViewName("redirect:/staticDay.do");	
	    		}
	    		else {
	    			mv.setViewName("redirect:/agentView.do");	
	    		}
	    		  
			}
	    	else mv.setViewName("login/login");
	    	return mv;
	    }
	 
	  

	  
	 
	 @RequestMapping("/agentView.do")
	    public ModelAndView api(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
	    	ModelAndView mv = new ModelAndView();
	    	HttpSession session = request.getSession();
			
	    	
	    	mv.setViewName("api/api");
	    	return mv;
	    }
	 
	 @RequestMapping("/logout.do")
	    public ModelAndView logout(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
	    	ModelAndView mv = new ModelAndView();
	    	HttpSession session = request.getSession();
			
	    	session.setAttribute("APIUserInfo", null);
	    	mv.setViewName("login/login");
	    	return mv;
	    }
	 
	 @RequestMapping("/login_proc.do")
	    public ModelAndView login_proc(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
	    	ModelAndView mv = new ModelAndView();
	    	HttpSession session = request.getSession();
			
	    	boolean secure= request.isSecure();
	    	
	    	String HOST_URL ="redirect:/swagger-ui/index.html";
	    
	    	
 	    	Map loginInfo =(HashMap) session.getAttribute("APIUserInfo");
	    	if(loginInfo !=null) {
	    		mv.setViewName("redirect:/staticDay.do");	
			}
	    	else {
	    		
	    		//searchMap.put("MEM_PWD", Seed256.Encrypt(""+searchMap.get("MEM_PWD")));
	      		Map map = new HashMap(); //mService.selectMemberID(mv, searchMap);
	      		
	      		if(map==null) {
	      			mv.setViewName("redirect:/login.do?error=-1");	
	      		}
	      		else {
	      			
	      			if("api".equals(searchMap.get("MEM_ID"))  && USER_API_PWD.equals(searchMap.get("MEM_PWD")) ) {
	      				map.put("MEM_LVL", "API");
	      				map.put("MEM_NM", "api");
	      				map.put("MEM_ID", "api");
	      				
	      				session.setAttribute("APIUserInfo", map);
	      				mv.setViewName("redirect:/agentView.do");	
	      			}
	      			else if("admin".equals(searchMap.get("MEM_ID")) && USER_ADMIN_PWD.equals(searchMap.get("MEM_PWD")) ) {
	      				map.put("MEM_LVL", "ADMIN");
	      				map.put("MEM_NM", "admin");
	      				map.put("MEM_ID", "admin");
	      				session.setAttribute("APIUserInfo", map);
	      				mv.setViewName("redirect:/staticDay.do");	
	      			}
	      			else mv.setViewName("redirect:/login.do?error=-2");	
	      		}
	      	}
	    	return mv;
	    }
	
  
}