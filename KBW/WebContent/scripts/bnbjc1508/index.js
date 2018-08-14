


$(function(){
    var controller = {

        init: function() {
        	if (amGloble.config.debug == true) alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
            this.$page10=$(".page10");
            this.$page12=$(".page12");
            this.$page20=$(".page20");
            this.$page21=$(".page21");
            this.$page221=$(".page221");
            this.$page222=$(".page222");
            this.$page22=$(".page22");
            this.$page120=$(".page120");
            this.$page30=$(".page30");
            this.$page40=$(".page40");
            this.$popup_rule=$(".popup_rule");
            this.$overlay=$(".overlay");

            this.$tasteticket=$(".tasteticket");
            this.$btn_product=$(".topbt.product");
            this.$btn_rule=$(".topbt.rule");
            this.$btn_ticket=$(".topbt.light");
            this.$btn_ticket_gray=$(".topbt.gray");
            this.$close_icon=$(".popup_rule .close_icon");
            this.$bt_return=$(".page12 .bt_return");
            this.$closefont=$(".page221 .closefont");
            this.$bt_yt=$(".page10 .bt_yt");
            this.$teacup=$(".page20 .teacup");
            this.$bt_yt21=$(".page21 .bt_yt");
            this.$bt_yt30=$(".page30 .bt_yt");
            this.$bt_yt40=$(".page40 .bt_yt");
            this.$top_banner=$(".topbt");
            this.$btn_collect=$(".page221 .bt_yt");
            this.$touch4home=$(".logo");
            
            //节点绑定
            this.$btn_rule.bind("touchstart click",function(){
               // _this.$page10.hide();
            	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户查看活动规则", "wt.msg", _this.getOpenid());
	        	}
            	_this.$tasteticket.hide();
                _this.$popup_rule.show();
                new IScroll("#wrapper");
            });

            this.$close_icon.bind("touchstart click",function(){
                _this.$popup_rule.hide();
               // _this.$page10.show();
            });
            this.$touch4home.bind("touchstart click",function(){
            	_this.$page12.hide();
            	_this.$page20.hide();
            	_this.$page21.hide();
            	_this.$page222.hide();
            	_this.$page22.hide();
            	_this.$page30.hide();
            	_this.$page40.hide();
//            	_this.$popup_rule.hide();
            	_this.$top_banner.show();
            	_this.initButton();
            	_this.$page10.show();
            });

            this.$btn_product.bind("touchstart click",function(){
            	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户查看产品信息", "wt.msg", _this.getOpenid());
	        	}
            	_this.$tasteticket.hide();
            	var displayofpage10=_this.$page10.css("display");
            	var displayofpage20=_this.$page20.css("display");
            	var displayofpage21=_this.$page21.css("display");
            	var displayofpage30=_this.$page30.css("display");
            	var displayofpage40=_this.$page40.css("display");
            	if(displayofpage10=="block"){
            		sessionStorage.setItem('pagefrom',"page10");
            		_this.$page10.hide();
            	}else if(displayofpage20=="block"){
            		sessionStorage.setItem('pagefrom',"page20");
            		_this.$page20.hide();
            	}else if(displayofpage21=="block"){
            		sessionStorage.setItem('pagefrom',"page21");
            		_this.$page21.hide();
            	}else if(displayofpage30=="block"){
            		sessionStorage.setItem('pagefrom',"page30");
            		_this.$page30.hide();
            	}else if(displayofpage40=="block"){
            		sessionStorage.setItem('pagefrom',"page40");
            		_this.$page40.hide();
            	}     		
            	
                _this.$top_banner.hide();
                
                _this.$page12.show();
            });

//            this.$bt_return.bind("touchstart click",function(){
            this.$page12.bind("touchstart click",function(){
                _this.$page12.hide();
                _this.$top_banner.show();
                _this.initButton();
                var pageto=sessionStorage.getItem('pagefrom');
                $("."+pageto).show();
                
            });

            this.$btn_ticket.bind("touchstart click",function(){
            	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户查看我的尝鲜券", "wt.msg", _this.getOpenid());
	        	}
            	_this.collect()
            	/*_this.$tasteticket.hide();
            	var displayofpage10=_this.$page10.css("display");
            	var displayofpage20=_this.$page20.css("display");
            	var displayofpage21=_this.$page21.css("display");
            	var displayofpage30=_this.$page30.css("display");
            	var displayofpage40=_this.$page40.css("display");
            	if(displayofpage10=="block"){
            		sessionStorage.setItem('pagefrom2',"page10");
            		_this.$page10.hide();
            	}else if(displayofpage20=="block"){
            		sessionStorage.setItem('pagefrom2',"page20");
            		_this.$page20.hide();
            	}else if(displayofpage21=="block"){
            		sessionStorage.setItem('pagefrom2',"page21");
            		_this.$page21.hide();
            	}else if(displayofpage30=="block"){
            		sessionStorage.setItem('pagefrom2',"page30");
            		_this.$page30.hide();
            	}else if(displayofpage40=="block"){
            		sessionStorage.setItem('pagefrom2',"page40");
            		_this.$page40.hide();
            	}
                _this.$top_banner.hide();
                _this.$page221.show();*/
            });

            this.$closefont.bind("touchstart click",function(){
                _this.$page221.hide();
                _this.$top_banner.show();
                _this.initButton();
                var pageto=sessionStorage.getItem('pagefrom2');
                $("."+pageto).show();
            });

            this.$bt_yt.bind("touchstart click",function(){
            	_this.$tasteticket.hide();
                _this.$page10.hide();
                _this.$page20.show();
                sessionStorage.setItem("shake_flag","true");
            });
            this.$bt_yt30.bind("touchstart click",function(){
            	_this.$page30.hide();
            	_this.$page20.show();
            	_this.$tasteticket.hide();
            	sessionStorage.setItem("shake_flag","true");
            });
            this.$bt_yt40.bind("touchstart click",function(){
            	_this.$page40.hide();
            	_this.$page20.show();
            	_this.$tasteticket.hide();
            	sessionStorage.setItem("shake_flag","true");
            });

            this.$teacup.bind("touchstart click",function(){
                //_this.$page20.hide();
                document.getElementById('myaudio').play();
                setTimeout(function(){
                	$('.page20').hide();
                	$('.page21').show();
                },2000);
            });
            //audio播放结束两秒时显示杯满
          /*  document.getElementById('myaudio').onended = function() {
            	$('.page20').hide();
            	$('.page21').show();
            };*/

            this.$bt_yt21.bind("touchstart click",function(){
                _this.$page22.show();
            });

            this.$page22.bind("touchstart click",function(){
                setTimeout(function(){
//                    _this.$page22.hide();
                    _this.share();
                },3000);
            });

            this.$btn_collect.bind("touchstart click",function(){
            	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户保存卡券", "wt.msg", _this.getOpenid());
	        	}
               _this.collect();
            });

            
