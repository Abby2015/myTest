 $(function(){
	
	var controller = {
			init: function() {
//				alert("===wx_share==="+JSON.stringify(location));
				
				var UA = navigator.userAgent;
				var channelType = "";
				var deviceType = "";
				if(UA.indexOf("iPhone")!= -1 || UA.indexOf("iPad")!=-1){
					deviceType = "1";
				}else if(UA.indexOf("Android")!= -1){
					deviceType = "0";
				}else{
					deviceType = "2";
				}
				if(UA.indexOf("MicroMessenger")!= -1){
					channelType = "1";
				}else{
					if(deviceType == "1" || deviceType == "0"){
						channelType = "0";
					}else{
						channelType = "2";
					}
				}
				
//				alert("deviceType==" + deviceType);
//				alert("channelType==" + channelType);
				
				if(channelType == "1"){
					this.goTarget();
				}else if(channelType == "0"){
					location.href = amGloble.config.selfRoot + "pages/dbwy1506/mobile.html";
				}else{
					location.href = amGloble.config.selfRoot + "pages/dbwy1506/web.html";
				}
					
			},
	
			goTarget : function(){
				var opt = {
						redirectUrl: amGloble.config.shareRedirectUrl
		    	};
//		    	alert("[-a09- input]"+JSON.stringify(opt));
		    	amGloble.api.a09.post(opt, function (ret) {
//		    		alert("[-a09- return]" + JSON.stringify(ret));
		    		if (ret.content.errCode != 0 || ret.content.data == null) {
		//    			alert(JSON.stringify(ret));
		    		} else {
		    			/*alert("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
	       						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_base&response_type=code"
	    						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect");*/
		        			
	        			location.href= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
	        						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_base&response_type=code"
	        						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect";
		    		}
		    	}, "application/json; charset=utf-8");
			}
			
	};
	controller.init();
});