package com.ywis.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
 
import sm.comm.CommUtil;
import sm.comm.FTPUtil;
import sm.comm.HttpComm;
import sm.comm.KTCommHeader;
import sm.comm.SFileUtil;
import sm.comm.SMLicense;
import sm.comm.StringUtil;
 
@Component
public class CheckScheduler {
		private static Logger logger = LoggerFactory.getLogger(CheckScheduler.class);
	  
		
		@Value("${kt_connection:null}")
		private String kt_connection;
		
		
		@Value("${kt_url_1:null}")
		private String KT_URL_1;
		
		@Value("${kt_url_2:null}")
		private String KT_URL_2;
		
		 
		
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

		@Value("${work.path:null}")
		private String WORK_FILE_PATH;
		
		
		@Value("${server.idleTime:null}")
		private String SERVER_IDLETIME;
	

		@Value("${server.mock_yn:null}")
		private String MOCK_YN;
		

		@Value("${license.product:null}")
		private String PRODUCT_KEY;

		@Value("${license.key:null}")
		private String LICENSE_KEY;
		
		
		
		
		public String LOAD_PROP_YN="NO";
		
		public long TOKEN_LAST_TIME;
		public int iErrorCnt=0;
		
		@Value("${server.error_time:null}")
		private String SERVER_ERROR_TIME;
		
		@Value("${server.save_day:null}")
		private String SERVER_SAVE_DAY;
		
		

		@Value("${ssl.ignore:null}")
		private String SSL_IGNORE;
		
		public boolean bLicenseReqFile=false;
	
		public String WORK_DAY="";
		