//            $(".topline").show();
//            _this.$page10.show();
            
           _this.loadImg();
           _this.shakeEvent();
           _this.initButton();
           _this.bindWChatAPI();
        },

        
        initButton : function(){
        	var _this = this;
        	var promoCode = sessionStorage.getItem("promoCode");
        	if(promoCode == "" || promoCode==null || promoCode=="null"){
        		_this.$btn_ticket.hide();
        		_this.$btn_ticket_gray.show();
        	}else{
        		_this.$btn_ticket.show();
        		_this.$btn_ticket_gray.hide();
        	};
        },

        share: function() {
            var _this = this;
            var opt = {
                    sid : sessionStorage.getItem("sid"),
                    userId: _this.getOpenid(),
                    channelType: _this.getChannelType(),
                    deviceType: _this.getDeviceType(),
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
    	        		_tag.dcsMultiTrack("wt.event", "用户分享成功", "wt.msg", _this.getOpenid());
    	        	}
                   //分享成功
                	_this.$page22.hide();
//                  sessionStorage.setItem('promoCode',ret.content.data.promoCode);
//                  sessionStorage.setItem('pagefrom',"page10");
//        			_this.$top_banner.hide();
//                  _this.$page21.hide();               
//                  _this.$page221.show();
                }
            }, "application/json; charset=utf-8");

        },

        
        shakeEvent : function(){
        	 var _this = this;
        	 var myShakeEvent = new Shake({
        	        threshold: 15
        	    });
        	    // start listening to device motion
        	    myShakeEvent.start();
        	    // register a shake event
        	    window.addEventListener('shake', shakeEventDidOccur, false);
        	    //shake event callback
        	    function shakeEventDidOccur () {
        	        //put your own code here etc.
        	    	if (amGloble.config.debug == true) alert('Shake!');
        	        var shake_flag_ = sessionStorage.getItem("shake_flag");
        	        if(shake_flag_=="true"){
//        	        	_this.$page20.hide();
//        	        	_this.$page30.hide();
//        	        	_this.$page40.hide();
        	        	document.getElementById('myaudio').play();
        	        	setTimeout(function(){
                        	$('.page20').hide();
                        	$('.page30').hide();
                        	$('.page40').hide();
                        	$('.page21').show();
                        },2000);
        	        	
        	        	sessionStorage.removeItem("shake_flag");
        	        }
        	    }
        },
        
        
        collect : function(){
        	var optpromoCode;
        	if(sessionStorage.getItem('promocode2')){
        		optpromoCode=sessionStorage.getItem('promocode2');
        	}else{
        		optpromoCode=sessionStorage.getItem('promoCode');
        	}
        	//alert("[sessionStoragepromoCode2]" + sessionStorage.getItem('promocode2'));
        	//alert("[sessionStoragepromoCode]" + sessionStorage.getItem('promoCode'));
        	
            var opt = {
                    promoCode : optpromoCode
                };
            if (amGloble.config.debug == true) alert("[-a03- input]" + JSON.stringify(opt));
            amGloble.api.a03.post(opt, function (ret) {
                if (amGloble.config.debug == true) alert("[-a03- return]" + JSON.stringify(ret));
                if (ret.content.errCode != 0 || ret.content.data == null) {
                    if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                } else {
                   location.href = "http://crmminisite.verystar.cn/wxcard/promo?key="+ret.content.data.data+"&wechat_card_js=1";
                }
            }, "application/json; charset=utf-8");
        },
        
        loadImg : function(){
        	var _this = this;
        	var imgArr = [
        	              amGloble.config.selfRef+"images/bnbjc1508/bg_about.jpg",
                          amGloble.config.selfRef+"images/bnbjc1508/bg_rule.jpg",
                      	  amGloble.config.selfRef+"images/bnbjc1508/bg_ruletitle.png",
                          amGloble.config.selfRef+"images/bnbjc1508/bg.jpg",
                          amGloble.config.selfRef+"images/bnbjc1508/bt_greenbg.png",
                          amGloble.config.selfRef+"images/bnbjc1508/bt_product.png",
                          amGloble.config.selfRef+"images/bnbjc1508/bt_rule.png",
                          amGloble.config.selfRef+"images/bnbjc1508/bt_ticket.png",
                          amGloble.config.selfRef+"images/bnbjc1508/icon_arrowdown.png",
                          amGloble.config.selfRef+"images/bnbjc1508/icon_close.png",
                          amGloble.config.selfRef+"images/bnbjc1508/logo.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_emptycup.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_here.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_note.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_notenew.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_share.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_tea.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_tea2.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_upprompt.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_xrmmc.png",
                          amGloble.config.selfRef+"images/bnbjc1508/pic_yt.png"
                          ];
            preloadimg(imgArr,function(){
                $('#loading').hide();
                $(".topline").show();
                
//                var shareCome_ = sessionStorage.getItem('shareCome');
                var other_ = sessionStorage.getItem('other');
                var isOtherDraw_ = sessionStorage.getItem('isOtherDraw');
                var hasPromoCode_ = sessionStorage.getItem('hasPromoCode');
                if (amGloble.config.debug == true) alert("[sessionStorage_hasPromoCode_]" +sessionStorage.getItem('hasPromoCode'));
                if (amGloble.config.debug == true) alert("[localStorage_hasPromoCode_]" + localStorage.getItem('hasPromoCode'));
                
                if(other_ == "true"){
                	//别人点击
                	if(isOtherDraw_=="false"){
                		//已被领取
                		_this.$page40.show();
                	}else{
                		//未被领取
                		_this.$tasteticket.show();
                		_this.$page30.show();
                	}
                }else{
                	//自己点击
                	_this.$page10.show();
                }
                
                if(hasPromoCode_ == "true"){
            		//点击优惠券进来的
                	_this.$tasteticket.hide();
                	var displayofpage10=_this.$page10.css("display");
                	var displayofpage30=_this.$page30.css("display");
                	var displayofpage40=_this.$page40.css("display");
                	if(displayofpage10=="block"){
                		//if (amGloble.config.debug == true) alert("[hasPromoCode]" + hasPromoCode_);
                		sessionStorage.setItem('pagefrom2',"page10");
                		_this.$page10.hide();
                	}else if(displayofpage30=="block"){
                		sessionStorage.setItem('pagefrom2',"page30");
                		_this.$page30.hide();
                	}else if(displayofpage40=="block"){
                		sessionStorage.setItem('pagefrom2',"page40");
                		_this.$page40.hide();
                	}
                    _this.$top_banner.hide();
                    _this.$page221.show();
            		
            	}
                
            });
        },
        
        bindWChatAPI : function(){
     	   var jsApiList = ['showOptionMenu']; 
 		   construct(amGloble.config.appid,  amGloble.config.selfRef + "pages/bnbjc1508/index.html", jsApiList);
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