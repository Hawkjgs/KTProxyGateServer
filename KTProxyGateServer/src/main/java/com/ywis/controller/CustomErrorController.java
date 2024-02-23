package com.ywis.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller 
public class CustomErrorController implements ErrorController { 
	private String PATH = "/errors"; 
	
	@RequestMapping(value = "/error") 
	public ModelAndView handleError(HttpServletRequest request) throws Exception{ 
		//System.out.println("main");
		String errCode="404"; 
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE); 
		if(status != null){ 
			int statusCode = Integer.valueOf(status.toString()); 
			if(statusCode == HttpStatus.NOT_FOUND.value()){ errCode= "404"; } 
			if(statusCode == HttpStatus.FORBIDDEN.value()){ errCode="500"; } 
		}
   	 	ModelAndView mv = new ModelAndView();
   	 	mv.setViewName("error/"+errCode);
   	 	return mv;

	} 
	
	@Override public String getErrorPath() { 
		return PATH; 
	} 
}

 