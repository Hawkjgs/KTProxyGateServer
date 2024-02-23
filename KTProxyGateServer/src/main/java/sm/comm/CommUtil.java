package sm.comm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper; 
 
public class CommUtil {
	
	
	
	public static String CapDate(int iDay) {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
		
		String chkDate = SDF.format(calendar.getTime());	
		calendar.add(Calendar.DATE, -1 * iDay);
		chkDate = SDF.format(calendar.getTime());		
		
		return chkDate;

	}
	
	
	public static String StrFormatDateToStr(String dateStr , String Format) {
		try {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		    Date date = formatter.parse(dateStr);
		    
		    SimpleDateFormat formatter_Dest = new SimpleDateFormat(Format);
		    String dateFmtStr = formatter_Dest.format(date);
		    return dateFmtStr;
	//	    System.out.println(date);
		} catch (ParseException ex) {
		    ex.printStackTrace();
		}
		return dateStr;
	}
	
	public static Map<String, Object> jsonToMap(String json) 
	 {
		 	Map retMap = new HashMap();
		 	ObjectMapper mapper = new ObjectMapper();
	        try {

	        	retMap= mapper.readValue(json, Map.class);

	        } catch (IOException e) { e.printStackTrace(); 
	        }
	        return retMap;
	 }
	
	
	public static HashMap HTTP_GET_URL(String url)
 	 {
  	String resData="";
  	HashMap res = new HashMap();
  	System.out.println(url);
  	String body="";
	 	try {
			HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
			HttpGet getRequest = new HttpGet(url); //GET 메소드 URL 생성
			getRequest.addHeader("accept","application/json"); //KEY 입력
			HttpResponse response = client.execute(getRequest);

			//Response 출력
			
			if (response.getStatusLine().getStatusCode() == 200) {
				ResponseHandler<String> handler = new BasicResponseHandler();
				resData = handler.handleResponse(response);
				res.put("HTTP_CODE", response.getStatusLine().getStatusCode());
				res.put("Data", resData);
			} else {
				res.put("HTTP_CODE", response.getStatusLine().getStatusCode());
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				res.put("Data", responseString);
			}
			 

		} catch (Exception e){
			//System.err.println(e.toString());
		}

 		 return res;
 	 }
  
	
	
	public static  String  getExcetionToStr(HttpServletRequest request,Exception e ,String msg) {
		HashMap hHeader = new HashMap();
		
		Enumeration<String> header = request.getHeaderNames(); 
		
		Enumeration<String> parameters = request.getParameterNames(); 
		 
	
		StringBuffer buf = new StringBuffer();
		
		buf.append("\n==========================");
		buf.append("\nURL :"+request.getRequestURI());
		buf.append("\nHeader Infomation");
		while (header.hasMoreElements()) {
				String HeaderName = (String) header.nextElement();
				String HeaderValue =  NvlToBlank(request.getHeader(HeaderName));
				buf.append("\n"+HeaderName + " : " +  HeaderValue);
		}
		
		buf.append("\nParmaters Infomation");
		while (parameters.hasMoreElements()) {
				String ParamName = (String) parameters.nextElement();
				String ParamValue =  NvlToBlank(request.getParameter(ParamName));
				if("pwd".equals(ParamName)) buf.append("\n"+ParamName + " : *****");
				else buf.append("\n"+ParamName + " : " +  ParamValue);
				
		}
		buf.append("\n==========================");
		buf.append("\n"+msg);
		buf.append("\n==========================");
		
		StringWriter sw = new StringWriter();
	 	e.printStackTrace(new PrintWriter(sw));
	 	String exceptionAsString = sw.toString();
	 	buf.append("\n"+exceptionAsString);
		return buf.toString();
	}
	 
