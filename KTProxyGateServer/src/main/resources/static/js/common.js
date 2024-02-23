   
function openWin(url, title){  
    window.open(url, "", "width=1000, height=700, toolbar=no, menubar=no, scrollbars=yes, resizable=yes" );  
}  

function openWin2(url, title){  
    window.open(url, "", "width=600, height=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes" );  
}  
function gotoPage(pageNo){
	var pagingForm = document.form1;
	pagingForm.pageIndex.value = pageNo;
	pagingForm.submit();
}

function searchFrom(){
	var pagingForm = document.form1;
	pagingForm.submit();
}
 

function isValidImgFile(param) {
	var ext = $(param).val().split('.').pop().toLowerCase();
      if($.inArray(ext, ['gif','png','jpg','jpeg']) == -1) {
    	  	alert('gif,png,jpg,jpeg 파일만 업로드 할수 있습니다.');
    	  	return false;
      }
	 return true;

}

function jsAjaxCodeList(path,id, value,all_nm) {
		var parms="SEQ="+value+"&ALL_NM="+all_nm;
		$.ajax({ 
		    url: path, 
		    type: 'POST', 
		  	data: parms, 
		    success: function(data) { 
		    		$(id).text("");	
		    		$(id).append(data);	
		 	},
		    error:function(data,status,er) { 
		       
		    }
		});
 }
	
function jsDefault(id,all_nm) {
	$(id).text("");	
	$(id).append('<option value="">'+all_nm+'</option>');	
}