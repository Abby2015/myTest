$(function(){
	var controller = {
			
			init: function() {
				if (amGloble.config.debug == true) alert("===transfer.js=== \n" + JSON.stringify(location));
				var _this = this;
				/*var utm_source = sessionStorage.getItem('utm_source');
				if(utm_source == null ||  utm_source == "" || utm_source=="null"){
					utm_source = "";
				}*/
				var openid_ = amGloble.getQueryParameter("openid");
				if(openid_=="" || openid_==null || openid_=="null"){
					var _tag =  sessionStorage.getItem("tag");
		        	if(_tag=="" || _tag=="null" || _tag == null){
		        		_tag = "";
		        	}
					location.href=amGloble.config.selfRoot + "pages/wxdgb1508/index.html?tag="+_tag;
				}else{
					_this.toTarget();
				}
			},
			
			toTarget : function(){
				var _this = this;
				//获取tag和normalshare
	        	var tag_ =  sessionStorage.getItem("tag");
	        	if(tag_=="" || tag_=="null" || tag_ == null){
	        		tag_ = "";
	        	}
	        	var normalShare_ =  sessionStorage.getItem("normalShare");
	        	if(normalShare_=="" || normalShare_=="null" || normalShare_ == null){
	        		normalShare_ = "";
	        	}
	        	if (amGloble.config.debug == true) alert("[-a01- tag  normalShare]"+ tag_+"  "+normalShare_);
	        	var opt = {
	        			channelType: _this.getChannelType(),
	        			userId: _this.getOpenid(),
	        			deviceType: _this.getDeviceType(),
	        			deviceId: _this.getDeviceId(),
	        			tag : tag_,
	        			normalShare:normalShare_,
	        			sceneId : sessionStorage.getItem('wxqr_scene_id')
	        	};
	        	
	        	if (amGloble.config.debug == true) alert("[-a01- input]"+JSON.stringify(opt));
	        	amGloble.api.a01.post(opt, function (ret) {
	        		if (amGloble.config.debug == true) alert("[-a01- return]" + JSON.stringify(ret));
	        		if (ret.content.errCode != 0 || ret.content.data == null) {
	        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
	        			if(ret.content.errCode="400025"){
	        				location.href=amGloble.config.selfRef + "pages/wxdgb1508/finish.html";
	        			}else if(ret.content.errCode="400029"){
	        				location.href=amGloble.config.selfRoot + "pages/wxdgb1508/index.html?tag="+tag_;
	        			}
	        		} else {
	        			sessionStorage.setItem("sid", ret.content.data.sid); 
	        			//sessionStorage.setItem("shareCome", ret.content.data.shareCome);
	        			sessionStorage.setItem("other", ret.content.data.other);
	        			sessionStorage.setItem("isDecode", ret.content.data.isDecode);
	        			sessionStorage.setItem("drawCoupon", ret.content.data.drawCoupon);
	        			sessionStorage.setItem("promoCode", ret.content.data.promoCode);
	        			sessionStorage.setItem("isFirst", ret.content.data.isFirst);
	        			sessionStorage.setItem("isSendLove", ret.content.data.isSendLove);
	        			//sessionStorage.setItem("tag", ret.content.data.tag);
	        			
        				if (amGloble.config.debug == true) alert(amGloble.config.selfRef + "pages/wxdgb1508/index.html?tag="+ret.content.data.tag + "&normalShare="+ret.content.data.normalShare);
        				if (window._tag) { 
        	        		_tag.dcsMultiTrack("wt.event", "用户进入活动首页", "wt.msg", _this.getOpenid());
        	        	}
        				location.href=amGloble.config.selfRef + "pages/wxdgb1508/index.html?tag="+ret.content.data.tag+"&normalShare="+ret.content.data.normalShare;
	        		}
	        	}, "application/json; charset=utf-8");
				
			},
			
			getOpenid: function() {
	    		var openid = amGloble.getQueryParameter("openid");
    			localStorage.setItem('openid', openid);
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