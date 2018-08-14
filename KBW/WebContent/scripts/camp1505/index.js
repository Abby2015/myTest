/**
 * Created by qing.wang on 2015/04/29.
 */
var _debug = false;

$(function(){
    var controller = {

        init: function() {
//        	alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$page4Catoon = $("#fullpage");
        	this.$page4Share = $(".share-pop");
        	this.$page4page1 = $(".page.page-1");
        	this.$page4page7 = $(".page.page-7");
        	this.$page4page8 = $(".page.page-8");
        	this.$page4page9 = $(".page.page-9");
        	this.$page4page10 = $(".page.page-10");
        	this.$page4page11 = $(".page.page-11");
        	
        	this.$page4Rule = $(".tabCont.tabCont1");
        	this.$page4WonList = $(".tabCont.tabCont2");
        	
        	this.$page4BigPic = $(".pop");
        	
        	this.$touch4HomePage = $(".logo");
        	this.$touch4Again = $(".remember_button.again");
        	this.$touch4Music = $("#music");
        	this.$touch4Rule = $(".active_rule");
        	this.$touch4Next = $("#button_next");
        	this.$touch4Return = $(".remember_button.return");
        	this.$touch4Share = $("#button_share");
        	this.$touch4SaveInfo = $("#button_info");

        	this.$touch4RuleDetail = $(".tab li:eq(0)");
        	this.$touch4WonList = $(".tab li:eq(1)");
        	
        	this.$touch4BigPic = $(".quan");
        	this.$touch4CloseBig = $(".close_popimg");
        	
        	this.$touch4Choose = $(".clickbtn");
        	this.$touch4Select = $(".selectarea li");
        	this.$page4Selected = $(".selectarea");

        	this.$txt4TryThing = $(".textarea");
        	
        	this.$txt4Prize = $("#prize");
        	
        	this.$txt4UserName = $("#userName");
        	this.$txt4UserPhone = $("#userPhone");
        	this.$txt4UserID = $("#userID");
        	this.$txt4UserAddress = $("#userAddress");
        	this.$txt4UserAddressCode = $("#userAddressCode");
        	
        	this.$txt4IdInfo = $("#idInfo");
        	
        	//节点绑定事件
        	this.$txt4UserID.blur(function(){
        		var idRegex1 = /^\d{15}$/,idRegex2 = /^\d{18}$/,
        			userId = $.trim($(this).val()),
        			resulst = userId.match(idRegex1) || userId.match(idRegex2);
        		if(!resulst){
        			_this.$txt4IdInfo.show();
        		}else{
        			_this.$txt4IdInfo.hide();
        		}
        	});
        	
        	this.$touch4Choose.bind("click",function(){
        		_this.$page4Selected.show();
        	});
        	this.$touch4Select.bind("click",function(){
        		_this.$touch4Choose.find("p").html($(this).html());
        		 $("title").html($(this).html());
        		_this.$page4Selected.hide();
        	});
        	
        	this.$touch4BigPic.bind("click",function(){
        		var imgSrc = _this.$touch4BigPic.find("img").attr("src");
        		_this.$page4BigPic.find("img").attr("src",imgSrc);
        		_this.$page4BigPic.show();
        	});
        	this.$touch4CloseBig.bind("click",function(){
        		_this.$page4BigPic.hide();
        	});
        	
        	this.$touch4RuleDetail.bind("click",function(){
        		_this.$touch4RuleDetail.addClass("cur");
        		_this.$touch4WonList.removeClass("cur");
        		_this.$page4WonList.hide();
        		_this.$page4Rule.show();
        	});
        	this.$touch4WonList.bind("click",function(){
        		_this.$touch4WonList.addClass("cur");
        		_this.$touch4RuleDetail.removeClass("cur");
        		_this.$page4Rule.hide();
        		_this.$page4WonList.show();
        	});
        	
        	this.$touch4Rule.bind("click",function(){
        		_this.$page4page1.hide();
        		_this.$page4page10.removeClass("none");
        		_this.$page4page10.show();
        		
        	});
        	this.$touch4Next.bind("click",function(){
        		_this.$page4page1.hide();
        		_this.$page4Catoon.show();
        	});
        	this.$touch4Return.bind("click",function(){
        		_this.$page4page10.hide();
        		_this.$page4page11.hide();
        		_this.$page4page1.show();
        	});
        	
        	this.$touch4HomePage.bind("click",function(){
        		location.href= amGloble.config.selfRoot + "pages/camp1505/index.html";
        	});
        	this.$touch4Again.bind("click",function(){
        		location.href= amGloble.config.selfRoot + "pages/camp1505/index.html";
        	});
        	
        	this.$touch4Share.bind("click",function(){
        		_this.shareRecord();
        	});
        	
        	this.$touch4Music.bind("click",function(){
        		var music = document.getElementById("myaudio");
        	    if (music.paused) {
        	        music.play();
        	        $('.music').addClass('music-move');
        	    } else {
        	        music.pause();
        	        $('.music').removeClass('music-move');
        	    }
        	});
        	
        	this.$page4Share.bind("click",function(){
        		setTimeout(function(){
        			_this.draw();
        		}, 500);
        	});
        	
        	this.$touch4SaveInfo.bind("click",function(){
        		_this.winInfo();
        		
        	});
        	
        	_this.initStoryPage();
        	_this.pickOneCoupon();
    		_this.saveOpenRecord();
        },
        
        //初始化故事滑动效果
        initStoryPage : function(){
        	var _this = this;
        	var storyPage = new pageSwitch('fullpage',{
				duration:600,
				start:0,
				direction:1,
				loop:false,
				ease:'ease',
				transition:'slideY',
				mousewheel:true,
				arrowkey:true
			});
        	storyPage.on("after",function(evt){
//        		console.dir(evt);
        		if (window._tag) {
        			_tag.dcsMultiTrack("wt.event", "用户浏览故事页面", "wt.msg", "第" + evt + "页");
            	} 
        	});
        },
        
        pickOneCoupon : function(){
        	var _this = this;
        	var couponArr = ["images/camp1505/f1.jpg","images/camp1505/f2.jpg",
        	                 "images/camp1505/f3.jpg","images/camp1505/f4.jpg",
        	                 "images/camp1505/f5.jpg","images/camp1505/f6.jpg",
        	                 "images/camp1505/f7.jpg","images/camp1505/f8.jpg"
        	                 ];
        	var index = Math.floor(Math.random() * couponArr.length);
        	_this.$touch4BigPic.find("img").attr("src",amGloble.config.selfRef+couponArr[index]);
        },
        
        //保存打开记录
        saveOpenRecord: function() {
        	var _this = this;
        	if (window._tag) { 
        		_tag.dcsMultiTrack("wt.event", "用户打开记录", "wt.msg", _this.getOpenid());
        	} 
        	
        	var opt = {
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId()
        	};
        	
//        	alert("[-a01- input]"+JSON.stringify(opt));
        	amGloble.api.a01.post(opt, function (ret) {
//        		alert("[-a01- return]" + JSON.stringify(ret));
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if(sessionStorage.getItem("sid") == null || sessionStorage.getItem("sid") == "" || sessionStorage.getItem("null")){
        				sessionStorage.setItem("sid", ret.content.data.sid); 
        			}

        		}
        	}, "application/json; charset=utf-8");
        },
        
        draw : function(){
        	var _this = this;
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			deviceType: _this.getDeviceType()
        	};
//        	alert("[-a07- input]"+JSON.stringify(opt));
        	amGloble.api.a07.post(opt, function (ret) {
//        		alert("[-a07- return]" + JSON.stringify(ret));
        		console.info(JSON.stringify(ret));
        		
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        			if (window._tag) { 
    					_tag.dcsMultiTrack("wt.event", "用户未中奖记录", "wt.msg",_this.getOpenid());
    				} 
    				_this.$page4Catoon.hide();
    				_this.$page4Share.hide();
    				_this.$page4page9.show();
        		} else {
        			var won = ret.content.data.win;
        			if (_debug) won = !ret.content.data.win;
        			
        			if (won){
        				if (window._tag) { 
        					_tag.dcsMultiTrack("wt.event", "用户中奖奖品", "wt.msg",ret.content.data.awardType);
        				} 
        				_this.$page4Catoon.hide();
        				_this.$page4Share.hide();
        				
        				if(ret.content.data.awardType == 17){
        					_this.$txt4Prize.html("格瓦拉电影兑换券一张，价值100元");
        				}else if(ret.content.data.awardType == 18){
        					_this.$txt4Prize.html("携程旅游券1张，价值1000元");
        				}else if(ret.content.data.awardType == 19){
        					_this.$txt4Prize.html("iPad Air 1台，价值2888元");
        				}
        				_this.$page4page7.show();
        			} else {
        				if (window._tag) { 
        					_tag.dcsMultiTrack("wt.event", "用户未中奖记录", "wt.msg",_this.getOpenid());
        				} 
	    				_this.$page4Catoon.hide();
	    				_this.$page4Share.hide();
	    				_this.$page4page9.show();
        			}
        		}
        	}, "application/json; charset=utf-8");
        },
        
        shareRecord : function(){
        	var _this = this;
        	
        	var option = _this.$touch4Choose.find("p").html();
	    	 if(option == "带上妈妈来一次久违的旅行" ){
	    		 option = 1;
	    	 }else if(option == "和妈妈看一场最新的热门电影"){
	    		 option = 2;
	    	 }else if(option == "带妈妈吃一顿浪漫的西式大餐"){
	    		 option = 3;
	    	 }else if(option == "为妈妈拍一组很青春的写真照"){
	    		 option = 4;
	    	 }else if(option == "教妈妈用最新的大屏智能手机"){
	    		 option = 5;
	    	 }
	    	 if($.trim(_this.$txt4TryThing.val())==""){
	    		 $("title").html(_this.$touch4Choose.find("p").html());
	    	 }else{
	    		 $("title").html(_this.$txt4TryThing.val());
	    	 }
	    	 
	    	 if (window._tag) { 
        		_tag.dcsMultiTrack("wt.event", "用户分享问题", "wt.msg",option);
        	 } 
      	  	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			deviceType: _this.getDeviceType(),
        			mediaType: "WX",
        			tasteOption: option,
        			otherContent: $.trim(_this.$txt4TryThing.val()),
        			shareResult : "1",
    				shareUrl : ""
        	};
      	  	