	 public static float ParserFloat(String sStr) {
		 float dfval=0;
		 try{
			 dfval = Float.parseFloat(sStr);
		 }
		 catch (NumberFormatException e) {
			 dfval=0.0f;
		 }
		 
		 return dfval;
		 
	 }
	 
	
	public static  boolean  isDateVaildCheck(String  checkDate){
     try{
          SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyyMMdd");

          dateFormat.setLenient(false);
          dateFormat.parse(checkDate);
          return  true;

       }catch (ParseException  e){
         return  false;
       }

	  }
	
	
	public static boolean isEmailVaildCheck(String email) {
		String email_pattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
		if(Pattern.matches(email_pattern, email)) {
		    return true;
		} else {            
		    return false;
		}
	}
	
	
	public static boolean isPasswdVaildCheck(String pwd) {
		 // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
		Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
		Matcher passMatcher1 = passPattern1.matcher(pwd);
		
		if(!passMatcher1.find()){
		    return false;
		 }
		return true;
		  
		
//		String pwd_pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
//		if(Pattern.matches(pwd_pattern, pwd)) {
//		    return true;
//		} else {            
//		    return false;
//		}
	}
	
	 
	public static String decode(String value) {
	    try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return "";
	}
	
		
	public static HashMap isVaildCheckSessionKey(HashMap hHeader,HashMap mUser)
	{
		HashMap hPara = new HashMap();
		HashMap hRet = new HashMap();
		String msg;
		try {
	    	 
	    	hPara.put("USER_SEQ", CommUtil.NvlToBlank(hHeader.get("USER_SEQ")));
	    	String LANG = CommUtil.NvlToBlank(hHeader.get("LANG"));
			if(mUser ==null ) {
				if(LANG.equals("KO")) msg="회원 정보가 없습니다.";
				else msg="No Member Information";
				hRet.put("result", CommUtil.setResult(402,msg));
				hRet.put("isVaild", false);
				
			}
			else {
				JSONObject notice =new JSONObject();
				String Authorization=CommUtil.NvlToBlank(hHeader.get("Authorization"));
				
				String UUID=CommUtil.NvlToBlank(mUser.get("SEQ"));
				String SessionKey=CommUtil.NvlToBlank(mUser.get("SESSION_KEY"));
			 	
				if(!SessionKey.equals(Authorization)) {
					if(LANG.equals("KO")) msg="중복 로그인 (로그 아웃처리됨) ";
					else msg="Duplicate login (logged out)";
					hRet.put("result", CommUtil.setResult(997,msg));
					hRet.put("isVaild", false);
				}
				else {
					hRet.put("isVaild", true);
				}
			}
			 
		} catch (Exception e) {
			hRet.put("result",  CommUtil.setResult(996,"Internel Error"));
			hRet.put("isVaild", false);
			e.printStackTrace();
		}
		
		
		return hRet;
	}
	 
	
	
	public static  HashMap getAuthorization(MultipartHttpServletRequest request) {
		HashMap hHeader = new HashMap();
		
		hHeader.put("Authorization", NvlToBlank(request.getHeader("Authorization")));
		hHeader.put("userAgent", NvlToBlank(request.getHeader("User-Agent")));
		hHeader.put("USER_SEQ", NvlToBlank(request.getHeader("USER_SEQ")));
		hHeader.put("DEVICE_TY", NvlToBlank(request.getHeader("DEVICE_TY")));
		hHeader.put("GUID", NvlToBlank(request.getHeader("GUID")));
		hHeader.put("LANG", NvlToBlank(request.getHeader("LANG")));
		hHeader.put("VER", NvlToBlank(request.getHeader("VER")));
		
		
		String REQ_IP =  NvlToBlank(request.getHeader("REQ_IP"));
		if(!"".equals(REQ_IP)) hHeader.put("ACCESS_IP", REQ_IP);
		else {
			String ACCESS_IP=NvlToBlank(request.getHeader("X-Forwarded-For"));
			if(!"".equals(ACCESS_IP)) hHeader.put("ACCESS_IP", ACCESS_IP);
			else hHeader.put("ACCESS_IP", request.getRemoteAddr());
//			hHeader.put("ACCESS_IP", request.getRemoteAddr());
		}
		return hHeader;
	}
	
	public static  String getRequestIP(HttpServletRequest request) {
		if(request ==null) return "DEAMON";
		String ACCESS_IP=NvlToBlank(request.getHeader("X-Forwarded-For"));
		if(!"".equals(ACCESS_IP)) return ACCESS_IP;
		else return request.getRemoteAddr();
	}
	
	
	/**
	 * 서버 통신전 기본적인 환경을 체크 한다.
	 * @param request
	 * @param logger
	 * @param jobInfo
	 * @param req_body
	 * @return
	 */
	
