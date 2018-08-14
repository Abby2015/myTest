/**
 * Created by qing.wang on 2015/03/23.
 */
$(function(){
    var controller = {
    		
        init: function() {
//        	alert(" ===index.jsp=== \n"+JSON.stringify(location));
        	this.$base_ctx = $('#base_ctx');
        	
        	this.$page4All = $(".am-app.all");
            this.$page4Question = $(".am-page.Reliable");
            
            this.$page4FuguYes = $("div.fugu > div.am-page.yes");
            this.$page4FuguNo = $("div.fugu > div.am-page.no");

            this.$page4KaoyaYes = $("div.kaoya > div.am-page.yes");
            this.$page4KaoyaNo = $("div.kaoya > div.am-page.no");
            
            this.$page4NewMenuYes = $("div.newMenu > div.am-page.yes");
            this.$page4NewMenuNo = $("div.newMenu > div.am-page.no");
            
            this.$page4Bill = $(".am-page.Cloud.Bill");
            this.$page4Bill2 = $(".am-page.Cloud.Bill2");
            this.$page4Bill3 = $(".am-page.Cloud.Bill3");
            this.$page4Coupon = $(".am-page.Cloud.First");
            
            this.$page4DoubleCoupon = $(".am-page.Cloud.Double");
            
            this.$page4Overlay = $(".overlay");
            this.$page4Pop_Up = $(".pop_up");
            
			this.$page4Share_Step = $(".share_tip");
			this.$page4Share_Step2 = $(".share_tip2");
			
			
			
			this.$page4Share_Show = $(".share_showtip");
			
            this.$page4Masking = $(".Masking");
            this.$page4PopUp = $(".popup");
            
            this.$page4Catoon1 = $(".catoon1");
            this.$page4Catoon2 = $(".catoon2");
            this.$page4Catoon3 = $(".catoon3");
            
            this.$touch4Catoon1_Yes = $(".catoon1 .kaopu.animated");
            this.$touch4Catoon2_Yes = $(".catoon2 .believeBtn.animated");
            this.$touch4Catoon3_Yes = $(".catoon3 .believeBtn.animated");
            
            this.$touch4Catoon1_No = $(".catoon1 .bukaopu.animated");
            this.$touch4Catoon2_No = $(".catoon2 .Donot_believeBtn.animated");
            this.$touch4Catoon3_No = $(".catoon3 .Donot_believeBtn1.animated");
            
            this.$touch4Draw = $(".rewardBtn.animated.gelatine");
            
            this.$touch4Share_NoWin = $(".share_Btn.animated.gelatine");
            this.$touch4Share_More = $(".click_share,.word");
            
            
//            this.$touch4Again = $(".once_againRed.animated.lightSpeedIn, .againBtn_red.animated.gelatine, .refuse_Btn.animated.lightSpeedIn, .againBtn_blue.animated.gelatine");
            this.$touch4Again = $(".once_againRed.animated.lightSpeedIn, .refuse_Btn.animated.lightSpeedIn, .againBtn_blue.animated.gelatine");
            
            this.$touch4AgainDouble = $(".againBtn_red.animated.gelatine, .againBtn_blue.animated.lightSpeedIn");
            
            
            this.$touch4Home = $(".logo");
            this.$touch4Help = $(".help");
            
            this.$touch4Rule = $(".activity_ruleBtn");
            this.$touch4WinPhone = $(".activity_nameBtn");
            
            this.$touch4Charge = $(".submitBtn.animated.gelatine");
            this.$touch4Charge2 = $(".submitBtn.animated.gelatine2");
            this.$touch4Charge3 = $(".rewardBtn.animated.gelatine3");
            this.$touch4Close = $(".close");
            
            
            
            this.$touch4ShareFriend = $(".friendBtn.animated.gelatine");
            
            this.$touch4ShowBig = $(".am-page.Cloud.First .set_meal img, .am-page.Cloud.Double .quan.animated.slideInDown img, .am-page.Cloud.Double .quan img");
            this.$page4BigShow = $(".pop");
            this.$touch4closeBig = $(".pop .close_popimg");
            this.$txt4ShowImg = $(".pop img");
            
            
            this.$txt4TelTips = $("#telTips");
            
            this.$txt4TelNo = $("#telNo");
            this.$txt4TelNo2 = $("#telNo2");
            this.$txt4Rule = $(".container.rule");
            this.$txt4Winlist = $(".container.winlist");
            this.$txt4ShowPhone = $("#showPhone");
            
            
            this.$img4FirstCopon = $(".am-page.Cloud.First  .set_meal  img, .am-page.Cloud.Double  .quan.animated.slideInDown  img");
            this.$img4DoubleCopon = $(".am-page.Cloud.Double  .magic.magic_Copy.animated  .quan  img");
            
            
            var _this = this;
            
            
            
            this.$touch4ShareFriend.bind("click",function(){
//            	_this.$page4Pop_Up.hide();
            	
            });
            
            
            this.$touch4closeBig.bind("click",function(){
            	_this.$page4BigShow.hide();
            });
            
            
            this.$touch4Rule.bind("click",function(){
            	_this.$touch4Rule.addClass("now");
            	_this.$touch4WinPhone.removeClass("now");
            	_this.$txt4Rule.show();
            	
            	_this.$txt4Winlist.hide();
            });
            
            this.$touch4WinPhone.bind("click",function(){
            	_this.$touch4WinPhone.addClass("now");
            	_this.$touch4Rule.removeClass("now");
            	_this.$txt4Rule.hide();
            	_this.$txt4Winlist.show();
            });
            
            
            this.$touch4Close.bind("click",function(){
            	_this.$page4Overlay.hide();
            	_this.$page4Pop_Up.hide();
            	_this.$page4Masking.hide();
            });
            
            
            this.$page4Share_Show.bind("click",function(){
            	setTimeout(function(){
            		_this.$page4Overlay.hide();
            		_this.$page4Share_Show.hide();
            	}, 2000);
//            	location.href = _this.$base_ctx.html()+"/api/camp1504/foolday/login.do";
            });
            
            this.$page4Share_Step.bind("click",function(){
//            	alert("$page4Share_Step");
            	
            	setTimeout(function(){
            		_this.$page4Share_Step.hide();
                	_this.$page4Overlay.hide();
                	_this.$page4Coupon.hide();
                	_this.pickDoubleCoupon();
                	_this.$page4DoubleCoupon.show();
            	}, 2000);
            	
        	});
            
            this.$page4Share_Step2.bind("click",function(){
//            	alert("$page4Share_Step");
            	setTimeout(function(){
            		_this.$page4Share_Step2.hide();
            		_this.$page4Overlay.hide();
            	}, 2000);
            	
            });
            
           /* this.$page4Masking.bind("click",function(){
	        	_this.$page4Overlay.hide();
	        	_this.$page4Masking.hide();
	        });
            */
            
        	this.$touch4Catoon1_Yes.bind("click",function(){
//            	alert("$touch4Catoon1_Yes");
            	
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第1个问题", "wt.msg","正确");
            	} 
        		
        		_this.showYesPage();
        	});
        	
        	this.$touch4Catoon2_Yes.bind("click",function(){
//            	alert("$touch4Catoon2_Yes");
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第2个问题", "wt.msg","正确");
            	} 
            	_this.showYesPage();
        	});
        	
        	this.$touch4Catoon3_Yes.bind("click",function(){
//            	alert("$touch4Catoon3_Yes");
        		
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第3个问题", "wt.msg","正确");
            	} 
        		
            	_this.showYesPage();
        	});
        	
        	this.$touch4Catoon1_No.bind("click",function(){
//        		alert("$touch4Catoon1_No");
        		
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第1个问题", "wt.msg","错误");
            	} 
        		
        		_this.showNoPage();
        	});
        	
        	this.$touch4Catoon2_No.bind("click",function(){
//        		alert("$touch4Catoon2_No");
        		
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第2个问题", "wt.msg","错误");
            	} 
        		
        		_this.showNoPage();
        	});
        	
        	this.$touch4Catoon3_No.bind("click",function(){
//        		alert("$touch4Catoon3_No");
        		
        		if (window._tag) { 
            		_tag.dcsMultiTrack("wt.event", "第3个问题", "wt.msg","错误");
            	} 
        		
        		_this.showNoPage();
        	});
        	
            
            $.each(_this.$touch4Help,function(i,item){
            	$(item).bind("click",function(){
            		_this.$page4Overlay.show();
            		_this.$page4Masking.show();
                	_this.$page4PopUp.show();
            	});
            });
            
            
            $.each(_this.$touch4Home,function(i,item){
            	$(item).bind("click",function(){
            		location.replace(_this.$base_ctx.html()+"/api/camp1504/foolday/login.do");
            	});
            });
            
            $.each(_this.$touch4ShowBig,function(i,item){
            	$(item).bind("click",function(){
            		_this.$page4BigShow.show();
            		_this.$txt4ShowImg.attr("src",$(item).attr("src"));
            	});
            });
            
            
            this.$touch4Draw.bind("click",function(){
//            	alert("$touch4Draw");
            	_this.draw();
            });
            
            this.$touch4Share_NoWin.bind("click",function(){
            	_this.shareRecord();
            	_this.$page4Overlay.show();
            	_this.$page4Share_Step2.show();
            	
        	});
            
            
            this.$touch4Charge.bind("click",function(){
            	_this.winPhone();
            });
            this.$touch4Charge2.bind("click",function(){
            	_this.updateWinPhone();
            });
            this.$touch4Charge3.bind("click",function(){
            	location.replace(_this.$base_ctx.html()+"/api/camp1504/foolday/login.do");
            });
            
            $.each(_this.$touch4Share_More,function(i,item){
            	$(item).bind("click",function(){
            		
            		_this.shareRecord();
            		
             		_this.$page4Overlay.show();
                 	_this.$page4Share_Step.show();
             	});
            });
            $.each(_this.$touch4Again,function(i,item){
            	$(item).bind("click",function(){
            		
            		var radom =  Math.random()*2;
            		if(sessionStorage.getItem("touched_flag") == "true"){
            			$(".click_share.animated.bounceIn img").attr("class","animated tada");
            			sessionStorage.removeItem("touched_flag");
            			$(item).attr("style","");
            			location.replace(_this.$base_ctx.html()+"/api/camp1504/foolday/login.do");
            		}else{
            			$(".click_share.animated.bounceIn img").attr("class","animated shake");
            			sessionStorage.setItem("touched_flag", "true");
            			if(radom>1){
            				$(item).css("margin-left","150px");
            			}else{
            				$(item).css("margin-right","150px");
            			}
            		}
            	});
            });
            
            $.each(_this.$touch4AgainDouble,function(i,item){
            	$(item).bind("click",function(){
            		location.replace(_this.$base_ctx.html()+"/api/camp1504/foolday/login.do");
            	});
            });
            
            
            _this.getChannelType();
            _this.getDeviceId();
            _this.getDeviceType();
            _this.getOpenid();
            _this.getToken();
            
            var shareInfo = amGloble.getQueryParameter("shareInfo");
            sessionStorage.setItem("shareInfo", shareInfo);
            
            var openid = localStorage.getItem('openid');
    		var token = sessionStorage.getItem('token');
    		if ( openid == "null" || token == "null" || openid == null || token == null ) {
    			location.href = $('#base_ctx').html()+"/api/camp1504/foolday/login.do";
    		}else{
    			/*if(_this.getChannelType()=="1"){
    				_this.isWinNotPhone();
    			}else{
    			}*/
    			_this.saveOpenRecord();
    		}
