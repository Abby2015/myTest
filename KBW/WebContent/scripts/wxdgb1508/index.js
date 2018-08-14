
var swiperParent;

$(function(){
    var controller = {

        init: function() {
        	if (amGloble.config.debug == true) alert(" ===index.html=== \n"+JSON.stringify(location));
        	var _this = this;
            
        	//初始化节点
        	this.$pagehome1=$(".pagehome1");
        	this.$pagehome2=$(".pagehome2");
        	this.$pageshow=$(".pageshow");
        	this.$pagelaunch=$(".pagelaunch");
        	this.$pageuncode=$(".pageuncode");
        	this.$pagedecoded=$(".pagedecoded");
        	this.$pageshare=$(".pageshare");
        	this.$pagegetlove=$(".pagegetlove");
           	this.$pagedecode=$(".pagedecode");
        	this.$pageotherlove=$(".pageotherlove");
        	this.$pagerule_pop=$(".rule_pop");
        	this.$pagedecode2=$(".pagedecode2");
        	
        	
        	//btn
        	this.$pagehome1bt_preview=$(".pagehome1 .bt_preview");
        	this.$pagehome1bt_send=$(".pagehome1 .bt_send");
        	this.$pagehome1bt_write=$(".pagehome1 .writebox_words");
        	
        	this.$pagehome2bt_preview=$(".pagehome2 .bt_preview");
        	this.$pagehome2bt_send=$(".pagehome2 .bt_send");
        	this.$pagehome2bt_back=$(".pagehome2 .writebox_words");
        	
        	this.$pageshowbt_gaobai=$(".pageshow .bt_bglarge");
        	this.$pageshowbt_back=$(".pageshow .marginlt");
        	
        	this.$pagegetlovebt_poyi=$(".pagegetlove .writebox_words");
        	
        	this.$pageuncodebt_send=$(".pageuncode .writebox_words");
        	
        	this.$pagedecodebt_quan=$(".pagedecode .writebox_words");
        	
        	this.$pagedecode2bt_quan=$(".pagedecode2 .writebox_words");
        	
        	this.$pagedecodedbt_quan=$(".pagedecoded .writebox_words");
        	
        	this.$pageotherlovebt_gaobai=$(".pageotherlove .writebox_words");
        	
        	this.$pagerulepopbt_close=$(".rule_pop .bt_close");
        	
        	this.$touch4info=$(".needfocus");
        	
        	this.$rule=$(".rulelink");
        	
        	


           
            
            //节点绑定
        	
        	this.$touch4info.bind("touchstart click",function(){
        		//texarea锁定焦点
        		_this.$touch4info.focus();
        	});
        	
        	this.$rule.bind("touchstart click",function(){
        		//打开活动细则页
        		_this.$pagerule_pop.show();
        		new IScroll("#wrapper");
        	});
        	
        	this.$pagerulepopbt_close.bind("touchstart click",function(){
                //关闭活动细则页
        		_this.$pagerule_pop.hide();
        	});
        	
        	
        	this.$pagehome1bt_preview.bind("touchstart click",function(){
        		//选择页进入预览页
        		sessionStorage.setItem('pagefrom',"pagehome1");
        		_this.setWord1();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户预览告白令", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagehome1bt_send.bind("touchstart click",function(){
        		//选择页进入发射页
        		_this.launchWord1();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户发射告白令", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagehome1bt_write.bind("touchstart click",function(){
        		//选择页进入输入页
        		_this.$pagehome1.hide();
        		_this.$pagehome2.show();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户进入原创告白输入页", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagehome2bt_preview.bind("touchstart click",function(){
        		//输入页进入预览页
        		sessionStorage.setItem('pagefrom',"pagehome2");
        		_this.setWord2();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户预览告白令", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagehome2bt_send.bind("touchstart click",function(){
        		//输入页进入发射页
        		_this.launchWord2();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户发射告白令", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagehome2bt_back.bind("touchstart click",function(){
        		//输入页进入选择页
        		_this.$pagehome2.hide();
        		_this.$pagehome1.show();
        		//$(".swiper-wrapper .swiper-slide").remove(); 
        		//_this.initSlide();
        		swiperParent.reInit();
            });
        	
        	this.$pageshowbt_gaobai.bind("touchstart click",function(){
        		//输入页进入发射页
        		_this.$pagelaunch.show();
        		_this.$pageshow.hide();
        		_this.ask();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户发射告白令", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pageshowbt_back.bind("touchstart click",function(){
        		//预览页进入输入页  或选择页
        		var page=sessionStorage.getItem('pagefrom');
        		$("."+page).show();
        		_this.$pageshow.hide();
            });
        	
  //      	this.$pagelaunch.bind("touchstart click",function(){
//        		_this.$pageshare.show();
//        		_this.$pageuncode.show();
//        		_this.$pagelaunch.hide();
  //          });
        	
        	this.$pageshare.bind("touchstart click",function(){
        		//分享页进入未解码页
        		setTimeout(function(){
        			_this.share();
                },1000);
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户分享成功", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pageuncodebt_send.bind("touchstart click",function(){
        		//未解码页进入分享页      再次发射
        		_this.launchagain();
        		
            });
        	
        	this.$pagegetlovebt_poyi.bind("touchstart click",function(){
        		//B未解码页进入解码页
        		_this.showWord();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户进入解码页", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagedecodebt_quan.bind("touchstart click",function(){
        		//破译者拿券
        		_this.collect();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户领取卡券", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagedecode2bt_quan.bind("touchstart click",function(){
        		//破译者拿券
        		_this.collect();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户领取卡券", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pagedecodedbt_quan.bind("touchstart click",function(){
        		//分享者拿券
        		_this.collect();
        		if (window._tag) { 
	        		_tag.dcsMultiTrack("wt.event", "用户领取卡券", "wt.msg", _this.getOpenid());
	        	}
            });
        	
        	this.$pageotherlovebt_gaobai.bind("touchstart click",function(){
        		//c玩游戏
        		_this.$pageotherlove.hide();
        		_this.$pagehome1.show();
        		_this.initSlide();
        		//swiperParent.reInit();
            });
        	
        	
        	
        	

            //初始化
           _this.loadImg();
          // _this.initSlide();
           _this.bindWChatAPI();
           
        },
        initSlide:function() {
        	//初始化slide选择页
        	 swiperParent = new Swiper('.swiper-parent',{
        	    pagination: '.pagination-parent',
        	    paginationClickable: true,
        	    loop: true,
        	    slidesPerView: 1
        	  })
        	 $('.prev').on('touchstart click', function(e){
        	    e.preventDefault()
        	    swiperParent.swipePrev()
        	  })
        	  $('.next').on('touchstart click', function(e){
        	    e.preventDefault()
        	    swiperParent.swipeNext()
        	  })
        },
        launchagain:function() {
        	var _this=this;
        	_this.$pageuncode.hide();
        	_this.$pagelaunch.show();
        	setTimeout(function(){	
        		_this.$pageuncode.show();
            	_this.$pagelaunch.hide();
        		_this.$pageshare.show();
            },3000);
        	
        	
        },
        setWord1:function() {
        	//选择页进入预览页
        	var _this=this;
        	var str=$(".pagehome1 .swiper-slide.swiper-slide-active .subjectdiv").text();
        	var img=$(".pagehome1 .swiper-slide.swiper-slide-active img").attr('src');
        	$(".pageshow .font_example").attr("src",img);
        	if(str.search("婚")>=0){
        		str=1;
        	}else if(str.search("餐")>=0){
        		str=2;
        	}else if(str.search("蜜罐")>=0){
        		str=3;
        	}
        	sessionStorage.setItem("loveOption",str);
        	sessionStorage.setItem("loveContent","");
        	setTimeout(function(){	
        		_this.$pageshow.show();
        		_this.$pagehome1.hide();
            },1000);
    		
        	
        },
        setWord2:function() {
        	//输入页进入预览页
        	var _this=this;
        	var str=$("textarea").val();
        	var strlength=str.length;
        	//alert(strlength);
        	if(strlength>=3){
            	switch(strlength){
            	case 3:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code3.png");
            		break;
            	case 4:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code4.png");
            		break;
            	case 5:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code5.png");
            		break;
            	case 6:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code6.png");
            		break;
            	case 7:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code7.png");
            		break;
            	case 8:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code8.png");
            		break;
            	case 9:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code9.png");
            		break;
            	case 10:
            		$(".pageshow .font_example").attr("src","../../images/wxdgb1508/pic_code10.png");
            		break;
            		
            	}
            	sessionStorage.setItem("loveOption","");
            	sessionStorage.setItem("loveContent",str);
            	setTimeout(function(){	
            		_this.$pageshow.show();
            		_this.$pagehome2.hide();
                },1000);
        		
        	}
        	
        },
        launchWord1:function() {
        	//选择页进入发射页
        	var _this=this;
        	var str=$(".pagehome1 .swiper-slide.swiper-slide-active .subjectdiv").text();
        	if(str.search("婚")>=0){
        		str=1;
        	}else if(str.search("餐")>=0){
        		str=2;
        	}else if(str.search("蜜罐")>=0){
        		str=3;
        	}
        	sessionStorage.setItem("loveOption",str);
        	sessionStorage.setItem("loveContent","");
        	_this.$pagelaunch.show();
    		_this.$pagehome1.hide();
    		_this.ask();
        },
        launchWord2:function() {
        	//输入页进入发射页
        	var _this=this;
        	var str=$("textarea").val();
        	var strlength=str.length;
        	//alert(strlength);
        	if(strlength>=3){
        		sessionStorage.setItem("loveOption","");
            	sessionStorage.setItem("loveContent",str);
        		_this.$pagelaunch.show();
        		_this.$pagehome2.hide();
        		_this.ask();
        	}
        	
        },
       // getWord:function() {
        	//var _this=this;
        	//sessionStorage.setItem("word", "example2"); 
        	//var option=sessionStorage.getItem("loveOption");
        	//var content=sessionStorage.getItem("loveContent");
        	
       // },
        showWord:function() {
        	//B未解码页进入解码页1或2 做原创还是非原创显示的判断
        	var _this = this;
        	//var str=sessionStorage.getItem("word");
        	var option=sessionStorage.getItem("loveOption");
        	var content=sessionStorage.getItem("loveContent");
        	if (amGloble.config.debug == true) alert(" ===解码信息option&content=== \n"+option+"  " +content);
        	if(option!=null&&content==""){
        		if(option==1){
            		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_example2.png");
            	}else if(option==2){
            		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_face_cn.png");
            	}else if(option==3){
            		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_ring_cn.png");
            	}
        		setTimeout(function(){	
        			_this.$pagedecode.show();
            		_this.$pagegetlove.hide();
                },1000);
        		
        	}
        	if(content!=""){
        		//只显示文字
        		$(".pagedecode2 .fontsdiv3").text(content);
        		setTimeout(function(){	
        			_this.$pagedecode2.show();
            		_this.$pagegetlove.hide();
                },1000);
        		
        	}
        	
        	
    		
        },
        share: function() {
        	//分享记录
            var _this = this;
            var url=amGloble.config.selfRef + "pages/wxdgb1508/index.html?tag="+sessionStorage.getItem("tag")+"&normalShare=1&openid="+_this.getOpenid();
            var opt = {
                    sid : sessionStorage.getItem("sid"),
                    userId: _this.getOpenid(),
                    channelType: _this.getChannelType(),
                    deviceType: _this.getDeviceType(),
                    mediaType: "WX",
                    shareResult : 1,
                    shareUrl : url
            };
            if (amGloble.config.debug == true) alert("[-a04- input]"+JSON.stringify(opt));
            amGloble.api.a04.post(opt, function (ret) {
                if (amGloble.config.debug == true) alert("[-a04- return]" + JSON.stringify(ret));
                if (ret.content.errCode != 0) {
                    if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                } else {
                	if (window._tag) { 
    	        		_tag.dcsMultiTrack("wt.event", "用户分享成功", "wt.msg", _this.getOpenid());
    	        	}
                   //分享成功
        			_this.$pageuncode.show();
        			_this.$pageshare.hide();
                }
            }, "application/json; charset=utf-8");

        },
        collect : function(){
        	//收藏卡券
        	var code="";
        	var promoCode2=sessionStorage.getItem('promoCode2');
        	if(promoCode2){
        		code=sessionStorage.getItem("promoCode2");
        	}else{
        		code=sessionStorage.getItem("promoCode");
        	}
        	
        	//alert("[promocode  promoCode2 ]" +sessionStorage.getItem("promoCode")+"|"+sessionStorage.getItem("promoCode2"));
        	
            var opt = {
                    promoCode : code
                };
            if (amGloble.config.debug == true) alert("[-a03- input]" + JSON.stringify(opt));
            amGloble.api.a03.post(opt, function (ret) {
                if (amGloble.config.debug == true) alert("[-a03- return]" + JSON.stringify(ret));
                if (ret.content.errCode != 0 || ret.content.data == null) {
                    if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                } else {
                   location.href = "http://crmminisite.verystar.cn/wxcard/promo?key="+ret.content.data.data+"&wechat_card_js=1";
                }
            }, "application/json; charset=utf-8");
        },
        ask : function(){
        	var _this = this;
        	//待火箭3s动画结束后调用  保存用书输入记录
        	setTimeout(function(){
        		
//        		var option=sessionStorage.getItem("loveOption");
//            	var content=sessionStorage.getItem("loveContent");
//        		sessionStorage.setItem("asked","true");
//        		location.href="file:///C:/Document/workplace/brandkfc/WebContent/pages/wxdgb1508/index.html?tag=111";

        		//待处理 这边是相对立的
            	var option=sessionStorage.getItem("loveOption");
            	var content=sessionStorage.getItem("loveContent");          	
                var opt = {
                        sid : sessionStorage.getItem("sid"),
                        userId: _this.getOpenid(),
                        channelType: _this.getChannelType(),
                        deviceType: _this.getDeviceType(),
                        loveOption:option,
                        loveContent:content
                };
                if (amGloble.config.debug == true) alert("[-a05- input]"+JSON.stringify(opt));
                amGloble.api.a05.post(opt, function (ret) {
                    if (amGloble.config.debug == true) alert("[-a05- return]" + JSON.stringify(ret));
                    if (ret.content.errCode != 0) {
                        if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                    } else {
                    	//sessionStorage.setItem("tag", ret.content.data.tag);
                    	sessionStorage.setItem("asked","true");
                    	if (amGloble.config.debug == true) alert(amGloble.config.selfRef + "pages/wxdgb1508/index.html?tag="+ret.content.data.tag+"&normalShare="+ret.content.data.normalShare);
        				location.href=amGloble.config.selfRef + "pages/wxdgb1508/index.html?tag="+ret.content.data.tag+"&normalShare="+ret.content.data.normalShare;
        				
                    }
                }, "application/json; charset=utf-8");

            },3000);
        	
        	
        },
        decode : function(){
        	//抢到券了 做原创或非原创图片显示的判断
        	var _this = this;
            var opt = {
                    userId: _this.getOpenid(),
                    channelType: _this.getChannelType(),
                    deviceType: _this.getDeviceType(),
                    deviceId: _this.getDeviceId(),
                    tag: sessionStorage.getItem("tag"),
                    normalShare: sessionStorage.getItem("normalShare"),
            };
            if (amGloble.config.debug == true) alert("[-a06- input]"+JSON.stringify(opt));
            amGloble.api.a06.post(opt, function (ret) {
                if (amGloble.config.debug == true) alert("[-a06- return]" + JSON.stringify(ret));
                if (ret.content.errCode != 0) {
                    if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                } else {
                	sessionStorage.setItem("loveContent", ret.content.data.loveContent);
                	sessionStorage.setItem("loveOption", ret.content.data.loveOption);
                	sessionStorage.setItem("promoCode", ret.content.data.promoCode);
                	
                	var option=sessionStorage.getItem("loveOption");
                	var content=sessionStorage.getItem("loveContent");
                	if (amGloble.config.debug == true) alert(" ===解码信息option&content=== \n"+option+"  " +content);
                	if(option){
                		if(option==1){
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/font_example1.png");
                    	}else if(option==2){
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/font_face.png");
                    	}else if(option==3){
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/font_ring.png");
                    	}
                	}
                	if(content){
                		var strlength=content.length;
                		switch(strlength){
                    	case 3:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code3.png");
                    		break;
                    	case 4:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code4.png");
                    		break;
                    	case 5:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code5.png");
                    		break;
                    	case 6:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code6.png");
                    		break;
                    	case 7:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code7.png");
                    		break;
                    	case 8:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code8.png");
                    		break;
                    	case 9:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code9.png");
                    		break;
                    	case 10:
                    		$(".pagegetlove .font_example").attr("src","../../images/wxdgb1508/pic_code10.png");
                    		break;
                    		
                    	}
                	}
                	setTimeout(function(){	
                		_this.$pagehome1.hide();
                    	_this.$pagegetlove.show();
                    },1000);
                	
                }
            }, "application/json; charset=utf-8");
		   
        },
        decode2 : function(){
        	//不是第一次解码
        	var _this = this;
            var opt = {
                    userId: _this.getOpenid(),
                    channelType: _this.getChannelType(),
                    deviceType: _this.getDeviceType(),
                    deviceId: _this.getDeviceId(),
                    tag: sessionStorage.getItem("tag"),
                    normalShare: sessionStorage.getItem("normalShare"),
            };
            if (amGloble.config.debug == true) alert("[-a06-2 input]"+JSON.stringify(opt));
            amGloble.api.a06.post(opt, function (ret) {
                if (amGloble.config.debug == true) alert("[-a06-2 return]" + JSON.stringify(ret));
                if (ret.content.errCode != 0) {
                    if (amGloble.config.debug == true) alert(JSON.stringify(ret));
                } else {
                	sessionStorage.setItem("loveContent", ret.content.data.loveContent);
                	sessionStorage.setItem("loveOption", ret.content.data.loveOption);
                	sessionStorage.setItem("promoCode", ret.content.data.promoCode);
                	
                	var option=sessionStorage.getItem("loveOption");
                	var content=sessionStorage.getItem("loveContent");
                	if (amGloble.config.debug == true) alert(" ===解码信息option&content=== \n"+option+"  " +content);
                	if(option!=null&&content==""){
                		if(option==1){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_example2.png");
                    	}else if(option==2){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_face_cn.png");
                    	}else if(option==3){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_ring_cn.png");
                    	}
                		setTimeout(function(){	
                			_this.$pagedecode.show();
                			_this.$pagehome1.hide();
                        },1000);
                		
                	}
                	if(content!=""){
                		//只显示文字
                		$(".pagedecode2 .fontsdiv3").text(content);
                		setTimeout(function(){	
                			_this.$pagedecode2.show();
                			_this.$pagehome1.hide();
                        },1000);
                		
                	}
                	
                }
            }, "application/json; charset=utf-8");
		   
        },
        gotoquanpage : function(){
        	
        	var _this = this;
            
                	var option=sessionStorage.getItem("loveOption");
                	var content=sessionStorage.getItem("loveContent");
                	if (amGloble.config.debug == true) alert(" ===解码信息option&content=== \n"+option+"  " +content);
                	if(option!=null&&content==""){
                		if(option==1){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_example2.png");
                    	}else if(option==2){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_face_cn.png");
                    	}else if(option==3){
                    		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_ring_cn.png");
                    	}
                		setTimeout(function(){	
                			_this.$pagedecode.show();
                			_this.$pagehome1.hide();
                        },1000);
                		
                	}
                	if(content!=""){
                		//只显示文字
                		$(".pagedecode2 .fontsdiv3").text(content);
                		setTimeout(function(){	
                			_this.$pagedecode2.show();
                			_this.$pagehome1.hide();
                        },1000);
                		
                	}
                	
                

        	
        	
        	
        },
        fromtuisong : function(){
        	var _this = this;
        	if(sessionStorage.getItem('isSender')=="true"){
        		//发送者
            	_this.$pagehome1.hide();
            	_this.$pagedecoded.show();
        	}else{
        		//受邀者
        		_this.$pagehome1.hide();
        		var option=sessionStorage.getItem("loveOption2");
            	var content=sessionStorage.getItem("loveContent2");
            	if (amGloble.config.debug == true) alert(" ===解码信息option2&content2=== \n"+option+"  " +content);
            	if(option!=null&&content==""){
            		if(option==1){
                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_example2.png");
                	}else if(option==2){
                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_face_cn.png");
                	}else if(option==3){
                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_ring_cn.png");
                	}
            		setTimeout(function(){	
            			_this.$pagedecode.show();
                		
                    },1000);
            		
            	}
            	if(option==""){
            		//url传进来的lovecontent如果是中文会出现乱码 这里重新请求decode接口获取lovecontent   不行 点击优惠券进来没有tag
            		//只显示文字
            		$(".pagedecode2 .fontsdiv3").text(content);
            		setTimeout(function(){	
            			_this.$pagedecode2.show();
                		
                    },1000);
            		
            	}
        	}
        },
        loadImg : function(){
        	var _this = this;
        	var imgArr = [
        	              "../../images/wxdgb1508/arrow_left.png",
        	              "../../images/wxdgb1508/arrow_right.png",
        	              "../../images/wxdgb1508/bg.jpg",
        	              "../../images/wxdgb1508/bg_bb.jpg",
        	              "../../images/wxdgb1508/bg_loading.jpg",
        	              "../../images/wxdgb1508/bg_preview.jpg",
        	              "../../images/wxdgb1508/bg_tt.jpg",
        	              "../../images/wxdgb1508/bg2.jpg",
        	              "../../images/wxdgb1508/bg3.jpg",
        	              "../../images/wxdgb1508/bt_bg.png",
        	              "../../images/wxdgb1508/bt_bg2.png",
        	              "../../images/wxdgb1508/bt_bglarge.png",
        	              "../../images/wxdgb1508/font_example1.png",
        	              "../../images/wxdgb1508/font_example2.png",
        	              "../../images/wxdgb1508/font_face.png",
        	              "../../images/wxdgb1508/font_face_cn.png",
        	              "../../images/wxdgb1508/font_ring.png",
        	              "../../images/wxdgb1508/font_ring_cn.png",
        	              "../../images/wxdgb1508/logo.png",
        	              "../../images/wxdgb1508/pic_activityname.png",
        	              "../../images/wxdgb1508/pic_code3.png",
        	              "../../images/wxdgb1508/pic_code4.png",
        	              "../../images/wxdgb1508/pic_code5.png",
        	              "../../images/wxdgb1508/pic_code6.png",
        	              "../../images/wxdgb1508/pic_code7.png",
        	              "../../images/wxdgb1508/pic_code8.png",
        	              "../../images/wxdgb1508/pic_code9.png",
        	              "../../images/wxdgb1508/pic_code10.png",
        	              "../../images/wxdgb1508/pic_rocket.png",
        	              "../../images/wxdgb1508/pic_rocket1.png",
        	              "../../images/wxdgb1508/pic_screen.png",
        	              "../../images/wxdgb1508/pic_screen2.png",
        	              "../../images/wxdgb1508/pic_tt1.png",
        	              "../../images/wxdgb1508/bg_rule.png",
        	              "../../images/wxdgb1508/icon_close.png",
        	              "../../images/wxdgb1508/pic_down.png",
        	              "../../images/wxdgb1508/pic_screen3.png",
        	              "../../images/wxdgb1508/pic_tt2.png"
                          ];
            preloadimg(imgArr,function(){
            	//在这里处理加载完图片之后，页面的显示
            	$('#loading').hide();
            	$(".topline").show();
            	//_this.$pagehome1.show();
            	//_this.initSlide();
            	
            	//var shareCome = sessionStorage.getItem('shareCome');
            	var other = sessionStorage.getItem('other');
                var isDecode = sessionStorage.getItem('isDecode');
                var drawCoupon = sessionStorage.getItem('drawCoupon');
                var isFirst = sessionStorage.getItem('isFirst');
                var isSendLove = sessionStorage.getItem('isSendLove');
                var asked=sessionStorage.getItem('asked');
                var promoCode2=sessionStorage.getItem('promoCode2');
                
                
                
                if (amGloble.config.debug == true) alert("[other isDecode drawCoupon isFirst isSendLove asked promoCode2]" + other+ isDecode+ drawCoupon+ isFirst+ isSendLove+ asked+ promoCode2);

                
                
                if(asked=="true"){
                	//在请求ask接口之后存放在sessionstorage中，重新加载页面如果有，直接跳转到分享页面
                	$(".pageshare").show();
                	$(".pageuncode").show();
                	$(".pagehome1").hide();
                	sessionStorage.setItem("asked","");
                	}else{
                	//没有asked标记，继续向下初始化页面
                		if(promoCode2){
                		//如果url中带有promocode，说明是点击优惠券进来的，直接通过url中的promocode，loveoption，lovecontent直接进入解码页
                			_this.fromtuisong();
                		}else{
                		//url中没有promocode标记，继续向下初始化页面
                		//没有以上标记，下面开始通过open接口中获得参数初始化页面

                			if(other!="true"){	//如果是发起者A自己登陆
                			  if(isDecode=="true"){	//如果已解码
                			    //跳转到已解码领取卡券页面（领取卡券时promoCode参数会用到）
                				 // _this.$pagedecoded.show();
                				  if(isSendLove=="true"){	//如果已经发送过告白
                   			       //跳转到未被解码再次发射页面，发射进行分享时：
                   			    	_this.$pagedecoded.show();
                   			    }else{
                   			       //跳转到首页。
                   			    	_this.$pagehome1.show();
                   			    	_this.initSlide();
                   			    }
                				
                			  }else{
                			    if(isSendLove=="true"){	//如果已经发送过告白
                			       //跳转到未被解码再次发射页面，发射进行分享时：
                			    	_this.$pageuncode.show();
                			    }else{
                			       //跳转到首页。
                			    	_this.$pagehome1.show();
                			    	_this.initSlide();
                			    }
                			  }
                			}else{	//如果是受邀者B点击A的分享链接进入
                			   if(drawCoupon=="true"){ //如果抢到半价券
                			      if(isFirst=="true"){ //如果是第一次点击A的分享链接
                			         if(quan=="true"){
                			         //在微信卡包跳转回来的url中添加quan=true，说明是从卡包回来的，不要再次破解了，直接回到解码页
                			        	 if (amGloble.config.debug == true) alert("[quan]" + quan);
                         				_this.gotoquanpage();
                			         }else{
                			         //跳转到等待破译页面
                			        	 _this.decode();
                			         }
                			      }else{
                			         //跳转到已经破译领取半价券的页面
                			    	  _this.decode2();
                			      }
                			   }else{
                			     //跳转到其余好友进入页,C的流程
                				   _this.$pageotherlove.show();
                			   }
                			}
                		}
                	}
                
                
                
                
                
                
                
                
//            	var shareCome = "true";
//            	var other = "true";
//            	var isDecode= "false";
//            	var drawCoupon="true";
//                if(shareCome == "true"){
//            		//点击分享链接进入
//                	_this.$pagehome1.hide();
//                	if(other == "true"){
//                		//他人
//                		if(drawCoupon == "true"){
//                    		//抢到券
//                			var quan=sessionStorage.getItem('quan');
//                			
//                			if(quan=="true"){
//                				if (amGloble.config.debug == true) alert("[quan]" + quan);
//                				_this.gotoquanpage();
//                			}else{
//                				_this.decode();
//                			}
//                			             			
//                    	}else{
//                    		//来晚了 c的流程
//                    		
//                    		//c重玩后点击发射后url上加tag跳转到分享页
//                    		var asked=sessionStorage.getItem('asked');
//                            if(asked=="true"){
//                            	$(".pageshare").show();
//                            	$(".pageuncode").show();
//                            }else{
//                            	//来晚了
//                            	_this.$pageotherlove.show();
//                            }
//                            sessionStorage.setItem("asked","");
//                    		
//                    		
//                    	}
//                		
//                	}else{
//                		//自己
//                		if(isDecode == "true"){
//                    		//已解码
//                			_this.$pagedecoded.show();
//                    	}else{
//                    		//未解码
//                    		_this.$pageuncode.show();
//                    	}
//                		
//                		
//                	}
//            	}else{
//            		//点击活动链接进入
//            		
//            		//A点击发射后url上加tag跳转到分享页
//            		var asked=sessionStorage.getItem('asked');
//                    if(asked=="true"){
//                    	$(".pageshare").show();
//                    	$(".pageuncode").show();
//                    	$(".pagehome1").hide();
//                    	sessionStorage.setItem("asked","");
//                    }else{
//                    	//不是点击发射链接进入  继续往下走
//                    	
//                    	//A点击优惠券  进入A解码页
//                        if(sessionStorage.getItem('promoCode2')){
//                        	if(sessionStorage.getItem('isSender')=="true"){
//                        		//发送者
//                            	_this.$pagehome1.hide();
//                            	_this.$pagedecoded.show();
//                        	}else{
//                        		//受邀者
//                        		_this.$pagehome1.hide();
//                        		var option=sessionStorage.getItem("loveOption2");
//                            	var content=sessionStorage.getItem("loveContent2");
//                            	if (amGloble.config.debug == true) alert(" ===解码信息option2&content2=== \n"+option+"  " +content);
//                            	if(option!=null&&content==""){
//                            		if(option==1){
//                                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_example2.png");
//                                	}else if(option==2){
//                                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_face_cn.png");
//                                	}else if(option==3){
//                                		$(".pagedecode .font_example").attr("src","../../images/wxdgb1508/font_ring_cn.png");
//                                	}
//                            		setTimeout(function(){	
//                            			_this.$pagedecode.show();
//                                		
//                                    },1000);
//                            		
//                            	}
//                            	if(option==""){
//                            		//url传进来的lovecontent如果是中文会出现乱码 这里重新请求decode接口获取lovecontent   不行 点击优惠券进来没有tag
//                            		//只显示文字
//                            		$(".pagedecode2 .fontsdiv3").text(content);
//                            		setTimeout(function(){	
//                            			_this.$pagedecode2.show();
//                                		
//                                    },1000);
//                            		
//                            	}
//                        	}
//                                      	
//                        }else{
//                        	//不是点击优惠券 继续往下走     这边做是不是第一次进入活动的判断
//                        }
//                    	
//                    }                   
//            	}
            	

      
                
            });
        },
        
        bindWChatAPI : function(){
     	   var jsApiList = ['showOptionMenu']; 
 		   construct(amGloble.config.appid,  amGloble.config.selfRef + "pages/bnbjc1508/index.html", jsApiList);
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
        }
    };
    
    controller.init();
});