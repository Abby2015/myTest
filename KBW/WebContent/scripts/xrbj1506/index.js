/**
 * Created by qing.wang on 2015/05/15.
 */
var _debug = false;

$(function(){
    var controller = {

        init: function() {
//        	alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$page4page1 = $(".page1");
        	this.$page4page5 = $(".page5");
        	this.$page4page6 = $(".page6");
        	this.$page4page7 = $(".page7");
        	this.$page4overlay = $(".overlay");
        	this.$page4rule = $(".rule_pop");
        	this.$page4Tips = $(".share_pop");
        	this.$page4ShareAll = $(".bosom_share");
        	
        	this.$touch4Rule = $(".rule_button");
        	this.$touch4Product = $(".product_button");
        	this.$touch4Product2 = $(".ice_cream");
        	this.$touch4Ticket_page1 = $(".ticket_button.ticket_coupon");
        	
        	this.$touch4Bosom = $(".bosom_button");
        	this.$touch4Bosom2 = $(".animated.rotateInUpLeft.girl");
        	this.$touch4Gays = $(".gays_button");
        	this.$touch4Gays2 = $(".animated.rotateInUpRight.boy");
        	
        	this.$touch4Back = $(".back,.ticket_back,.close");
        	this.$touch4RuleClose = $(".close_icon");
        	this.$touch4saveBag = $(".animated.swing.convert");
        	
        	this.$txt4imgContainer = $(".swiper-wrapper");
        	
        	
        	//节点绑定事件
        	this.$touch4saveBag.bind("click",function(){
        		_this.saveToCardBag();
        	});
        	this.$touch4Rule.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看规则[首页]", "wt.msg", _this.getOpenid());
            	} 
        		_this.$page4overlay.show();
        		_this.$page4rule.show();
        	});
        	this.$touch4RuleClose.bind("click",function(){
//        		var $hh=$(".rule_areas").offset().top;
//        		$(".rule_areas").animite({scrollTop:$hh},"fast");
        		_this.$page4rule.hide();
        		_this.$page4overlay.hide();
        	});
        	this.$touch4Product.bind("click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户点击按钮查看产品信息[首页]", "wt.msg", _this.getOpenid());
            	} 
        		_this.$page4page1.hide();
        		_this.$page4page5.show();
        	});
        	this.$touch4Product2.bind("click",function(){
        		if (window._tag) { 
        			_tag.dcsMultiTrack("wt.event", "用户点击花麒麟图片查看产品信息[首页]", "wt.msg", _this.getOpenid());
        		} 
        		_this.$page4page1.hide();
        		_this.$page4page5.show();
        	});
        	this.$touch4Ticket_page1.bind("click",function(){
        		_this.myCoupon();
        	});
        	/*this.$touch4Ticket_help.bind("click",function(){
        		_this.$page4overlay.show();
        		_this.$page4ShareAll.show();
        	});*/
        	
        	this.$touch4Bosom.bind("click",function(){
        		_this.askFriend("girl",false);
        	});
        	this.$touch4Bosom2.bind("click",function(){
        		_this.askFriend("girl",true);
        	});
        	
        	this.$touch4Gays.bind("click",function(){
        		_this.askFriend("boy",false);
        	});
        	this.$touch4Gays2.bind("click",function(){
        		_this.askFriend("boy",true);
        	});
        	this.$touch4Back.bind("click",function(){
        		_this.$page4page7.hide();
        		_this.$page4page6.hide();
        		_this.$page4page5.hide();
        		_this.$page4page1.show();
        	});
        	this.$page4Tips.bind("click",function(){
        		_this.$page4overlay.hide();
        		_this.$page4Tips.hide();
        	});
        	this.$page4ShareAll.bind("click",function(){
        		setTimeout(function(){
        			_this.shareRecord();
        		},500);
        	});
        	
        	
        	/*var result = [{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"},{"inviterName":"dsds","askType":"0","promoCode":"dsdsaddsa","couponTime":"2013-09-22"}];
        	_this.$txt4imgContainer.empty();
			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/xrbj1506/ticket.png' alt='券'/>"
	           + "<div>券号："+ result[0].promoCode 
	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
			_this.$txt4imgContainer.append($($slideNode));
			
			_this.$page4page1.hide();
			_this.$page4page7.show();*/

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
//        	alert("[-a03- input]"+JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
//        		alert("[-a03- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户分享成功[首页卡包页面]", "wt.msg", localStorage.getItem("openid"));
                	} 
        			_this.$page4overlay.hide();
            		_this.$page4ShareAll.hide();
        		}
        	}, "application/json; charset=utf-8");
        },
        
        myCoupon : function(){
        	var _this = this;
        	var opt = {
        			userId : _this.getOpenid(),
        			channelType : _this.getChannelType()
        		};
//        	alert("[-a04- input]" + JSON.stringify(opt));
        	amGloble.api.a04.get(opt, function (ret) {
//        		alert("[-a04- return]" + JSON.stringify(ret));
        		if (window._tag) { 
        			_tag.dcsMultiTrack("wt.event", "用户点击查看半价券[首页]", "wt.msg", _this.getOpenid());
        		}
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			_this.$page4page1.hide();
    				_this.$page4page6.show();
//        			alert(JSON.stringify(ret));
        		} else {
        			var result = ret.content.data;
        			_this.$txt4imgContainer.empty();
        			var $slideNode = "<div class='swiper-slide'><img src='"+ amGloble.config.selfRef +"images/xrbj1506/ticket.png' alt='券'/>"
        	           + "<div>券号："+ result[0].promoCode 
        	           + "</div><div style='display:none' class='promoCode'>"+result[0].promoCode+"</div>" +"</div>";
        			_this.$txt4imgContainer.append($($slideNode));
        			
        			_this.$page4page1.hide();
        			_this.$page4page7.show();
        			/*$.each(result,function(i,item){
        				var $slideNode = "<div class='swiper-slide'></div>";
        				var couponTime_ = item.couponTime;
        				couponTime_ = new Date(couponTime_);
        				var month_ = couponTime_.getMonth()+1;
        				couponTime_ = couponTime_.getFullYear()+"-"+month_+"-"+couponTime_.getDate()+" "+couponTime_.getHours()+":"+couponTime_.getMinutes();
        				var inviterName_ = item.inviterName;
        				if(item.inviterName =="" || item.inviterName==null || item.inviterName=="null"){
        					inviterName_ = "";
        				}
        				var $tempNode = "<img src='"+ amGloble.config.selfRef +"images/xrbj1506/ticket.png' alt='券'/>";
        				if(item.askType=="0" || item.askType==0){
        					$tempNode = $tempNode + "<div>与好闺蜜"+inviterName_+"在"+couponTime_+"成功拼券</div><div>券号："+ item.promoCode +
        					$tempNode = $tempNode + "<div>券号："+ item.promoCode +
				 			"</div><div style='display:none' class='promoCode'>"+item.promoCode+"</div>";
        				}else{
    						$tempNode = $tempNode + "<div>与好兄弟"+inviterName_+"在"+couponTime_+"成功拼券</div><div>券号："+ item.promoCode +
        					$tempNode = $tempNode + "<div>券号："+ item.promoCode +
    								 			"</div><div style='display:none' class='promoCode'>"+item.promoCode+"</div>";
        				}
        				$($slideNode).append($tempNode);
//        				_this.$txt4imgContainer.append($tempNode);
        				_this.$txt4imgContainer.append($($slideNode).append($tempNode));
        			});
        			_this.$page4page1.hide();
    				_this.$page4page7.show();
        			var mySwiper = new Swiper(".swiper-container",{
      	      	      	speed:750,
      	      	      	mode:"vertical",
      	      	      	loop:true,
      	      	      	prevButton:'.swiper-button-prev',
      	      	      	nextButton:'.swiper-button-next'
      	        	});*/

        		}
        	}, "application/json; charset=utf-8");
        },
        
        saveToCardBag : function(){
        	var _this = this;
        	var opt = {
//        			promoCode : $.trim($(".swiper-slide.swiper-slide-active .promoCode").html())
        			promoCode : $.trim($(".promoCode").html())
        		};
//        	alert("[-a05- input]" + JSON.stringify(opt));
        	amGloble.api.a05.post(opt, function (ret) {
//        		alert("[-a05- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户查看卡包[首页]", "wt.msg", _this.getOpenid());
                	}
        			location.href = "http://crmminisite.verystar.cn/wxcard/promo?key="+ret.content.data.data+"&wechat_card_js=1";
        		}
        	}, "application/json; charset=utf-8");
        },
        
        
        askFriend : function(type,limit){
        	var _this = this;
        	
        	/*var askTime = localStorage.getItem("askTime");
        	var flag = false;
        	if(askTime=="" || askTime==null  || askTime=="null"){
        		flag = true;
        	}else{
        		var nowTime  = new Date();
        		var nowTime_ = nowTime.getFullYear()+"-"+nowTime.getMonth()+"-"+nowTime.getDate();
        		var askId = localStorage.getItem("askId");
    			if(askId == _this.getOpenid()){
    				if(nowTime_ == askTime){
            			//TODO  弹出图层
            			_this.$page4overlay.show();
                		_this.$page4Tips.show();
            		}else{
            			flag = true;
            		}
    			}else{
    				flag = true;
    			}
        	}*/
        	
        	/*if(flag || limit){*/
        		var askType_ = "";
            	if(type == "boy"){
            		askType_ = "1";
            		if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户求助ji友[首页]", "wt.msg", _this.getOpenid());
                	} 
            	}else{
            		askType_ = "0";
            		if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户求助闺蜜[首页]", "wt.msg", _this.getOpenid());
                	} 
            	}
            	var opt = {
            			sid : sessionStorage.getItem("sid"),
            			userId: _this.getOpenid(),
            			channelType: _this.getChannelType(),
            			deviceType: _this.getDeviceType(),
            			deviceId: _this.getDeviceId(),
            			tag : sessionStorage.getItem("tag"),
            			menuType: sessionStorage.getItem("menuType"),
            			askType: askType_,
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
                    		location.href= amGloble.config.selfRoot + "pages/xrbj1506/boy.html?foid="+_this.getOpenid()+"&tag="+ret.content.data.tag+"&normalShare=1";
                    	}else{
                    		location.href= amGloble.config.selfRoot + "pages/xrbj1506/girl.html?foid="+_this.getOpenid()+"&tag="+ret.content.data.tag+"&normalShare=1";
                    	}
            			if(!limit){
            				var nowTime  = new Date();
            				var nowTime_ = nowTime.getFullYear()+"-"+nowTime.getMonth()+"-"+nowTime.getDate();
            				localStorage.setItem("askTime", nowTime_);
            				localStorage.setItem("askId", _this.getOpenid());
            			}
            		}
            	}, "application/json; charset=utf-8");
        	/*}*/
        	
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