package com.ywis.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
 
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import sm.comm.CommUtil;
import sm.comm.HttpComm;
import sm.comm.JSONUtils;
import sm.comm.KTCommHeader;
import sm.comm.SFileUtil;
import sm.comm.SMLicense;
import sm.comm.Seed256;
 

@Tag(name = "4.라이센스 요청 ", description = "4.라이센스 요청 ")
@Api(tags = {"4.라이센스 요청 "})   
@RestController
public class LicenseController {
	 
	private Logger logger = LoggerFactory.getLogger(DestinationApiController.class);
  

	private static String VER ="1.10";
	private static String COPY_RIGHTS ="Copyright by 2023 YWIS ALL RIGHTS RESERVED";
	private static String DATE ="2023.12.20";
	private static String AUTHOR ="Jang Gye Sun";
	private static String KT_API ="전국대표번호 건강보험공단 콜센터 연동_V10 (2023.12.22)";
	
	
	
	
	@Value("${license.key:null}")
	private String LICENSE_KEY;
	
	@Value("${server.ip:null}")
	private String SERVER_IP_ADDR;
	 
	@Value("${license.product:null}")
	private String PRODUCT_KEY;
	
	
	@ApiOperation(value = "라이센스 확인" , notes = "라이센스 확인")
	@RequestMapping(value = "/ktgateway/licence", method = RequestMethod.POST,produces = "application/html" )
	public ResponseEntity<String>   licence(HttpServletRequest request) {
		 
		String keyRead= SFileUtil.readFile(LICENSE_KEY) ;
	
		StringBuffer buf = new StringBuffer();
		
		if(!SMLicense.MakeLicCheck(PRODUCT_KEY, keyRead, SERVER_IP_ADDR)) {
			buf.append("\n==============================");
			buf.append("\nKTYGateWay : No License");
			buf.append("\n==============================\n");
			String LicenceReqKey = SMLicense.MakeLic(PRODUCT_KEY, SERVER_IP_ADDR);
			buf.append(LicenceReqKey);
			buf.append("\n==============================\n");
		 
		}
		else {
			buf.append("\n==============================");
			buf.append("\nKTYGateWay : License OK");
			buf.append("\n==============================\n");
		}
	    return ResponseEntity.status(HttpStatus.OK).body(buf.toString());
	 }
	
	@ApiOperation(value = "라이센스요청" , notes = "라이센스요청")
	@RequestMapping(value = "/ktgateway/licenseRequest", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<String>   licenseRequest(HttpServletRequest request){
		JSONObject obj =new JSONObject();
		 
//		String keyRead= SFileUtil.readFile(LICENSE_KEY) ;
//    	StringBuffer buf = new StringBuffer();
		
    	String LicenceReqKey = SMLicense.MakeLic(PRODUCT_KEY, SERVER_IP_ADDR);
		SFileUtil.writeFile(LICENSE_KEY+".req", LicenceReqKey, "");
	     
	    return ResponseEntity.status(HttpStatus.OK).body(LicenceReqKey);
	}
	
	
	@ApiOperation(value = "관리자 라이센스 생성 " , notes = "관리자 라이센스 생성  ")
	@ApiImplicitParams(
	 {
		 @ApiImplicitParam(name = "PASSWD" , value = "PASSWD " , required = true  , paramType = "query"   )
	 })
	@RequestMapping(value = "/ktgateway/licenseYwisMake", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<String>   licenseYwisMake(HttpServletRequest request){
		JSONObject obj =new JSONObject();
		 
		
		String PASSWD =  CommUtil.NvlToBlank(request.getParameter("PASSWD")); 
		 
		String keyRead= SFileUtil.readFile(LICENSE_KEY+".req") ;
    
		String LicenceStatus ="";
		
		if ("".equals(keyRead)) {
			keyRead = SMLicense.MakeLic(PRODUCT_KEY, SERVER_IP_ADDR);
			SFileUtil.writeFile(LICENSE_KEY+".req", keyRead, "");
		}
		
		if(!Seed256.Encrypt("nhis@ywis.co.kr").equals( Seed256.Encrypt(PASSWD))) {
			LicenceStatus="PassWord Error \n";
		}
		else if(SMLicense.MakeLicGenerator(keyRead, LICENSE_KEY)) {
			LicenceStatus="License Generator Ok \n";
		}
		else {
			LicenceStatus="License Generator Fail \n";
		}
		
    
	     
	    return ResponseEntity.status(HttpStatus.OK).body(LicenceStatus);
	}
	
	
	@RequestMapping(value = "/ktgateway/appVersion", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<String>   appVersion(HttpServletRequest request, HttpServletResponse  response){
		
	  
		StringBuffer AppVersion = new StringBuffer();
		
		AppVersion.append("\n==========================================================\n");
		AppVersion.append("Product: KT GATEWAY \n");
		AppVersion.append("==========================================================\n");
		AppVersion.append("DATE  : "+DATE +"\n");
		AppVersion.append("VER   : "+ VER+"\n");
		AppVersion.append("AUTHOR: "+AUTHOR+"\n");
		AppVersion.append("KT_API: "+KT_API+"\n");
		
		AppVersion.append("==========================================================\n");
		AppVersion.append(COPY_RIGHTS+"\n");
		AppVersion.append("==========================================================\n");
		
		 
	    return ResponseEntity.status(HttpStatus.OK).body(AppVersion.toString());
	}
	
	
	
 
	
  
}