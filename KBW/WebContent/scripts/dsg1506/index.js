var Dsg = {};
Dsg.Page1 = Dsg.Page2 = Dsg.Page3 = Dsg.Page4 = Dsg.Page5 = Dsg.Page6 = Dsg.Page7 = Dsg.Page8 = {};
Dsg = {
	answers : [],
	init : function (el) {
		for (var i = 1; i <= 8; i++) {			
			Dsg['Page'+i].init(el);
		}
		Dsg.bindWChatAPI();
		this.commonInit(el);
	},
	resetTitle : function () {
		$('title').html('做有态度的单身汪，一人也可享海盐焦糖风味花淇淋半价！');
	},
	commonInit : function (el) {		
		//逆袭宝典--活动详情
		el.find('.rule_button').bind('touchstart', function (){
			//todo
			el.find('.overlay').show();
			el.find('.rule_pop').show();
			new IScroll("#wrapper");
		});
		
		el.find('.rule_pop .close_icon').bind('touchstart', function (){
			el.find('.overlay').hide();
			el.find('.rule_pop').hide();
		});
		
		//我的半价券
		el.find('.ticket_button').bind('touchstart', function (){
			/*var grayPng = amGloble.config.selfRoot + 'images/dsg1506/ticket_button_gray.png',
				defaultPng = amGloble.config.selfRoot + 'images/dsg1506/ticket_button.png';*/
			if (amGloble.config.debug == true) {alert(typeof sessionStorage.getItem("promoCode") + ',promoCode='+sessionStorage.getItem("promoCode"));}
			if (sessionStorage.getItem("promoCode") == '' || sessionStorage.getItem("promoCode") == null) {
				Dsg.goToPage(2);
			} else {
				Dsg.toCardBag();
			}
		});
		
		//分享主界面
		el.find('.pop_share').bind('touchstart', function (){
			var self = this;
			setTimeout(function(){
				$(self).hide();
				//调用api：保存分享记录	----POST: /dsg1506/share		
		    	var opt = {
		    			sid: Dsg.getSid(),
		    			channelType: Dsg.getChannelType(),
		    			userId: Dsg.getOpenid(),
		    			deviceType: Dsg.getDeviceType(),
	        			mediaType: "WX",
	        			shareResult : 1,
	        			shareUrl : ""
		    	};
				amGloble.api.a03.post(opt, function(ret){
					if (amGloble.config.debug == true) alert("[-a03- return]" + JSON.stringify(ret));
		    		if (ret.content.errCode != 0) {
		    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
		    		} else {
		    			//todo
		    			$('.overlay').hide();		    			
		    			Dsg.goToPage(8);
		    		}
		    	}, "application/json; charset=utf-8");
				
			},100);
		
		});
		
		//结果页公共部分；
		//保存到卡包--秀出态度
		el.find('.save_button').bind('touchstart', function (){	
			var pageIndex = $(this).parent().parent().parent().attr('class').replace("page page","");
			Dsg.share(pageIndex);
		});
		
		//再测一次
		el.find('.show_button').bind('touchstart', function (){	
			
			Dsg.goToPage(2);
		});
		
		
	},
	bindWChatAPI : function(){
		var jsApiList = ['showOptionMenu']; 
		construct(amGloble.config.appid,  amGloble.config.selfRoot + "pages/dsg1506/index.html", jsApiList);
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
		if (amGloble.config.env == 'local') {
			return openid = "14D1CBF3CC1F9E78F90FF364CF2B73020A69BB22253";
		} else {
			return openid;
		}
		
    },
    getChannelType: function() {
    	var channelType = sessionStorage.getItem('channelType');
    	if ( channelType == null ||  channelType == "" || channelType=="null" ) {
    		channelType = amGloble.getQueryParameter("channelType");
    		sessionStorage.setItem('channelType', channelType);
    	}
    	if (amGloble.config.env == 'local') {
    		return channelType = 1;
    	} else {
    		return channelType;
    	}
    },
    getDeviceType: function() {
    	var deviceType = sessionStorage.getItem('deviceType');
    	if ( deviceType == null ||  deviceType == "" || deviceType=="null" ) {
    		deviceType = amGloble.getQueryParameter("deviceType");
    		sessionStorage.setItem('deviceType', deviceType);
    	}
    	if (amGloble.config.env == 'local') {
    		return deviceType = 1;
    	} else {
    		return deviceType;
    	}
    },
    getDeviceId: function() {
    	var deviceid = localStorage.getItem('deviceid');
    	if ( deviceid == null ||  deviceid == "" || deviceid=="null"  ) {
    		deviceid = amGloble.guid();
    		localStorage.setItem('deviceid', deviceid);
    	}
    	if (amGloble.config.env == 'local') {
    		return deviceid = '3996e7ac-a3bf-d5ce-5f90-aab21ec68c0a';
    	} else {
    		return deviceid ;
    	}
    },
    getSid: function () { 
    	return sessionStorage.getItem('sid');
    },    
	goToPage : function (index) {
		var msg = '';
		switch(parseInt(index)) {
			case 1:
				msg = '进入首页';
				break;
			case 2:
				msg = '进入答题页1';
				break;
			case 3:
				msg = '进入答题页2';
				break;
			case 4:
				msg = '进入答题页3';
				break;
			case 5:
				msg = '进入A结果页';
				break;
			case 6:
				msg = '进入B结果页';
				break;
			case 7:
				msg = '进入C结果页';
				break;
				
		}
		Dsg.saveOpenRecord(msg);
		for (var i = 1; i <= 8; i++ ) {			
			if (i == index) {
				$('.page'+i).show();
			} else {
				$('.page'+i).hide();
			}
		}
		//页面每次切换，都重置title
		Dsg.resetTitle();
	},
	checkAnswers: function () {
		var group = {a:0,b:0,c:0}, len = Dsg.answers.length,tmp = [0,0,0];
		for (var i = 0 ; i < len; i++) {
			switch (Dsg.answers[i]){
				case 'a':
					tmp[0] = ++group['a'];
					break;
				case 'b':
					tmp[1] = ++group['b'];
					break;
				case 'c':
					tmp[2] = ++group['c'];
					break;
			}
		}
		var index = 0;
		if (tmp[0] == 1 && tmp[1] == 1 && tmp[2] == 1) {	//ABC各选了一个
			index = 7;
		} else {
			var max = Math.max.apply(Math,tmp), maxChar = null;
			for (prop in group) {
				if (group[prop] ==  max) {
					maxChar = prop;
					break;
				}
			}
			switch (maxChar){
				case 'a':
					index = 5;	//A多页面
					break;
				case 'b':
					index = 6;	//B多页面
					break;
				case 'c':
					index = 7;	//C多页面
					break;
			}
		}
		return index;
	},
	toCardBag : function () {
		if (amGloble.config.env == 'local') {return ;}
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
	},
	share : function (fromPageIndex) {
		$('.overlay').show();					
		var title1 = title2 = "";
		switch (parseInt(fromPageIndex)) {
			case 5:	//a 多
				title1 = "一个人也要享半价，就是单身汪的完美生活态度！";
				//title2 = "我心里居然住了一个完美主义型单身汪，那你呢？";
				break;
				
			case 6:	//b 多
				title1 = "就算一个人也绝不将就，get神技能畅享半价！";
				//title2 = "据说每个人心里都住了一个单身汪，敢不敢来测测！";
				break;
				
			case 7:	//c 多 或 ABC各一个
				title1 = "一个人也能享的半价，就是单身汪的任性style！";
				//title2 = "对单身汪而言，一个任性享半价是种什么体验？";
				break;
		}
		//分享前，提前修改title
		$('title').html(title1);
		switch (parseInt(fromPageIndex)) {
			case 5:	//a 多
				//shareClass = 'pop_share1';
				$(".pop_share1").show();
				break;
				
			case 6:	//b 多
				//shareClass = 'pop_share2';
				$(".pop_share2").show();
				break;
				
			case 7:	//c 多 或 ABC各一个
				//shareClass = 'pop_share3';
				$(".pop_share3").show();
				break;
		}
	}
};


