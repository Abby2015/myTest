$(function(){
	
	var controller = {
			
			init: function() {
//				alert("===wx_share_redirect==="+JSON.stringify(location));
				this.saveOpenRecord();
			},
	    	
	    	saveOpenRecord: function() {
	        	var _this = this;
	        	if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户打开记录", "wt.msg", _this.getCoid());
	        	} 
	        	var normalShare_ =  sessionStorage.getItem("normalShare");
	        	if(normalShare_=="" || normalShare_=="null" || normalShare_ == null){
	        		normalShare_ = "";
	        	}
	        	var tag_ =  sessionStorage.getItem("tag");
	        	if(tag_=="" || tag_=="null" || tag_ == null){
	        		tag_ = "";
	        	}
	        	
	        	var opt = {
	        			channelType: _this.getChannelType(),
	        			userId: _this.getCoid(),
	        			deviceType: _this.getDeviceType(),
	        			deviceId: _this.getDeviceId(),
	        			tag : tag_,
	        			normalShare : normalShare_
	        	};
	        	
//	        	alert("[-a01- input]"+JSON.stringify(opt));
	        	amGloble.api.a01.post(opt, function (ret) {
//	        		alert("[-a01- return--]" + JSON.stringify(ret));
	        		if (ret.content.errCode != 0 || ret.content.data == null) {
//	        			alert(JSON.stringify(ret));
	        			location.href=amGloble.config.selfRoot + "api/xrbj1506/login.do";
	        		} else {
	        			sessionStorage.setItem("sid", ret.content.data.sid); 
	        			sessionStorage.setItem("other", ret.content.data.other); 
	        			sessionStorage.setItem("menuType", ret.content.data.menuType); 
	        			sessionStorage.setItem("drawCoupon", ret.content.data.drawCoupon);
	        			sessionStorage.setItem("shareCome", ret.content.data.shareCome);
	        			sessionStorage.setItem("tag", ret.content.data.tag);
	        			var drawCoupon = ret.content.data.drawCoupon;
	        			if(drawCoupon == true || drawCoupon == "true"){
//	        				alert(amGloble.config.selfRoot + 'pages/xrbj1506/success.html?foid='+_this.getCoid()+"&tag="+ret.content.data.tag+"&normalShare=0");
	        				location.href=amGloble.config.selfRoot + 'pages/xrbj1506/success.html?foid='+_this.getCoid()+"&tag="+ret.content.data.tag+"&normalShare=0";
	        			}else{
//	        				alert(amGloble.config.selfRoot + 'pages/xrbj1506/fail.html?foid='+_this.getCoid()+"&tag="+ret.content.data.tag+"&normalShare=0");
	        				location.href=amGloble.config.selfRoot + 'pages/xrbj1506/fail.html?foid='+_this.getCoid()+"&tag="+ret.content.data.tag+"&normalShare=0";
	        			}
	        		}
	        	}, "application/json; charset=utf-8");
	        },
	    	
	        getCoid: function() {
	        	var coid = amGloble.getQueryParameter("coid");
	        	localStorage.setItem('openid', coid);
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