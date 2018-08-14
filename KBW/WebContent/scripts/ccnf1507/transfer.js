/**
 * Created by qing.wang on 2015/07/06.
 */

$(function(){
	var controller = {
			
			init: function() {
				if (amGloble.config.debug == true) alert("===transfer.js=== \n" + JSON.stringify(location));
				var _this = this;
				
				/*var utm_source = sessionStorage.getItem('utm_source');
				if(utm_source == null ||  utm_source == "" || utm_source=="null"){
					utm_source = "";
				}*/
				_this.toTarget();
			},
			
			toTarget : function(){
				var _this = this;
	        	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户进入活动首页", "wt.msg", _this.getOpenid());
	        	}
	        	
	        	
	        	
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
	        			sessionStorage.setItem("sid", ret.content.data.sid); 
	        			if (amGloble.config.debug == true) alert(amGloble.config.selfRef + "pages/ccnf1507/index.html");
	        			location.href=amGloble.config.selfRef + "pages/ccnf1507/index.html";
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