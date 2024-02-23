   
function openWin(url, title){  
    window.open(url, "", "width=1000, height=700, toolbar=no, menubar=no, scrollbars=yes, resizable=yes" );  
}  

function openWin2(url, title){  
    window.open(url, "", "width=600, height=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes" );  
}  
function gotoPage(pageNo){
	var pagingForm = document.form;
	pagingForm.pageIndex.value = pageNo;
	pagingForm.submit();
}

function searchFrom(){
	var pagingForm = document.form;
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
  

function emailVaildChk(email){
 var regEmail = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
 if( email=='' ){
     alert('이메일주소를 입력 해 주세요');
     return false;
 } else {
     if(!regEmail.test(email)) {
         alert('이메일은 영문/숫자/특수문자(.,-,_)만 입력 가능합니다');
         return false;
     }
 }
 return true;
}
