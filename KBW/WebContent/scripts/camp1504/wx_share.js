 $(function(){
	
	var controller = {
			init: function() {
//				alert("===wx_share==="+JSON.stringify(location));
				
				var src = sessionStorage.getItem('src');
				if ( src == null ) {
					src = amGloble.getQueryParameter("src");
			    	sessionStorage.setItem('src', src);
				}
				var qId = sessionStorage.getItem('qId');
				if ( qId == null ) {
					qId = amGloble.getQueryParameter("qId");
					sessionStorage.setItem('qId', qId);
				}
				
				var foid = sessionStorage.getItem('foid');
				if ( foid == null ) {
					foid = amGloble.getQueryParameter("foid");
					sessionStorage.setItem('foid', foid);
				}
				var tag = sessionStorage.getItem('tag');
				if ( tag == null ) {
					tag = amGloble.getQueryParameter("tag");
					sessionStorage.setItem('tag', tag);
				}
				//新增
				var channelType = sessionStorage.getItem('channelType');
	        	if ( !channelType ) {
	        		channelType = amGloble.getQueryParameter("channelType");
	        		sessionStorage.setItem('channelType', channelType);
	        	}
	        	var deviceType = sessionStorage.getItem('deviceType');
	        	if ( !deviceType ) {
	        		deviceType = amGloble.getQueryParameter("deviceType");
	        		sessionStorage.setItem('deviceType', deviceType);
	        	}
	        	/*var redirectUrl = amGloble.config.selfBase + "/share_redirect.do";
	        	if(channelType == '1'){
	        		location.href="http://wechat.kfc.com.cn/KFCAPI/api/WXOpenidRedirect.aspx?state="+redirectUrl+
	        		"&code="+src;
	        	}else{
//	        		alert("===wx_share.js===  redirectUrl \n"+redirectUrl);
	        		location.href= redirectUrl;
	        	}*/
	        	this.goTarget();
			},
	
			goTarget : function(){
				var opt = {
						redirectUrl: amGloble.config.shareRedirectUrl
		    	};
//		    	alert("[-a09- input]"+JSON.stringify(opt));
		    	amGloble.api.a09.post(opt, function (ret) {
//		    		alert("[-a09- return]" + JSON.stringify(ret));
		    		if (ret.content.retCode != 0 || ret.content.data == null) {
		//    			alert(JSON.stringify(ret));
		    		} else {
		    			/*alert("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
								 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_openid&response_type=code"
							 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect");*/
		    			
		    			location.href= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
		    						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_openid&response_type=code"
		    						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect";
		    		}
		    	}, "application/json; charset=utf-8");
			}
			
	};
	controller.init();
});