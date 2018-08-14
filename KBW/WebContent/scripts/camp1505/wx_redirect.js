$(function(){
	var controller = {
			
			init: function() {
//				alert("===wx_redirect=== \n" + JSON.stringify(location));
				
				var utm_source = amGloble.getQueryParameter("utm_source");
		    	sessionStorage.setItem('utm_source', utm_source);
				
				/*var redirect_code = sessionStorage.getItem('redirect_code');
				if ( redirect_code == null ) {
					redirect_code = amGloble.getQueryParameter("redirect_code");
			    	sessionStorage.setItem('redirect_code', redirect_code);
				}
				var redirectUrl = amGloble.config.selfBase+"/wx_redirect.do";
				location.href="http://wechat.kfc.com.cn/KFCAPI/api/WXOpenidRedirect.aspx?state="+redirectUrl+"&code="+redirect_code;*/
		    	
		    	this.goTarget();
			},
			
			goTarget : function(){
				var opt = {
						redirectUrl: amGloble.config.redirectUrl
	        	};
//	        	alert("[-a09- input]"+JSON.stringify(opt));
	        	amGloble.api.a09.post(opt, function (ret) {
//	        		alert("[-a09- return]" + JSON.stringify(ret));
	        		if (ret.content.retCode != 0 || ret.content.data == null) {
//	        			alert(JSON.stringify(ret));
	        		} else {
	        			alert("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
       						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_openid&response_type=code"
    						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect");
	        			
	        			location.href= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
	        						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_openid&response_type=code"
	        						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect";
	        		}
	        	}, "application/json; charset=utf-8");
			}
	};
	controller.init();
});