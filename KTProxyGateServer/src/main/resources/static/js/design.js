$(document).ready(function(){
	docHeight();

}); 

$(window).resize(function(){
	docHeight();
}); 

//docment height
function docHeight(){
	var containertH = document.documentElement.clientHeight;
	var leftmH = $(".leftWrap").outerHeight(); 
	if(leftmH>containertH){
		$("#container").css("min-height",leftmH);
	}else{
		$("#container").css("min-height",'100%');
	}
}

//메뉴관리
function menuList(){
	$('.depth_div li > a').on('click', function(){
		var menuLi = $(this).parent().siblings().find('> a');
		menuLi.removeClass('active');
		$(this).addClass('active');
	});
}

//Lnb 메뉴 열기/닫기
function lnbM(){
	$(".current").parents("ul").show();
	

	$('.sideNavi > li > a').on('click', function(){
		var dep2Ul = $(this).next('ul');
		if(dep2Ul.length != 0){
			if(dep2Ul.is(':visible')){
				dep2Ul.hide();
			}else{
				dep2Ul.show();
			}		
		}else{
			$('.sideNavi > li > ul:visible').hide();
			$('.sideNavi li').removeClass("active");
			$('.sideNavi li a').removeClass("current");
			$(this).parent("li").addClass("active")
		}

	});
	
	$('.sideNavi > li > ul > li > a').on("click", function(){
		$('.sideNavi li a').removeClass("current");
		$(this).addClass("current");
		$('.sideNavi li').removeClass("active");
		$('.sideNavi > li > ul').hide();
		$(this).parent().parent().parent().addClass("active");
		$(this).parent().parent().show();
		
	});

	$('.sideNavi > li > ul > li > ul > li > a').on("click", function(){
		$('.sideNavi li').removeClass("active");
		$(this).parent().parent().parent().parent().parent().addClass("active");
		$('.sideNavi > li:not(".active") > ul').hide();
		$('.sideNavi li a').removeClass("current");
		$(this).addClass("current");
	
	});	
}

//탭메뉴
function tabmenu(){
	var tabBtn = $('.tabmenu a');
	tabBtn.on('click', function(e){
		var tabTarget = $(this).attr('href');
		$('.tabmenu a').removeClass('active');
		$(this).addClass('active');
		$('.tab_panel').removeClass('active');
		$(tabTarget).addClass('active');
		e.preventDefault();
	});
}

menuList();
lnbM();
tabmenu();