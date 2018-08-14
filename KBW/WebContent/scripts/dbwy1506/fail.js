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
        	this.$page4page4 = $(".page4");
        	this.$page4page5 = $(".page5");
        	this.$page4page6 = $(".page6");
        	this.$page4page7 = $(".page7");
        	this.$page4gays_share = $(".gays_share");
        	this.$page4bosom_share = $(".bosom_share");
        	
        	this.$page4overlay = $(".overlay");
        	this.$page4Tips = $(".share_pop");
        	
        	this.$page4rule = $(".rule_pop");
        	
        	
        	this.$touch4Bosom = $(".animated.rotateInUpRight.boy");
        	this.$touch4Gays = $(".animated.rotateInUpLeft.girl");
        	this.$touch4quan = $(".ticket_button.page4");
        	this.$touch4ticket_help = $(".ticket_button.ticket_help");
        	
        	this.$touch4Rule = $(".rule_button");
        	this.$touch4RuleClose = $(".close_icon");
        	
        	this.$touch4Product = $(".product_button");
        	this.$touch4Back = $(".back,.ticket_back,.close,.page5");
        	this.$touch4saveBag = $(".animated.swing.convert");
        	
        	this.$txt4imgContainer = $(".swiper-wrapper");
        	
        	//节点绑定事件
        	this.$touch4saveBag.bind("click",function(){
        		_this.saveToCardBag();
        	});
        	this.$touch4Back.bind("click",function(){
        		_this.$page4page5.hide();
        		_this.$page4page7.hide();
        		_this.$page4page4.show();
        	});
        	
        	this.$touch4Product.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户点击按钮查看产品信息[获取券失败页面]", "wt.msg", localStorage.getItem("openid"));
            	} 
        		_this.$page4page4.hide();
        		_this.$page4page5.show();
        	});
        	
        	this.$touch4Rule.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看规则[获取券失败页面]", "wt.msg", localStorage.getItem("openid"));
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
        			_this.$page4page4.hide();
        			_this.$page4page6.show();
        		}else{
        			_this.$page4page4.hide();
        			_this.$page4page7.show();
        		}
