<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles"       prefix="t"%>
	<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmm");
%>
<html>
<head>
  <meta charset="utf-8">
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>
YWIS KTGateWay Docs
  </title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link rel="apple-touch-icon-precomposed"  href="/dist/img/sm.png" />
  <link rel="shortcut icon" href="/dist/img/sm.ico" type="image/x-icon" />
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="${contextPath}/bootstrap/css/bootstrap.min.css">
   <link rel="stylesheet" href="${contextPath}/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${contextPath}/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${contextPath}/css/ionicons.min.css">
 
   <link rel="stylesheet" href="/dist/css/swiper.min.css">
  <!-- daterange picker -->
  <link rel="stylesheet" href="${contextPath}/plugins/daterangepicker/daterangepicker.css">
  <!-- bootstrap datepicker -->
  <link rel="stylesheet" href="${contextPath}/plugins/datepicker/datepicker3.css">
  
    <!-- Select2 -->
  <link rel="stylesheet" href="${contextPath}/plugins/select2/select2.min.css">
  
  
   <!-- Theme style -->
  <link rel="stylesheet" href="${contextPath}/dist/css/AdminLTE.css">
  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
  <link rel="stylesheet" href="${contextPath}/dist/css/skins/_all-skins.min.css">
  
  <!--  Morris charts -- -->
  <link rel="stylesheet" href="${contextPath}/plugins/morris/morris.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="${contextPath}/js/html5shiv.min.js"></script>
  <script src="${contextPath}/js/respond.min.js"></script>
  <![endif]--> 
 
 
 
   <!-- fullCalendar 2.2.5-->
  <link rel="stylesheet" href="${contextPath}/plugins/fullcalendar/fullcalendar.min.css">
  <link rel="stylesheet" href="${contextPath}/plugins/fullcalendar/fullcalendar.print.css" media="print">
  
   

  <!-- Theme style -->
   
  
  <script src="${contextPath}/js/common.js"></script>
  
  
<!-- jQuery 2.2.3 -->
<script src="${contextPath}/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="${contextPath}/plugins/jQuery/jquery.cookie.js"></script>

<!-- Bootstrap 3.3.6 -->
<script src="${contextPath}/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${contextPath}/plugins/fastclick/fastclick.js"></script>

  <!-- AdminLTE App -->
<script src="${contextPath}/dist/js/app.js"></script>


<!-- InputMask -->
<script src="${contextPath}/plugins/input-mask/jquery.inputmask.js"></script>
<script src="${contextPath}/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="${contextPath}/plugins/input-mask/jquery.inputmask.extensions.js"></script>

<!-- date-range-picker -->
<script src="${contextPath}/js/moment.min.js"></script>

<script src="${contextPath}/plugins/datepicker/bootstrap-datepicker.js"></script>
<script src="${contextPath}/plugins/datepicker/locales/bootstrap-datepicker.kr.js" charset="UTF-8"></script>
<script src="${contextPath}/plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
<!-- bootstrap time picker -->
<script src="${contextPath}/plugins/timepicker/bootstrap-timepicker.min.js"></script>

<!-- fullCalendar 2.2.5 -->
<script src="${contextPath}/plugins/fullcalendar/fullcalendar.min.js"></script>
 
 
 
</head>
 
<!-- ADD THE CLASS layout-top-nav TO REMOVE THE SIDEBAR. -->
<body class="hold-transition skin-blue layout-top-nav">
<div class="wrapper">

  <t:insertAttribute name="header"/>
  <t:insertAttribute name="content"/>
  
  <t:insertAttribute name="footer"/>
</div>
<!-- ./wrapper -->
<!-- jQuery 2.2.3 -->
<script src="/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/bootstrap/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="/plugins/iCheck/icheck.min.js"></script>
</body>
</html>