Dsg.Page1 = {
	init : function (el) {		
		//测出态度		
		el.find('.page1 .start_button').bind('touchstart', function (){
			Dsg.goToPage(2);
		});
		el.find('.page1 .page1_p').bind('touchstart', function (){
			Dsg.goToPage(2);
		});
		el.find('.page1 .icecream').bind('touchstart', function (){
			Dsg.goToPage(2);
		});
	}	
};

Dsg.Page2 = {
	init : function (el) {		
		el.find('.page2 .question>li').bind('touchstart', function (){
			var tmp = $(this).attr('class').split('');
			Dsg.answers[0] = tmp[0];
			Dsg.goToPage(3);
		});
	}	
};

Dsg.Page3 = {
	init : function (el) {
		el.find('.page3 .question>li').bind('touchstart', function (){
			var tmp = $(this).attr('class').split('');
			Dsg.answers[1] = tmp[0];
			Dsg.goToPage(4);
		});
	}	
};

Dsg.Page4 = {
	init : function (el) {
		el.find('.page4 .question>li').bind('touchstart', function (){
			var tmp = $(this).attr('class').split('');
			Dsg.answers[2] = tmp[0];
			var toIndex = Dsg.checkAnswers();
			//调用接口跳转页面---api/dsg1506/ask  {sid,userId,channelType,deviceType,firstSelect,	secondSelect,thirdSelect}
			var opt = {
				sid:	Dsg.getSid(),
				channelType: Dsg.getChannelType(),
				userId: Dsg.getOpenid(),
				deviceType: Dsg.getDeviceType(),
				firstSelect: Dsg.answers[0],
				secondSelect:Dsg.answers[1],
				thirdSelect:Dsg.answers[2]
	    	};
	    	
			if (amGloble.config.debug == true) alert("[-a02- input]"+JSON.stringify(opt));
	    	amGloble.api.a02.post(opt, function (ret) {
	    		if (amGloble.config.debug == true) alert("[-a02- return]" + JSON.stringify(ret));
	    		if (ret.content.errCode != 0 || ret.content.data == null) {
	    			if (amGloble.config.debug == true)	alert(JSON.stringify(ret));
	    		} else {
					var answerResult = ret.content.data.result;
					if (answerResult == 0) {	//有券						
						sessionStorage.setItem("promoCode", ret.content.data.promoCode);
						localStorage.setItem("localPromoCode", ret.content.data.promoCode);
					} else if (answerResult == 1) {	//无券
						sessionStorage.setItem("promoCode", "");
					}
					Dsg.goToPage(toIndex);
	    		}
		    }, "application/json; charset=utf-8");
		});
	}	
};