  public static HashMap   getCommmCheck(boolean bTokenChk,HttpServletRequest request,Logger logger,
		 HashMap jobInfo ,String req_body,List listChkParams) {
	  
	  String LICENSE_KEY="", PRODUCT_KEY="", SERVER_IP_ADDR="",ACC_IP_YN="";
	  String ACC_IP_ADDRESS="", REQ_IPADDR="";
	  
	  HashMap hCheckMap = new HashMap();
	   ACC_IP_YN = NvlToBlank(jobInfo.get("ACC_IP_YN")) ;
	   LICENSE_KEY = NvlToBlank(jobInfo.get("LICENSE_KEY")) ;
	   PRODUCT_KEY = NvlToBlank(jobInfo.get("PRODUCT_KEY")) ;
	   SERVER_IP_ADDR = NvlToBlank(jobInfo.get("SERVER_IP_ADDR")) ;
	   ACC_IP_ADDRESS = NvlToBlank(jobInfo.get("ACC_IP_ADDRESS")) ;
	   ACC_IP_YN = NvlToBlank(jobInfo.get("ACC_IP_YN")) ;
	   REQ_IPADDR = NvlToBlank(jobInfo.get("REQ_IPADDR")) ;
		
	   
	   if("N".equals(ACC_IP_YN)) ;
	   else if(ACC_IP_ADDRESS.indexOf(REQ_IPADDR) ==-1) {
		   hCheckMap.put("result", 9999);
		   hCheckMap.put("ERR_TY", "FAIL");
		   hCheckMap.put("result_msg", "This is not an approved IP :"+REQ_IPADDR);
		   return hCheckMap;
		   
	   }
		 
		if(SMLicense.LICENSE == false ) {
			String keyRead= SFileUtil.readFile(LICENSE_KEY) ;
			StringBuffer buf = new StringBuffer();
			SMLicense.LICENSE=SMLicense.MakeLicCheck(PRODUCT_KEY, keyRead, SERVER_IP_ADDR);
			if (!SMLicense.LICENSE) {
				hCheckMap.put("result", 9994);
				hCheckMap.put("ERR_TY", "FAIL");
				hCheckMap.put("result_msg", "No Server License");
				return hCheckMap;
			}
		}
		
		if(bTokenChk) {
			if("".equals(KTCommHeader.TOKEN)) {
				hCheckMap.put("result", 9993);
				hCheckMap.put("ERR_TY", "FAIL");
				
				String mgs = "KT Token reception error";
				if("".equals(KTCommHeader.TOKEN_COMM_ERR_MSG)) {
					hCheckMap.put("result_msg",mgs + "("+KTCommHeader.TOKEN_COMM_ERR_MSG+")");
				}
				else {
					hCheckMap.put("result_msg",mgs);
				}
				
				
				return hCheckMap;
			} 
			 
		}	
			
		HashMap hRet = JSONUtils.stringToJSON(req_body);
		if(!"0".equals( CommUtil.NvlToBlank(hRet.get("result"))) ) {
			hCheckMap.put("result", hRet.get("result"));
			hCheckMap.put("ERR_TY", "FAIL");
			hCheckMap.put("result_msg",hRet.get("result_msg"));
			return hCheckMap;
		}
	 
		HashMap data = (HashMap) hRet.get("data");
		
		
		HashMap hData = null ;
		String  inKey ="";
	 
		String  inDefVal="";
		String  sPara="";
		
		for ( int i=0;i<listChkParams.size();i++) {
			hData =  (HashMap)listChkParams.get(i) ;
			inKey =  CommUtil.NvlToBlank(hData.get("KEY"));
			sPara =  CommUtil.NvlToBlank(data.get(inKey));
			inDefVal =  CommUtil.NvlToBlank(hData.get("DEFVAL"));
			
//			logger.info(inKey +": "+inDefVal + "==>"+inDefVal);
//			sPara= CommUtil.NvlToBlank(inKey);
			if("".equals(sPara)) {
				hCheckMap.put("result", 9997);
			    hCheckMap.put("ERR_TY", "FAIL");
				hCheckMap.put("result_msg",  "Input Data Miss :(Field:"+inKey+")");
				return hCheckMap;
			}
			else if(!"".equals(inDefVal)) {
				if(!inDefVal.equals(sPara)) {
					hCheckMap.put("result", 9995);
					hCheckMap.put("result_msg",  "Default  Data Miss :(Field:"+inKey+"="+inDefVal+")");
					return hCheckMap;
				}
			
			}
		}
		return hRet;
	}

  
  
  //logger.error(CommUtil.geRequestLog(request,"This is not an approved IP. : "+ sReqIPAddr));
  
	
	public static boolean  getAccessIPCheck(String req_ip, String acc_ip,String ACC_IP_YN) {
		
		if("N".equals(ACC_IP_YN)) return true;
		if(acc_ip.indexOf(req_ip) >=0) return true;
		else return false; 
	}
	
	
	public static  String  geRequestLog(HttpServletRequest request,String msg) {
		HashMap hHeader = new HashMap();
		
		Enumeration<String> header = request.getHeaderNames(); 
		
		Enumeration<String> parameters = request.getParameterNames(); 
		 
	
		StringBuffer buf = new StringBuffer();
		
		buf.append("\n==============================================================================");
		buf.append("\nURI :"+request.getRequestURI());
		buf.append("\nURL :"+request.getRequestURL());
		buf.append("\nMethod :"+request.getMethod());
		buf.append("\nRemote Addr :"+request.getRemoteAddr());
		buf.append("\nX-Forwarded-For :"+NvlToBlank(request.getHeader("X-Forwarded-For")));
		buf.append("\n"+msg);
		buf.append("\n==============================================================================");
		return buf.toString();
	}
	 
	
	
	public static  ResponseEntity<String> ExitAuthorization(String ip) {
		JSONObject obj =new JSONObject();
	    obj.put("result", 9999);
	    obj.put("result_msg", "This is not an approved IP :"+ip);
	    
	    return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
	     
	}
	