//            _this.showCartoon();
//            _this.bindWChatAPI();
        },  
        
        
        showYesPage : function(){
        	var _this = this;
        	var opt = {
        			sid: sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			channelType: _this.getChannelType(),
        			questionId: jQuery.parseJSON(sessionStorage.getItem("qInfo")).questionId,
        			choice : "1",
        			isCorrect : "1",
        			recycleCount : jQuery.parseJSON(sessionStorage.getItem("qInfo")).recycleCount
        	};
//        	alert("[-a03- input]" + JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
//        		alert("[-a03- return]" + JSON.stringify(ret));
        		console.info(JSON.stringify(ret));
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
//        			alert(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code);
        			
        			if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "001"){
        				_this.$page4Catoon1.hide();
        				_this.$page4All.show();
        				_this.$page4FuguYes.show();
        			}else if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "002"){
        				_this.$page4Catoon2.hide();
        				_this.$page4All.show();
        				_this.$page4KaoyaYes.show();
        			}else if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "003"){
        				_this.$page4Catoon3.hide();
        				_this.$page4All.show();
        				_this.$page4NewMenuYes.show();
        			}
        			
        			sessionStorage.setItem("tag", ret.content.data.tag);
        			sessionStorage.setItem("answered_flag", true);
        			
        			/*if(_this.getChannelType() =='1'){
                		_this.bindWChatAPI();
                	}*/
        		}
        	}, "application/json; charset=utf-8");
        },
        showNoPage : function(){
        	var _this = this;
        	var opt = {
        			sid: sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			channelType: _this.getChannelType(),
        			questionId: jQuery.parseJSON(sessionStorage.getItem("qInfo")).questionId,
        			choice : "0",
        			isCorrect : "0",
        			recycleCount : jQuery.parseJSON(sessionStorage.getItem("qInfo")).recycleCount
        	};
//        	alert("[-a03- input]" + JSON.stringify(opt));
        	amGloble.api.a03.post(opt, function (ret) {
//        		alert("[-a03- return]" + JSON.stringify(ret));
        		console.info(JSON.stringify(ret));
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
//        			alert(jQuery.parseJSON(sessionStorage.getItem("qInfo")).questionId);
        			if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "001"){
        				_this.$page4Catoon1.hide();
        				_this.$page4All.show();
        				_this.$page4FuguNo.show();
        			}else if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "002"){
        				_this.$page4Catoon2.hide();
        				_this.$page4All.show();
        				_this.$page4KaoyaNo.show();
        			}else if(jQuery.parseJSON(sessionStorage.getItem("qInfo")).code == "003"){
        				_this.$page4Catoon3.hide();
        				_this.$page4All.show();
        				_this.$page4NewMenuNo.show();
        			}
        			
        			sessionStorage.setItem("tag", ret.content.data.tag);
        			sessionStorage.setItem("answered_flag", true);
        			/*if(_this.getChannelType() =='1'){
                		_this.bindWChatAPI();
                	}*/
        		}
        	}, "application/json; charset=utf-8");
        },
        
        
        //判断用户是否中奖并且填写手机信息
        isWinNotPhone : function(){
        	var _this = this;
        	var opt = {
        			userId: _this.getOpenid(),
        			channelType: _this.getChannelType()
        	};
        	
        	var flag = false;
        	
        	$.ajaxSetup({ async : false });

//        	alert("[-a07- input]" + JSON.stringify(opt));
        	amGloble.api.a07.get(opt, function (ret) {
//        		alert("[-a07- return]" + JSON.stringify(ret));
        		console.info(JSON.stringify(ret));
        		
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
        			if(ret.content.data.winAward == "true" || ret.content.data.winAward == true){
        				if(ret.content.data.winPhone == "" || ret.content.data.winPhone == "null" || ret.content.data.winPhone == null){
//        					alert("===$page4Bill2===");
        					_this.$page4All.show();
        					_this.$page4Bill2.show();
            			}else{
//            				alert("===$page4Bill3===");
            				_this.$txt4ShowPhone.html("恭喜! 您的中奖手机号码:   " + ret.content.data.winPhone);
            				_this.$page4All.show();
            				_this.$page4Bill3.show();
            			}
        			}else{
        				flag = true;
        			}
        		}
        	}, "application/json; charset=utf-8");
        	
        	$.ajaxSetup({ async : true });
        	
        	return flag;
        },
        
        //保存打开记录
        saveOpenRecord: function() {
        	var _this = this;
        	if (window._tag) { 
        		_tag.dcsMultiTrack("wt.event", "用户打开记录", "wt.msg", _this.getOpenid());
        	} 
        	
        	var opt = {
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId(),
        			tag: sessionStorage.getItem("tag")
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
        			_this.pickCartoon();
        		}
        	}, "application/json; charset=utf-8");
        },
        
        
        showFirstCatoon : function(result){
        	$(".catoon1").show();
			$(".catoon2").hide();
			$(".catoon3").hide();
			$(".catoon2").attr("class","catoon2");
			$(".catoon3").attr("class","catoon3");
			$(".catoon1").attr("class","wrapper catoon1");
			
//			$("#pages1  p:eq(0)").html( result.yesPercent + "<sup>%</sup>");
//			$("#pages1  p:eq(1)").html( result.noPercent + "<sup>%</sup>");
//			$("#pages1  p:eq(2)").html( result.naPercent + "<sup>%</sup>");
			var a=new pageSwitch('pages1',{
				duration:600,
				start:0,
				direction:1,
				loop:false,
				ease:'ease',
				transition:'slideY',
				mousewheel:true,
				arrowkey:true
			});
			a.on("after", function(n) {
				6 == n ? $(".arrowB").hide() : $(".arrowB").show();	
			});
        },
        
        showSecondCatoon : function(result){
        	$(".catoon2").show();
			$(".catoon1").hide();
			$(".catoon3").hide();
			$(".catoon1").attr("class","catoon1");
			$(".catoon3").attr("class","catoon3");
			$(".catoon2").attr("class","wrapper catoon2");
			
//			$("#pages2  p:eq(0)").html( result.yesPercent + "<sup>%</sup>");
//			$("#pages2  p:eq(1)").html( result.noPercent + "<sup>%</sup>");
//			$("#pages2  p:eq(2)").html( result.naPercent + "<sup>%</sup>");
			
			var a=new pageSwitch('pages2',{
				duration:600,
				start:0,
				direction:1,
				loop:false,
				ease:'ease',
				transition:'slideY',
				mousewheel:true,
				arrowkey:true
			});
			
			a.on("after", function(n) {
				6 == n ? $(".arrowB").hide() : $(".arrowB").show();	
			});
        },
        showThirdCatoon : function(result){
        	$(".catoon3").show();
			$(".catoon2").hide();
			$(".catoon1").hide();
			$(".catoon2").attr("class","catoon2");
			$(".catoon1").attr("class","catoon1");
			$(".catoon3").attr("class","wrapper catoon3");
			
//			$("#pages3  p:eq(0)").html( result.yesPercent + "<sup>%</sup>");
//			$("#pages3  p:eq(1)").html( result.noPercent + "<sup>%</sup>");
//			$("#pages3  p:eq(2)").html( result.naPercent + "<sup>%</sup>");
			
			var a=new pageSwitch('pages3',{
				duration:600,
				start:0,
				direction:1,
				loop:false,
				ease:'ease',
				transition:'slideY',
				mousewheel:true,
				arrowkey:true
			});
			a.on("after", function(n) {
				5 == n ? $(".arrowB").hide() : $(".arrowB").show();	
			});
        },
        
        //选择漫画
        pickCartoon : function(){
        	var _this = this;
        	var recycleCount_ = "";
        	if(sessionStorage.getItem("qInfo")!=null){
        		recycleCount_ = jQuery.parseJSON(sessionStorage.getItem("qInfo")).recycleCount;
        	}
        	var opt = {
        			sid: sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			tag: sessionStorage.getItem("tag"),
        			recycleCount: recycleCount_ == "" ? 1 : recycleCount_
        	};
//        	alert("[-a02- input]"+JSON.stringify(opt));
        	amGloble.api.a02.post(opt, function (ret) {
//        		alert("[-a02- return]" + JSON.stringify(ret));
        		/*{
        		    "result": 0,
        		    "content": {
        		        "retCode": 0,
        		        "retMsg": null,
        		        "data": {
        		            "naPercent": 0,
        		            "correct": 1,
        		            "questionId": "21DF76D25195F4414AE8F4537F885A37338C54CFF41",
        		            "yesPercent": 33,
        		            "code": "002",
        		            "noPercent": 67,
        		            "recycleCount": 2
        		        }
        		    }
        		}*/
        		console.info(JSON.stringify(ret));
        		
        		if (ret.content.retCode != 0 || ret.content.data == null) {
//        			alert(JSON.stringify(ret));
        		} else {
        			//Todo:  根据抽中的问题显示后面的流程
        			sessionStorage.setItem("qInfo", JSON.stringify(ret.content.data));
        			
        			
        			var shareInfo = sessionStorage.getItem("shareInfo");
                	if(shareInfo==null || shareInfo == '' || shareInfo == "null"){
                		//如果 shareInfo为空
                		var tag = sessionStorage.getItem("tag") == null ? "" : sessionStorage.getItem("tag");
                		location.href = amGloble.config.selfPage + "/index.jsp?shareInfo=" +
    	            	ret.content.data.code + 
    	                "__" + localStorage.getItem('openid') +
    	                "__" + tag;
                		
                		return;
                	}else{
                		var shareInfoArr = shareInfo.split("__");
                		var qId = shareInfoArr[0];
                		var soid = shareInfoArr[1];
                		var tag = shareInfoArr[2];
                		if(soid != _this.getOpenid()){
                			sessionStorage.setItem("foid",soid);
                			sessionStorage.setItem("tag",tag);
                			sessionStorage.setItem("qId",qId);
                		}
                	}
        			
                	
                	
                	if(_this.isWinNotPhone()){
//            			alert("===qInfo==" + JSON.stringify(ret.content.data));
//            			alert("===qId==" + sessionStorage.getItem("qId"));
                    	
                    	if(sessionStorage.getItem("foid")==null){
//            				alert("foid===null"); 
                    		if(ret.content.data.code == "001"){
                    			_this.showFirstCatoon(ret.content.data);
                    		}else if (ret.content.data.code == "002"){
                    			_this.showSecondCatoon(ret.content.data);
                    		}else if (ret.content.data.code == "003"){
                    			_this.showThirdCatoon(ret.content.data);
                    		}
                    	}else{
//            				alert("foid!==null");
                    		if(sessionStorage.getItem("qId") == "001"){
                    			_this.showFirstCatoon(ret.content.data);
                    		}else if (sessionStorage.getItem("qId") == "002"){
                    			_this.showSecondCatoon(ret.content.data);
                    		}else if (sessionStorage.getItem("qId") == "003"){
                    			_this.showThirdCatoon(ret.content.data);
                    		}
                    	}
                    	sessionStorage.removeItem("tag");
                    	sessionStorage.removeItem("foid");
                    	sessionStorage.removeItem("qId");

                	}
                	
        		}
        	}, "application/json; charset=utf-8");
        },
        
        draw : function(){
        	var _this = this;
        	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId()
        	};
