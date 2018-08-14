$(function(){
	
	var controller = {
			
			init: function() {
				alert("===wx_share_redirect==="+JSON.stringify(location));
				this.saveOpenRecord();
			},
	    	
	    	saveOpenRecord: function() {
	        	var _this = this;
	        	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户打开记录", "wt.msg", _this.getCoid());
	        	} 
	        	
	        	var opt = {
	        			channelType: _this.getChannelType(),
	        			userId: _this.getCoid(),
	        			deviceType: _this.getDeviceType(),
	        			deviceId: _this.getDeviceId()
	        	};
	        	
	        	alert("[-a01- input]"+JSON.stringify(opt));
	        	amGloble.api.a01.post(opt, function (ret) {
	        		alert("[-a01- return]" + JSON.stringify(ret));
	        		if (ret.content.errCode != 0 || ret.content.data == null) {
//	        			alert(JSON.stringify(ret));
	        		} else {
	        			sessionStorage.setItem("sid", ret.content.data.sid);
	        			location.replace(amGloble.config.selfRoot + 'pages/bmh1506/index.html');
	        		}
	        	}, "application/json; charset=utf-8");
	        },
	    	
	        getCoid: function() {
	        	if ( !coid || coid=='null' ) {
	        		coid = amGloble.getQueryParameter("coid");
	        		localStorage.setItem('openid', coid);
	        	}
	        	return coid;
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