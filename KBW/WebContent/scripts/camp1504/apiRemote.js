$(function () {
	jQuery.support.cors = true;
    $.ajaxSetup({
        cache: false
    });
    
//   var baseUrl = "http://localhost/brandkfc";
   var baseUrl = "http://t.a.kfc.com.cn/brandkfc";
//   var baseUrl = "http://a.kfc.com.cn/brandkfc";
    amGloble.api = {
            a01: new amGloble.Api(
                {
                    url: baseUrl + '/api/camp1504/foolday/open?rd=' + ('v'+Math.random()).replace('.','')
                }
            ),
            a02: new amGloble.Api(
                {
                    url: baseUrl + '/api/camp1504/foolday/getQuestion?rd=' + ('v'+Math.random()).replace('.','')
                }
            ),
            a03: new amGloble.Api(
                {
                    url: baseUrl + '/api/camp1504/foolday/answer?rd=' + ('v'+Math.random()).replace('.','')
                }
            ),
            a04: new amGloble.Api(
            		{
            			url: baseUrl + '/api/camp1504/foolday/share?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a05: new amGloble.Api(
                {
                    url: baseUrl + '/api/camp1504/foolday/draw?rd=' + ('v'+Math.random()).replace('.','')

                }
            ),
            a06: new amGloble.Api(
            		{
            			url: baseUrl + '/api/camp1504/foolday/winPhone?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a07: new amGloble.Api(
            		{
            			url: baseUrl + '/api/camp1504/foolday/isWinNotPhone?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a08: new amGloble.Api(
            		{
            			url: baseUrl + '/api/camp1504/foolday/updateWinPhone?rd=' + ('v'+Math.random()).replace('.','')
            		}
            ),
            a09: new amGloble.Api(
		    		{
		    			url: baseUrl + '/api/camp1504/foolday/getThirdWechatState?rd=' + ('v'+Math.random()).replace('.','')
		    		}
		    ),
            
            querySignature: new amGloble.Api(
            	{
            		url: baseUrl + '/api/camp1504/foolday/getTicket.do?rd=' + ('v'+Math.random()).replace('.','')
            	}
            ),
		    getCode: new amGloble.Api(
	    		{
	    			url: baseUrl + '/api/camp1504/foolday/getWechatCode?rd=' + ('v'+Math.random()).replace('.','')
	    		}
		    )
        	// ...
        };
        
    });


