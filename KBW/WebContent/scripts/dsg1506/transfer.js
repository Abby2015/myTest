$(function(){
	var controller = {
			
			init: function() {
				if (amGloble.config.debug == true) alert("===transfer.js=== \n" + JSON.stringify(location));
				var _this = this;
				
				_this.getOpenid();
				_this.getChannelType();
				_this.getDeviceType();
				_this.getDeviceId();

				_this.toTarget();
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
		    		} else {
		    			sessionStorage.setItem("sid", ret.content.data.sid);
		    			//获取券号  promoCode
		    			sessionStorage.setItem("promoCode", ret.content.data.promoCode);
		    			location.href=amGloble.config.selfRoot + "pages/dsg1506/index.html";
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