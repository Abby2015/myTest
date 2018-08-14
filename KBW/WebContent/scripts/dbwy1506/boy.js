﻿/**
 * Created by qing.wang on 2015/05/15.
 */
var _debug = false;

$(function(){
    var controller = {

        init: function() {
//        	alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$page4page2boy = $(".page2.boy");
        	this.$page4page6 = $(".page6");
        	this.$page4page7 = $(".page7");
        	
        	this.$page4share = $(".overlay");
        	this.$page4gays_share = $(".gays_share");
        	
        	this.$touch4Ticket_page2_boy = $(".ticket_button.page2_boy");
        	
        	this.$touch4BoyShare = $(".ticket_help.boy,.ticket_button.ticket_help");
        	this.$touch4Back = $(".ticket_back,.close");
        	
//        	this.$touch4Bosom2 = $(".animated.rotateInUpLeft.girl");
//        	this.$touch4Gays2 = $(".animated.rotateInUpRight.boy");
        	this.$touch4Bosom2 = $(".animated.rotateInUpRight.boy");
        	this.$touch4Gays2 = $(".animated.rotateInUpLeft.girl");
        	this.$touch4saveBag = $(".animated.swing.convert");
        	
//        	this.$touch4care = $(".click_care_btn");
        	
        	
        	this.$txt4imgContainer = $(".swiper-wrapper");

        	//节点绑定事件
        	/*this.$touch4care.bind("click",function(){
        		location.href= amGloble.config.selfRoot + "pages/dbwy1506/mobile.html?foid="+localStorage.getItem("openid")+"&tag="+sessionStorage.getItem("tag")+"&normalShare=1";
        	});*/
        	this.$touch4saveBag.bind("click",function(){
        		_this.saveToCardBag();
        	});
        	this.$touch4Bosom2.bind("click",function(){
        		_this.askFriend("girl");
        	});
        	this.$touch4Gays2.bind("click",function(){
        		_this.askFriend("boy");
        	});
        	this.$touch4Back.bind("click",function(){
        		_this.$page4page6.hide();
        		_this.$page4page7.hide();
        		_this.$page4page2boy.show();
        	});
        	this.$touch4Ticket_page2_boy.bind("click",function(){
        		var promoCode = sessionStorage.getItem("promoCode");
        		if(promoCode=="" || promoCode=="null" || promoCode==null){
        			_this.$page4page2boy.hide();
        			_this.$page4page6.show();
        		}else{
        			_this.$page4page2boy.hide();
        			_this.$page4page7.show();
        		}
//        		_this.myCoupon();
        	});
        	
        	this.$touch4BoyShare.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户开始分享[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
            	}
        		_this.$page4share.show();
        		_this.$page4gays_share.show();
        	});
        	
        	this.$page4gays_share.bind("click",function(){
        		setTimeout(function(){
        			_this.shareRecord();
        		},500);
        	});
        	
        	/*var result = [{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"},{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"}];
			_this.$txt4imgContainer.empty();
			
			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
	           + "<div>券号："+ result[0].promoCode 
	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
			_this.$txt4imgContainer.append($($slideNode));
			
			
			_this.$page4page2boy.hide();
			_this.$page4page7.show();*/
        	
        },
        
        myCoupon : function(){
        	var _this = this;
        	var opt = {
        			userId : localStorage.getItem("openid"),
        			channelType : sessionStorage.getItem("channelType")
        		};
//        	alert("[-a04- input]" + JSON.stringify(opt));
        	amGloble.api.a04.get(opt, function (ret) {
//        		alert("[-a04- return]" + JSON.stringify(ret));
        		if (window._tag) { 
        			_tag.dcsMultiTrack("wt.event", "用户点击查看半价券[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
        		}
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			_this.$page4page2boy.hide();
    				_this.$page4page6.show();
//        			alert(JSON.stringify(ret));
        		} else {
        			var result = ret.content.data;
        			_this.$txt4imgContainer.empty();
        			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
      	           + "<div>券号："+ result[0].promoCode 
      	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
      			_this.$txt4imgContainer.append($($slideNode));
         			_this.$page4page2boy.hide();
     				_this.$page4page7.show();

        		}
        	}, "application/json; charset=utf-8");
        },
        saveToCardBag : function(){
        	var _this = this;
        	var opt = {
//        			promoCode : $.trim($(".swiper-slide.swiper-slide-active .promoCode").html())
//        			promoCode : $.trim($(".promoCode").html())
        			promoCode : sessionStorage.getItem("promoCode")
        		};
//        	alert("[-a05- input]" + JSON.stringify(opt));
        	amGloble.api.a05.post(opt, function (ret) {
//        		alert("[-a05- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户查看卡包[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
                	}
        			location.href = "http://crmminisite.verystar.cn/wxcard/promo?key="+ret.content.data.data+"&wechat_card_js=1";
        		}
        	}, "application/json; charset=utf-8");
        },
        askFriend : function(type){
        	var _this = this;
        	
    		var askType_ = "";
        	if(type == "boy"){
        		askType_ = "0";
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户求助逗比青年[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        	}else{
        		askType_ = "1";
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户求助文艺青年[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        	}
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: localStorage.getItem("openid"),
        			channelType: sessionStorage.getItem("channelType"),
        			deviceType: sessionStorage.getItem("deviceType"),
        			askType: askType_,
        			tag : sessionStorage.getItem("tag"),
        			normalShare : sessionStorage.getItem("normalShare")
        	};
        	
//            	alert("[-a02- input]"+JSON.stringify(opt));
        	amGloble.api.a02.post(opt, function (ret) {
//            		alert("[-a02- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
//            			alert(JSON.stringify(ret));
        		} else {
        			sessionStorage.setItem("tag", ret.content.data.tag);
        			if(type == "boy"){
                		location.href= amGloble.config.selfRoot + "pages/dbwy1506/boy.html?foid="+localStorage.getItem("openid")+"&tag="+ret.content.data.tag+"&normalShare=1";
                	}else{
                		location.href= amGloble.config.selfRoot + "pages/dbwy1506/girl.html?foid="+localStorage.getItem("openid")+"&tag="+ret.content.data.tag+"&normalShare=1";
                	}
        		}
        	}, "application/json; charset=utf-8");
        	
        },
        shareRecord : function(){
        	var _this = this;
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: localStorage.getItem("openid"),
        			channelType: sessionStorage.getItem("channelType"),
        			deviceType: sessionStorage.getItem("deviceType"),
        			mediaType: "WX",
        			shareResult : 1,
        			shareUrl : ""
        	};
//        	alert("[-a03- input]"+JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
//        		alert("[-a03- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户分享成功[求逗比青年页面]", "wt.msg", localStorage.getItem("openid"));
                	} 
        			_this.$page4gays_share.hide();
        			_this.$page4share.hide();
        		}
        	}, "application/json; charset=utf-8");
        }
    };
    
    controller.init();
});