﻿/**
 * Created by qing.wang on 2015/07/06.
 */
var Result = {
	count : 0,	
	init  : function () {
		Result.count = 0;
		$('#result').text(0);
	}
};

var Timer = {
	info : [0,0],
	init  : function () {
		Timer.info = [0,0];
	}
};

var clock_ = setInterval(function(){
	Timer.info[1]++;
	if (Timer.info[1] == 9) {
		Timer.info[0]++;
		Timer.info[1] = 0;
	}
	$('#timer').text(Timer.info.join(":"));
},100);

var Game = {
	init : function () {
		Result.init();
		Timer.init();
		$(".arrow").attr("style","");
	},
	selected : function () {	//选中效果
		$('#result').text(++Result.count);
	}
};



$(function(){
    var controller = {

        init: function() {
        	if (amGloble.config.debug == true) alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$page4page1 = $(".page1");
        	this.$page4page2 = $(".page2");
        	this.$page4page3 = $(".page3");
        	this.$page4page4 = $(".page4");
        	this.$page4page5 = $(".page5");
        	
        	this.$page4rule = $(".rule_pop");
        	this.$page4overlay = $(".overlay");
        	this.$page4tips = $(".popovery");
        	this.$page4share = $(".share_pop");
        	
        	this.$touch4home = $(".logo,.fullimg.again");
        	this.$touch4Rule = $(".rule_button");
        	this.$touch4closeRule = $(".close_icon.rule");
        	this.$touch4go2game = $(".go");
        	this.$touch4share = $(".fullimg.share");
        	this.$touch4choose = $(".img");
        	
        	this.$touch4collect = $(".save_button");
        	
        	this.$txt4result = $("#result");
        	this.$txt4time = $("#s_time");
        	this.$txt4title = $("title");
        	this.$txt4imgTexts = $(".fullimg.texts");
        	this.$txt4imgFloors = $(".fullimg.floor_");
        	
        	
        	
        	var count_1 = 0;
        	var catons1_ = setInterval(function(){
        		count_1++;
        		_this.$txt4imgTexts.attr("src",amGloble.config.selfRef+"images/ccnf1507/fly"+count_1+".png")
        		if(count_1==17){
        			count_1 = 0;
        		}
        	},410);
        	var count_2 = 0;
        	var catons2_ = setInterval(function(){
        		count_2++;
        		_this.$txt4imgFloors.attr("src",amGloble.config.selfRef+"images/ccnf1507/floor"+count_2+".png")
        		if(count_2==4){
        			count_2 = 0;
        		}
        	},500);
        	
        	
        	//节点绑定事件
        	this.$touch4collect.bind("touchstart click",function(){
        		_this.collectToCardBag();
        	});
        	this.$page4share.bind("touchstart click",function(){
        		_this.shareRecord();
        	});
        	this.$touch4home.bind("touchstart click",function(){
        		_this.toTarget();
        		_this.transLocation();
        	});
        	
        	this.$touch4go2game.bind("touchstart click",function(){
        		_this.$page4page1.hide();
        		_this.$page4page2.show();
        		setTimeout(function(){
        			_this.$page4page2.hide();
        			_this.$page4page3.show();
        			setTimeout(function(){
        				_this.$page4page3.hide();
        				_this.$page4page4.fadeIn(2000);
        				Game.init();
        				if (window._tag) { 
        					_tag.dcsMultiTrack("wt.event", "用户进入游戏页面", "wt.msg", _this.getOpenid());
        				}
        			},1500);
        		},4000);
        	});
        	this.$touch4Rule.bind("touchstart click",function(){
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "用户查看规则", "wt.msg", _this.getOpenid());
            	}
        		_this.$page4overlay.show();
        		_this.$page4rule.show();
        		new IScroll("#wrapper");
        	});
        	this.$touch4closeRule.bind("touchstart click",function(){
        		_this.$page4rule.hide();
        		_this.$page4overlay.hide();
        	});
        	this.$touch4share.bind("touchstart click",function(){
//        		_this.$page4page5.hide();
        		_this.$page4overlay.show();
        		_this.$page4share.show();
        	});
        	
        	this.$page4tips.bind("touchstart click",function(){
        		_this.$page4tips.hide();
        		_this.$page4overlay.hide();
        	});
        	
        	this.$touch4choose.bind('touchstart click', function () {
    			//1.区分是否已经选中过
        		_this.chooseWings(this);
    		});
        	
        	_this.transLocation();
        	
        	//判断是否从卡包返回
        	/* var flag = sessionStorage.getItem("flag");
             if(flag == "true"){
             	_this.$txt4time.html(localStorage.getItem("time_"));
             	_this.$txt4title.html("我在"+localStorage.getItem("time_")+"S内戳住鸡翅,肯德基插翅难飞拼眼疾手快，高手来战！");
             	_this.$page4page5.show();
             }else{
             	_this.loadImg();
             }*/
        	
        	
        	_this.loadImg();
        	_this.bindWChatAPI();
        },
        
        transLocation : function(){
        	var _this = this;
        	$ds = $(".container.animate-stage").find(".img.not:eq(1)");
        	$(".container.animate-stage").find(".img.not:eq(1)").remove();
        	
        	var random_ = Math.floor(4 + Math.random()*6);
        	
        	$(".container.animate-stage").find(".img :eq("+random_+")").after($ds);
        	_this.$touch4choose = $(".img");
        	_this.$touch4choose.unbind("touchstart click");
        	_this.$touch4choose.bind('touchstart click', function () {
    			//1.区分是否已经选中过
        		_this.chooseWings(this);
    		});
//        	console.dir($(".container.animate-stage").find(".img.not:eq(1)"));
        },
        
        
        chooseWings : function(param){
        	var _this = this;
        	if ($(param).find('.arrow').attr("style") != undefined && $(param).find('.arrow').attr("style")!="") {
        		console.info("have selected...");
        	} else {
        		//2.对于非鸡翅选择,惩罚性添加时间
        		$(param).find('.arrow').show();
        		if($(param).hasClass("not")){
        			Timer.info[0] += 10;
        		}else{
        			Game.selected();
        		}
        	}
        	//3.跳转结果页面
        	var num = _this.$txt4result.text();
        	var time_ = Timer.info[0];
        	
        	if(num>=10 || num=="10"){
        		_this.$txt4time.html(time_);
//        			_this.$txt4resultDesc.html("手速快如闪电，超级无影手当之无愧!");
        		localStorage.setItem("time_", time_);
        		_this.$txt4title.html("我在"+time_+"S内戳住鸡翅,肯德基插翅难飞拼眼疾手快，高手来战！");
        		_this.askCoupon();
        		
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
        
        bindWChatAPI : function(){
        	   var jsApiList = ['showOptionMenu']; 
    		   construct(amGloble.config.appid,  amGloble.config.selfRef + "pages/ccnf1507/index.html", jsApiList);
        },
        
        askCoupon : function(type){
        	var _this = this;
        	
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			gameResult : true
        		};
        	if (amGloble.config.debug == true) alert("[-a02- input]" + JSON.stringify(opt));
        	amGloble.api.a02.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a02- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        		} else {
        			sessionStorage.setItem("result", ret.content.data.result);
        			if(ret.content.data.result !="4"){
        				localStorage.setItem("promoCode", ret.content.data.promoCode); 
        			}
    				_this.$page4page4.hide();
    				_this.$page4page5.show();
        			
        		}
        	}, "application/json; charset=utf-8");
        },
        
        collectToCardBag : function(){
        	var _this = this;
        	var result = sessionStorage.getItem("result");
        	if (amGloble.config.debug == true) alert("collectToCardBag result=="+result);
        	var nowTime  = new Date();
    		var nowTime_ = nowTime.getFullYear()+"-"+nowTime.getMonth()+"-"+nowTime.getDate();
    		var collect_flag = localStorage.getItem("collect_flag");
        	
        	if(result == "1" && nowTime_ == collect_flag){
        		_this.$page4overlay.show();
				_this.$page4tips.show();
        	}else{
        		if(result == "4"){
        			$("#show_tips").show().fadeOut(3000);
//        			$("#show_tips").fadeOut(3000);
        		}else{
        			//保存当天存储记录
            		localStorage.setItem('collect_flag', nowTime_);
            		
            		var opt = {
                			promoCode : localStorage.getItem("promoCode")
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
        		}
        	}
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
            		_this.$page4overlay.hide();
        			_this.$page4share.hide();
        		}
        	}, "application/json; charset=utf-8");
        },
        
        toTarget : function(){
			var _this = this;
        	var opt = {
        			channelType: _this.getChannelType(),
        			userId: _this.getOpenid(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId()
        	};
        	
        	if (amGloble.config.debug == true) alert("[-a01- input]"+JSON.stringify(opt));
        	amGloble.api.a01.post(opt, function (ret) {
        		if (amGloble.config.debug == true) alert("[-a01- return]" + JSON.stringify(ret));
        		if (ret.content.errCode != 0 || ret.content.data == null) {
        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
        			location.href=amGloble.config.selfRoot + "api/ccnf1507/login.do";
        		} else {
        			if (window._tag) { 
                		_tag.dcsMultiTrack("wt.event", "用户进入活动首页", "wt.msg", _this.getOpenid());
                	}
        			
        			sessionStorage.setItem("sid", ret.content.data.sid); 
        			if (amGloble.config.debug == true) alert(amGloble.config.selfRef + "pages/ccnf1507/index.html");
        			_this.$page4page2.hide();
            		_this.$page4page3.hide();
            		_this.$page4page4.hide();
            		_this.$page4page5.hide();
//            		_this.$page4page6.hide();
            		_this.$page4tips.hide();
//            		$('title').html("肯德基插翅难飞拼眼疾手快，高手来战！");
            		_this.$page4page1.show();
        		}
        	}, "application/json; charset=utf-8");
		},
        
		/**
		 * 预加载图片
		 */
        loadImg : function(){
        	var _this = this;
        	var imgArr = [
        	              amGloble.config.selfRef+"images/ccnf1507/again_button.png",
                          amGloble.config.selfRef+"images/ccnf1507/arrow.png",
                          amGloble.config.selfRef+"images/ccnf1507/barrel.png",
                      	  amGloble.config.selfRef+"images/ccnf1507/body.jpg",
                          amGloble.config.selfRef+"images/ccnf1507/close.png",
                          amGloble.config.selfRef+"images/ccnf1507/fire.png",
                          amGloble.config.selfRef+"images/ccnf1507/fires.png",
                          amGloble.config.selfRef+"images/ccnf1507/floor.png",
                          amGloble.config.selfRef+"images/ccnf1507/floor1.png",
                          amGloble.config.selfRef+"images/ccnf1507/floor2.png",
                          amGloble.config.selfRef+"images/ccnf1507/floor3.png",
                          amGloble.config.selfRef+"images/ccnf1507/floor4.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly1.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly10.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly11.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly12.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly13.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly14.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly15.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly16.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly17.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly2.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly3.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly4.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly5.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly6.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly7.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly8.png",
                          amGloble.config.selfRef+"images/ccnf1507/fly9.png",
                          amGloble.config.selfRef+"images/ccnf1507/go.png",
                          amGloble.config.selfRef+"images/ccnf1507/group.png",
                          amGloble.config.selfRef+"images/ccnf1507/icon.png",
                          amGloble.config.selfRef+"images/ccnf1507/kfc_p.png",
                          amGloble.config.selfRef+"images/ccnf1507/light.png",
                          amGloble.config.selfRef+"images/ccnf1507/line1.png",
                          amGloble.config.selfRef+"images/ccnf1507/logo.png",
                          amGloble.config.selfRef+"images/ccnf1507/page1.jpg",
                          amGloble.config.selfRef+"images/ccnf1507/page2_p.png",
                          amGloble.config.selfRef+"images/ccnf1507/page2.jpg",
                          amGloble.config.selfRef+"images/ccnf1507/page3.jpg",
                          amGloble.config.selfRef+"images/ccnf1507/page4.jpg",
                          amGloble.config.selfRef+"images/ccnf1507/page5_p.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic1.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic10.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic11.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic12.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic2.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic3.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic4.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic5.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic6.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic7.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic8.png",
                          amGloble.config.selfRef+"images/ccnf1507/pic9.png",
                          amGloble.config.selfRef+"images/ccnf1507/save_button.png",
                          amGloble.config.selfRef+"images/ccnf1507/share_button.png",
                          amGloble.config.selfRef+"images/ccnf1507/share_icon.png",
                          amGloble.config.selfRef+"images/ccnf1507/share.png",
                          amGloble.config.selfRef+"images/ccnf1507/ticket.png",
                          amGloble.config.selfRef+"images/ccnf1507/time_icon.png",
                          amGloble.config.selfRef+"images/ccnf1507/tong.png"
                          ];
            preloadimg(imgArr,function(){
                $('#loading').hide();
                var flag = sessionStorage.getItem("flag");
                if(flag == "true"){
                	_this.$txt4time.html(localStorage.getItem("time_"));
                	_this.$txt4title.html("我在"+localStorage.getItem("time_")+"S内戳住鸡翅,肯德基插翅难飞拼眼疾手快，高手来战！");
                	_this.$page4page5.show();
                }else{
                	_this.$page4page1.show();
                }
//                _this.$page4page1.show();
                
            });
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