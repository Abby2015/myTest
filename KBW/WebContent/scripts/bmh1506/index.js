/**
 * Created by qing.wang on 2015/05/15.
 */

var Bmh = {};
Bmh.Page1 = Bmh.Page2 = Bmh.Page3 = Bmh.Page4  = Bmh.Page5  = Bmh.Page6  = Bmh.Page7  = Bmh.Page8  = Bmh.Page9 = {};

Bmh = {
	init : function(el){
	
		for (var i = 1; i <= 9; i++ ) {
			Bmh['Page'+i].init(el);
		}
		
		//初始化蒙层效果
		el.find('.share').bind('touchstart',function(){
			setTimeout(function(){
				el.find('.share').hide();
				//调用api：保存分享记录	----POST: /bmh1506/share		
		    	var opt = {
		    			sid: Bmh.getSid(),
		    			channelType: Bmh.getChannelType(),
		    			userId: Bmh.getOpenid(),
		    			deviceType: Bmh.getDeviceType(),
	        			mediaType: "WX",
	        			shareResult : 1,
	        			shareUrl : ""
		    	};
				amGloble.api.a03.post(opt, function(ret){
					if (amGloble.config.debug == true) alert("[-a03- return]" + JSON.stringify(ret));
		    		if (ret.content.errCode != 0) {
		    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
		    		} else {
		    			//el.find('.share_prize').show();
		    		}
		    	}, "application/json; charset=utf-8");
				
				//调用api：判断是否中奖	a06 ----POST: /bmh1506/draw {sid/channelType/userId/deviceType}
				opt = {
		    			sid: Bmh.getSid(),
		    			channelType: Bmh.getChannelType(),
		    			userId: Bmh.getOpenid(),
		    			deviceType: Bmh.getDeviceType()
		    	};
				if (amGloble.config.debug == true) alert("[-a06- input]" + JSON.stringify(opt));
				amGloble.api.a06.post(opt, function(ret){
					if (amGloble.config.debug == true) alert("[-a06- return]" + JSON.stringify(ret));
		    		if (ret.content.errCode != 0) {
		    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
		    			//$('#prizeTips').show();
		    			Bmh.toCardBag();
		    		} else {
		    				if (amGloble.config.debug == true) alert(typeof ret.content.data.win +  'win='+ret.content.data.win);
		    				Bmh.toCardBag();
		    				/*if (ret.content.data.win == true) {	//抽奖成功
		    					//跳转到填写手机号码页面---待做；
		    					$('#prizeForm').show();
		    				} else {
		    					//失败直接跳转到参与获取watch页面；---todo
		    					$('#prizeTips').show();
		    				}*/
		    		}
		    	}, "application/json; charset=utf-8");
				
			},100);
		});
		
		//区域放大
		el.find('#prizeForm  .cont').bind('touchstart',function(){
			$('#prizeForm  input').focus();
		});
		
		el.find('#prizeForm  .submit').bind('touchstart',function(){
			$('#errorTips').hide();
			//验证手机号码
			var telephone = $.trim($('#prizeForm input').val());
			if (telephone == '') {
				$('#prizeForm input').focus();
				$('#errorTips').text('手机号码不能为空！').show();
				return false;
			}
			var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
			var resulst = telephone.match(telRegex1) || telephone.match(telRegex2) || telephone.match(telRegex3);
			if(!resulst){
				$('#prizeForm input').val("");
				$('#prizeForm input').focus();
				$('#errorTips').text('手机号码有误！').show();
				return false;
			}
			//调用接口提交号码信息：---POST: /bmh1506y/winInfo a07  sid	channelType		userId	phone
			opt = {
	    			sid: Bmh.getSid(),
	    			channelType: Bmh.getChannelType(),
	    			userId: Bmh.getOpenid(),
	    			phone: telephone
	    	};
			if (amGloble.config.debug == true) alert("[-a07- input]" + JSON.stringify(opt));
			amGloble.api.a07.post(opt, function(ret){
				if (amGloble.config.debug == true) alert("[-a07- return]" + JSON.stringify(ret));
	    		if (ret.content.errCode != 0) {
	    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
	    		} else {
	    			setTimeout(function(){
	    				location.href = amGloble.config.selfRoot + 'pages/bmh1506/index.html';
	    			},100);
	    		}
	    	}, "application/json; charset=utf-8");
			
		});
		
		el.find('#prizeTips').bind('touchstart',function(){
			el.find('.overlay').hide();
			$(this).hide();
			setTimeout(function(){
				location.href = amGloble.config.selfRoot + 'pages/bmh1506/index.html';
			},100);
		});
		
		if (this.isRedirect()){
			Bmh.goToPage(6);
		} else {
			//Bmh.goToPage(1);
		}	
	},
	isRedirect : function(){
		var localPromoCode = localStorage.getItem("localPromoCode");
		var localScore = localStorage.getItem("gameScore");
		if ( localPromoCode == null || localScore == null) {
			return false;
		} else {
			return true;
		}	
	},
	goToPage : function(index){	//控制显示页面
		var msg = '';
		switch(index) {
			case 1:
				msg = '进入活动首页';
				break;
			case 2:
				msg = '进入游戏向导页面';
				break;
			case 3:
				msg = '进入游戏页面';
				break;
			case 4:
				msg = '';
				break;
			case 5:
				msg = '';
				break;
			case 6:
				msg = '进入半价券页面';
				break;
			case 7:
				msg = '进入无券页面';
				break;
			case 8:
				msg = '进入失败页面';
				break;
			case 9:
				msg = '进入产品介绍页面';
				break;
				
		}
		Bmh.saveOpenRecord(msg);
		for (var i = 1; i <= 9; i++) {
			if (i == index) {
				$('.page'+i).show();
				if (i==3) {Game.init();}
				if (i == 6 || i == 7 || i == 8) {
					Bmh['Page'+i].initGameScore();
				}
			} else {
				$('.page'+i).hide();
			}
		}
	},
	share : function() {
		//显示萌层
		$('.overlay').show();
		$('.share').show();
	},
	//保存打开记录
    saveOpenRecord: function(msg) {
    	var _this = this;
    	if (window._tag) { 
    		_tag.dcsMultiTrack("wt.event", msg, "wt.msg", _this.getOpenid());
    	}
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
    },
    getSid: function () { 
    	return sessionStorage.getItem('sid');
    },
    toCardBag : function () {
    	var opt = {
			promoCode: sessionStorage.getItem("promoCode")
    	};
		if (amGloble.config.debug == true) alert("[-a05- input]" + JSON.stringify(opt));
		amGloble.api.a05.post(opt, function(ret){
			if (amGloble.config.debug == true) alert("[-a05- return]" + JSON.stringify(ret));
    		if (ret.content.errCode != 0) {
    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
    		} else {
    			//开始跳转
    			var url = 'http://crmminisite.verystar.cn/wxcard/promo?key='+ret.content.data.data+'&wechat_card_js=1';
    			if (amGloble.config.debug == true) alert(url);
    			location.href = url;
    		}
    	}, "application/json; charset=utf-8");
    }
};

