$(function(){
	
	var controller = {
			
			init: function() {
//				alert("===wx_share_redirect==="+JSON.stringify(location));
				this.$base_ctx = $('#base_ctx').html();
				
				this.$touch4Retry = $("a");
				this.initData = {
						breakfast: sessionStorage.getItem('qId'),
						fromOpenId: sessionStorage.getItem('foid'),
						tag: sessionStorage.getItem('tag')
				};
				var _this = this;
				
				_this.getChannelType();
				_this.getDeviceType();
				_this.getCtoken();
				
				this.$touch4Retry.bind("touchstart click", function(evt) {
					evt.stopPropagation();
					evt.preventDefault();
					_this.$touch4Retry.hide();
					_this.directToRealTarget(location.search);
				});
				this.directToRealTarget();
			},
	    	
	    	directToRealTarget: function() {
	    		var  currentOpenId = this.getCoid();
	    		var _this = this;
	    		
	    		if ( typeof currentOpenId != 'string' || currentOpenId.length < 28 ){
					_this.$touch4Retry.show();
				} else {
					_this.$touch4Retry.hide();
					location.replace(amGloble.config.selfPage + '/index.jsp?&channelType='+_this.getChannelType()+'&deviceType='+_this.getDeviceType()+'&token='+_this.getCtoken()+'&openid='+currentOpenId);
				}
	    	},
	    	
	        getCoid: function() {
	        	var coid_ = localStorage.getItem('openid');
	        	if(coid_!=null && coid_!='' && coid_ != 'null' ){
	        		localStorage.setItem('openid', coid_);
	        	}
	        	var coid = coid_!=null ? coid_ : localStorage.getItem('openid');
	        	if ( !coid || coid=='null' ) {
	        		coid = amGloble.getQueryParameter("coid");
	        		localStorage.setItem('openid', coid);
	        	}
	        	return coid;
	        },
	        getCtoken: function() {
	        	var ctoken = sessionStorage.getItem('ctoken');
	        	if ( !ctoken ) {
	        		ctoken = amGloble.getQueryParameter("token");
	        		sessionStorage.setItem('token', ctoken);
	        	}
	        	return ctoken;
	        },
	        getDeviceId: function() {
	        	var deviceid = localStorage.getItem('deviceid');
	        	if ( !deviceid ) {
	        		deviceid = amGloble.guid();
	        		localStorage.setItem('deviceid', deviceid);
	        	}
	        	return deviceid;
	        },
	    	
	        getChannelType: function() {
	        	var channelType = sessionStorage.getItem('channelType');
	        	if ( !channelType ) {
	        		channelType = amGloble.getQueryParameter("channelType");
	        		sessionStorage.setItem('channelType', channelType);
	        	}
	        	return channelType;
	        },
	        getDeviceType: function() {
	        	var deviceType = sessionStorage.getItem('deviceType');
	        	if ( !deviceType ) {
	        		deviceType = amGloble.getQueryParameter("deviceType");
	        		sessionStorage.setItem('deviceType', deviceType);
	        	}
	        	return deviceType;
	        }
	        
	};
	
	controller.init();
	
});