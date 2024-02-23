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
 

@Tag(name = "3.서버인증", description = "서버인증")
@Api(tags = {"3.서버인증"})   
@RestController
public class ServerAuthAPIController {
	 
	private Logger logger = LoggerFactory.getLogger(DestinationApiController.class);
 
	@Value("${access.ipaddress:null}")
	private String ACC_IP_ADDRESS;
 
	
	@Value("${access.yn:null}")
	private String ACC_IP_YN;

	@Value("${work.path:null}")
	private String WORK_FILE_PATH;
	 
	@Value("${kt_pre_fix:null}")
	private String PRE_FIX;
	
	@Value("${server.ip:null}")
	private String SERVER_IP_ADDR;
	
	@Value("${server.mac:null}")
	private String SERVER_MAC;
	

	@Value("${server.nm:null}")
	private String SYSTEM_NM;
	
	@Value("${server.cust_no:null}")
	private String SYSTEM_CUSTNO;

	@Value("${server.mock_yn:null}")
	private String MOCK_YN;
	   
	@Value("${license.product:null}")
	private String PRODUCT_KEY;
	
	@Value("${license.key:null}")
	private String LICENSE_KEY;
	
	@Value("${client.mode:null}")
	private String CLIENT_MODE;

	@Value("${ssl.ignore:null}")
	private String SSL_IGNORE;
	 
	
	@ApiOperation(value = "접속 인증 및 토큰 발급" , notes = "접속 인증 및 토큰 발급")
	@ResponseBody
	@RequestMapping(value = "/ktgateway/infe/auth/v1/login/nhis", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String>  nhis_login(@RequestBody @ApiParam(value = "", required = true) String req_body , HttpServletRequest request){
		JSONObject obj =new JSONObject();
		HashMap hError = new HashMap();
		String sReqIPAddr =CommUtil.getRequestIP(request);
		String JOB_ID= CommUtil.GUID(request);
		
		HashMap jobInfo = new HashMap();
		jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/auth/v1/login/nhis");
		jobInfo.put("JOB_TY","POST");
		jobInfo.put("REQ_IP",sReqIPAddr);
		jobInfo.put("JOB_STATUS","FAIL");
		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
		
		
		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
		jobInfo.put("REQ_IPADDR",sReqIPAddr);
		 
		String addr= KTCommHeader.SERVER_IP_ADDR;
		String mac= KTCommHeader.SERVER_MAC;
		String system= KTCommHeader.SYSTEM_NM;
		String custNo= KTCommHeader.SYSTEM_CUSTNO;
		 
		
		String other_params[] =new String[]{"addr","system","custNo"};
		
		List listChkParams = KTCommHeader.getParamCheck("","", other_params);
		    	  
		HashMap hCommCheck= CommUtil.getCommmCheck(false,request,logger,jobInfo,req_body,listChkParams) ;
		if(!"0".equals( CommUtil.NvlToBlank(hCommCheck.get("result"))) ) {
			
			logger.error(CommUtil.geRequestLog(request,"req_body\n"+ req_body +" \nres_body \n"+ hCommCheck.get("result_msg").toString()));
			String ERR_TY=CommUtil.NvlToBlank(hCommCheck.get("ERR_TY"));
			String ERR_CD=CommUtil.NvlToBlank(hCommCheck.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, ERR_TY,ERR_CD ,  req_body,  CommUtil.ExitCodeStr(hCommCheck));
			
		    return CommUtil.ExitCode(hCommCheck);
		}
		
		
		HashMap data = (HashMap) hCommCheck.get("data");
		
		String resultJson="";
		
		try {
			
			if("Y".equals(MOCK_YN)) {
				resultJson="{\r\n"
						+ "    \"result\": 0,\r\n"
						+ "    \"token\": \""+ CommUtil.today("yyyyMMddHHmmss")+"\"\r\n"
						+ "}";
			}
			else {
				String GET_DESTINATION_URL= KTCommHeader.KT_ACTIVE_SERVER_IP+PRE_FIX+"/infe/auth/v1/login/nhis";
			 
		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("POST",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
		     
		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
		    	
		    	if("200".equals(StatusCode)) {
		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
		    	}
		    	else {
		    		
					hError.put("result", CommUtil.parseInt(StatusCode));
					hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					jobInfo.put("JOB_STATUS","ERROR");
					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					return CommUtil.ExitCode(hError);	
		    	}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hError.put("result", 9900);
			hError.put("result_msg", "Internel Error");
			jobInfo.put("JOB_STATUS","ERROR");
			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
		    return CommUtil.ExitCode(hError);	
		}
		jobInfo.put("JOB_STATUS","SUSS");
		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" , "0", req_body, resultJson);
	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);

	}
 
	
	//2023.12.1 V8 에 삭제 
	// @ApiOperation(value = "토큰 갱신" , notes = "토큰 갱신")
//	@RequestMapping(value = "/ktgateway/infe/auth/v1/token", method = RequestMethod.PUT , produces = "application/json")
//	public ResponseEntity<String>   Req_ReflashToken(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap();
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/auth/v1/token");
//		jobInfo.put("JOB_TY","PUT");
//		jobInfo.put("REQ_IP",sReqIPAddr);
//		jobInfo.put("JOB_STATUS","FAIL");
//		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
//		
//		
//		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
//		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
//		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
//		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
//		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
//		jobInfo.put("REQ_IPADDR",sReqIPAddr);
//		
//	
//		List listChkParams = KTCommHeader.getParamCheck("","", null);
//		    	  
//		HashMap hCommCheck= CommUtil.getCommmCheck(false,request,logger,jobInfo,req_body,listChkParams) ;
//		if(!"0".equals( CommUtil.NvlToBlank(hCommCheck.get("result"))) ) {
//			
//			logger.error(CommUtil.geRequestLog(request,"req_body\n"+ req_body +" \nres_body \n"+ hCommCheck.get("result_msg").toString()));
//			String ERR_TY=CommUtil.NvlToBlank(hCommCheck.get("ERR_TY"));
//			String ERR_CD=CommUtil.NvlToBlank(hCommCheck.get("result"));
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, ERR_TY,ERR_CD ,  req_body,  CommUtil.ExitCodeStr(hCommCheck));
//			
//		    return CommUtil.ExitCode(hCommCheck);
//		}
//		
//		HashMap data = (HashMap) hCommCheck.get("data");
//		  
//	
//		String resultJson="";
//		
//		try {
//			if("Y".equals(MOCK_YN)) {
//				 resultJson="{\r\n"
//							+ "    \"result\": 0,\r\n"
//							+ "    \"token\": \""+ CommUtil.today("yyyyMMddHHmmss")+"\"\r\n"
//							+ "}";	
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/auth/v1/token";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode) || "-8".equals(StatusCode)) {
//		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
//		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//		    	}
//		    	else {
//		    		
//					hError.put("result", CommUtil.parseInt(StatusCode));
//					hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					jobInfo.put("JOB_STATUS","ERROR");
//					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					 return CommUtil.ExitCode(hError);	
//		    	}
//			}
//		 	//logger.info(resultMap.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			 
//			hError.put("result", 9900);
//			hError.put("result_msg", "Internel Error");
//			jobInfo.put("JOB_STATUS","ERROR");
//			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
//			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
//		    return CommUtil.ExitCode(hError);	
//		}
//		jobInfo.put("JOB_STATUS","SUSS");
//		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
//	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0",  req_body, resultJson);
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//
//	}
//	
//	
//	
//	@ApiOperation(value = "현재 토큰 확인" , notes = "현재 토큰 확인")
//	@RequestMapping(value = "/ktgateway/currentToken", method = RequestMethod.POST , produces = "application/json")
//	public ResponseEntity<String>   tokenInfo(HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap();
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		HashMap jobInfo = new HashMap();
//		   
//		String resultJson="";
//		resultJson="{\r\n"
//						+ "    \"result\": 0,\r\n"
//						+ "    \"token\": \""+KTCommHeader.TOKEN+"\",\r\n"
//						+ "    \"status\": \""+KTCommHeader.TOKEN_STATUS+"\",\r\n"
//						+ "    \"update dt\": \""+KTCommHeader.TOKEN_UPDATE_DT+"\"\r\n"
//						+ "}";	
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//	}
  
	
	@ApiOperation(value = "접속 인증" , notes = "접속 인증 ")
	@ResponseBody
	@RequestMapping(value = "/ktgateway/login", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String>  nhis_login_auto(HttpServletRequest request){
		JSONObject obj =new JSONObject();
		HashMap hError = new HashMap();
		String sReqIPAddr =CommUtil.getRequestIP(request);
		String JOB_ID= CommUtil.GUID(request);
		
		HashMap jobInfo = new HashMap();
		jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/auth/v1/login/nhis");
		jobInfo.put("JOB_TY","POST");
		jobInfo.put("REQ_IP",sReqIPAddr);
		jobInfo.put("JOB_STATUS","FAIL");
		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
		
		
		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
		jobInfo.put("REQ_IPADDR",sReqIPAddr);
		 
		String addr= KTCommHeader.SERVER_IP_ADDR;
		String mac= KTCommHeader.SERVER_MAC;
		String system= KTCommHeader.SYSTEM_NM;
		String custNo= KTCommHeader.SYSTEM_CUSTNO;
		 
		
		String req_body="";
		req_body="{\r\n"
						+ "    \"addr\": \""+SERVER_IP_ADDR+"\",\r\n"
						+ "    \"system\": \""+SYSTEM_NM+"\",\r\n"
						+ "    \"custNo\": \""+SYSTEM_CUSTNO+"\"\r\n"
						+ "}";	
		
		
		String other_params[] =new String[]{"addr","system","custNo"};
		
		List listChkParams = KTCommHeader.getParamCheck("","", other_params);
		    	  
		HashMap hCommCheck= CommUtil.getCommmCheck(false,request,logger,jobInfo,req_body,listChkParams) ;
		if(!"0".equals( CommUtil.NvlToBlank(hCommCheck.get("result"))) ) {
			
			logger.error(CommUtil.geRequestLog(request,"req_body\n"+ req_body +" \nres_body \n"+ hCommCheck.get("result_msg").toString()));
			String ERR_TY=CommUtil.NvlToBlank(hCommCheck.get("ERR_TY"));
			String ERR_CD=CommUtil.NvlToBlank(hCommCheck.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, ERR_TY,ERR_CD ,  req_body,  CommUtil.ExitCodeStr(hCommCheck));
		    return CommUtil.ExitCode(hCommCheck);
		}
		
		
		HashMap data = (HashMap) hCommCheck.get("data");
		
		String resultJson="";
		
		try {
			
			if("Y".equals(MOCK_YN)) {
				resultJson="{\r\n"
						+ "    \"result\": 0,\r\n"
						+ "    \"token\": \""+ CommUtil.today("yyyyMMddHHmmss")+"\"\r\n"
						+ "}";
			}
			else {
				String GET_DESTINATION_URL= KTCommHeader.KT_ACTIVE_SERVER_IP+PRE_FIX+"/infe/auth/v1/login/nhis";
			 
		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("POST",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
		     
		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
		    	
		    	if("200".equals(StatusCode)) {
		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
		    	}
		    	else {
		    		
					hError.put("result", CommUtil.parseInt(StatusCode));
					
					if("-8".equals(StatusCode)) {
						hError.put("result_msg", "KT Server 인증오류 :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					}
					else hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					jobInfo.put("JOB_STATUS","ERROR");
					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					return CommUtil.ExitCode(hError);	
		    	}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hError.put("result", 9900);
			hError.put("result_msg", "Internel Error");
			jobInfo.put("JOB_STATUS","ERROR");
			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
		    return CommUtil.ExitCode(hError);	
		}
		jobInfo.put("JOB_STATUS","SUSS");
		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" , "0", req_body, resultJson);
	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);

	}
	
	@ApiOperation(value = "KT접속 서버 정보" , notes = "KT접속 서버 정보")
	@RequestMapping(value = "/ktgateway/activeServer", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<String>   activeServer(HttpServletRequest request, HttpServletResponse  response){
		
	  
		StringBuffer ktServerInfo = new StringBuffer();
		ktServerInfo.append("\n==========================================================\n");
		ktServerInfo.append("KT Server  Info\n");
		ktServerInfo.append("==========================================================\n");
		ktServerInfo.append("Active Server  : "+KTCommHeader.KT_ACTIVE_SERVER_IP +"\n");
		ktServerInfo.append("Server 1    : "+ KTCommHeader.KT_SERVER_ACTIVE_1+"\n");
		ktServerInfo.append("Server 2    : "+ KTCommHeader.KT_SERVER_ACTIVE_2+"\n");
		ktServerInfo.append("==========================================================\n");
		
		 
	    return ResponseEntity.status(HttpStatus.OK).body(ktServerInfo.toString());
	}
	
	
	@ApiOperation(value = "KT접속 변경 요청" , notes = "KT접속 변경 요청")
	@RequestMapping(value = "/ktgateway/activeChangeServer", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<String>   activeChangeServer(HttpServletRequest request, HttpServletResponse  response){
		
	  
		KTCommHeader.setChangeServer() ;
		
		StringBuffer ktServerInfo = new StringBuffer();
		
		ktServerInfo.append("\n==========================================================\n");
		ktServerInfo.append("KT Server  Info\n");
		ktServerInfo.append("==========================================================\n");
		ktServerInfo.append("Active Server  : "+KTCommHeader.KT_ACTIVE_SERVER_IP +"\n");
		ktServerInfo.append("Server 1    : "+ KTCommHeader.KT_SERVER_ACTIVE_1+"\n");
		ktServerInfo.append("Server 2    : "+ KTCommHeader.KT_SERVER_ACTIVE_2+"\n");
		ktServerInfo.append("==========================================================\n");
		
		 
	    return ResponseEntity.status(HttpStatus.OK).body(ktServerInfo.toString());
	}
	
	
	
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "토큰 갱신 자동" , notes = "토큰 갱신 자동 ")
//	@ResponseBody
//	@RequestMapping(value = "/ktgateway/reflashToken", method = RequestMethod.POST , produces = "application/json")
//	public ResponseEntity<String>  reflashToken(HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap();
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/auth/v1/token");
//		jobInfo.put("JOB_TY","PUT");
//		jobInfo.put("REQ_IP",sReqIPAddr);
//		jobInfo.put("JOB_STATUS","FAIL");
//		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
//		
//		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
//		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
//		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
//		
//		String req_body="";
//		req_body="{\r\n"
//						+ "    \"token\": \""+KTCommHeader.TOKEN+"\"\r\n"
//					 
//						+ "}";	
//		
//		 
//		List listChkParams = KTCommHeader.getParamCheck("","", null);
//		    	  
//		HashMap hCommCheck= CommUtil.getCommmCheck(false,request,logger,jobInfo,req_body,listChkParams) ;
//		if(!"0".equals( CommUtil.NvlToBlank(hCommCheck.get("result"))) ) {
//			
//			logger.error(CommUtil.geRequestLog(request,"req_body\n"+ req_body +" \nres_body \n"+ hCommCheck.get("result_msg").toString()));
//			String ERR_TY=CommUtil.NvlToBlank(hCommCheck.get("ERR_TY"));
//			String ERR_CD=CommUtil.NvlToBlank(hCommCheck.get("result"));
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, ERR_TY,ERR_CD ,  req_body,  CommUtil.ExitCodeStr(hCommCheck));
//			
//		    return CommUtil.ExitCode(hCommCheck);
//		}
//		
//		
//		HashMap data = (HashMap) hCommCheck.get("data");
//		
//		String resultJson="";
//		
//		try {
//			
//			if("Y".equals(MOCK_YN)) {
//				resultJson="{\r\n"
//						+ "    \"result\": 0,\r\n"
//						+ "    \"token\": \""+ CommUtil.today("yyyyMMddHHmmss")+"\"\r\n"
//						+ "}";
//			}
//			else {
//				String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/auth/v1/token";
//			 
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode) || "-8".equals(StatusCode)) {
//		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
//		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//		    	}
//		    	else {
//		    		
//					hError.put("result", CommUtil.parseInt(StatusCode));
//					hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					jobInfo.put("JOB_STATUS","ERROR");
//					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					return CommUtil.ExitCode(hError);	
//		    	}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			hError.put("result", 9900);
//			hError.put("result_msg", "Internel Error");
//			jobInfo.put("JOB_STATUS","ERROR");
//			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
//			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
//		    return CommUtil.ExitCode(hError);	
//		}
//		jobInfo.put("JOB_STATUS","SUSS");
//		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
//	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" , "0", req_body, resultJson);
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//
//	}
	
  
}