		// 
		@Scheduled(fixedDelay = 180000)
		public void WorkOldFileDeleteScheduler()
		{
		
			//KTGateWay
			String  curDay= CommUtil.today("yyyyMMddHH");
			
			
			
			int iWorkFolderIdx = WORK_FILE_PATH.length();
			
					
			if(!curDay.equals(WORK_DAY) && (iWorkFolderIdx>0 ) &&  (WORK_FILE_PATH.indexOf("work") > 0)) {
				logger.info("============================================================");
				logger.info("Work File Delete Scheduler");
				logger.info("============================================================");
				WORK_DAY=curDay;
				File root_folder = new File(WORK_FILE_PATH);
				
				int iSERVER_SAVE_DAY = CommUtil.parseIntDef(SERVER_SAVE_DAY, 10);
				String DelDate = CommUtil.CapDate(iSERVER_SAVE_DAY);
				 
				int iCurYear  =  CommUtil.parseInt(CommUtil.today("yyyy"));
				int iDelYear  =  CommUtil.parseInt( DelDate.substring(0,4));
				
				int iDelMonth  =  CommUtil.parseInt( DelDate.substring(0,6));
				int iDelDay =  CommUtil.parseInt( DelDate);
				
				//Root Folder 
				if(root_folder.exists()) {
					File[] year_list = root_folder.listFiles(); //파일리스트 얻어오기
					
					int iWorkYear  =0;
					int iWorkMonth =0;
					int iWorkDay =0;
					
					for (int i = 0; i < year_list.length; i++) {
						//folder_list[j].delete(); //파일 삭제 
						if(year_list[i].isDirectory()) { // 폴더 
							iWorkYear = CommUtil.parseInt(year_list[i].getName()); 
							
							try {
								// 폴더가 숫자 이고 이전 연도의 인경우 삭제 한다.
								if (iWorkYear!=0   && (iDelYear >iWorkYear) ) {
									System.out.println(year_list[i].getName());
									FileUtils.cleanDirectory(year_list[i]);
									 if (year_list[i].isDirectory()) {
										 year_list[i].delete(); // 대상폴더 삭제
									      logger.info(year_list[i] + "폴더가 삭제되었습니다.");
									    }
								}
								else if(iWorkYear!=0) { // 수자로 된 폴더 
									String wYear=year_list[i].getName();
									
									File[] month_list = year_list[i].listFiles(); //파일리스트 얻어오기
									
									for (int j = 0; j < month_list.length; j++) {
										if(month_list[j].isDirectory()) { // 폴더 
											iWorkMonth = CommUtil.parseInt(year_list[i].getName()+month_list[j].getName()); 
											//저장이 이전 월 폴더의 삭제 
											if (iWorkMonth!=0   && (iDelMonth >iWorkMonth) ) {
												System.out.println(month_list[j].getName());
												FileUtils.cleanDirectory(month_list[j]);
												 if (month_list[j].isDirectory()) {
													 month_list[j].delete(); // 대상폴더 삭제
												      logger.info(month_list[j] + "폴더가 삭제되었습니다.");
												 }
											}
											else { //날짜 기준으로 삭제 처리 
												
												File[] day_list = month_list[j].listFiles(); //파일리스트 얻어오기
												for (int k = 0; k < day_list.length; k++) {
													iWorkDay = CommUtil.parseInt(day_list[k].getName()); 
													if(day_list[k].isDirectory()) { // 폴더 
														
														if (iWorkDay!=0   && (iDelDay >iWorkDay) ) {
															FileUtils.cleanDirectory(day_list[k]);
															if (day_list[k].isDirectory()) {
																day_list[k].delete(); // 대상폴더 삭제
															      logger.info(day_list[k] + "폴더가 삭제되었습니다.");
															 }
															
															
														}
													}
													
												}
												
											}
										}
										
									}
									
									
								}
									 
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//하위 폴더와 파일 모두 삭제
							
						}
						//System.out.println("파일이 삭제되었습니다.");
								
					}
					
				
				}
			}
			
		}
		
		
		@Scheduled(fixedDelay = 1000)
		public void CheckScheduler()
		{
			 
			
			int iSERVER_ERROR_TIME= CommUtil.parseIntDef(SERVER_ERROR_TIME,30);
			
			if(bLicenseReqFile==false) {
				String keyRead= SFileUtil.readFile(LICENSE_KEY+".req") ;
				if("".equals(keyRead)) {
					
			    	String LicenceReqKey = SMLicense.MakeLic(PRODUCT_KEY, SERVER_IP_ADDR);
					SFileUtil.writeFile(LICENSE_KEY+".req", LicenceReqKey, "");
					bLicenseReqFile=true;
				}
			}
			if("Y".equals(MOCK_YN))  {
				KTCommHeader.TOKEN=CommUtil.today("yyyyMMddHHmmss");
			}
			else {
				
				
				
				
				if("NO".equals(LOAD_PROP_YN)) {
					KTCommHeader.setKTServerIP(KT_URL_1, KT_URL_2);
					KTCommHeader.LOGIN_URL =  PRE_FIX+"/infe/auth/v1/login/nhis"; 
//					KTCommHeader.TOKEN_REFLASH_URL = PRE_FIX+"/infe/auth/v1/token"; 
					KTCommHeader.SYSTEM_CUSTNO = SYSTEM_CUSTNO;
					KTCommHeader.SERVER_IP_ADDR = SERVER_IP_ADDR;
					KTCommHeader.SERVER_MAC = SERVER_MAC;
					KTCommHeader.SYSTEM_NM = SYSTEM_NM;
					// KTCommHeader.SUB_NB=SUB_NB;
					KTCommHeader.TOKEN="";
					LOAD_PROP_YN="YES";
				}
				
				if("REQ".equals(KTCommHeader.KT_ACTIVE_SERVER_CHG)) {
					 
					String DOWN_SERVER=KTCommHeader.KT_ACTIVE_SERVER_IP; 
					KTCommHeader.setChangeServer();
					KTCommHeader.KT_ACTIVE_SERVER_TIME=CommUtil.today("yyyyMMdd HH:mm:ss");
					
					String ACTIVE_SERVER=KTCommHeader.KT_ACTIVE_SERVER_IP; 
					KTCommHeader.KT_ACTIVE_SERVER_CHG="";
					String JOB_ID= CommUtil.GUID(null);
					
					HashMap jobInfo = new HashMap();
					
					jobInfo.put("JOB_URL","KT Server IP Change");
					jobInfo.put("JOB_TY","POST");
					jobInfo.put("REQ_IP","127.0.0.1");
					jobInfo.put("JOB_STATUS","SUSS");
					jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
					
					String req = "{ \"down_server\": \""+DOWN_SERVER+"\"}";
					String res = "{ \"active_server\": \""+ACTIVE_SERVER+"\"}";
					CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0",  req,res);
						
					 
				}
				
				if("N".equals(kt_connection)) {
					System.out.println("No KT Server Connection");
				}
				else {
					// TOKEN정보가 없으면 로그인을 시도하여 Token정보를 가져온다.
					if("".equals(KTCommHeader.TOKEN) || "TOKEN_401_ERROR".equals(KTCommHeader.TOKEN_STATUS)  || "TOKEN_TIMEOUT".equals(KTCommHeader.TOKEN_STATUS)) {
						
						if("TOKEN_401_ERROR".equals(KTCommHeader.TOKEN_STATUS)) {
							iErrorCnt=0;
						}
						 
						if(iErrorCnt==0) {
							String JOB_ID= CommUtil.GUID(null);
							HashMap retMap = HttpComm.NHIS_LOGIN_URL(SSL_IGNORE,logger);
							int  iRetCode=  (int) retMap.get("result");
							
							HashMap jobInfo = new HashMap();
							jobInfo.put("JOB_URL",KTCommHeader.KT_ACTIVE_SERVER_IP+"/infe/auth/v1/login/nhis");
							jobInfo.put("JOB_TY","POST");
							jobInfo.put("REQ_IP","127.0.0.1");
							jobInfo.put("JOB_STATUS","FAIL");
							jobInfo.put("JOB_DT",CommUtil.today("yyyy-MM-dd HH:mm:ss"));
							
							if(iRetCode==0) {
								jobInfo.put("JOB_STATUS","SUSS");
							    CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "SUSS" ,"0", ""+retMap.get("req_body"), ""+ retMap.get("res_body"));
								logger.info("[TOKEN NHIS_LOGIN_URL ] Reflash Time "+ CommUtil.parseIntDef( CommUtil.NvlToBlank(SERVER_IDLETIME),25) +"분 : KTCommHeader.TOKEN :" +KTCommHeader.TOKEN + ", KTCommHeader.TOKEN_UPDATE_DT:" +KTCommHeader.TOKEN_UPDATE_DT );
								iErrorCnt=0;
							}
							else {
								iErrorCnt++;
								logger.error("[NHIS_LOGIN_URL ERROR ] "+  retMap.get("res_body") );
						        CommUtil.JOB_WRITE(jobInfo,WORK_FILE_PATH, JOB_ID, "ERROR" ,"9991",  ""+retMap.get("req_body"),""+ retMap.get("res_body"));
									
							}
						}
						else {
							iErrorCnt++;
							if(iErrorCnt > iSERVER_ERROR_TIME) iErrorCnt=0;
							
						}
						
					}
	 				else {
	 					// Token 자동 갱신관련처
	 					SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
						 try {
				                Date last_date = dateParser.parse(KTCommHeader.TOKEN_UPDATE_DT);
				                
				                Date cur_date = new java.util.Date();
				                
				                
				                long timeMil1 = last_date.getTime();
				        		long timeMil2 = cur_date.getTime();
				    			
				        		// 비교 
				        		long diff = timeMil2 - timeMil1;
				    			
				        		long diffSec = diff / 1000;
				        		long diffMin = diff / (1000 * 60);
				    			
				        		
				        		if(TOKEN_LAST_TIME!=diffMin) {
				        			TOKEN_LAST_TIME =diffMin;
				        			logger.info("[IDLE TIME]: " + diffMin + "/"+CommUtil.parseIntDef( CommUtil.NvlToBlank(SERVER_IDLETIME),25) +" 분 KTCommHeader.TOKEN :" +KTCommHeader.TOKEN + ", KTCommHeader.TOKEN_UPDATE_DT:"+KTCommHeader.TOKEN_UPDATE_DT);
				        			
				        		}
				        		if(diffMin >= CommUtil.parseIntDef( CommUtil.NvlToBlank(SERVER_IDLETIME),25)) {
				        			logger.info("[TOKEN REFLASH TIMEOUT] IDLE REFLASH TIME : " +CommUtil.parseIntDef( CommUtil.NvlToBlank(SERVER_IDLETIME),25) +" 분");
				        			TOKEN_LAST_TIME=0;
				        			KTCommHeader.TOKEN_UPDATE_DT="";
				        			KTCommHeader.TOKEN_STATUS="TOKEN_TIMEOUT"; 
				        			
				        		}
						 } catch (ParseException e) {
							KTCommHeader.TOKEN="";
				            e.printStackTrace();
				        }
					}
				}
			}
		}
		
}
 
