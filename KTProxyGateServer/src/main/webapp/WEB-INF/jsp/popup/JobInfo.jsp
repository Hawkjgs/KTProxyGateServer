<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 <% pageContext.setAttribute("enter", "\n"); %> 
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
 

<form name="form1" method="post"   enctype="multipart/form-data">	
 
<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
      	<i class="glyphicon glyphicon-th-large"></i> 작업 상세 내역
        <small></small>
      </h1>
       
    </section>

 <section class="content">
      <div class="row">
        <div class="col-md-12">
          <div class="box">
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered">
        	 <tr>
					<th width="200">작업 ID    </th>
				    <td  width="40%" class="left">${JOB_INFO.JOB_ID } </td>
				    <th  width="200">URL    </th>
				    <td   width="40%"  class="left">${JOB_INFO.JOB_URL } </td>
				    
			</tr>
			<tr>
					<th >상태코드    </th>
				    <td  class="left">${JOB_INFO.ERR_CD } </td>
				    <th >작업 상태   </th>
				    <td  class="left">${JOB_INFO.JOB_STATUS } </td>
			</tr>
			<tr>
					<th >요청 IP    </th>
				    <td  class="left">${JOB_INFO.REQ_IP } </td>
				    <th  >작업 구분   </th>
				    <td  class="left">${JOB_INFO.JOB_TY } </td>
			</tr>
			<tr>
					<th>파일명    </th>
				    <td   class="left">${JOB_INFO.FILE_NM } </td>
				    <th>요청일</th>
				    <td   class="left">${JOB_INFO.JOB_DT } </td>
			</tr>
			 
			 <tr>
				    <td  colspan=4 class="left"> 
				    <textarea wrap="off" style="width:100%;" rows=25 >${JOB_JSON }</textarea>
			 </table>
            </div>
        </div>
	</div>
</div>
   
 </section>
 
 
</div>
 </form>
 

