/**
 * Created by qing.wang on 2015/05/15.
 */

var UpdatePhoneBmh = {};

UpdatePhoneBmh = {
	init : function(el){
		//区域放大
		el.find('#prizeForm  .cont').bind('touchstart',function(){
			$('#prizeForm  input').focus();
		});
		
		el.find('#prizeForm  .submit').bind('touchstart',function(){
			$('#errorTips').hide();
			//验证手机号码
			var telephone = $.trim($('#prizeForm input').val());
			if (telephone == '') {
				$('#prizeForm input').focus();
				$('#errorTips').text('手机号码不能为空！').show();
				return false;
			}
			var telRegex1 = /^13\d{9}$/, telRegex2 = /^15[^4]\d{8}$/, telRegex3 = /^1[78]\d{9}$/;
			var result = telephone.match(telRegex1) || telephone.match(telRegex2) || telephone.match(telRegex3);
			if(!result){
				$('#prizeForm input').val("");
				$('#prizeForm input').focus();
				$('#errorTips').text('手机号码有误！').show();
				return false;
			}
			
			var userId = amGloble.getQueryParameter('userId');
			//调用接口提交号码信息：---POST: /bmh1506/winInfo a07  channelType		userId	phone
			opt = {	    			
	    			channelType: '1',
	    			userId: userId,
	    			phone: telephone
	    	};
			if (amGloble.config.debug == true) alert("[-a07- input]" + JSON.stringify(opt));
			amGloble.api.a07.post(opt, function(ret){
				if (amGloble.config.debug == true) alert("[-a07- return]" + JSON.stringify(ret));
	    		if (ret.content.errCode != 0) {
	    			if (amGloble.config.debug == true) alert(JSON.stringify(ret));
	    			$('#prizeForm').html('<div class="area" style="padding-bottom:0;"><div class="cont"><div style="display: -webkit-box;-webkit-box-pack: center;-webkit-box-align: center;display: box;box-pack: center;box-align: center;box-orient: vertical;-webkit-box-orient: vertical;box-orient: vertical;	width: 100%;height: 100px;position: relative;margin: 0 auto"><h3>'+ret.content.errMsg+'</h3></div></div></div>');
	    		} else {
	    			$('#prizeForm').html('<div class="area" style="padding-bottom:0;"><div class="cont"><div style="display: -webkit-box;-webkit-box-pack: center;-webkit-box-align: center;display: box;box-pack: center;box-align: center;box-orient: vertical;-webkit-box-orient: vertical;box-orient: vertical;	width: 100%;height: 100px;position: relative;margin: 0 auto"><h3>操作成功！</h3></div></div></div>');
	    		}
	    	}, "application/json; charset=utf-8");
			
		});
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
    },
    getSid: function () { 
    	return sessionStorage.getItem('sid');
    }
};
$(function(){	
	UpdatePhoneBmh.init($('.am-app'));
});