//        		_this.myCoupon();
        	});
        	this.$touch4Bosom.bind("click",function(){
        		_this.askFriend("girl");
        	});
        	this.$touch4ticket_help.bind("click",function(){
        		_this.$page4overlay.show();
        		_this.$page4bosom_share.show();
        	});
        	
        	this.$touch4Gays.bind("click",function(){
        		_this.askFriend("boy");
        	});
        	
        	
        	var askFor = sessionStorage.getItem("askFor");
        	if(askFor!=null && askFor!="null" && askFor!=""){
        		if(askFor=="boy"){
        			$("title").html("天气不错，快来个逗比和我拼海盐焦糖风味花淇淋半价券吧！");
        			$(".share_link").attr("src",amGloble.config.selfRef+"images/dbwy1506/linkPic.jpg");
        			_this.$page4overlay.show();
    				_this.$page4gays_share.show();
        		}else{
        			$("title").html("海盐焦糖风味花淇淋半价券和文青甜蜜有约，是文青你就来哦！");
        			$(".share_link").attr("src",amGloble.config.selfRef+"images/dbwy1506/linkPic2.jpg");
        			_this.$page4overlay.show();
            		_this.$page4bosom_share.show();
        		}
        	}
        	
        	this.$page4gays_share.bind("click",function(){
        		setTimeout(function(){
        			_this.shareRecord("boy");
        		},500);
        	});
        	this.$page4bosom_share.bind("click",function(){
        		setTimeout(function(){
        			_this.shareRecord("girl");
        		},500);
        	});
        	
        	this.$page4Tips.bind("click",function(){
        		_this.$page4overlay.hide();
        		_this.$page4Tips.hide();
        	});
        	
        	/*var result = [{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"},{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"}];
			_this.$txt4imgContainer.empty();
			
			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
	           + "<div>券号："+ result[0].promoCode 
	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
			_this.$txt4imgContainer.append($($slideNode));
			_this.$page4page4.hide();
			_this.$page4page7.show();*/
        	
        },
        
        loadImg : function(){
        	var imgArr = [
                          amGloble.config.selfRef+"images/dbwy1506/back.png",
                          amGloble.config.selfRef+"images/dbwy1506/bosom_button2.png",
                          amGloble.config.selfRef+"images/dbwy1506/bosom_fail.png",
                          amGloble.config.selfRef+"images/dbwy1506/bosom_ice_cream.png",
                          amGloble.config.selfRef+"images/dbwy1506/bosom_share.png",
                    	  amGloble.config.selfRef+"images/dbwy1506/fail_p.png",
                          amGloble.config.selfRef+"images/dbwy1506/footer.png",
                          amGloble.config.selfRef+"images/dbwy1506/gays_button2.png",
                          amGloble.config.selfRef+"images/dbwy1506/gays_share.png",
                          amGloble.config.selfRef+"images/dbwy1506/half_ticket.png",
                          amGloble.config.selfRef+"images/dbwy1506/linkPic2.jpg",
                          amGloble.config.selfRef+"images/dbwy1506/one.png",
                          amGloble.config.selfRef+"images/dbwy1506/pic1.jpg",
                          amGloble.config.selfRef+"images/dbwy1506/product_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/product_desc.png",
                          amGloble.config.selfRef+"images/dbwy1506/product.png",
                          amGloble.config.selfRef+"images/dbwy1506/rule_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/share_pop.png",
                          amGloble.config.selfRef+"images/dbwy1506/ticket_button.png",
                          amGloble.config.selfRef+"images/dbwy1506/ticket.png"
                          ];
            preloadimg(imgArr,function(){
                $('#loading').hide();
                $('.page4').show();
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
        			_tag.dcsMultiTrack("wt.event", "用户点击查看半价券[获取券失败页面]", "wt.msg", localStorage.getItem("openid"));
        		}
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			_this.$page4page4.hide();
    				_this.$page4page6.show();
//        			alert(JSON.stringify(ret));
        		} else {
        			var result = ret.content.data;
        			_this.$txt4imgContainer.empty();
        			
        			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/dbwy1506/ticket.png' alt='券'/>"
        	           + "<div>券号："+ result[0].promoCode 
        	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
        			_this.$txt4imgContainer.append($($slideNode));
        			_this.$page4page4.hide();
        			_this.$page4page7.show();
        			
        		}
        	}, "application/json; charset=utf-8");
        },
        saveToCardBag : function(){
        	var _this = this;
        	var opt = {
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
                		_tag.dcsMultiTrack("wt.event", "用户查看卡包[获取券失败页面]", "wt.msg", localStorage.getItem("openid"));
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
            		_tag.dcsMultiTrack("wt.event", "用户开始求助逗比青年[获取券失败页面]", "wt.msg", _this.getOpenid());
            	} 
        	}else{
        		askType_ = "1";
        		if (window._tag) { 
        			_tag.dcsMultiTrack("wt.event", "用户开始求助文艺青年[获取券失败页面]", "wt.msg", _this.getOpenid());
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
        				sessionStorage.setItem("askFor", "boy");
        				location.href= amGloble.config.selfRoot + "pages/dbwy1506/fail.html?foid="+localStorage.getItem("openid")+"&tag="+ret.content.data.tag+"&normalShare=1";
                	}else{
                		sessionStorage.setItem("askFor", "girl");
                		location.href= amGloble.config.selfRoot + "pages/dbwy1506/fail.html?foid="+localStorage.getItem("openid")+"&tag="+ret.content.data.tag+"&normalShare=1";
                	}
        			var nowTime  = new Date();
            		var nowTime_ = nowTime.getFullYear()+"-"+nowTime.getMonth()+"-"+nowTime.getDate();
            		localStorage.setItem("askTime", nowTime_);
    				localStorage.setItem("askId", _this.getOpenid());
        		}
        	}, "application/json; charset=utf-8");
        },
        shareRecord : function(type){
        	var _this = this;
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: localStorage.getItem("openid"),
        			channelType: sessionStorage.getItem("channelType"),
        			deviceType: sessionStorage.getItem("deviceType"),
        			mediaType: "WX",
        			shareResult : "1",
        			shareUrl : ""
        	};
//        	alert("[-a03- input]"+JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
//        		alert("[-a03- return]" + JSON.stringify(ret));
        		sessionStorage.removeItem("askFor");
    			_this.$page4gays_share.hide();
            	_this.$page4bosom_share.hide();
            	_this.$page4overlay.hide();
        		if (ret.content.errCode != 0) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if(type == "boy"){
                		if (window._tag) { 
                    		_tag.dcsMultiTrack("wt.event", "用户分享逗比青年成功[获取券失败页面]", "wt.msg", _this.getOpenid());
                    	} 
                	}else{
                		if (window._tag) {
                			_tag.dcsMultiTrack("wt.event", "用户分享文艺青年成功[获取券失败页面]", "wt.msg", _this.getOpenid());
                		} 
                	}
        			sessionStorage.removeItem("askFor");
        			_this.$page4gays_share.hide();
                	_this.$page4bosom_share.hide();
                	_this.$page4overlay.hide();
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