//        	alert("[-a05- input]"+JSON.stringify(opt));
        	amGloble.api.a05.post(opt, function (ret) {
//        		alert("[-a05- return]" + JSON.stringify(ret));
        		console.info(JSON.stringify(ret));
        		
        		if (ret.content.retCode != 0 || ret.content.data == null) {
        			
//        			alert(JSON.stringify(ret));
        			_this.$page4FuguYes.hide();
					_this.$page4KaoyaYes.hide();
					_this.$page4NewMenuYes.hide();
					
					_this.pickOneCoupon();
					_this.$page4Coupon.show();
        		} else {
        			
        			_this.$page4FuguYes.hide();
					_this.$page4KaoyaYes.hide();
					_this.$page4NewMenuYes.hide();
					
        			if(ret.content.data.gift == "1"){
        				if (window._tag) { 
        					_tag.dcsMultiTrack("wt.event", "用户中奖记录", "wt.msg","中奖50元");
        				} 
        				_this.$page4Bill.show();
        			}else{
        				if (window._tag) { 
        					_tag.dcsMultiTrack("wt.event", "用户中奖记录", "wt.msg","没有中奖50元");
        				} 
        				_this.pickOneCoupon();
        				_this.$page4Coupon.show();
        			}
        		}
        	}, "application/json; charset=utf-8");
        },
        
        shareRecord : function(){
        	var _this = this;
        	
        	if (window._tag) { 
        		_tag.dcsMultiTrack("wt.event", "用户分享问题", "wt.msg","用户开始分享");
        	} 
        	
      	  	var opt = {
        			sid : sessionStorage.getItem("sid"),
        			userId: _this.getOpenid(),
        			token: _this.getToken(),
        			channelType: _this.getChannelType(),
        			deviceType: _this.getDeviceType(),
        			deviceId: _this.getDeviceId(),
        			mediaType: "WX",
        			shareUrl : "",
        			shareResult : "1"
        	};
//        	alert("[-a04- input]"+JSON.stringify(opt));
        	amGloble.api.a04.post(opt, function (ret) {
//        		alert("[-a04- return]" + JSON.stringify(ret));
        		
//        		__this.$page4Overlay.hide();
        	}, "application/json; charset=utf-8");
        },
        
        
        winPhone : function(){
        	var _this = this;
        	var telNo = $.trim(_this.$txt4TelNo.val());
        	
        	
        	if (this.checkTelNo()) {
        		var opt = {
        				sid : sessionStorage.getItem("sid"),
        				userId: _this.getOpenid(),
        				token: _this.getToken(),
        				channelType: _this.getChannelType(),
        				phone: telNo
        		};
//	        	alert("[-a06- input]"+JSON.stringify(opt));
        		amGloble.api.a06.post(opt, function (ret) {
        			
        			if (window._tag) { 
        				_tag.dcsMultiTrack("wt.event", "手机中奖用户和号码", "wt.msg",telNo);
        			} 
        			
//	        		alert("[-a06- return]" + JSON.stringify(ret));
        			console.info(JSON.stringify(ret));
        			
        			_this.$page4Overlay.show();
        			_this.$page4Pop_Up.show();
        			
        		}, "application/json; charset=utf-8");
        		
        	} else {
        		this.$txt4TelNo.val("").blur();
        	}
        },
        
        updateWinPhone : function(){
        	var _this = this;
        	var telNo = $.trim(_this.$txt4TelNo2.val());
        	
        	
        	if (this.checkTelNo2()) {
				var opt = {
	        			userId: _this.getOpenid(),
	        			channelType: _this.getChannelType(),
	        			phone: telNo
	        	};
//	        	alert("[-a08- input]"+JSON.stringify(opt));
	        	amGloble.api.a08.post(opt, function (ret) {
//	        		alert("[-a08- return]" + JSON.stringify(ret));
	        		if (window._tag) { 
	            		_tag.dcsMultiTrack("wt.event", "中奖用户重新填写手机号码", "wt.msg",telNo);
	            	} 
	        		console.info(JSON.stringify(ret));
	        		_this.$touch4Charge2.hide();
	        		_this.$txt4TelTips.show();
	        		setTimeout(function(){
	        			location.replace(_this.$base_ctx.html()+"/api/camp1504/foolday/login.do");
	        		}, 3000);
	        		
	        	}, "application/json; charset=utf-8");
				
			} else {
				this.$txt4TelNo2.val("").blur();
			}
        },
        
        
        pickOneCoupon : function(){
        	var _this = this;
        	var couponArr = ["/images/camp1504/f1.jpg","/images/camp1504/f2.jpg","/images/camp1504/f3.jpg","/images/camp1504/f4.jpg"];
        	var index = Math.floor(Math.random() * couponArr.length);
        	
        	$.each(_this.$img4FirstCopon,function(i,item){
        		$(item).attr("src",_this.$base_ctx.html()+couponArr[index]);
        	});
        },
        
        pickDoubleCoupon : function(){
        	var _this = this;
        	var couponArr = ["/images/camp1504/f5.jpg","/images/camp1504/f6.jpg","/images/camp1504/f7.jpg","/images/camp1504/f8.jpg"];
        	var index = Math.floor(Math.random() * couponArr.length);
        	
        	$.each(_this.$img4DoubleCopon,function(i,item){
        		$(item).attr("src",_this.$base_ctx.html()+couponArr[index]);
        	});
        },
        
        checkTelNo2: function() {
        	var _this = this;
        	var telNo = $.trim(_this.$txt4TelNo2.val());
        	var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
        	
        	var resulst = telNo.match(telRegex1) || telNo.match(telRegex2) || telNo.match(telRegex3);
        	
        	if(!resulst){
//        		_this.$txt4TelNo2.val("");
        		_this.$txt4TelNo2.val("").css({"background":"url("+amGloble.config.selfRoot+"images/camp1504/placeholder.png) no-repeat center center",
					"background-size": "117px 15px"});
        		//$(".placeholder").show();
        	}
        	
        	return resulst;
        },
        
        checkTelNo: function() {
        	var _this = this;
			var telNo = $.trim(_this.$txt4TelNo.val());
			var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
			
			var resulst = telNo.match(telRegex1) || telNo.match(telRegex2) || telNo.match(telRegex3);
			
			if(!resulst){
				_this.$txt4TelNo.val("");
				_this.$txt4TelNo.val("").css({"background":"url("+amGloble.config.selfRoot+"images/camp1504/placeholder.png) no-repeat center center",
								"background-size": "117px 15px"});
				//$(".placeholder").show();
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
        },
        
        makeShareLink: function() {
        	
            return amGloble.config.selfBase + "/share.do?shareInfo=" +
            	jQuery.parseJSON(sessionStorage.getItem("qInfo")).code + 
                "__" + localStorage.getItem('openid') +
                "__" + sessionStorage.getItem("tag");
        },

        makeShareTitle: function() {
//        	alert("万能的朋友圈：肯德基“鸡”密大放送，不看后悔");
            return "万能的朋友圈：肯德基“鸡”密大放送，不看后悔";
        },

        makeShareIcon: function() {
//        	alert(amGloble.config.selfRoot + "images/camp1504/linkPic.jpg");
            return amGloble.config.selfRoot + "images/camp1504/linkPic.jpg";
        },

        makeShareDesc: function(type) {
            var desc = "KFC的愚人节玩笑，你们感受一下";
            if (type != 0) {
                desc = "万能的朋友圈：肯德基“鸡”密大放送，不看后悔";
            }
            
//            alert(desc);
            
            return desc;
        },
        
        bindWChatAPI: function() {

//          alert("[-wx-]\n" + wx);
          var _this = this,
              timestamp = new Date().getTime(),
              nonceStr = ('r'+Math.random()).replace(".", ""),
              opt = {
                  timestamp: timestamp,
                  nonceStr: nonceStr,
                  url: location.href.split('#')[0]
              };

//          alert("[-query signatuer input-]\n" + JSON.stringify(opt));

          amGloble.api.querySignature.get(opt, function(ret){
//              alert("[-query signatuer return-]\n" + JSON.stringify(ret));

              if (ret.result != 0 || ret.content.data == null) {
//                  alert("[-err-]"+JSON.stringify(ret));

              } else {

                  var wxConfig = {
                      debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来
                      appId: amGloble.config.appId, // 必填，公众号的唯一标识
                      timestamp: timestamp, // 必填，生成签名的时间戳
                      nonceStr: nonceStr, // 必填，生成签名的随机串
                      signature: ret.content.data.signature,// 必填，签名，见附录1
                      jsApiList: ["onMenuShareTimeline","onMenuShareAppMessage","hideMenuItems"] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                  };

//                  alert("[-wxconfig-]\n" + JSON.stringify(wxConfig));

                  wx.config(wxConfig);

                  wx.ready(function(){
//                      alert("wx config success");

                      wx.onMenuShareTimeline({
                          title: _this.makeShareTitle(), // 分享标题
                          link: _this.makeShareLink(), // 分享链接
                          imgUrl: _this.makeShareIcon(), // 分享图标
                          success: function () {
                        	  var __this = _this;
                        	  var opt = {
              	        			sid : sessionStorage.getItem("sid"),
              	        			userId: __this.getOpenid(),
              	        			token: __this.getToken(),
              	        			channelType: __this.getChannelType(),
              	        			deviceType: __this.getDeviceType(),
              	        			deviceId: __this.getDeviceId(),
              	        			mediaType: "WX",
              	        			shareUrl : "",
              	        			shareResult : "1"
              	        	};
//              	        	alert("[-a04- input]"+JSON.stringify(opt));
              	        	amGloble.api.a04.post(opt, function (ret) {
//              	        		alert("[-a04- return]" + JSON.stringify(ret));
              	        		
              	        	}, "application/json; charset=utf-8");
                        	  
//                              alert("share0 success");
                          },
                          cancel: function () {
//                              alert("cancel0 success");
                          }
                      });
                      wx.onMenuShareAppMessage({
                          title: _this.makeShareTitle(), // 分享标题
                          desc: _this.makeShareDesc(0), // 分享描述
                          link: _this.makeShareLink(), // 分享链接
                          imgUrl: _this.makeShareIcon(), // 分享图标
                          success: function () {
                        	  var __this = _this;
                        	  var opt = {
              	        			sid : sessionStorage.getItem("sid"),
              	        			userId: __this.getOpenid(),
              	        			token: __this.getToken(),
              	        			channelType: __this.getChannelType(),
              	        			deviceType: __this.getDeviceType(),
              	        			deviceId: __this.getDeviceId(),
              	        			mediaType: "WX",
              	        			shareUrl : "",
              	        			shareResult : "1"
              	        	};
//              	        	alert("[-a04- input]"+JSON.stringify(opt));
              	        	amGloble.api.a04.post(opt, function (ret) {
//              	        		alert("[-a04- return]" + JSON.stringify(ret));
              	        		__this.$page4Overlay.hide();
              	        	}, "application/json; charset=utf-8");
                        	  
//                              alert("share1 success");
                          },
                          cancel: function () {
//                              alert("cancel1 success");
                          }
                      });
                  });
                  
                  wx.hideMenuItems({
                	    menuList: ["menuItem:share:qq"]
            	  });
                  
                  wx.error(function(res){
//                      alert("wx config error:\n"+JSON.stringify(res));
                  });

              }
          });
      }
    };
    
    window.onbeforeunload = function(event) {
		$(".catoon1").hide();
		$(".catoon2").hide();
		$(".catoon3").hide();
		
    	sessionStorage.removeItem("answered_flag");
		sessionStorage.removeItem("tag");
		sessionStorage.removeItem("qId");
		sessionStorage.removeItem("touched_flag");
    };
    controller.init();

});