	public static  ResponseEntity<String> NoLicense() {
		JSONObject obj =new JSONObject();
	    obj.put("result", 9994);
	    obj.put("result_msg", "No Server License");
	    
	    return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
	     
	}
	
	public static  ResponseEntity<String> ExitCode(HashMap ret) {
		JSONObject obj =new JSONObject();
	    obj.put("result", ret.get("result"));
	    obj.put("result_msg",ret.get("result_msg"));
	    
	    return ResponseEntity.status(HttpStatus.OK).body(obj.toString());
	     
	}
	
	public static  String ExitCodeStr(HashMap ret) {
		JSONObject obj =new JSONObject();
	    obj.put("result", ret.get("result"));
	    obj.put("result_msg",ret.get("result_msg"));
	    
	    return obj.toString();
	     
	}
 
 

	
	
    
	public static JSONObject setResult(int iCode, String code_msg ) {
		  JSONObject result = new JSONObject();
		  result.put("code", iCode);
		  result.put("code_msg", code_msg);
		  return result;
		    
	}
	
	public static  String getUniqueFileName() {
   	 return  UUID.randomUUID().toString();
   	}
	
	
	public static  String GEN_UUID(String seq) {
		
		  UUID uuid = UUID.randomUUID();
		  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
		  String str = "USR_"+seq+"_"+today("yyyyMMddHHmm")+ Long.toString(l, 8);
		  return  Seed256.Encrypt(str);
	   	}
	
