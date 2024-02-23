$(function(){
	
	 
	var dropbox = $('#dropbox'),
		message = $('.message', dropbox);
	
	dropbox.filedrop({
		// The name of the $_FILES entry:
		paramname:'IMG_NM',
		
		maxfiles: 20,
    	maxfilesize: 10,
		url: '/imageUploadAjax.do',
		
		uploadFinished:function(i,file,response){
			$.each( response, function( key, val ) {
				 
				if(key=='imgUrl') {
			    	$.data(file).children(".imgUrl").text(val);
			    	$.data(file).children(".img").attr('src',val);
			    	 
		     	
			   }
				if(key=='fileName') {
			    	$.data(file).children(".fileName").text(val);
			    	
			     
			 	
			   }
				if(key=='orgName') {
			    	$.data(file).children(".alt").text(val);
			    	$.data(file).children(".progressText").children(".file_nm").text(val); 
			 //   	$.data(file).attr("title",val);
				}
				
				if(key=='exist') {
				 	if(val=='Y') {
				 		$.data(file).addClass('error');
				 	}
			 		else $.data(file).addClass('done');
			 	 
				    $.data(file).children(".exits").text(val);
				    
				}
				
			    
			});
			//alert($.data(file).css("fileName").text()); 
		},
		
    	error: function(err, file) {
			switch(err) {
				case 'BrowserNotSupported':
					showMessage('Your browser does not support HTML5 file uploads!');
					break;
				case 'TooManyFiles':
					alert('Too many files! Please select 20 at most!');
					break;
				case 'FileTooLarge':
					alert(file.name+' is too large! Please upload files up to 10mb.');
					break;
				default:
					alert(err);
					break;
			}
		},
		
		// Called before each upload is started
		beforeEach: function(file){
			if(!file.type.match(/^image\//)){
				//alert('Only images are allowed!');
				// Returning false will cause the
				// file to be rejected
				return false;
			}
		},
		
		uploadStarted:function(i, file, len){
			createImage(file);
		},
		
		progressUpdated: function(i, file, progress) {
			$.data(file).find('.progress').width(progress);
		}
    	 
	});
	
	var template = '<li  onclick="selectImg(this)">'+
							'<img class="img" />'+
							'<span class="uploaded"></span>'+
							'<span class="fileName"></span>'+
							'<span class="alt"></span>'+
							'<span class="exits"></span>'+
							'<input type="checkbox" title="선택"/>'+
						''+
						'<div class="progressHolder">'+
							'<div class="progress"></div>'+
						'</div>'+
						'<div class="progressText">'+
							'<span class="file_nm"></span>'+
						'</div>'+
					'</li>'; 
	
	function createImage(file){

		 
		var preview = $(template), 
			//li = $('li', preview),
			image = $('img', preview);
			
			
		var reader = new FileReader();
		
		image.width = 100;
		image.height = 100;
		
		reader.onload = function(e){
			
			// e.target.result holds the DataURL which
			// can be used as a source of the image:
			
			//image.attr('src',e.target.result);
		};
		//li.attr('title','test');
		// Reading the file as a DataURL. When finished,
		// this will trigger the onload function above:
		reader.readAsDataURL(file);
		
		message.hide();
		
		preview.appendTo($("#sortable_box"));
		 
		
		// Associating a preview container
		// with the file, using jQuery's $.data():
		
		$.data(file,preview);
	}

	function showMessage(msg){
	//	message.html(msg);
	}

});