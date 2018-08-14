$(function(){
	var controller = {
			
			init: function() {
//				alert("===wx_redirect=== \n" + JSON.stringify(location));
		    	this.goTarget();
			},
			
			goTarget : function(){
				var opt = {
						redirectUrl: amGloble.config.redirectUrl
	        	};
//	        	alert("[-a09- input]"+JSON.stringify(opt));
	        	amGloble.api.a09.post(opt, function (ret) {
//	        		alert("[-a09- return]" + JSON.stringify(ret));
	        		if (ret.content.errCode != 0 || ret.content.data == null) {
//	        			alert(JSON.stringify(ret));
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