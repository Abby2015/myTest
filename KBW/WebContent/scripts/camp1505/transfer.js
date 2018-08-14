$(function(){
	var controller = {
			
			init: function() {
//				alert("===transfer.js=== \n" + JSON.stringify(location));
				var _this = this;
				
				_this.getOpenid();
				_this.getChannelType();
				_this.getDeviceType();
				_this.getToken();
				_this.getDeviceId();
				
				var utm_source = sessionStorage.getItem('utm_source');
				if(utm_source == null ||  utm_source == "" || utm_source=="null"){
					utm_source = "";
				}
				
				location.href=amGloble.config.selfRoot + "pages/camp1505/index.html?utm_source="+utm_source;
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