Dsg.Page5 = {
	init : function (el) {
		//保存到卡包--秀出态度
		el.find('.page5 .save_button').bind('touchstart', function (){	
			Dsg.share();
			//Dsg.toCardBag();
		});
		
		//再测一次
		el.find('.page5 .show_button').bind('touchstart', function (){			
			Dsg.goToPage(2);
		});
	}	
};

Dsg.Page6 = {
	init : function (el) {
		//保存到卡包--秀出态度
		el.find('.page6 .save_button').bind('touchstart', function (){
			Dsg.share();
			//Dsg.toCardBag();
		});
		
		//再测一次
		el.find('.page6 .show_button').bind('touchstart', function (){			
			Dsg.goToPage(2);
		});
	}	
};
	
Dsg.Page7 = {
	init : function (el) {
		//保存到卡包--秀出态度
		el.find('.page7 .save_button').bind('touchstart', function (){	
			Dsg.share();
		});
		
		//再测一次
		el.find('.page7 .show_button').bind('touchstart', function (){			
			Dsg.goToPage(2);
		});
	}	
};

Dsg.Page8 = {
	init : function (el) {
		el.find('.page8 .save_weixin').bind('touchstart', function (){
			Dsg.toCardBag();
		});
		el.find('.page8 .close_button').bind('touchstart', function (){
			var toIndex = Dsg.checkAnswers();
			Dsg.goToPage(toIndex);
		});
	}	
};

$(function(){
	Dsg.init($('body'));
});