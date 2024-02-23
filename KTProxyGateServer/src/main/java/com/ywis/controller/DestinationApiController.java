package com.ywis.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
 

@Tag(name = "1.착신지별 상태 조회 및 변경", description = "상태 조회 및 변경")
@Api(tags = {"1.착신지별 상태 조회 및 변경"})   
@RestController
public class DestinationApiController {
	 
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
	 
	@ApiOperation(value = "등록된 착신지별 정보 조회" , notes = "등록된 착신지별 정보 조회")
	@ResponseBody
	@RequestMapping(value = "/ktgateway/infe/api/v1/destination", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String>   getDestination(@RequestBody @ApiParam(value = "", required = true) String req_body , HttpServletRequest request){
		JSONObject obj =new JSONObject();
		HashMap hError = new HashMap();
		String JOB_ID= CommUtil.GUID(request);
		String sReqIPAddr =CommUtil.getRequestIP(request);
		HashMap jobInfo = new HashMap();
		jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/api/v1/destination");
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
		
 
		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK","SUB_NB_CHK", null);
  
		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
				resultJson="{\"result\":0,\"listLength\":2,\"list\":[[\"\",\"\",\"\"],[\"\",\"\",\"\"]]}";
			}
			else {
				
		    	String GET_DESTINATION_URL= KTCommHeader.KT_ACTIVE_SERVER_IP+PRE_FIX+"/infe/api/v1/destination";
		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("POST",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
		     
		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
		    	
		    	if("200".equals(StatusCode) || "-8".equals(StatusCode)) {
		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
		    	}
		    	else {
		    		
					hError.put("result", CommUtil.parseInt(StatusCode));
					hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					jobInfo.put("JOB_STATUS","ERROR");
					
					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
				 
					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
					 return CommUtil.ExitCode(hError);	
		    	}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 
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
	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0",  req_body, resultJson);
	   
	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);

	}
 
	
	@ApiOperation(value = "착신지 정보 상태 변경" , notes = "착신지 정보 상태 변경")
	@RequestMapping(value = "/ktgateway/infe/api/v1/destination", method = RequestMethod.PUT , produces = "application/json")
	public ResponseEntity<String>   setDestination(@RequestBody @ApiParam(value = "", required = true) String req_body , HttpServletRequest request){
		JSONObject obj =new JSONObject();
		HashMap hError = new HashMap();
		String sReqIPAddr =CommUtil.getRequestIP(request);
		String JOB_ID= CommUtil.GUID(request);
		
		HashMap jobInfo = new HashMap();
		jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/api/v1/destination");
		jobInfo.put("JOB_TY","PUT");
		jobInfo.put("REQ_IP",sReqIPAddr);
		jobInfo.put("JOB_STATUS","FAIL");
		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
		
		

		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
		jobInfo.put("REQ_IPADDR",sReqIPAddr);
		
		//String other_params[] =new String[]{"option","destId","destName","status"};
		String other_params[] =new String[]{"destId"};
		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK", "SUB_NB_CHK",other_params);
		   
		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
				resultJson="{\"result\":0}";
			}
			else {
				
		    	String GET_DESTINATION_URL= KTCommHeader.KT_ACTIVE_SERVER_IP+PRE_FIX+"/infe/api/v1/destination";
		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
		     
		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
		    	
		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
			 
			hError.put("result", 9900);
			hError.put("result_msg", "Internel Error");
			jobInfo.put("JOB_STATUS","ERROR");
			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
		    return CommUtil.ExitCode(hError);	
		}
		jobInfo.put("JOB_STATUS","SUSS");
		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" , "0", req_body, resultJson);
	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);

	}
	
	
	@ApiOperation(value = "착신지명 변경" , notes = "착신지명 변경")
	@RequestMapping(value = "/ktgateway/infe/api/v1/destinationName", method = RequestMethod.PUT , produces = "application/json")
	public ResponseEntity<String>   setDestinationName(@RequestBody @ApiParam(value = "", required = true) String req_body , HttpServletRequest request){
		JSONObject obj =new JSONObject();
		HashMap hError = new HashMap();
		String sReqIPAddr =CommUtil.getRequestIP(request);
		String JOB_ID= CommUtil.GUID(request);
		
		HashMap jobInfo = new HashMap();
		jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/api/v1/destinationName");
		jobInfo.put("JOB_TY","PUT");
		jobInfo.put("REQ_IP",sReqIPAddr);
		jobInfo.put("JOB_STATUS","FAIL");
		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
		
		

		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
		jobInfo.put("REQ_IPADDR",sReqIPAddr);
		
		//String other_params[] =new String[]{"option","destId","destName","status"};
		String other_params[] =new String[]{"destId","destName"};
		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK", "SUB_NB_CHK",other_params);
		   
		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
				resultJson="{\"result\":0}";
			}
			else {
				
		    	String GET_DESTINATION_URL= KTCommHeader.KT_ACTIVE_SERVER_IP+PRE_FIX+"/infe/api/v1/destinationName";
		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
		     
		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
		    	
		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
			 
			hError.put("result", 9900);
			hError.put("result_msg", "Internel Error");
			jobInfo.put("JOB_STATUS","ERROR");
			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
		    return CommUtil.ExitCode(hError);	
		}
		jobInfo.put("JOB_STATUS","SUSS");
		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" , "0", req_body, resultJson);
	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);

	}
	
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "착신지별 착신번호 조회 " , notes = "착신지별 착신번호 조회")
//	@RequestMapping(value = "/ktgateway/infe/api/v1/route", method = RequestMethod.POST , produces = "application/json")
//	public ResponseEntity<String>   getRoute(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap();
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/api/v1/route");
//		jobInfo.put("JOB_TY","POST");
//		jobInfo.put("REQ_IP",sReqIPAddr);
//		jobInfo.put("JOB_STATUS","FAIL");
//		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
//		
//		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
//		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
//		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
//		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
//		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
//		jobInfo.put("REQ_IPADDR",sReqIPAddr);
//		
//		
//		String other_params[] =new String[]{"destId"};
//		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK", "SUB_NB_CHK",other_params);
//		   
//		 
//		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
//			if("Y".equals(MOCK_YN)) {
//				 resultJson="{\"result\":0,\"listLength\":2,\"list\":[[\"\",\"\",\"\"],[\"\",\"\",\"\"]]}";
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/api/v1/route";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("POST",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
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
	
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "착신지별 착신번호 설정" , notes = "등록된 착신지별 정보 조회착신지별 착신번호 설정")
//	@RequestMapping(value = "/ktgateway/infe/api/v1/route", method =  RequestMethod.PUT , produces = "application/json")
//	public ResponseEntity<String>   setRoute(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap(); 
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/api/v1/route");
//		jobInfo.put("JOB_TY","PUT");
//		jobInfo.put("REQ_IP",sReqIPAddr);
//		jobInfo.put("JOB_STATUS","FAIL");
//		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
//		
//		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
//		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
//		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
//		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
//		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
//		jobInfo.put("REQ_IPADDR",sReqIPAddr);
//		
//		String other_params[] =new String[]{"option","destId","listLength","list"};
//		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK","SUB_NB_CHK", other_params);
//		    	
//		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
//			if("Y".equals(MOCK_YN)) {
//				 resultJson="{\"result\":0}";
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/api/v1/route";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,  ERR_CD,tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
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
//	   
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//
//	}
 
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "착신지별 발신지역 정보 조회" , notes = "등록된 착신지별 정보 조회")
//	@RequestMapping(value = "/ktgateway/infe/api/v1/area", method = { RequestMethod.POST} , produces = "application/json")
//	public ResponseEntity<String>   getArea(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap(); 
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/api/v1/area");
//		jobInfo.put("JOB_TY","POST");
//		jobInfo.put("REQ_IP",sReqIPAddr);
//		jobInfo.put("JOB_STATUS","FAIL");
//		jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
//		 
//		jobInfo.put("LICENSE_KEY",LICENSE_KEY);
//		jobInfo.put("PRODUCT_KEY",PRODUCT_KEY);
//		jobInfo.put("SERVER_IP_ADDR",SERVER_IP_ADDR);
//		jobInfo.put("ACC_IP_ADDRESS",ACC_IP_ADDRESS);
//		jobInfo.put("ACC_IP_YN",ACC_IP_YN);
//		jobInfo.put("REQ_IPADDR",sReqIPAddr);
//		
//		
//		String other_params[] =new String[]{ "destId" };
//		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK","SUB_NB_CHK", other_params);
//		    	 
//		
//		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
//			if("Y".equals(MOCK_YN)) {
//				resultJson="{\"result\":0,\"listLength\":2,\"list\":[[\"\",\"\",\"\"],[\"\",\"\",\"\"]]}";
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/api/v1/area";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("POST",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
//			
//		 	//logger.info(resultMap.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			 
//			hError.put("result", 9900);
//			hError.put("result_msg", "Internel Error");
//			jobInfo.put("JOB_STATUS","ERROR");
//			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
//			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
//		    return CommUtil.ExitCode(hError);	
//		}
//		
//		jobInfo.put("JOB_STATUS","SUSS");
//		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
//	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS", "0" ,  req_body, resultJson);
//	   
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson); 
//
//	}
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "착신지별 발신지역 정보 설정" , notes = "착신지별 발신지역 정보 설정")
//	@RequestMapping(value = "/ktgateway/infe/api/v1/area", method = RequestMethod.PUT , produces = "application/json")
//	public ResponseEntity<String>   setArea(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap(); 
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/api/v1/area");
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
//		String other_params[] =new String[]{"destId","listLength","list"};
//		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK","SUB_NB_CHK", other_params);
//		    	 
//		
//		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
//		String resultJson="";
//		
//		try {
//			if("Y".equals(MOCK_YN)) {
//				resultJson="{\"result\":0}";
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/api/v1/area";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode)|| "-8".equals(StatusCode)) {
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
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
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
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" , ERR_CD, req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
//		    return CommUtil.ExitCode(hError);	
//		}
//		jobInfo.put("JOB_STATUS","SUSS");
//		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
//	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0",  req_body, resultJson);
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//
//	}
	
	//2023.12.1 V8 에 삭제 
	//@ApiOperation(value = "착신지별 Route 정보를 호처리 테이블에 설정" , notes = "착신지별 Route 정보를 호처리 테이블에 설정")
