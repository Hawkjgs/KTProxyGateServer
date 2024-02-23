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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

import org.apache.commons.io.FilenameUtils;
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
 

public class KTCommHeader {
	
	public static String TOKEN="";
	public static String TOKEN_UPDATE_DT="";
	public static String TOKEN_STATUS="WAIT";
//	public static String TOKEN_REFLASH_URL="";
	public static String TOKEN_COMM_ERR_MSG="";
	
	
	public static String KT_ACTIVE_SERVER_CHG="";
	
	
	public static String LOGIN_URL="";
	public static String SYSTEM_CUSTNO="";
	public static String SERVER_IP_ADDR="";
	public static String SERVER_MAC="";
	public static String SYSTEM_NM="";
	
	
	public static String KT_ACTIVE_SERVER_IP="";
	
	public static String KT_ACTIVE_SERVER_TIME="";
	
	public static String KT_ACTIVE_SERVER_REQ_TIME="";
	
	public static String KT_SERVER_ACTIVE_1="";
	public static String KT_SERVER_ACTIVE_2="";
	
	public static int TOT_CNT;
	public static HashMap CUR_WORK_JOB ;
  
	
	public static void  setChangeServer() {
		if(KT_ACTIVE_SERVER_IP.equals(KT_SERVER_ACTIVE_1)) KT_ACTIVE_SERVER_IP=KT_SERVER_ACTIVE_2;
		else KT_ACTIVE_SERVER_IP=KT_SERVER_ACTIVE_1;
		
		KT_ACTIVE_SERVER_CHG="OK";
		
	}
	public static void  setKTServerIP(String server_1,String server_2 ) {
		KT_SERVER_ACTIVE_1= server_1;
		KT_SERVER_ACTIVE_2= server_2;
		KT_ACTIVE_SERVER_IP=server_1;
		KT_ACTIVE_SERVER_CHG="OK";
	}
	
public static int  getJobInfoUsed(HashMap map,Map searchMap) {
		boolean bJOB_STATUS=false;
		boolean bSearch=false; 
		boolean bJOB_TY=false;
		boolean bERR_CD=false;
		String JOB_ID="";
		String JOB_URL="";
		String JOB_STATUS="";
		String REQ_IP="";
		String JOB_TY=""; 
		String ERR_CD=""; 
		
		JOB_ID = CommUtil.NvlToBlank(map.get("JOB_ID")); 
		JOB_URL = CommUtil.NvlToBlank(map.get("JOB_URL"));
		JOB_STATUS = CommUtil.NvlToBlank(map.get("JOB_STATUS"));
		REQ_IP = CommUtil.NvlToBlank(map.get("REQ_IP"));
		JOB_TY = CommUtil.NvlToBlank(map.get("JOB_TY"));
		ERR_CD = CommUtil.NvlToBlank(map.get("ERR_CD"));

		String searchWrd = CommUtil.NvlToBlank(searchMap.get("searchWrd"));
		String searchStatus = CommUtil.NvlToBlank(searchMap.get("JOB_STATUS"));
		String searchErrCd= CommUtil.NvlToBlank(searchMap.get("ERR_CD"));
		  
		if(!"".equals(searchStatus)) {
				bJOB_STATUS=true;
		}
		
		if(!"".equals(searchErrCd)) {
			bERR_CD=true;
		}
		
		
		
		if(bJOB_STATUS && bERR_CD) {
			if(!"".equals(CommUtil.NvlToBlank(searchWrd).trim())) {
				if(ERR_CD.indexOf(CommUtil.NvlToBlank(searchWrd))>=0 && JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchStatus))>=0)  bSearch=true;
			}
			else {
				if(!"0".equals(ERR_CD) && JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchStatus))>=0) 
				 bSearch=true;
			}
		}
		else  if(bJOB_STATUS && !bERR_CD) {
			if(!"".equals(CommUtil.NvlToBlank(searchWrd).trim()) && JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchStatus))>=0) {
				if(JOB_ID.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
				else if(JOB_URL.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
				else if(ERR_CD.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
			}
			else {
				if(JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchStatus))>=0)  bSearch=true;
			}
		}
		else  if(!bJOB_STATUS && bERR_CD) {
		 	if(!"".equals(CommUtil.NvlToBlank(searchWrd).trim()) && !"0".equals(ERR_CD)) {
				if(ERR_CD.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
			}
			else {
				//System.out.println("ERR_CD:"+ERR_CD);
				if(!"0".equals(ERR_CD))  bSearch=true;
			}
		}
		else {
			if(!"".equals(CommUtil.NvlToBlank(searchWrd).trim())) {
				if(JOB_ID.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
				else if(JOB_URL.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
				else if(JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
				else if(ERR_CD.indexOf(CommUtil.NvlToBlank(searchWrd))>=0)  bSearch=true;
			}
			else bSearch=true;
			
		}
		 
//		}
//		else if(bJOB_STATUS && bERR_CD) {
//		
//		if(!bJOB_STATUS && !bERR_CD) {
//			
//		}
//		else if(!bJOB_STATUS && !bERR_CD) {
//			
//		}
//		else if(!bJOB_STATUS && !bERR_CD) {
//			
//		}
//		else {
//			
//		}
		
		
//		if(!bJOB_STATUS && !bERR_CD) {
//			if(!"".equals(CommUtil.NvlToBlank(searchWrd).trim())) {
//				if(JOB_ID.indexOf(CommUtil.NvlToBlank(searchWrd))>=0) {
//					bSearch=true;
//				}
//				
//				if(JOB_URL.indexOf(CommUtil.NvlToBlank(searchWrd))>=0) {
//					bSearch=true;
//				}
//				
//				if(JOB_STATUS.indexOf(CommUtil.NvlToBlank(searchWrd))>=0) {
//					bSearch=true;
//				}
//				
//				if(ERR_CD.indexOf(CommUtil.NvlToBlank(searchWrd))>=0) {
//					bSearch=true;
//				}
//			}
//			else {
//				bSearch=true;
//			}
//			
//		}
//		else {
//			bSearch=false;
//		}
		
		if(bSearch) return 1;
		else return 0;
		 
    	 
	}

	public static int getFolderFileCount(String path , Map searchMap) {
		List fileList = (List) CUR_WORK_JOB.get("FILE_LIST");
		int iCount=0;
		HashMap map = null;
		String fName="";
	   	for (int j = 0; j < fileList.size(); j++) {
			map =  (HashMap) fileList.get(j);
			int iret = getJobInfoUsed(map,searchMap);
			if(iret==1) iCount++;
	   	}
	 	return iCount;
	  }
	
	public static HashMap getFolderFileList(String path, int s_idx , int iFileTotalCnt,Map searchMap) {
		List fileList = (List) CUR_WORK_JOB.get("FILE_LIST");
	 
		List listFolder = new ArrayList();
		HashMap mapFile = new HashMap();
	 
		int icount=0;
		int idx=iFileTotalCnt - s_idx+1;
		//s_idx = (s_idx -1) *10 +1;
		int e_idx = s_idx+10;
 
		HashMap  map  = null;
		for (int j = 0; j < fileList.size(); j++) {
			map  =(HashMap) fileList.get(j);
			 
	    	int iret = getJobInfoUsed(map,searchMap);
	    	map.put("IDX", idx);
	    	//map.put("LENGTH", byteCalculation(""+ folder_list[j].length()));
			if(iret==1) {
				icount++;
			    if((s_idx <= icount)  &&  (e_idx > icount )) {
			    	idx--;
			    	listFolder.add(map);
			    }
			}
		}
		mapFile.put("FileList", listFolder);
		mapFile.put("FileTotalCnt", icount);
		
		return mapFile;
		
		
	}
	
	
	public static int getFolderTotalJSONFileCount(String path) {
		File folder = new File(path);
		int iCount=0;
		try {
		    if(folder.exists()) {
				File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
				String fName="";
				for (int j = 0; j < folder_list.length; j++) {
					fName = folder_list[j].getName();
					String fext = FilenameUtils.getExtension(fName).toLowerCase();
				    if(folder_list[j].isFile() && fext.indexOf("json") >=0)  {
				    	iCount++;
				    }
				}
		    }	
		} catch (Exception e) {
				e.getStackTrace();
		}
		return iCount;
	  }
	
	
	public static void getWorkDirInfo( Logger logger,String path ,String WORK_DAY,Map<String,Object> searchMap) {
		
		int iTotalFileCount =getFolderTotalJSONFileCount(path);
		
		if (TOT_CNT!=iTotalFileCount || !WORK_DAY.equals(CommUtil.NvlToBlank(CUR_WORK_JOB.get("CUR_DAY")))) {
			
			if (TOT_CNT!=iTotalFileCount ) {
				logger.info("만들어진 파일의 파일 수가 달라 다시 목록 생성 : Cur : " + TOT_CNT + " ==> 갱신  : " + iTotalFileCount );
			}
			if (!WORK_DAY.equals(CommUtil.NvlToBlank(CUR_WORK_JOB.get("CUR_DAY")))) {
				logger.info("만들어진 파일의 수가 다르거나 , 검색  일자가 다른경우 : Cur : " + CommUtil.NvlToBlank(CUR_WORK_JOB.get("CUR_DAY")) + " ==> Search Day : " + WORK_DAY );
			}
			CUR_WORK_JOB.put("CUR_DAY",WORK_DAY);
			
			File folder = new File(path);
			int iCount=0;
			List fileList = new ArrayList();
			try {
			    if(folder.exists()) {
					File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
					String fName="";
					for (int j = 0; j < folder_list.length; j++) {
						fName = folder_list[j].getName();
						String fext = FilenameUtils.getExtension(fName).toLowerCase();
					    if(folder_list[j].isFile() && fext.indexOf("json") >=0)  {
					    	HashMap  map  = new HashMap();
					    	int iret = SFileUtil.getJobInfoUsed(map,path+File.separator +fName,searchMap);
					    	map.put("FILE_NM", fName);
					    	map.put("JOB_DATE", WORK_DAY);
					    	fileList.add(map);
					    	iCount++;
					    }
					}
			    }	
			    
			    
			    
			    Collections.sort(fileList, new Comparator<HashMap<String, Object>>() {
					@Override
					public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
						String name1 = (String) o1.get("JOB_DT");
						String name2 = (String) o2.get("JOB_DT");
						return name2.compareTo(name1);
					}
				});
			    
			    CUR_WORK_JOB.put("FILE_LIST", fileList);
			} catch (Exception e) {
					e.getStackTrace();
			}
			TOT_CNT = iCount;
			
			
		}
		CUR_WORK_JOB.put("CUR_DAY", WORK_DAY);
	}
	
	public static List getParamCheck(String toekn,String subNb, String [] inParam) {
	  
		List listChkParams = new ArrayList();

		if("TOKEN_CHK".equals(toekn)) {
			HashMap hToken = new HashMap();
			hToken.put("KEY", "token");
			hToken.put("DEFVAL", "TOKEN_DATA"); 
			listChkParams.add(hToken);
		 
		}
		if("SUB_NB_CHK".equals(subNb)) {
			HashMap hSubNb = new HashMap();
			hSubNb.put("KEY", "subNb");
			//hSubNb.put("DEFVAL", "SUB_NB_DATA"); 
			listChkParams.add(hSubNb);
		}
	    
		if(inParam!= null) {
			for( int i=0;i<inParam.length;i++) {
				
				HashMap hInPara = new HashMap();
				hInPara.put("KEY",inParam[i]);
				 listChkParams.add(hInPara);
				
			}
		}
		 
		return listChkParams;
    }
	
	 
}