	public static  boolean CHK_UUID(String key) {
		
		String str;
		try {
			str = Seed256.Decrypt(key);
			
			return true;
		} catch (Exception e) {
			
			 return false;
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		 
				 
	   	}
	
	
	public static  String UUID() {
	   	 return  UUID.randomUUID().toString();
	  }
    
 	
	public  static Map <String , String> getMapRandomFile(String dir,  String szFileName)
	 {
		 Map map = new HashMap();
		 SimpleDateFormat df = new SimpleDateFormat ("yyyyMMdd_HHmmss_SSS");
        String jobid = df.format(new java.util.Date());
        String fileNames[]= szFileName.split("\\.");
       if(fileNames.length <= 0) return map;
       boolean bloop =true;
       for(int i=1;i<100000 && bloop;i++) {
       	String rFile =jobid+"_"+getUniqueFileName()+"_"+i+"."+fileNames[fileNames.length-1];
       	szFileName =dir+File.separator+rFile;
       
       	File file = new File(szFileName);
       	if(file.exists()){
       		
       	}
       	else {
       		 map.put("EXT", fileNames[fileNames.length-1]);
       		 map.put("NAME",rFile);
        		 return map;
       	}
       }
       return map;
    }
   
	
	public static HashMap fileUpload(final MultipartHttpServletRequest request,Map<String,Object> searchMap, String dir)  {
		HashMap finfo  = new HashMap();
		Map m = null;
		String fileNm="";
		String fileExt="";
		String mFileName="IMG_NM";
		try {
			
			if(!request.getFile(mFileName).isEmpty()){
				
				String uloadPath =dir;
				m = getMapRandomFile(uloadPath, request.getFile(mFileName).getOriginalFilename());
				fileExt=""+m.get("EXT");
				fileNm=""+m.get("NAME");
				finfo.put("FILE_REAL_NM", request.getFile(mFileName).getOriginalFilename());
				finfo.put("FILE_NM", fileNm);
				request.getFile(mFileName).transferTo(new File(uloadPath+File.separator+fileNm));
			}
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	    	return finfo;
    }
	
	public static HashMap fileUpload(String mFileName ,final MultipartHttpServletRequest request,Map<String,Object> searchMap, String dir)  {
		HashMap finfo  = new HashMap();
		Map m = null;
		String fileNm="";
		String fileExt="";
		try {
			
			if(!request.getFile(mFileName).isEmpty()){
				
				String uloadPath =dir;
				m = getMapRandomFile(uloadPath, request.getFile(mFileName).getOriginalFilename());
				fileExt=""+m.get("EXT");
				fileNm=""+m.get("NAME");
				finfo.put("FILE_REAL_NM", request.getFile(mFileName).getOriginalFilename());
				finfo.put("FILE_NM", fileNm);
				request.getFile(mFileName).transferTo(new File(uloadPath+File.separator+fileNm));
			}
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	    	return finfo;
    } 
	 
	public static HashMap SystemLogBean(String gubun,String content,String status,String url,HttpServletRequest request)
    {
    	
    	HashMap hLog = new HashMap();
		
		if(request==null) {
			hLog.put("REG_NM", "system");
			hLog.put("REG_ID", "system");
			hLog.put("REG_IP", "127.0.0.1");
		}
		else  {
			
			HttpSession session = request.getSession();
			Map loginInfo =(HashMap) session.getAttribute("AdminUserInfo");
			if(loginInfo!=null) {
				hLog.put("REG_NM", NvlToBlank(loginInfo.get("MEM_NM")));
				hLog.put("REG_ID", NvlToBlank(loginInfo.get("MEM_ID")));
				hLog.put("REG_IP", request.getRemoteAddr());
			}
			else {
				hLog.put("REG_NM", "Guest");
				hLog.put("REG_ID", "Guest");
				hLog.put("REG_IP", request.getRemoteAddr());
			}
		}
		hLog.put("GUBUN", gubun);
		hLog.put("STATUS", status);
		hLog.put("CONTENT", content);
		
		Enumeration params = request.getParameterNames();
		String s_para = "";
		while(params.hasMoreElements()) {
		  String name = (String) params.nextElement();
		  s_para=s_para+"["+ name + " : " + request.getParameter(name)+"]"; 
		}
		
		if(!"/login_proc".equals(url)) {
			hLog.put("REQUEST_BODY", s_para);
		}
		
		
		hLog.put("URL", url);
		return hLog;
	}
	
	
	public static HashMap SystemChkLogBean(String gubun,String content,String status,String url,HttpServletRequest request)
    {
    	
    	HashMap hLog = new HashMap();
		
		if(request==null) {
			hLog.put("REG_NM", "system");
			hLog.put("REG_ID", "system");
			hLog.put("REG_IP", "127.0.0.1");
		}
		else  {
			
			HttpSession session = request.getSession();
			Map loginInfo =(HashMap) session.getAttribute("CHKUserInfo");
			if(loginInfo!=null) {
				hLog.put("REG_NM", NvlToBlank(loginInfo.get("CHK_MEM_NM")));
				hLog.put("REG_ID", NvlToBlank(loginInfo.get("CHK_MEM_ID")));
				hLog.put("REG_IP", request.getRemoteAddr());
			}
			else {
				hLog.put("REG_NM", "Guest");
				hLog.put("REG_ID", "Guest");
				hLog.put("REG_IP", request.getRemoteAddr());
			}
		}
		hLog.put("GUBUN", gubun);
		hLog.put("STATUS", status);
		hLog.put("CONTENT", content);
		
		Enumeration params = request.getParameterNames();
		String s_para = "";
		while(params.hasMoreElements()) {
		  String name = (String) params.nextElement();
		  s_para=s_para+"["+ name + " : " + request.getParameter(name)+"]"; 
		}
		
		if(!"/login_proc".equals(url)) {
			hLog.put("REQUEST_BODY", s_para);
		}
		
		
		hLog.put("URL", url);
		return hLog;
	}
	
	
	 
	public static String GUID(HttpServletRequest request) {
		String guid ="";
		
		
		String ipAddress = getRequestIP(request);
		
		if(request == null) {
			ipAddress = "127.0.0.1";
		}
		else ipAddress = getRequestIP(request);
		
		ipAddress=ipAddress.replaceAll(":", "-");
		String fmt ="yyyyMMddHHmmss-SSS";
    	SimpleDateFormat df3 = new SimpleDateFormat (fmt);
		String c_time = df3.format(new java.util.Date());
	
		guid = ipAddress +"_"+c_time;//+"_"+uuid;
		 
		return guid;
	}
	
	
	public static String JOB_WRITE(HashMap jobInfo,String Path,String jobId,String status
			,String ERR_CD, String reqJson , String resJson) {
		String guid ="";
		String uuid = UUID.randomUUID().toString();
		
		
		HashMap hRet = JSONUtils.stringToJSON(resJson);
		 
		if("SUSS".equals(status)) {
			
			int iResult =  (int) hRet.get("result");
			 
			 if(iResult==0) {
				 HashMap m = (HashMap) hRet.get("data");
				 if(m!=null) {
					 iResult =  CommUtil.parseInt(CommUtil.NvlToBlank(""+m.get("result"))); 
				 }
			 }
			 
			 if(iResult!=0) ERR_CD=""+ iResult;
			 
		}
		 
		
		String fileNm =jobId +"_"+status+".json";
		String toYear =  today("yyyy");
		String toMonth =  today("MM");
		String toDay =  today("yyyyMMdd");
		
		SFileUtil.makeDir(Path + File.separator + toYear);
		
		SFileUtil.makeDir(Path + File.separator + toYear + File.separator +toMonth);
		SFileUtil.makeDir(Path + File.separator + toYear + File.separator +toMonth + File.separator+ toDay);
		
		
		String JOB_URL= NvlToBlank(jobInfo.get("JOB_URL"));
		String JOB_TY= NvlToBlank(jobInfo.get("JOB_TY"));
		String REQ_IP= NvlToBlank(jobInfo.get("REQ_IP"));
		String JOB_STATUS= NvlToBlank(jobInfo.get("JOB_STATUS"));
		String JOB_DT= NvlToBlank(jobInfo.get("JOB_DT"));
		
		
		
		String wData  = "JOB_ID="+jobId+"|JOB_URL="+JOB_URL+"|JOB_TY="+JOB_TY+"|REQ_IP="+REQ_IP+"|JOB_STATUS="+JOB_STATUS+"|JOB_DT="+JOB_DT+"|ERR_CD="+ERR_CD;
		wData += "\nRequest Data:\n";
		wData += reqJson +"\n";
		wData +=  "============================================================\n";
		wData += "\nResponse Data:\n";
		wData += resJson +"\n";
		wData +=  "============================================================\n";
		String FullFileNm = Path + File.separator + toYear + File.separator +toMonth + File.separator+
				toDay+File.separator+ fileNm;
		SFileUtil.writeFile(FullFileNm, wData, "");
		 
		return guid;
	}
	
	
	public static String NvlToBlank(Object o) {
		if(o== null) return "";
		///if("null".equals(o)) return "";
		return o.toString().trim().replaceAll("\u200B", "");
	}
	
	public static String NvlToBlank(Object o,String defStr) {
		if(o== null) return defStr;
		return o.toString().trim().replaceAll("\u200B", "");
	}
	 public static int ParserInt(String sInt,int idef) {
		 int iret=0;
		 try{
			 iret = Integer.parseInt(sInt);
		 }
		 catch (NumberFormatException e) {
			 	iret=idef;
		 }
		 
		 return iret;
		 
	 }
	 public static int ParserInt(String sInt) {
		 int iret=0;
		 try{
			 iret = Integer.parseInt(sInt);
		 }
		 catch (NumberFormatException e) {
			 	iret=0;
		 }
		 
		 return iret;
		 
	 }
	 
	 public static int parseInt(Object s) {
		if (s==null) return 0;
		int number = 0;
			try {
				 number = Integer.parseInt(""+s);
	        } catch (NumberFormatException exception) {
	        	number=0;
	        }
			return number;
		}
	 
	 public static int parseIntDef(Object s, int idef) {
			if (s==null) return idef;
			int number = idef;
			try {
				 number = Integer.parseInt(""+s);
	        } catch (NumberFormatException exception) {
	        	number=idef;
	        }
			return number;
	 } 
	   
	 public static float parseFloat(Object s) {
		if (s==null) return 0;
		if (s instanceof String ) {
			float number = 0;
			try {
				 number = Float.parseFloat((String) s);
	        } catch (NumberFormatException exception) {
	        	number=0;
	        }
			return number;
		}
		return 0;
	}
	 
	 public static Map<String, Object> paginationRendererMap( Map<String, Object> searchMap ) {
		 	String pageUnit=NvlToBlank(searchMap.get("pageUnit"));
	    	
	    	int ipageUnit = ParserInt(pageUnit,4);
	    	
	    	String naviSize=NvlToBlank(searchMap.get("naviSize"));
	    	int inaviSize = ParserInt(naviSize,5);
	    	
	    	PaginationInfo paginationInfo = new PaginationInfo();
	    	
	    	int irecordCountPerPage=0;
	    	int currentPageNo;
	    	if(searchMap.get("pageIndex") == null || "".equals(searchMap.get("pageIndex"))){
	    		currentPageNo = 1;
	    		irecordCountPerPage=ipageUnit;
	    	}else{
	    		currentPageNo = Integer.valueOf((NvlToBlank(searchMap.get("pageIndex"))));
	    		irecordCountPerPage=currentPageNo*ipageUnit;
	    	}

	    	paginationInfo.setCurrentPageNo(currentPageNo);
	    	paginationInfo.setRecordCountPerPage(ipageUnit);
	    	paginationInfo.setPageSize(inaviSize);
	    	    	
	    	searchMap.put("firstIndex", paginationInfo.getFirstRecordIndex());
	    	searchMap.put("lastIndex", paginationInfo.getLastRecordIndex());
	    	//searchMap.put("recordCountPerPage", paginationInfo.getRecordCountPerPage());
	    	searchMap.put("recordCountPerPage", ipageUnit);
	    	return searchMap;
	    	
		 
	 }
	 
	 public static HashMap  paginationRenderer( Map<String, Object> searchMap , int iTotalPageCount) {
	    	
		 	HashMap retMap = new HashMap();
		 	String pageUnit=NvlToBlank(searchMap.get("pageUnit"));
	    	int ipageUnit = ParserInt(pageUnit,4);
	    	
	    	String naviSize=NvlToBlank(searchMap.get("naviSize"));
	    	int inaviSize = ParserInt(naviSize,5);
	    	
	    	PaginationInfo paginationInfo = new PaginationInfo();
	    	
	    	int irecordCountPerPage=0;
	    	int currentPageNo;
	    	if(searchMap.get("pageIndex") == null || "".equals(searchMap.get("pageIndex"))){
	    		currentPageNo = 1;
	    		irecordCountPerPage=ipageUnit;
	    	}else{
	    		currentPageNo = Integer.valueOf((String) searchMap.get("pageIndex"));
	    		irecordCountPerPage=currentPageNo*ipageUnit;
	    	}

	    	paginationInfo.setCurrentPageNo(currentPageNo);
	    	paginationInfo.setRecordCountPerPage(ipageUnit);
	    	paginationInfo.setPageSize(inaviSize);
	    	    	
	    	retMap.put("firstIndex", paginationInfo.getFirstRecordIndex());
	    	retMap.put("lastIndex", paginationInfo.getLastRecordIndex());
	    	retMap.put("recordCountPerPage", ipageUnit);
	    	retMap.put("curPage", currentPageNo);
	    	retMap.put("PageUnit", ipageUnit);
	    	
	    	 
	    	int resultCnt =  iTotalPageCount;
	    	
 
	    	
	    	//page table row count
	    	paginationInfo.setTotalRecordCount(resultCnt);
	    	
	    	 
	    	retMap.put("resultCnt", resultCnt);	//총카운터
	    	return retMap;
	    }
	 
	 public static void paginationRenderer(ModelAndView model,  Map<String, Object> searchMap , List pageList , int iTotalPageCount) {
	    	
		 String pageUnit=NvlToBlank(searchMap.get("pageUnit"));
	    	int ipageUnit = ParserInt(pageUnit,10);
	    	
	    	String naviSize=NvlToBlank(searchMap.get("naviSize"));
	    	int inaviSize = ParserInt(naviSize,5);
	    	
	    	PaginationInfo paginationInfo = new PaginationInfo();
	    	
	    	int irecordCountPerPage=0;
	    	int currentPageNo;
	    	if(searchMap.get("pageIndex") == null || "".equals(searchMap.get("pageIndex"))){
	    		currentPageNo = 1;
	    		irecordCountPerPage=ipageUnit;
	    	}else{
	    		currentPageNo = Integer.valueOf((String) searchMap.get("pageIndex"));
	    		irecordCountPerPage=currentPageNo*ipageUnit;
	    	}

	    	paginationInfo.setCurrentPageNo(currentPageNo);
	    	paginationInfo.setRecordCountPerPage(ipageUnit);
	    	paginationInfo.setPageSize(inaviSize);
	    	    	
	    	searchMap.put("firstIndex", paginationInfo.getFirstRecordIndex());
	    	searchMap.put("lastIndex", paginationInfo.getLastRecordIndex());
	    	searchMap.put("recordCountPerPage", ipageUnit);
	    	
	    	 
	    	int resultCnt =  iTotalPageCount;
	    	
 
	    	
	    	//page table row count
	    	paginationInfo.setTotalRecordCount(resultCnt);
	    	
	    	searchMap.put("resultList",pageList);
	    	model.addObject("resultList", pageList);	//조회결과
	    	model.addObject("resultCnt", resultCnt);	//총카운터
	    	model.addObject("paginationInfo", paginationInfo); //페이지렌더링
	    	model.addObject("paginationCustInfo", MakePagination(paginationInfo,"li")); //페이지렌더링
	    	
	    	
	    	model.addObject("searchMap", searchMap); //검색조건
	    }
	 
	
	 public static String MakePagination(PaginationInfo pInfo,String tag)
	    {
	    	StringBuffer sPaging= new StringBuffer();
	    	
	    	int iCurrentPageNo=pInfo.getCurrentPageNo();
	    	int iRecordCountPerPage = pInfo.getRecordCountPerPage();
	    	int iPageSize= pInfo.getPageSize();
	    	int iTotalRecordCount= pInfo.getTotalRecordCount();
	    	int iCurrenBlk = (iCurrentPageNo / iRecordCountPerPage);
	    	int iTotalPageCount = ((iTotalRecordCount -1)/ iRecordCountPerPage )+1;
	    	int iTotalBlkCount = (iTotalRecordCount / iRecordCountPerPage) +1; 
	    	
	    	int iStartPage=(((iCurrentPageNo -1) / iPageSize)*iPageSize)  +1;
	    	int iEndPage=iStartPage;
	    	
	    	
	    	if(iStartPage > 1) {
	    		sPaging.append("<"+tag+" >");
	    		sPaging.append("<a href=\"#\" onclick=\"gotoPage(1);\">&lt;&lt;</a>");
	    		sPaging.append("</"+tag+">");
	    	}
	    	
	    	if( (iStartPage-iPageSize) >0) {
	    		sPaging.append("<"+tag+" >");
	    		sPaging.append("<a href=\"#\" onclick=\"gotoPage("+(iStartPage-iPageSize)+");\">&lt;</a>");
	    		sPaging.append("</"+tag+">");
	    	}
	    	
	    	
	    
	    	for(int i=iStartPage;i<(iStartPage+iPageSize) && (i<=iTotalPageCount);i++) {
	    		iEndPage++;
	    		sPaging.append("<"+tag+"");
	    		if(i==iCurrentPageNo) {
	    			sPaging.append(" class=\"active\"");
	    		}
	    		sPaging.append(">");
	    		sPaging.append("<a href=\"#\" onclick=\"gotoPage("+i+");\">"+(i)+"</a>");
	    		sPaging.append("</"+tag+">");
	    	}
	    
	    	
	    	if(iEndPage <= iTotalPageCount) {
	    		sPaging.append("<"+tag+">");
	    		sPaging.append("<a href=\"#\" onclick=\"gotoPage("+(iEndPage)+");\">&gt;</a>");
	    		sPaging.append("</"+tag+">");
	    	}
	    	
	    	if(iEndPage <= iTotalPageCount) {
	    		sPaging.append("<"+tag+">");
	    		sPaging.append("<a href=\"#\" onclick=\"gotoPage("+iTotalPageCount+");\">&gt;&gt;</a>");
	    		sPaging.append("</"+tag+">");
	    	}
	    	 
	    	return sPaging.toString();
	    }
	    
	 
	 
	public static String renderPagination(int iCurPage , int iPageSize,  int iTotalCnt) {
		StringBuffer str = new StringBuffer();
		int iTotalBlk= (iTotalCnt + iPageSize -1)/ iPageSize ;
		
		int iCurBlk  = ((iCurPage - 1 ) / 5 ) *5;
		String acive="active";
		if( iCurBlk > 1) {
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage(1);'>&lt;&lt;</a>\n");
            str.append(" </li>\n");
		}
		if( iCurBlk > 1) {
			 
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+iCurBlk+");'>&lt;</a>\n");
            str.append(" </li>\n");
		}
		
		int iTmpBlock = ((iCurPage-1) / 5 ) * 5  +1 ;
		if(iTmpBlock <=0) iTmpBlock=1;
		for( int i=iTmpBlock;i<(iTmpBlock+5)  && (i  <=iTotalBlk) ;i++) {
			if(i == iCurPage) str.append("<li class='active'>\n");
			else  str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+i+");'>"+ i+"</a>\n");
            str.append(" </li>\n");
		}
//		<li class="active"><a href="#" onclick="gotoPage(1);">1</a>
		

		if( (iTmpBlock + 5) <= iTotalBlk) {
			int NextBlk =  iTmpBlock + 5;
			if(NextBlk < iTotalBlk ) {
				NextBlk=NextBlk;
			}
			else NextBlk =iTotalBlk;
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+NextBlk+");'>&gt;</a>\n");
            str.append(" </li>\n");
			
		}

		if( ( iTmpBlock + 5) <= iTotalBlk) {
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+iTotalBlk+");'>&gt;&gt;</a>\n");
            str.append(" </li>\n");
			
		}
		return str.toString();		
	}
	
	
	 public static String today()
	 {
		SimpleDateFormat df3 = new SimpleDateFormat ("yyyyMMdd");
		String s_today = df3.format(new java.util.Date());
		return  s_today;
	 }
    
   
	 public static String today(String fmt)
	 {
		SimpleDateFormat df3 = new SimpleDateFormat (fmt);
		String s_today = df3.format(new java.util.Date());
		return  s_today;
	 }
	 
	 public static String getTimeToStr(String fmt , Date date)
	 {
    	
    	SimpleDateFormat df3 = new SimpleDateFormat (fmt);
		String c_time = df3.format(date);
		return  c_time;
	 }
	 
	   public static String getCurrntTime(String fmt)
		 {
	    	
	    	if("YYYYMMDDHHMMSS".equals(fmt))  fmt ="yyyyMMddHHmmss";
	    	else  fmt ="yyyyMMddHHmmss";
	    	
			SimpleDateFormat df3 = new SimpleDateFormat (fmt);
			String c_time = df3.format(new java.util.Date());
			return  c_time;
		 }
	  
	   
		public static String HTTP_GET_IF(String url)
		{
	    	String ret="";
	    	String body="";
		 	try {
				HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
				HttpGet getRequest = new HttpGet(url); //GET 메소드 URL 생성
				HttpResponse response = client.execute(getRequest);
				//Response 출력
				if (response.getStatusLine().getStatusCode() == 200) {
					ResponseHandler<String> handler = new BasicResponseHandler();
					body = handler.handleResponse(response);
					//System.out.println(body);
				} else {
					//System.out.println("response is error : " + response.getStatusLine().getStatusCode());
				}
				 

			} catch (Exception e){
				//System.err.println(e.toString());
			}

			return body;
		}
		
		public static String  getInternetInfo(String url)
		{
			String result= HTTP_GET_IF(url) ;
			
			if("".equals(result)) return "Not Conection Internet";
			JSONParser parser = new JSONParser();
			JSONObject obj;
			String  sInternetIP ="";
			try {
				obj = (JSONObject)parser.parse(result);
				sInternetIP = (String)obj.get("ip");
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return sInternetIP;
		}
	 
	 
}