//  			alert("[-a06- input]"+JSON.stringify(opt));
  	  		amGloble.api.a06.post(opt, function (ret) {
//  	  			alert("[-a06- return]" + JSON.stringify(ret));
  	  			_this.$page4Share.show();
  	  		}, "application/json; charset=utf-8");
      	  		
        },
        
        
        winInfo : function(){
        	var _this = this;
        	if (_this.checkData()) {
        		var opt = {
        				sid : sessionStorage.getItem("sid"),
        				channelType: _this.getChannelType(),
        				userId: _this.getOpenid(),
        				token: _this.getToken(),
        				deviceType: _this.getDeviceType(),
        				name : $.trim(_this.$txt4UserName.val()),
        				phone : $.trim(_this.$txt4UserPhone.val()),
        				identityNum : $.trim(_this.$txt4UserID.val()),
        				address : $.trim(_this.$txt4UserAddress.val()),
        				postCode : $.trim(_this.$txt4UserAddressCode.val())
        		};
//	        	alert("[-a08- input]"+JSON.stringify(opt));
        		amGloble.api.a08.post(opt, function (ret) {
//	        		alert("[-a08- return]" + JSON.stringify(ret));
	        		if (ret.content.retCode != 0) {
	        			
	        		}else{
	        			if (window._tag) { 
	        				_tag.dcsMultiTrack("wt.event", "手机中奖用户和号码", "wt.msg", _this.getOpenid() + " || " + "" );
	        			} 
	        			
	        			_this.$page4page7.hide();
	        			_this.$page4page8.show();
	        		}
	        		
        			
        		}, "application/json; charset=utf-8");
        		
        	} 
        },
        
        checkData: function() {
        	var _this = this;
        	
        	var userName = $.trim(_this.$txt4UserName.val());
        	var telNo = $.trim(_this.$txt4UserPhone.val());
        	var userAddress = $.trim(_this.$txt4UserAddress.val());
        	var userAddressCode = $.trim(_this.$txt4UserAddressCode.val());
        	
        	
        	if(userName == ""){
				_this.$txt4UserName.focus();
				resulst = false;
				return resulst;
			}else{
				resulst = true;
			}
        	
			var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
			var resulst = telNo.match(telRegex1) || telNo.match(telRegex2) || telNo.match(telRegex3);
			
			if(!resulst){
				_this.$txt4UserPhone.val("");
				_this.$txt4UserPhone.focus();
				return resulst;
			}
			
			if(userAddress == ""){
				_this.$txt4UserAddress.focus();
				resulst = false;
				return resulst;
			}else{
				resulst = true;
			}
			var codeRegex = /^\d{6}$/;
			resulst = userAddressCode.match(codeRegex);
			if(!resulst){
				_this.$txt4UserAddressCode.val("");
				_this.$txt4UserAddressCode.focus();
				return resulst;
			}
			return resulst;
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
        
        getToken: function() {
        	var token = sessionStorage.getItem('token');
        	if ( token == null ||  token == "" || token=="null" ) {
        		token = amGloble.getQueryParameter("token");
	        	sessionStorage.setItem('token', token);
        	}
        	return token;
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