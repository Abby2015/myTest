﻿/**
 * Created by qing.wang on 2015/07/06.
 */

$(function(){
	var controller = {
			
			init: function() {
				if (amGloble.config.debug == true) alert("===wx_redirect=== \n" + JSON.stringify(location));
				var flag = amGloble.getQueryParameter("flag");
				sessionStorage.setItem('flag', flag);
		    	this.goTarget();
			},
			
			goTarget : function(){
				var opt = {
						redirectUrl: amGloble.config.redirectUrl
	        	};
	        	if (amGloble.config.debug == true) alert("[-a08- input]"+JSON.stringify(opt));
	        	amGloble.api.a08.post(opt, function (ret) {
	        		if (amGloble.config.debug == true) alert("[-a08- return]" + JSON.stringify(ret));
	        		if (ret.content.errCode != 0 || ret.content.data == null) {
	        			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
	        		} else {
	        			if (amGloble.config.debug == true)
	        			alert("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
       						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_base&response_type=code"
    						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect");
	        			
	        			location.href= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ amGloble.config.appid 
	        						 + "&redirect_uri=http://crmminisite.verystar.cn/drawboard/oauth_snsapi_base&response_type=code"
	        						 + "&scope=snsapi_base&state=" + ret.content.data.data + "#wechat_redirect";
	        		}
	        	}, "application/json; charset=utf-8");
			}
	};
	controller.init();
});