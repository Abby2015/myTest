﻿var amGloble = amGloble || {
	config: {
		appid : "wx97deba69fd949ca2",
//		debug : true,
		debug : false,
		
//		selfRoot: "http://localhost/brandkfc/",
//		selfRef: "http://localhost/brandkfc/",
		
		
//		selfRef: "http://t.a.kfc.com.cn/brandkfc_hyjt/",
//		selfRoot: "http://t.a.kfc.com.cn/brandkfc_hyjt/",
//		redirectUrl : "http://t.a.kfc.com.cn/brandkfc_hyjt/api/hyjt1506/wx_redirect.do",
//		shareRedirectUrl : "http://t.a.kfc.com.cn/brandkfc_hyjt/api/hyjt1506/share_redirect.do",
		
		selfRoot: "http://summer.kfc.com.cn/brandkfc_hyjt/",
		selfRef: "http://summer.kfc.com.cn/HYJT/",
		redirectUrl : "http://summer.kfc.com.cn/brandkfc_hyjt/api/hyjt1506/wx_redirect.do",
		shareRedirectUrl : "http://summer.kfc.com.cn/brandkfc_hyjt/api/hyjt1506/share_redirect.do",
	}
};

amGloble.loading = {
    show: function () {
        var $loading = $("body").find("#loading");
        if ($loading.length == 0) {
            $loading = $("<div id='loading'></div>");
            $("body").append($loading);
        }
        $loading.show();
    },
    hide: function () {
        var $loading = $("body").find("#loading");
        $loading.hide();
    }
};

amGloble.Api = function (opt) {
    if (!opt) return;
    if (!opt.url) {
//        alert("url lost");
    } else {
        this.url = opt.url;
    }
};
amGloble.Api.prototype = {
    get: function (opt, cb) {
        this.getdata("GET", opt, cb);
    },
    post: function (opt,cb, ct) {
        this.getdata("POST", opt, cb, ct);
    },
    getdata: function (method, option, cb, jsonContentType) {
    	var cfg = {
            type: method,
            url: this.url,
            data: option,
            timeout: 200000,
            success: function (ret) {
                cb && cb({ result: 0, content: ret });
            },
            error: function (ret) {
                cb && cb({ result: 1, content: ret });
            }
        };
    	if (jsonContentType) {
    		cfg.contentType = jsonContentType;
    		cfg.dataType = "json";
    		cfg.data = JSON.stringify(option);
    	}
        $.ajax(cfg);

    }
};
amGloble.getQueryParameter = function(name) {
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r!=null) return unescape(r[2]); return null;
};
amGloble.guid = (function() {
		function s4() {
		    return Math.floor((1 + Math.random()) * 0x10000)
		               .toString(16)
		               .substring(1);
		}
		return function() {
		    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		       s4() + '-' + s4() + s4() + s4();
		};
	}
)();

$(function () {
	jQuery.support.cors = true;
    $.ajaxSetup({
        cache: false
    });
    

    amGloble.api = {
            a01: new amGloble.Api(
                {
                    url: amGloble.config.selfRoot + 'api/hyjt1506/open?rd=' + ('v'+Math.random()).replace('.','')
                }
            ),
            a02: new amGloble.Api(
            		{
            			url: amGloble.config.selfRoot + 'api/hyjt1506/ask?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a03: new amGloble.Api(
            		{
            			url: amGloble.config.selfRoot + 'api/hyjt1506/collect?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a04: new amGloble.Api(
            		{
            			url: amGloble.config.selfRoot + 'api/hyjt1506/share?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a05: new amGloble.Api(
            		{
            			url: amGloble.config.selfRoot + 'api/hyjt1506/draw?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
		    a06: new amGloble.Api(
		    		{
		    			url: amGloble.config.selfRoot + 'api/hyjt1506/winInfo?rd=' + ('v'+Math.random()).replace('.','')
		    		}
		    ),
		    a08: new amGloble.Api(
		    		{
		    			url: amGloble.config.selfRoot + 'api/hyjt1506/getThirdWechatState?rd=' + ('v'+Math.random()).replace('.','')
		    		}
		    )
        	// ...
    };
    
});

var preloadimg = function(arr,comp){
	var n = 0;
	var loadImg = function(src){
		var img = new Image();
		img.onload = function(){
			n++;
			var t = Math.round(n/l*100);
			$("#loading").html(t+" %");
			if(n == l){
				comp();
			};
		};
		img.src = src;
	}
	if(typeof(arr) == "string"){
		var l = 1;
		var w = new loadImg(arr);
	}else{
		var l = arr.length;
		for(var i=0;i<l;i++){
			var w = new loadImg(arr[i]);
		};
	}
};

