<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title> YWIS KT Proxy Gateway Server</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <link rel="apple-touch-icon-precomposed"  href="/dist/img/sm.png" />
  <link rel="shortcut icon" href="/dist/img/sm.ico" type="image/x-icon" />
  
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="${contextPath}/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${contextPath}/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${contextPath}/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="${contextPath}/dist/css/AdminLTE.min.css">
  <!-- iCheck -->
  <link rel="stylesheet" href="${contextPath}/plugins/iCheck/square/blue.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
   YWIS KT Proxy Gateway Server
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body">
  	
    <form  name="form1" action="${contextPath}/license_reg.do" method="post">
      <div class="form-group ">라이센스 권한이 없습니다. 
        
      </div>
     
      <div class="row">
        
    	<div class="col-xs-12">
    	<div class="form-group  ">
       <textarea wrap="off" style="width:100%;" rows=5 >${reqLicense }</textarea>
    	</div>
    
      </div>
      
      <div class="col-xs-12">
    	<div class="form-group  ">
       <textarea name="LICENCE_REQ" id="LICENCE_REQ" wrap="off" style="width:100%;" rows=5 ></textarea>
    	</div>
    
      </div>
      
    	<div class="col-xs-12">
          <a href="#" class="btn btn-primary btn-block btn-flat" onclick="jsRegLicense();">라이센스 등록</a>
        </div>
    	</div>
    </form>
    
</div>
</div>
<!-- /.login-box -->

<!-- jQuery 2.2.3 -->
<script src="${contextPath}/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="${contextPath}/bootstrap/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="${contextPath}/plugins/iCheck/icheck.min.js"></script>
<script>
  
   function jsRegLicense() {
	   if($("#LICENCE_REQ").val()=="") {
			alert("라이센스 정보가  입력되지 않았습니다. ");
			$("#LICENCE_REQ").focus();
			return ;
		}
	   
	   if (!confirm('등록하시겠습니까 ?')) {
				return false;
		}
		var form = document.form1;
		form.action = '/license_reg.do'  ;
		form.submit();
		
	    
	   
   } 
</script>
</body>
</html>
 
