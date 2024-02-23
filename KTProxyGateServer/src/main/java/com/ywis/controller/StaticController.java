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
import sm.comm.KTCommHeader;
import sm.comm.SFileUtil;
import sm.comm.Seed256;
import sm.comm.StringUtil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
public class StaticController {
	
	private Logger logger = LoggerFactory.getLogger(StaticController.class);

	

	@Value("${work.path:null}")
	private String WORK_FILE_PATH;
	 
	
	@RequestMapping("/jobInfo.do")
    public ModelAndView jobInfo(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
    	ModelAndView mv = new ModelAndView();
    	HttpSession session = request.getSession(); 
    	mv.addObject("left_menu","static");
    	mv.addObject("body_level_1","day");
    	
    	
    	long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
    	
    	String ST_DT_STR ="";
    
    	ST_DT_STR = CommUtil.NvlToBlank(searchMap.get("JOB_DATE")).replaceAll("-", "");
    	
    	String YEAR = ST_DT_STR.substring(0,4);
    	
    	String MONTH = ST_DT_STR.substring(4,6);
    	
    	String JOB_ID =CommUtil.NvlToBlank(searchMap.get("JOB_ID"));
        String path = WORK_FILE_PATH+File.separator  + YEAR + File.separator + MONTH + File.separator + ST_DT_STR;
    	
        logger.info("jobInfo:"+ path);
        logger.info("JOB_ID:"+ JOB_ID);
        String FILE_NM= path+File.separator + JOB_ID;
    	String JOB_JSON =SFileUtil.readFile(FILE_NM);

    	
    	logger.info("FILE_NM:"+ FILE_NM);
    	logger.info("JOB_JSON:"+ JOB_JSON);
    	
    	
    	mv.addObject("JOB_JSON",JOB_JSON);
    	
    	mv.addObject("JOB_INFO",SFileUtil.getJonInfo(path, JOB_ID));
    	mv.setViewName("popup/JobInfo");
    	 
    	
    	long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
    	long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
    	//System.out.println("시간차이(m) : "+secDiffTime);
    	
    	return mv;
    }
	
	 
	 @RequestMapping("/staticDay.do")
    public ModelAndView staticDay(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
    	ModelAndView mv = new ModelAndView();
    	HttpSession session = request.getSession(); 
    	mv.addObject("left_menu","static");
    	mv.addObject("body_level_1","day");
    	
    	String ST_DT_STR ="";
    	if("".equals(CommUtil.NvlToBlank(searchMap.get("ST_DT")))) {
    		searchMap.put("ST_DT", CommUtil.today("yyyy-MM-dd"));
    	}
    	
    	
    	if(KTCommHeader.CUR_WORK_JOB == null) {
    		KTCommHeader.CUR_WORK_JOB = new HashMap ();
    	}
    	
    	String page =  StringUtil.NullToBlank(searchMap.get("pageIndex"),"1");
    	 
    	ST_DT_STR = CommUtil.NvlToBlank(searchMap.get("ST_DT")).replaceAll("-", "");
    	 
    	
    	String YEAR = ST_DT_STR.substring(0,4);
    	
    	String MONTH = ST_DT_STR.substring(4,6);
    	
    	
    	searchMap.put("ST_DT_STR",ST_DT_STR);
    	
    	
    	String path = WORK_FILE_PATH+File.separator  + YEAR + File.separator + MONTH + File.separator + ST_DT_STR;
    
    	
    	int iCurPage=StringUtil.parseInt(page,1);
		
    	 HashMap  hMap = new HashMap();
    	 KTCommHeader.getWorkDirInfo(logger,path,ST_DT_STR,hMap);
    	
    	 int iFileTotalCnt =KTCommHeader.getFolderFileCount(path,searchMap);
		 
		 mv.addObject("renderPagination",SFileUtil.renderPagination(iCurPage , 10, iFileTotalCnt)); ;
		 
		 int s_idx= (iCurPage -1) * 10 +1;
		 
		 HashMap map =  KTCommHeader.getFolderFileList(path,s_idx,iFileTotalCnt,searchMap);
		 mv.addObject("resultList",map.get("FileList"));
    	
    	mv.addObject("searchMap",searchMap);
    	mv.setViewName("admin/staticDay");
    	return mv;
    }
	 
	 @RequestMapping("/TokenInvaildTest.do")
	    public ModelAndView staticMonth(@RequestParam Map<String,Object> searchMap, HttpServletRequest request,HttpServletResponse response) throws Exception{   
	    	ModelAndView mv = new ModelAndView();
	    	HttpSession session = request.getSession(); 
	    	mv.addObject("left_menu","TokenInvaildTest");
	    	mv.addObject("body_level_1","day");
	    	 
	    	
	    	KTCommHeader.TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJIZXJpdCIsInN1YiI6IntcInByaXZpbGVnZVwiOlwiUExBVEZPUk1cIixcInN2Y19JZFwiOlwiTkhJU1wiLFwiYXBwX0lkXCI6XCJuaGlzMmFcIixcImFwcF9JcFwiOlwiMTE3LjUyLjg0LjU0XCIsXCJzdmNfU2FtcGxpbmdcIjpcIjBcIixcImFwcF9TYW1wbGluZ1wiOlwiMFwifSIsImF1ZCI6IjExNy41Mi44NC41NCIsImV4cCI6MTY5ODY1Nzk3NywianRpIjoiY2FkYzIwYmMtYmM0ZS00ZDEzLTk5MGUtOTUyMWRmZTE5N2JkIn0.U1yj6WiOcz8tTJLGVzPqeGI4DqFYYnmZ-69nlIxWAIs";
	    	
	    	mv.addObject("searchMap",searchMap);
	    	mv.setViewName("admin/TokenInvaildTest");
	    	return mv;
	    }
	 
	 
}