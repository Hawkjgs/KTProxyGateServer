<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmm");
%>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>
      YWIS KT Proxy Gateway Server</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <link rel="apple-touch-icon-precomposed"  href="/dist/img/sm.png" />
  <link rel="shortcut icon" href="/dist/img/sm.ico" type="image/x-icon" />
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="/css/ionicons.min.css">
  <!-- Theme style -->
<link rel="stylesheet" href="${contextPath}/dist/css/AdminLTE.css?<%= sf.format(nowTime) %>">
  <!-- iCheck -->
  <link rel="stylesheet" href="/plugins/iCheck/square/blue.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="/js/html5shiv.min.js"></script>
  <script src="/js/respond.min.js"></script>
  <![endif]-->
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
    <a href="/login.do">
 	KT Proxy Gateway Server
    </a>
  </div>
 <div class="login-box-body">
  	   	<p class="login-box-msg">로그인</p>
		 
        

    <form action="/login_proc.do" method="post">
      <div class="form-group has-feedback">
        <input type="text" class="form-control" placeholder="아이디" name="MEM_ID" id="MEM_ID" />
        <span class="glyphicon  glyphicon-text-background form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" class="form-control" placeholder="비밀번호" name="MEM_PWD" id="MEM_PWD" />
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <div class="row">
          <!-- /.col -->
        <div class="col-xs-6">
          <button type="submit" class="btn btn-primary btn-block btn-flat">로그인</button>
        </div>
         <div class="col-xs-6">
         <a href="/swagger-ui/index.html" class="btn btn-primary btn-block btn-flat" target="_blank">API Docs</a>
        </div>
        <!-- /.col -->
      </div>
    </form>
    
</div>
<!-- /.login-box -->

<!-- jQuery 2.2.3 -->
<script src="/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/bootstrap/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="/plugins/iCheck/icheck.min.js"></script>
<script>
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
  });
  
  jQuery(document).ready(function(){
	
	


	$('#MEM_PWD').bind('keypress', function(e) {
		if(e.keyCode==13) jsLogin();
	});
	$('#MEM_ID').bind('keypress', function(e) {
		if(e.keyCode==13) $("#MEM_PWD").focus();
	});
});
  
  
  $(function () {
	 	<c:if test="${!empty error}">
		 	<c:if test="${error==-1}">
		 		alert('login member not found');
		 	</c:if>
		 	<c:if test="${error==-2}">
	 		alert('계정과 비빌번호가 일치하지 않습니다.');
	 	</c:if>
	 	</c:if>
	  });
	  
  
</script>
</body>
</html>
 
