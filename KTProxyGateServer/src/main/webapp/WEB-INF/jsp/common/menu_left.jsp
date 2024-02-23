

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
<!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar ">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
      <!-- Sidebar user panel -->
      <div class="user-panel">
        <div class="pull-left image">
          <img src="${contextPath}/dist/img/photo.jpg" class="img-circle" alt="User Image" onerror="this.src='${contextPath}/dist/img/photo.jpg'">
        </div>
        <div class="pull-left info">
          <p>${sessionScope.APIUserInfo.MEM_ID} <br></p>
          <!-- <a href="#"><i class="fa fa-circle text-success"></i> Online</a> -->
        </div>
      </div>
      <!-- /.search form -->
      <!-- sidebar menu: : style can be found in sidebar.less -->
      <ul class="sidebar-menu">
        <li class="header">MAIN NAVIGATION</li>
          
       <li class="treeview  active">
          <a href="#">
            <i class="fa fa-user"></i> <span>연동정보  </span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
          	<li class="treeview  ${left_menu=='static'? 'active':'' }"><a href="/staticDay.do"><i class="fa fa-circle-o"></i>연동 내역</a></li>
         	<li class="treeview  ${left_menu=='apidoc'? 'active':'' }"><a href="/swagger-ui/index.html" target="_blank"><i class="fa fa-circle-o"></i>API Doc</a></li>
          
          </ul>
        </li>
         
      </ul>
    </section>
    <!-- /.sidebar -->
  </aside>
 