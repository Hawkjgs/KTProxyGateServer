<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

 <form name="form1"   id="form1" method="post" action="${contextPath}/staticDay.do">
<input type="hidden"  id="flag" name="flag" value="">
<input type="hidden"  id="pageIndex" name="pageIndex" >  

 
<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
     <i class="glyphicon glyphicon-th-large"></i>  지능망 연계 내역
        <small></small>
      </h1>
      
    </section>

	<section class="content">
      <div class="row">
        <div class="col-md-12">
          <div class="box">
            <jsp:include page="./inc_tab.jsp" flush="false" />   
            <div class="box-header with-border">
              <h3 class="box-title">&nbsp;</h3>
              
                 <div class="box-tools ">
                <div class="pull-right">
                
                <div class="btn-group">
                   <input type="text" class="form-control input-sm " placeholder="조회일" name="ST_DT" id="ST_DT" readonly value="${searchMap.ST_DT}">
                  </div>
                  <div class="btn-group ">
                  	<select name="JOB_STATUS"  class="form-control input-sm ">
							<option value=""  >전체 </option>
							<option value="SUSS"  ${searchMap.JOB_STATUS =='SUSS' ? "selected":"" } >SUSS </option>
							<option value="FAIL"  ${searchMap.JOB_STATUS =='FAIL' ? "selected":"" } >FAIL </option>
							<option value="ERROR"  ${searchMap.JOB_STATUS =='ERROR' ? "selected":"" } >ERROR </option>
					</select> 
                    
                  </div>
                  <%-- <div class="btn-group ">
                  	<select name="JOB_TY"  class="form-control input-sm ">
							<option value=""  >전체 </option>
							<option value="POST"  ${searchMap.JOB_TY =='POST' ? "selected":"" } >POST </option>
							<option value="PUT"  ${searchMap.JOB_TY =='PUT' ? "selected":"" } >PUT </option> 
					</select> 
                    
                  </div> --%>
                  <div class="btn-group ">
                  	<select name="ERR_CD"  class="form-control input-sm ">
							<option value=""  >전체 </option>
							<option value="ERROR"  ${searchMap.ERR_CD =='ERROR' ? "selected":"" } >Error Code </option>
					</select> 
                    
                  </div>
                  <div class="btn-group ">
                   <input type="text" class="form-control input-sm " placeholder="검색" name="searchWrd" id="searchWrd" value="${searchMap.searchWrd}">
                  </div>
                  <div class="btn-group">
                    <a href="#" onclick="searchFrom();" class="btn btn-default " ><i class="glyphicon glyphicon-search"></i>검색 </a>
                  </div>
                  <!-- /.btn-group -->
                </div>
              </div> 
              	
				
            </div>
            <!-- /.box-header -->
            <div class="box-body">
            
              <table class="table table-bordered">
                <tr class="tr_bg">
					<th width="40" >번호</th>
					<th width="150">작업 ID</th>
					<th width="60">요청타입</th>
					<th width="150">URL</th>
					<th width="60" >코드</th>
					<th width="60" >상태</th>
					<th width="100" >요청 IP</th>
					 
					<th width="120" >등록일</th>
                </tr>
                <c:choose>
				<c:when test="${!empty resultList}">
				
				<c:forEach var="item" varStatus="i" items="${resultList}">
				
                  <tr >
                    <td>${item.IDX}</td>
                    <td>
                    <a href="#" onclick="jsJobInfo('${item.JOB_DATE}','${item.FILE_NM}');"> 
                    ${item.JOB_ID}
                    </a></td>
                   
                    <td>${item.JOB_TY}</td>
                    <td  class="left">${item.JOB_URL}</td>
                    <td>${item.ERR_CD}</td>
                    <td>${item.JOB_STATUS}</td>
                    <td>${item.REQ_IP}</td>
                    <td>${item.JOB_DT}</td>
                  </tr>
                  </c:forEach>
                  </c:when>
                  <c:otherwise>
	 			<tr>
	 				<td colspan="8"  class="center"> 조회 정보가 없습니다. </td>
	 			</tr>
	 		</c:otherwise>
                  </c:choose>
                  
               
              </table>
            </div>
            <!-- /.box-body -->
            <div class="box-footer clearfix">
             <ul class="pagination pagination-sm no-margin pull-right">
               	${renderPagination }
              </ul>
            </div>
          </div>
          <!-- /.box -->
</div>
</div>
 
</section>
</div>
</form> 

<script>
  
   function jsJobInfo(JOB_DATE ,JOB_ID ) {
	   var url = "/jobInfo.do?JOB_DATE="+JOB_DATE+"&JOB_ID="+JOB_ID;
	    var name = "jobInfo";
	    var option = "width = 1000, height = 800, top = 100, left = 200, location = no"
	    window.open(url, name, option);
	    
	   
   }
  $(function () {
    //Date picker
    $('#ST_DT').datepicker({
      autoclose: true,
      format: 'yyyy-mm-dd'
    }); 
    
    
  });
</script>