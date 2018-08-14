﻿/**
 * Created by qing.wang on 2015/06/17.
 */


$(function(){
    var controller = {

        init: function() {
        	if (amGloble.config.debug == true) alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$page4page1 = $(".page1");
        	this.$page4page2 = $(".page2");
        	this.$page4page2_2 = $(".page2-2");
        	this.$page4page3 = $(".page3");
        	this.$page4page4 = $(".page4");
        	this.$page4page5 = $(".page5");
        	this.$page4page6 = $(".page6");
        	this.$page4rule = $(".rule_pop");
        	this.$page4overlay = $(".overlay");
        	this.$page4failShow = $(".fail_pop");
        	this.$page4successShow = $(".success_pop");
        	this.$page4win = $(".draw_pop.tel");
        	this.$page4fail = $(".draw_pop.Instagram");
        	this.$page4submitSuceess = $(".pop.submit_pop");
        	this.$page4End = $(".finish_pop");
        	
        	this.$touch4myCoupon_light = $(".ticket_button");
        	this.$touch4myCoupon_light1 = $(".ticket_button.light1");
        	this.$touch4myCoupon_light2 = $(".ticket_button.light2");
        	this.$touch4myCoupon_light3 = $(".ticket_button.light3");
        	this.$touch4myCoupon_gray = $(".ticket_button.gray");
        	
        	
        	this.$touch4winPhone = $(".ticket_button.mobile_button");
        	this.$touch4Rule = $(".rule_button");
        	this.$touch4product = $(".about_button,.icecream.pro");
        	this.$touch4back = $(".back,.close_icon,.page6");
        	
        	this.$touch4close1 = $(".close_button.clo1");
        	this.$touch4close2 = $(".close_button.clo2");
        	this.$touch4close3 = $(".close_button.clo3");

        	this.$touch4home = $(".refresh_button");
        	this.$touch4failShow = $(".show_button.fail");
        	this.$touch4successShow = $(".show_button.success");
        	
        	this.$touch4collect = $(".save_button");
        	
        	this.$touch4game = $(".start_button");
        	this.$touch4gameGo = $(".page.page2");
        	this.$touch4submit = $(".submit");
        	
        	this.$txt4tel = $("#mobile");
        	
        	
			this.$txt4tel.bind("touchstart click",function(){
    			_this.$txt4tel.focus();
    		});
        	
        	this.$page4submitSuceess.bind("touchstart click",function(){
        		_this.$page4submitSuceess.hide();
        		_this.$page4overlay.hide();
        	});
        	this.$touch4myCoupon_light1.bind("touchstart click",function(){
        		_this.$page4page1.hide();
        		_this.$touch4close1.show();
        		_this.$touch4close2.hide();
        		_this.$touch4close3.hide();
        		_this.$page4page5.show();
        	});
        	this.$touch4myCoupon_light2.bind("touchstart click",function(){
        		_this.$page4page3.hide();
        		_this.$touch4close2.show();
        		_this.$touch4close1.hide();
        		_this.$touch4close3.hide();
        		_this.$page4page5.show();
        	});
        	this.$touch4myCoupon_light3.bind("touchstart click",function(){
        		_this.$page4page4.hide();
        		_this.$touch4close3.show();
        		_this.$touch4close2.hide();
        		_this.$touch4close1.hide();
        		_this.$page4page5.show();
        	});
        	this.$touch4Rule.bind("touchstart click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看规则", "wt.msg", _this.getOpenid());
            	} 
        		_this.$page4overlay.show();
        		_this.$page4rule.show();
        		new IScroll("#wrapper");
        	});
        	
        	this.$touch4product.bind("touchstart click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看产品信息", "wt.msg", _this.getOpenid());
            	} 
        		_this.$page4page1.hide();
        		_this.$page4page6.show();
        	});
        	
        	this.$touch4back.bind("touchstart click",function(){
        		_this.$page4page6.hide();
        		_this.$page4rule.hide();
        		_this.$page4overlay.hide();
        		_this.$page4page1.show();
        	});
        	this.$touch4close1.bind("touchstart click",function(){
        		_this.$page4page5.hide();
        		_this.$page4page1.show();
        	});
        	this.$touch4close2.bind("touchstart click",function(){
        		_this.$page4page5.hide();
        		_this.$page4page3.show();
        	});
        	this.$touch4close3.bind("touchstart click",function(){
        		_this.$page4page5.hide();
        		_this.$page4page4.show();
        	});
        	
        	this.$touch4home.bind("touchstart click",function(){
//        		_this.toTarget();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户返回活动首页", "wt.msg", _this.getOpenid());
	        	}
        		localStorage.removeItem('wxqr_scene_id');
        		_this.initButton();
        		_this.resetGame();
        		_this.$page4page3.hide();
        		_this.$page4page4.hide();
        		_this.$page4page1.show();
        	});
        	this.$touch4failShow.bind("touchstart click",function(){
        		location.href=amGloble.config.selfRef + "pages/hyjt1506/index.html?tag="+sessionStorage.getItem("tag")+"&normalShare=1&refresh_flag=true"+"&share_type=fail";
        	});
        	this.$touch4successShow.bind("touchstart click",function(){
        		location.href=amGloble.config.selfRef + "pages/hyjt1506/index.html?tag="+sessionStorage.getItem("tag")+"&normalShare=1&refresh_flag=true"+"&share_type=success";
        	});
        	
        	this.$page4failShow.bind("touchstart click",function(){
        		_this.shareRecord();
        	});
        	this.$page4successShow.bind("touchstart click",function(){
        		_this.shareRecord();
        	});
        	
        	this.$page4fail.bind("touchstart click",function(){
        		_this.$page4fail.hide();
        		_this.$page4overlay.hide();
        	});
        	
        	this.$touch4submit.bind("touchstart click",function(){
        		_this.winPhone();
        	});
        	this.$touch4game.bind("touchstart click",function(){
        		_this.$page4page1.hide();
        		_this.$page4page2.show();
        	});
        	this.$touch4collect.bind("touchstart click",function(){
        		_this.collectToCardBag();
        	});
        	this.$touch4gameGo.bind("touchstart click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户开始游戏", "wt.msg", _this.getOpenid());
            	} 
        		_this.$page4page2.hide();
        		_this.$page4page2_2.show();
        		_this.initGame();
        	});
        	
        	this.$touch4winPhone.bind("touchstart click",function(){
        		if (window._tag) { 
        			_tag.dcsMultiTrack("wt.event", "用户补填手机号码", "wt.msg", _this.getOpenid());
        		} 
        		_this.$page4overlay.show();
        		_this.$page4win.show();
        	});
        	
        	
        	
        	var refresh_flag = sessionStorage.getItem("refresh_flag");
        	
        	var share_type = sessionStorage.getItem("share_type");
        	if(!(refresh_flag=="" || refresh_flag==null || refresh_flag=="null")){
        		_this.loadImg(true);
        		if(share_type == "success"){
        			$("title").html("眼神要如此犀利，才能轻轻松松享半价！");
        			_this.$page4page3.show();
        			_this.$page4overlay.show();
        			_this.$page4successShow.show();
        		}else{
        			$("title").html("来这里拼眼力，还能畅享半价哦！");
        			_this.$page4page4.show();
        			_this.$page4overlay.show();
        			_this.$page4failShow.show();
        		};
        	}else{
        		_this.loadImg(false);
        	}
        	
        	_this.initButton();
        	_this.bindWChatAPI();
        	
        	/*var isOutOfDate = sessionStorage.getItem("isOutOfDate");
        	if(isOutOfDate == "true"){
        		_this.$page4overlay.show();
        		_this.$page4End.show();
        	}*/
        },
        
        initButton : function(){
        	var _this = this;
        	var promoCode = sessionStorage.getItem("promoCode");
        	if(promoCode == "" || promoCode==null || promoCode=="null"){
        		_this.$touch4myCoupon_light.hide();
        		_this.$touch4myCoupon_gray.show();
        	}else{
        		_this.$touch4myCoupon_light.show();
        		_this.$touch4myCoupon_gray.hide();
        	};
        	
        	var winNotPhone = sessionStorage.getItem("winNotPhone");
        	if(winNotPhone == "true" || winNotPhone == true){
        		_this.$touch4winPhone.removeAttr("style");
        	};
        },
        
        checkTelNo: function() {
        	var _this = this;
			var telNo = $.trim(_this.$txt4tel.val());
			var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
			
			var resulst = telNo.match(telRegex1) || telNo.match(telRegex2) || telNo.match(telRegex3);
			
			if(!resulst){
				_this.$txt4tel.val("");
				_this.$txt4tel.focus();
				
				if(telNo.length==0){
					$("#tips").show();
					$("#tips").fadeOut(1000);
				}
			}
			return resulst;
		},
        
		resetGame : function(){
			$(".rotate > .tong :eq(0)").removeClass("tong2");
			$(".rotate > .tong :eq(0)").addClass("tong1");
			$(".rotate > .tong :eq(0)").removeAttr("style");
			$(".rotate > .tong :eq(1)").removeAttr("style");
			$(".rotate > .tong :eq(2)").removeAttr("style");
			$(".rotate > .tong :eq(2)").removeClass("tong2");
			$(".rotate > .tong :eq(2)").addClass("tong3");
			/*$("#handmove").removeAttr("style");*/
			$(".page2_p1").css("opacity",0);
			$(".realImg").show();
			$(".tong").hide();
		},
		
		initGame : function(){
        	var _this = this;
        	
        	//图片的宽高
    		var wid = $(".rotate .tong").width();
    		var hei = 1.67*wid/2;
        	//中心点横坐标
    		var dotLeft = $(".rotate").width()/2 - wid/2;
    		//中心点纵坐标
    		var dotTop = $(".rotate").height()/2 - hei/1.5;
    		//椭圆长边
    		var a = dotLeft;
    		//椭圆短边
    		var b = dotTop*2/3;

    		//每一个BOX对应的角度;
    		var avd = 360/$(".rotate .tong").length;
    		//每一个BOX对应的弧度;
    		var ahd = avd*Math.PI/180;
    		//运动的速度
    		var speed = 2;
    		//总的TOP值
    		var totTop = dotTop;
    		
    		//运动函数
    		var fun_animat = function(){
//    			$(".realImg").hide();
    			$(".tong div").attr("class","tong1-1");
    			$(".tong").attr("class","tong tong2 fadeInDownBig");
    			//运运的速度
    			speed+=1.5;
    			//运动距离，即运动的弧度数;
    			var ainhd = speed*Math.PI/180*15;
    			//按速度来定位DIV元素
    			$(".rotate .tong").each(function(index, element){
    				var allpers = (Math.cos((ahd*index+ainhd))*b+dotTop)/totTop;
    				$(this).css({
    					"left":Math.sin((ahd*index+ainhd))*a+dotLeft,
    					"top":Math.cos((ahd*index+ainhd))*b+dotTop,
    					"z-index":Math.ceil(allpers*10),
    					"opacity":1
    				});
         		});
    		};
    		var fun_animat_anti = function(){
    			speed = speed<2?2:speed;
    			//运运的速度
    			speed+=1.5;
    			//运动距离，即运动的弧度数;
    			var ainhd = speed*Math.PI/180*15;
    			//按速度来定位DIV元素
    			$(".rotate .tong").each(function(index, element){
    				var allpers = (Math.cos((ahd*index+ainhd))*b+dotTop)/totTop;
    				$(this).css({
    					"left":Math.sin(-(ahd*index+ainhd))*a+dotLeft,
    					"top":Math.cos(-(ahd*index+ainhd))*b+dotTop,
    					"z-index":Math.ceil(allpers*10),
    					"opacity":1
    				});
    			});
    		};
    		
    		//运动过后绑定的事件
    		var bind_event = function(){
    			$(".realImg").removeClass("zoomOut");
    			$(".tong").removeClass("fadeInDownBig");
				$(".realImg").hide();
				$(".page2_p1").css("opacity",1);
    			
    			/*var $hand = $("#handmove");*/
//    			var hand_X = $hand[0].offsetLeft;
//    			var hand_Y = $hand[0].offsetTop;
//    			var hand_W = $hand[0].offsetWidth;
//    			var hand_H = $hand[0].offsetHeight;
    			
    			/*var $tong1 = $(".rotate .tong")[0];
    			var $tong2 = $(".rotate .tong")[1];
    			var $tong3 = $(".rotate .tong")[2];
    			var tong1_X = $tong1.offsetLeft; 
    			var tong1_Y = $tong1.offsetTop;
    			var tong1_W = $tong1.offsetWidth;
    			var tong1_H = $tong1.offsetHeight;
    			
    			
    			var tong2_X = $tong2.offsetLeft; 
    			var tong2_Y = $tong2.offsetTop;
    			var tong2_W = $tong2.offsetWidth;
    			var tong2_H = $tong2.offsetHeight;
    			
    			var tong3_X = $tong3.offsetLeft; 
    			var tong3_Y = $tong3.offsetTop;
    			var tong3_W = $tong3.offsetWidth;
    			var tong3_H = $tong3.offsetHeight;
    			
    			console.info(hand_X,hand_Y,hand_W,hand_H);
    			console.info(tong1_X,tong1_Y,tong1_W,tong1_H);
    			console.info(tong2_X,tong2_Y,tong2_W,tong2_H);
    			console.info(tong3_X,tong3_Y,tong3_W,tong3_H);*/
    			
    			
    			$(".rotate .tong").each(function(index, element){
					var $this = $(this);
					console.dir($this);
					
					if(index==0){
						$(this).bind("touchstart click",function(){
							/*$hand.css({
								'transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (0) +'px)',
								'-webkit-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (0) +'px)',
								'-moz-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (0) +'px)',
								'-o-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (0) +'px)',
								'-ms-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (0) +'px)',
								'transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)'
								'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
							});
							setTimeout(function(){
								$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/ice.png");
//								show_result();
								$hand.css({
									'transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (-(hand_Y-tong1_Y)-(hand_H-tong1_H)/2) +'px)',
									'-webkit-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (-(hand_Y-tong1_Y)-(hand_H-tong1_H)/2) +'px)',
									'-moz-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (-(hand_Y-tong1_Y)-(hand_H-tong1_H)/2) +'px)',
									'-o-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (-(hand_Y-tong1_Y)-(hand_H-tong1_H)/2) +'px)',
									'-ms-transform' : ' translate('+(-(hand_X-tong1_X)-(hand_W-tong1_W)/2) +'px,'+ (-(hand_Y-tong1_Y)-(hand_H-tong1_H)/2) +'px)',
									'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
								});
							},350);*/
							$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/ice.png");
							$(".rotate .tong").unbind("touchstart click");
							setTimeout(function(){
								_this.$page4page2_2.hide();
								_this.initButton();
								_this.askCoupon(false);
								_this.$page4page4.show();
								$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/barrel.png");
							},1600);
						});
					}else if (index==1){
						$(this).bind("touchstart click",function(){
							/*$hand.css({
								'transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (0) +'px)',
								'-webkit-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (0) +'px)',
								'-moz-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (0) +'px)',
								'-o-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (0) +'px)',
								'-ms-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (0) +'px)',
								'transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)'
								'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
							});
							setTimeout(function(){
								$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/icecream.png");
//								show_result();
								$hand.css({
									'transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (-(hand_Y-tong2_Y)-(hand_H-tong2_H)/2) +'px)',
									'-webkit-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (-(hand_Y-tong2_Y)-(hand_H-tong2_H)/2) +'px)',
									'-moz-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (-(hand_Y-tong2_Y)-(hand_H-tong2_H)/2) +'px)',
									'-o-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (-(hand_Y-tong2_Y)-(hand_H-tong2_H)/2) +'px)',
									'-ms-transform' : ' translate('+(-(hand_X-tong2_X)-(hand_W-tong2_W)/2) +'px,'+ (-(hand_Y-tong2_Y)-(hand_H-tong2_H)/2) +'px)',
									'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
								});
							},350);*/
							$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/icecream.png");
							$(".rotate .tong").unbind("touchstart click");
							setTimeout(function(){
								_this.$page4page2_2.hide();
								_this.initButton();
								_this.askCoupon(false);
								_this.$page4page3.show();
								$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/barrel.png");
							},1600);
						});
					}else{
						$(this).bind("touchstart click",function(){
							/*$hand.css({
								'transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (0) +'px)',
								'-webkit-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (0) +'px)',
								'-moz-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (0) +'px)',
								'-o-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (0) +'px)',
								'-ms-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (0) +'px)',
								'transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88,-0.28, 0.21, 0.8)'
								'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
								'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
							});
							setTimeout(function(){
								$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/tea.png");
//								show_result();
								$hand.css({
									'transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (-(hand_Y-tong3_Y)-(hand_H-tong3_H)/2) +'px)',
									'-webkit-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (-(hand_Y-tong3_Y)-(hand_H-tong3_H)/2) +'px)',
									'-moz-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (-(hand_Y-tong3_Y)-(hand_H-tong3_H)/2) +'px)',
									'-o-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (-(hand_Y-tong3_Y)-(hand_H-tong3_H)/2) +'px)',
									'-ms-transform' : ' translate('+(-(hand_X-tong3_X)-(hand_W-tong3_W)/2) +'px,'+ (-(hand_Y-tong3_Y)-(hand_H-tong3_H)/2) +'px)',
									'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
									'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
								});
							},350);*/
							$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/tea.png");
							$(".rotate .tong").unbind("touchstart click");
							setTimeout(function(){
								_this.$page4page2_2.hide();
								_this.initButton();
								_this.askCoupon(true);
    							_this.$page4page4.show();
    							$this.find("img").attr("src", amGloble.config.selfRef + "images/hyjt1506/barrel.png");
							},1600);
						});
					};
				});
    		}
    		
    		setTimeout(function(){
    			var setAnimate = setInterval(fun_animat,100);
    			setTimeout(function(){
    				clearInterval(setAnimate);
    				$(".tong").addClass("fadeInDownBig");
    				$(".tong").show();
    				setTimeout(function(){
        				$(".realImg").addClass("zoomOut");
        			},500);
    			},500); 
			},1000);
    		
    		//2.35S后开始正旋转0.92S 总共3.27S
    		var p_rotate = function(){
    			var setAnimate = setInterval(fun_animat,102);
    			setTimeout(function(){
    				clearInterval(setAnimate);
    			},918);
    		}
    		setTimeout(p_rotate,2350);
    		
    		//3.5S后随机交叉2.1s 总共5.6S
    		var cross_animation = function(){
//    			console.dir($(".rotate .tong")[0]);
    			
    			var $tong1 = $(".rotate .tong")[0];
    			var $tong2 = $(".rotate .tong")[1];
    			var $tong3 = $(".rotate .tong")[2];
    			
    			var $tong1_1= $(".rotate > .tong > .tong1-1 :eq(0)");
    			var $tong2_2= $(".rotate > .tong > .tong1-1 :eq(1)");
    			var $tong3_3= $(".rotate > .tong > .tong1-1 :eq(2)");
    			var tong1_X = $tong1.offsetLeft; 
    			var tong1_Y = $tong1.offsetTop;
    			var tong2_X = $tong2.offsetLeft; 
    			var tong2_Y = $tong2.offsetTop;
    			var tong3_X = $tong3.offsetLeft; 
    			var tong3_Y = $tong3.offsetTop;
    			
//    			console.info(tong1_X,tong1_Y,tong2_X,tong2_Y,tong3_X,tong3_Y);
    			
//    			1【1】 <--> 2【2】互换位置
    			setTimeout(function(){
    				$tong1_1.css({
						'transform' : ' translate('+(-(tong1_X-tong2_X)) +'px,'+ (-(tong1_Y-tong2_Y)) +'px)',
						'-webkit-transform' : ' translate('+(-(tong1_X-tong2_X)) +'px,'+ (-(tong1_Y-tong2_Y)) +'px)',
						'-moz-transform' : ' translate('+(-(tong1_X-tong2_X)) +'px,'+ (-(tong1_Y-tong2_Y)) +'px)',
						'-o-transform' : ' translate('+(-(tong1_X-tong2_X)) +'px,'+ (-(tong1_Y-tong2_Y)) +'px)',
						'-ms-transform' : ' translate('+(-(tong1_X-tong2_X)) +'px,'+ (-(tong1_Y-tong2_Y)) +'px)',
						'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
					});
    				
    				$tong2_2.css({
    					'transform' : ' translate('+(-(tong2_X-tong1_X)) +'px,'+ (-(tong2_Y-tong1_Y)) +'px)',
    					'-webkit-transform' : ' translate('+(-(tong2_X-tong1_X)) +'px,'+ (-(tong2_Y-tong1_Y)) +'px)',
    					'-moz-transform' : ' translate('+(-(tong2_X-tong1_X)) +'px,'+ (-(tong2_Y-tong1_Y)) +'px)',
    					'-o-transform' : ' translate('+(-(tong2_X-tong1_X)) +'px,'+ (-(tong2_Y-tong1_Y)) +'px)',
    					'-ms-transform' : ' translate('+(-(tong2_X-tong1_X)) +'px,'+ (-(tong2_Y-tong1_Y)) +'px)',
    					'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
    				});
    				
    			},100);
//    			3【3】 <--> 1【2】 互换位置
    			setTimeout(function(){
    				$tong3_3.css({
						'transform' : ' translate('+(-(tong3_X-tong1_X)) +'px,'+ (-(tong3_Y-tong1_Y)) +'px)',
						'-webkit-transform' : ' translate('+(-(tong3_X-tong1_X)) +'px,'+ (-(tong3_Y-tong1_Y)) +'px)',
						'-moz-transform' : ' translate('+(-(tong3_X-tong1_X)) +'px,'+ (-(tong3_Y-tong1_Y)) +'px)',
						'-o-transform' : ' translate('+(-(tong3_X-tong1_X)) +'px,'+ (-(tong3_Y-tong1_Y)) +'px)',
						'-ms-transform' : ' translate('+(-(tong3_X-tong1_X)) +'px,'+ (-(tong3_Y-tong1_Y)) +'px)',
						'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
						'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
					});
    				$tong2_2.css({
    					'transform' : ' translate('+(-(tong2_X-tong3_X)) +'px,'+ (-(tong2_Y-tong3_Y)) +'px)',
    					'-webkit-transform' : ' translate('+(-(tong2_X-tong3_X)) +'px,'+ (-(tong2_Y-tong3_Y)) +'px)',
    					'-moz-transform' : ' translate('+(-(tong2_X-tong3_X)) +'px,'+ (-(tong2_Y-tong3_Y)) +'px)',
    					'-o-transform' : ' translate('+(-(tong2_X-tong3_X)) +'px,'+ (-(tong2_Y-tong3_Y)) +'px)',
    					'-ms-transform' : ' translate('+(-(tong2_X-tong3_X)) +'px,'+ (-(tong2_Y-tong3_Y)) +'px)',
    					'transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-webkit-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-moz-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-o-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)',
    					'-ms-transition' : 'all 0.3s cubic-bezier(0.88, 0.04, 0.24, 0.68)'
    				});
    			},1000);
    			
    		}
    		setTimeout(cross_animation,3500);
    		
    		//5.75S后开始逆旋转0.9S 总共6.65S
    		var r_rotate = function(){
    			var setAnimate = setInterval(fun_animat_anti,100);
    			var $tong1_1= $(".rotate > .tong > .tong1-1 :eq(0)");
    			var $tong2_2= $(".rotate > .tong > .tong1-1 :eq(1)");
    			var $tong3_3= $(".rotate > .tong > .tong1-1 :eq(2)");
    			$tong1_1.attr("style","");
    			$tong2_2.attr("style","");
    			$tong3_3.attr("style","");
    			
    			var deviceType = _this.getDeviceType();
    			if(deviceType == 0 || deviceType == "0"){
    				//对于Android
    				setTimeout(function(){
    					clearInterval(setAnimate);
    					bind_event();
    				},800);
    			}else{
    				//对于iphone 或者 wp
    				setTimeout(function(){
    					clearInterval(setAnimate);
    					bind_event();
    				},900);
    			}
    			
    		}
    		setTimeout(r_rotate,5750);
        },

        loadImg : function(show_flag){
        	var _this = this;
        	var imgArr = [
        	              amGloble.config.selfRef+"images/hyjt1506/1.png",
                          amGloble.config.selfRef+"images/hyjt1506/about_button.png",
                      	  amGloble.config.selfRef+"images/hyjt1506/back.png",
                          amGloble.config.selfRef+"images/hyjt1506/barrel.png",
                          amGloble.config.selfRef+"images/hyjt1506/body.jpg",
                          amGloble.config.selfRef+"images/hyjt1506/close_p.png",
                          amGloble.config.selfRef+"images/hyjt1506/close.png",
                          amGloble.config.selfRef+"images/hyjt1506/closeBtn.png",
                          amGloble.config.selfRef+"images/hyjt1506/desc.png",
                          amGloble.config.selfRef+"images/hyjt1506/fail_pop.png",
                          amGloble.config.selfRef+"images/hyjt1506/footer.png",
                          amGloble.config.selfRef+"images/hyjt1506/hand.png",
                          amGloble.config.selfRef+"images/hyjt1506/ice_cream.png",
                          amGloble.config.selfRef+"images/hyjt1506/ice.png",
                          amGloble.config.selfRef+"images/hyjt1506/icecream.png",
                          amGloble.config.selfRef+"images/hyjt1506/lid_coat.png",
                          amGloble.config.selfRef+"images/hyjt1506/linkPic.jpg",
                          amGloble.config.selfRef+"images/hyjt1506/look_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/page1_ice_crream.png",
                          amGloble.config.selfRef+"images/hyjt1506/page1_p.png",
                          amGloble.config.selfRef+"images/hyjt1506/page1.jpg",
                          amGloble.config.selfRef+"images/hyjt1506/page2_p.png",
                          amGloble.config.selfRef+"images/hyjt1506/page2_p1.png",
                          amGloble.config.selfRef+"images/hyjt1506/page2.jpg",
                          amGloble.config.selfRef+"images/hyjt1506/page3_img.png",
                          amGloble.config.selfRef+"images/hyjt1506/page3_p.png",
                          amGloble.config.selfRef+"images/hyjt1506/page4_img.png",
                          amGloble.config.selfRef+"images/hyjt1506/page4_p.png",
                          amGloble.config.selfRef+"images/hyjt1506/refresh_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/rule_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/rule.png",
                          amGloble.config.selfRef+"images/hyjt1506/show_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/start_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/success_pop.png",
                          amGloble.config.selfRef+"images/hyjt1506/submit.png",
                          amGloble.config.selfRef+"images/hyjt1506/tea.png",
                          amGloble.config.selfRef+"images/hyjt1506/ticket_button_gray.png",
                          amGloble.config.selfRef+"images/hyjt1506/ticket_button.png",
                          amGloble.config.selfRef+"images/hyjt1506/ticket.png"
                          ];
            preloadimg(imgArr,function(){
                $('#loading').hide();
                if(!show_flag){
                	_this.$page4page1.show();
                }else{
                	_this.$page4page1.hide();
                }
            });
        },
        
        bindWChatAPI : function(){
        	   var jsApiList = ['showOptionMenu']; 
    		   construct(amGloble.config.appid,  amGloble.config.selfRef + "pages/hyjt1506/index.html", jsApiList);
        },
        
        askCoupon : function(type){
        	var _this = this;
        	
        	if(type){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户猜中结果", "wt.msg", _this.getOpenid());
            	}
        	}else{
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户猜错结果", "wt.msg", _this.getOpenid());
            	}
        	}
        	
        	var normalShare_ =  sessionStorage.getItem("normalShare");
        	if(normalShare_=="" || normalShare_=="null" || normalShare_ == null){
        		normalShare_ = "";
        	}
        	var tag_ =  sessionStorage.getItem("tag");
        	if(tag_=="" || tag_=="null" || tag_ == null){
        		tag_ = "";
        	}
        	
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			guessRight : type,
        			tag : tag_,
        			normalShare : normalShare_
        		};
        	if (amGloble.config.debug == true) alert("[-a02- input]" + JSON.stringify(opt));
        	amGloble.api.a02.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a02- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        		} else {
        			sessionStorage.setItem("tag", ret.content.data.tag); 
        			sessionStorage.setItem("promoCode", ret.content.data.promoCode); 
        		}
        	}, "application/json; charset=utf-8");
        },
        
        collectToCardBag : function(){
        	var _this = this;
        	var opt = {
        			promoCode : sessionStorage.getItem("promoCode")
        		};
        	if (amGloble.config.debug == true) alert("[-a03- input]" + JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a03- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户查看卡包", "wt.msg", _this.getOpenid());
                	}
        			location.href = "http://crmminisite.verystar.cn/wxcard/promo?key="+ret.content.data.data+"&wechat_card_js=1";
        		}
        	}, "application/json; charset=utf-8");
        },
        
        shareRecord : function(){
        	var _this = this;
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId(),
        			mediaType: "WX",
        			shareResult : 1,
        			shareUrl : ""
        	};
        	if (amGloble.config.debug == true) alert("[-a04- input]"+JSON.stringify(opt));
        	amGloble.api.a04.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a04- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户分享成功", "wt.msg", localStorage.getItem("openid"));
                	} 
        			_this.draw();
        		}
        	}, "application/json; charset=utf-8");
        },
        
        draw : function(){
        	var _this = this;
        	_this.$page4failShow.hide();
        	_this.$page4successShow.hide();
        	
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			deviceType: _this.getDeviceType()
        	};
        	if (amGloble.config.debug == true) alert("[-a05- input]" + JSON.stringify(opt));
        	amGloble.api.a05.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a05- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        			_this.$page4fail.show();
        		} else {
        			if(ret.content.data.win){
        				if (window._tag) { 
            				_tag.dcsMultiTrack("wt.event", "用户中奖", "wt.msg", _this.getOpenid());
            			}
        				_this.$page4win.show();
        				
        			}else{
        				if (window._tag) { 
            				_tag.dcsMultiTrack("wt.event", "用户未中奖", "wt.msg", _this.getOpenid());
            			}
        				_this.$page4fail.show();
        			}
        		}
        	}, "application/json; charset=utf-8");
        },
        
        winPhone : function(){
        	var _this = this;
        	
        	if(_this.checkTelNo()){
        		if (window._tag) { 
    				_tag.dcsMultiTrack("wt.event", "用户中奖提交手机号码", "wt.msg", _this.getOpenid() + "  ||  " + $.trim(_this.$txt4tel.html()));
    			}
            	
            	var opt = {
            			sid : sessionStorage.getItem("sid"),
            			channelType: _this.getChannelType(),
            			userId: _this.getOpenid(),
            			phone: $.trim(_this.$txt4tel.val())
            	};
            	if (amGloble.config.debug == true) alert("[-a06- input]" + JSON.stringify(opt));
            	amGloble.api.a06.post(opt, function (ret) {
            		if (amGloble.config.debug == true) alert("[-a06- return]" + JSON.stringify(ret));
            		if (ret.content.errCode != 0) {
            			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
            		} else {
                		_this.$page4win.hide();
//                		_this.$page4overlay.hide();
            			_this.$page4submitSuceess.show();
            		}
            	}, "application/json; charset=utf-8");
        	}
        },
        
        
        toTarget : function(){
			var _this = this;
        	if (window._tag) { 
        		_tag.dcsMultiTrack("wt.event", "用户进入活动首页", "wt.msg", _this.getOpenid());
        	}
        	var opt = {
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId(),
        			tag : "",
        			normalShare : ""
        	};
        	
        	if (amGloble.config.debug == true) alert("[-a01- input]"+JSON.stringify(opt));
        	amGloble.api.a01.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a01- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        			location.href=amGloble.config.selfRef + "api/hyjt1506/login.do";
//        			location.href=amGloble.config.selfRef + "pages/hyjt1506/index.html?tag="+ret.content.data.tag+"&normalShare=0&refresh_flag_=true"+"&share_type=";
        		} else {
        			sessionStorage.setItem("sid", ret.content.data.sid); 
        			sessionStorage.setItem("promoCode", ret.content.data.promoCode);
        			sessionStorage.setItem("tag", ret.content.data.tag);
        			sessionStorage.setItem("winNotPhone", ret.content.data.winNotPhone);
        			if (amGloble.config.debug == true) alert(amGloble.config.selfRef + "pages/hyjt1506/index.html?foid="+_this.getOpenid()+"&tag="+ret.content.data.tag+"&normalShare=0"+"&utm_source="+sessionStorage.getItem("utm_source"));
        			location.href=amGloble.config.selfRef + "pages/hyjt1506/index.html?tag="+ret.content.data.tag+"&normalShare=0&refresh_flag_=true"+"&share_type=";
        		}
        	}, "application/json; charset=utf-8");
			
		},
        
        
        getOpenid: function() {
    		var openid = localStorage.getItem('openid');
    		if ( openid == null ||  openid == "" || openid=="null") {
    			openid = amGloble.getQueryParameter("openid");
    			localStorage.setItem('openid', openid);
    		}
        	return openid;
        },
        
        getChannelType: function() {
        	var channelType = sessionStorage.getItem('channelType');
        	if ( channelType == null ||  channelType == "" || channelType=="null" ) {
        		channelType = amGloble.getQueryParameter("channelType");
        		sessionStorage.setItem('channelType', channelType);
        	}
        	return channelType;
        },
        
        getDeviceType: function() {
        	var deviceType = sessionStorage.getItem('deviceType');
        	if ( deviceType == null ||  deviceType == "" || deviceType=="null" ) {
        		deviceType = amGloble.getQueryParameter("deviceType");
        		sessionStorage.setItem('deviceType', deviceType);
        	}
        	return deviceType;
        },
        
        getDeviceId: function() {
        	var deviceid = localStorage.getItem('deviceid');
        	if ( deviceid == null ||  deviceid == "" || deviceid=="null"  ) {
        		deviceid = amGloble.guid();
        		localStorage.setItem('deviceid', deviceid);
        	}
        	return deviceid;
        }
    };
    
    controller.init();
});