package com.ywis.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
 
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import sm.comm.CommUtil;
import sm.comm.SFileUtil;
import sm.comm.SMLicense; 


/*
 * Program ID   : CommonAPIController
 * Description  : 공통 코드 관련 처리   
 * Creator      : djkoo
 * @vertions V1.0
 */
@RestController
public class CommonAPIController {

	private Logger logger = LoggerFactory.getLogger(CommonAPIController.class);
	 

	@Value("${pki.validation.dir:null}")
	private String PKI_VALIDATION_DIR;
	
	@Value("${license.key:null}")
	private String LICENSE_KEY;
	

	@Value("${server.ip:null}")
	private String SERVER_IP_ADDR;
	
	@Value("${server.mac:null}")
	private String SERVER_MAC;
	
	@Value("${license.product:null}")
	private String PRODUCT_KEY;
	
	@RequestMapping(value = "/.well-known/pki-validation/{validation}")
	public ResponseEntity<String>   pki_validation(@PathVariable String validation,HttpServletRequest request) {
		String msg="NoFile";
		if(SFileUtil.FileExits(PKI_VALIDATION_DIR+validation)){
			msg = SFileUtil.readFile(PKI_VALIDATION_DIR+validation);
		}
		else {
			
		}
	    
		
	    return ResponseEntity.status(HttpStatus.OK).body(msg.toString());
	 }
	 
	
	
	
	@RequestMapping(value={"/denied.do","/licenseReq.do"})
    public ModelAndView denied(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
    	ModelAndView mv = new ModelAndView();
    	HttpSession session = request.getSession();
		 
		
    	session.setAttribute("APIUserInfo", null);
    	
    	String keyRead= SFileUtil.readFile(LICENSE_KEY) ;
    	
		StringBuffer buf = new StringBuffer();
		
		if(!SMLicense.MakeLicCheck(PRODUCT_KEY, keyRead, SERVER_IP_ADDR)) {
			String LicenceReqKey = SMLicense.MakeLic(PRODUCT_KEY, SERVER_IP_ADDR);
			buf.append(LicenceReqKey);
			mv.addObject("reqLicense",buf.toString());
			SFileUtil.writeFile(LICENSE_KEY+".req", LicenceReqKey, "");
	    	mv.setViewName("login/denied");
		}
		else {
			mv.setViewName("redirect:/login.do");	
		}
		return mv;
    }
	 
	
	@RequestMapping(value="/license_reg.do")
    public ModelAndView license_reg(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
    	ModelAndView mv = new ModelAndView();
    	
    	String LICENCE_REQ= CommUtil.NvlToBlank(searchMap.get("LICENCE_REQ"));
    	SFileUtil.writeFile(LICENSE_KEY, LICENCE_REQ, "");
    	
    	mv.setViewName("redirect:/login.do");	
    	return mv;
    }
	
	
  
}