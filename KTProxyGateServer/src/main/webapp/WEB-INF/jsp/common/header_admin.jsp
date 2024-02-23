<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
/*
 * Program ID   : .jsp
 * MENU CODE    :    
 * Description  :   
 * Creator      : gsjang
 * Create Date  : 
 * Updater      : 
 * Update Date  : 
 * Update Desc  : 
 *
 * @vertions V1.0
 */

%>   
 <script language="JavaScript" type="text/JavaScript">
 
 function jsSliderChg()
 {
	 $.cookie('pmt_sidebar',$('#body').hasClass('sidebar-collapse'),{ expires: 1,path : '/' });

 }
 </script>
 
 
<header class="main-header">

    <!-- Logo -->
    <a href="#" class="logo">
  		<span class="logo-mini"><b>KTGateWay</b>Admin </span>
   	   	<span class="logo-lg"><b>KTGateWay</b>Admin</span>
    </a>

    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <!-- Sidebar toggle button-->
       
      <!-- Navbar Right Menu -->
      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
           
           
          <!-- Tasks: style can be found in dropdown.less -->
           
          <!-- User Account: style can be found in dropdown.less -->
          <li class="dropdown user user-menu">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <img src="${contextPath}/dist/img/photo.jpg" class="user-image" alt="User Image" onerror="this.src='${contextPath}/dist/img/photo.jpg'">
              <span class="hidden-xs">${sessionScope.APIUserInfo.MEM_ID}</span>
            </a>
            <ul class="dropdown-menu">
              <!-- User image -->
              <li class="user-header">
                <img src="${contextPath}/dist/img/photo.jpg" class="img-circle" alt="User Image" onerror="this.src='${contextPath}/dist/img/photo.jpg'">

                <p>
                 ${sessionScope.APIUserInfo.MEM_ID}
                </p>
              </li>
              
              <!-- Menu Footer-->
              <li class="user-footer">
                <div class="pull-right">
                  <a href="${contextPath}/logout.do" class="btn btn-default btn-flat">로그아웃</a>
                </div>
              </li>
            </ul>
          </li>
           
        </ul>
      </div>

    </nav>
  </header>