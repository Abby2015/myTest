﻿/**
 * Created by qing.wang on 2015/05/15.
 */
var _debug = false;

$(function(){
    var controller = {

        init: function() {
//        	alert(" ===success.html=== \n"+JSON.stringify(location));
        	var _this = this;
        	this.loadImg();
            
        	//初始化节点
        	this.$page4page3 = $(".page3");
        	this.$page4page5 = $(".page5");
        	this.$page4page6 = $(".page6");
        	this.$page4page7 = $(".page7");
        	
        	
        	this.$page4rule = $(".rule_pop");
        	this.$page4overlay = $(".overlay");
        	
        	this.$touch4quan = $(".animated.rotateInDownLeft.half,.ticket_button.page3");
        	this.$touch4again = $(".animated.rotateInDownLeft.again");
        	
        	this.$touch4Rule = $(".rule_button");
        	this.$touch4RuleClose = $(".close_icon");
        	
        	this.$touch4Product = $(".product_button");
        	this.$touch4Back = $(".back,.close,.ticket_back,.page5");
        	
//        	this.$touch4Bosom2 = $(".animated.rotateInUpLeft.girl");
//        	this.$touch4Gays2 = $(".animated.rotateInUpRight.boy");
        	this.$touch4Bosom2 = $(".animated.rotateInUpRight.boy");
        	this.$touch4Gays2 = $(".animated.rotateInUpLeft.girl");
        	this.$touch4saveBag = $(".animated.swing.convert");
        	
        	this.$txt4imgContainer = $(".swiper-wrapper");
        	
        	//节点绑定事件
        	this.$touch4saveBag.bind("click",function(){
        		_this.saveToCardBag();
        	});
        	this.$touch4Back.bind("click",function(){
        		_this.$page4page5.hide();
        		_this.$page4page6.hide();
        		_this.$page4page7.hide();
        		_this.$page4page3.show();
        	});
        	
        	this.$touch4Bosom2.bind("click",function(){
        		_this.askFriend("girl");
        	});
        	this.$touch4Gays2.bind("click",function(){
        		_this.askFriend("boy");
        	});
        	this.$touch4Product.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户点击按钮查看产品信息[成功得券页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        		_this.$page4page3.hide();
        		_this.$page4page5.show();
        	});
        	
        	this.$touch4Rule.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看规则[成功得券页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        		_this.$page4overlay.show();
        		_this.$page4rule.show();
        	});
        	this.$touch4RuleClose.bind("click",function(){
        		_this.$page4rule.hide();
        		_this.$page4overlay.hide();
        	});
        	
        	this.$touch4quan.bind("click",function(){
        		var promoCode = sessionStorage.getItem("promoCode");
        		if(promoCode=="" || promoCode=="null" || promoCode==null){
        			_this.$page4page3.hide();
        			_this.$page4page6.show();
        		}else{
        			_this.$page4page3.hide();
        			_this.$page4page7.show();
        		}
//        		_this.myCoupon();
        	});
        	this.$touch4again.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户返回首页[获取券成功页面]", "wt.msg", localStorage.getItem("openid"));
            	}
        		location.href= amGloble.config.selfRoot + "pages/dbwy1506/index.html?foid="+localStorage.getItem('openid')+"&tag="+sessionStorage.getItem("tag")+"&normalShare=0";;
        	});
        	
        	/*var result = [{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"},{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"}];
        	_this.$txt4imgContainer.empty();
			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
	           + "<div>券号："+ result[0].promoCode 
	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
			_this.$txt4imgContainer.append($($slideNode));
			
			_this.$page4page3.hide();
			_this.$page4page7.show();*/

        },
        
        loadImg : function(){
        	var imgArr = [
                          amGloble.config.selfRef+"images/dbwy1506/back.png",
                          amGloble.config.selfRef+"images/dbwy1506/bosom_share.png",
                          amGloble.config.selfRef+"images/dbwy1506/button.png",
                          amGloble.config.selfRef+"images/dbwy1506/footer.png",
                          amGloble.config.selfRef+"images/dbwy1506/gays_share.png",
                          amGloble.config.selfRef+"images/dbwy1506/half_ticket.png",
                          amGloble.config.selfRef+"images/dbwy1506/linkPic2.jpg",
                          amGloble.config.selfRef+"images/dbwy1506/one.png",
                          amGloble.config.selfRef+"images/dbwy1506/page3-p.png",
                          amGloble.config.selfRef+"images/dbwy1506/pic1.jpg",
                          amGloble.config.selfRef+"images/dbwy1506/product_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/product_desc.png",
                          amGloble.config.selfRef+"images/dbwy1506/product.png",
                          amGloble.config.selfRef+"images/dbwy1506/rule_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/success_p.png",
                          amGloble.config.selfRef+"images/dbwy1506/share_pop.png",
                          amGloble.config.selfRef+"images/dbwy1506/ticket_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/ticket.png"
                          ];
            preloadimg(imgArr,function(){
                $('#loading').hide();
                $('.page3').show();
            });
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
        			_tag.dcsMultiTrack("wt.event", "用户点击查看半价券[获取券成功页面]", "wt.msg", localStorage.getItem("openid"));
        		}
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			_this.$page4page3.hide();
    				_this.$page4page6.show();
//        			alert(JSON.stringify(ret));
        		} else {
        			var result = ret.content.data;
        			_this.$txt4imgContainer.empty();
        			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
        	           + "<div>券号："+ result[0].promoCode 
        	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
        			_this.$txt4imgContainer.append($($slideNode));
        			
        			_this.$page4page3.hide();
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
                		_tag.dcsMultiTrack("wt.event", "用户查看卡包[获取券成功页面]", "wt.msg", localStorage.getItem("openid"));
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
            		_tag.dcsMultiTrack("wt.event", "用户求助逗比青年[获取券成功页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        	}else{
        		askType_ = "1";
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户求助文艺青年[获取券成功页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        	}
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
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
        	
        }
     
    };
    
    controller.init();
});