<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% pageContext.setAttribute("newLineChar", "\n"); %> 
 
           <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
            	<li class="${body_level_1=='day' ? 'active':''} "  >
            	<a href="#" >
            	<span class="handle"><i class="glyphicon glyphicon-folder-${body_level_1=='day' ? 'open':'close'}"></i></span>&nbsp;&nbsp;요청내역 </a></li>
			</ul>
            </div>
            