Bmh.Page1 = {
	init : function(el){
		Bmh.saveOpenRecord("进入活动首页");
		//立即开始
		el.find('.start_button').bind('touchstart',function (){
			Bmh.goToPage(2);
		});
		//导航菜单 我的半价券 page6
		el.find('.page1 .ticket_button').bind('touchstart',function (){
			if (amGloble.config.debug == true) {alert(typeof sessionStorage.getItem("promoCode") + ',promoCode='+sessionStorage.getItem("promoCode"));}
			if (sessionStorage.getItem("promoCode") == '' || sessionStorage.getItem("promoCode") == null) {
				Bmh.goToPage(2);
			} else {
				Bmh.toCardBag();				
			}
		});
		//了解产品
		el.find('.know_button').bind('touchstart',function (){
			Bmh.goToPage(9);
		});
		//活动规则
		el.find('.rule_button').bind('touchstart',function (){
			el.find('.overlay').show();
			el.find('.rule_pop').show();
			new IScroll("#wrapper");
		});
		//关闭活动规则
		el.find('.rule_pop .close').bind('touchstart',function (){
			el.find('.overlay').hide();
			el.find('.rule_pop').hide();
		});
	}	
};

Bmh.Page2 = {
	init : function(el){
		//进入游戏
		el.find('.page2').bind('touchstart',function (){
			Bmh.goToPage(3);
		});
	}
};

Bmh.Page3 = {
	init : function(el){
	}
};

Bmh.Page4 = {	//结果页：成功---待确认
	init : function(el){
	}	
};

Bmh.Page5 = {	//结果页：失败---待确认
	init : function(el){
	}	
};
Bmh.Page6 = {
	initGameScore : function () {
		//修改次数
		var score = localStorage.getItem('gameScore');
		var localPromoCode = localStorage.getItem("localPromoCode");
		$('.page6 .num').text(score+'次');
		$('#pcode').html(localPromoCode);
	},	
	init : function(el){
		//我的半价券--》进入卡包
		el.find('.page6 .ticket_button').bind('touchstart',function (){
			//调用接口进入卡包；POST: /bmh1506/collect
			Bmh.toCardBag();
		});
		//需求待定--收入郎中--》进入卡包
		el.find('.page6 .look_button').bind('touchstart',function (){
			//调用接口进入卡包；POST: /bmh1506/collect
			//Bmh.toCardBag();
			Bmh.share();
			$('.page6').hide();
		});
		//显摆一下
		el.find('.page6 .show_button').bind('touchstart',function (){
			Bmh.share();
			$('.page6').hide();
		});
		
		//再玩一次
		el.find('.page6 .again_button1').bind('touchstart',function (){
			$('title').html('爆米花爱上花淇淋');
			Bmh.goToPage(3);
		});
	}	
};

Bmh.Page7 = {	//无券页
	initGameScore : function () {
		//修改次数
		var score = localStorage.getItem('gameScore');
		$('.page7 .num').text(score+'次');
	},	
	init : function(el){
		//显摆一下
		el.find('.page7').bind('touchstart',function (){
			Bmh.share();
			$('.page7').hide();
		});
	}	
};
Bmh.Page8 = {	//失败页
	initGameScore : function () {
		//修改次数
		var score = localStorage.getItem('gameScore');
		$('.page8 .num').text(score+'次');
	},	
	init : function(el){
		//再玩一次
		el.find('.page8 .again_button1').bind('touchstart',function (){
			$('title').html('爆米花爱上花淇淋');
			Bmh.goToPage(3);
		});
		//求助好友
		el.find('.page8 .guys_button1').bind('touchstart',function (){
			//todo 分享操作 调用API
			Bmh.share();
			$('.page8').hide();
		});
	}	
};
Bmh.Page9 = {	//产品介绍
	init : function(el){
		//返回
		el.find('.page9').bind('touchstart',function (){
			Bmh.goToPage(1);
		});
	}	
};


$(function(){	
	Bmh.init($('.am-app'));
});