//	@RequestMapping(value = "/ktgateway/infe/api/v1/call", method = { RequestMethod.PUT} , produces = "application/json")
//	public ResponseEntity<String>   setCall(@RequestBody @ApiParam(value = "", required = true) String req_body ,HttpServletRequest request){
//		JSONObject obj =new JSONObject();
//		HashMap hError = new HashMap(); 
//		String sReqIPAddr =CommUtil.getRequestIP(request);
//		String JOB_ID= CommUtil.GUID(request);
//		HashMap jobInfo = new HashMap();
//		jobInfo.put("JOB_URL","/infe/api/v1/call");
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
//		String other_params[] =new String[]{"area","destId"};
//		List listChkParams = KTCommHeader.getParamCheck("TOKEN_CHK","SUB_NB_CHK", other_params);
//		    	  
//		HashMap hCommCheck= CommUtil.getCommmCheck(true,request,logger,jobInfo,req_body,listChkParams) ;
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
//		String resultJson="";
//		
//		try {
//			if("Y".equals(MOCK_YN)) {
//				resultJson="{\"result\":0}";
//			}
//			else {
//		    	String GET_DESTINATION_URL= KT_URL+PRE_FIX+"/infe/api/v1/call";
//		    	HashMap resultMap = HttpComm.HTTP_COMM_URL("PUT",GET_DESTINATION_URL, req_body,CLIENT_MODE,SSL_IGNORE,logger) ;
//		     
//		    	String StatusCode=  CommUtil.NvlToBlank(resultMap.get("StatusCode"));
//		    	
//		    	if("200".equals(StatusCode) || "-8".equals(StatusCode)) {
//		    		resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
//		    		req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//		    	}
//		    	else {
//		    		hError.put("result", CommUtil.parseInt(StatusCode));
//					hError.put("result_msg", "KT Server Connection Error :"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					jobInfo.put("JOB_STATUS","ERROR");
//					String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//					String  tmp_req_body = CommUtil.NvlToBlank(resultMap.get("body")) ;
//					logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg"))));
//					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  tmp_req_body, CommUtil.ExitCodeStr(hError)+ "\n"+ CommUtil.NvlToBlank(resultMap.get("ErrMsg")));
//					 return CommUtil.ExitCode(hError);	
//		    	}
//			}
//		 	 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			 
//			hError.put("result", 9900);
//			hError.put("result_msg", "Internel Error");
//			jobInfo.put("JOB_STATUS","ERROR");
//			logger.error(CommUtil.geRequestLog(request,CommUtil.ExitCodeStr(hError)));
//			String ERR_CD=CommUtil.NvlToBlank(hError.get("result"));
//			 
//			CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,ERR_CD,  req_body, CommUtil.ExitCodeStr(hError)+ "\n"+data.toString());
//		    return CommUtil.ExitCode(hError);	
//		}
//		jobInfo.put("JOB_STATUS","SUSS");
//		logger.info(CommUtil.geRequestLog(request,jobInfo.toString())+"\n"+resultJson);
//	    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0" ,  req_body, resultJson);
//	    return ResponseEntity.status(HttpStatus.OK).body(resultJson);
//
//	}
 
  
}