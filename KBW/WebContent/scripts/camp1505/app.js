var browser = {
    versions: function() {
        var u = navigator.userAgent, app = navigator.appVersion;
        return {//移动终端浏览器版本信息
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/) || !!u.match(/AppleWebKit/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
            iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
        };
    }(),
    language: (navigator.browserLanguage || navigator.language).toLowerCase()
}

if (browser.versions.ios || browser.versions.iPhone || browser.versions.iPad) {
    var  window_width = window.innerWidth,
         window_height = window.innerHeight;
}
else if (browser.versions.android) {
    var  window_width = $(window).width(),
        window_height = $(window).height();
}
var design_width = 750,
    design_height = 1206,
    //window_width = window.innerWidth,
    //window_height = window.innerHeight,
    percent_w,
    percent_h,
    $coverPage,
    imgArr,
    id;
var bool = true;
var music = document.getElementById("audioplay");
function fullpageScroll(){
    $('#fullpage').fullpage({
        'verticalCentered': false,
        'css3': true,
        anchors: ['s1', 's2', 's3', 's4', 's5', 's6', 's7', 's8'],
        'navigation': false,
        touchSensitivity:15,
        normalScrollElementTouchThreshold:1,
        afterLoad:function(anchors,index){
            switch (index){
                case 1:{
                    break;
                }
                case 2:{
                    var $screen_2 = $('#fullpage .screen-2');
                    if($screen_2.hasClass('off')){
                        break;
                    }else{
                        _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow2',,,false]);
                        $screen_2.addClass('off');
                        $screen_2.find('.paopao').each(function(){
                            var randomNum = Math.floor(Math.random()*6 + 1);
                            var randomHeight = Math.floor(Math.random()*9 + 1);
                            $(this).css({
                                transition : 'bottom 13s',
                                bottom : randomHeight+'0%',
                                opacity : '0.'+randomHeight
                            })
                        });
                        $screen_2.find('.title').fadeIn(1500,function(){
                            $('.slogan,.slogan-2').fadeIn(2000,function(){
                                $('.slogan,.slogan-2').fadeOut(900,function(){
                                    $('.screen-2').find('.slogan-3,.slogan-4').fadeIn(1500,function(){
                                        $('.screen-2').find('.bottom-icon').fadeIn();
                                    })
                                })
                            })
                        });
                    }
                    break;
                }
                case 3:{
                    var $screen_3 = $('#fullpage .screen-3');
                    if($screen_3.hasClass('off')){
                        break;
                    }else{
                        _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow3',,,false]);
                        $screen_3.addClass('off');
                        $screen_3.find('.title,.detail').fadeIn(1500);
                        $screen_3.find('.product').css({
                            width:765/design_height*$coverPage.width()
                        });
                        if($screen_3.find('.first-slick').hasClass('on')){
                            $screen_3.find('.first-slick').fadeIn(1500);
                            return;
                        }else{
                            $screen_3.find('.first-slick').addClass('on');
                            $('.first-slick').fadeIn(1500,function(){
                                $screen_3.find('.bottom-icon').fadeIn();
                            }).slick({
                                dots: true,
                                infinite: false,
                                speed: 500,
                                cssEase: 'linear',
                                arrows: false
                            });
                            $('.first-slick').on('afterChange', function(event){
                                $('#fullpage .screen-3 ul.detail li').eq(event.currentTarget.slick.currentSlide).fadeIn().siblings()
                                    .hide();
                            });
                        }
                    }
                    break;
                }
                case 4:{
                    var $screen_4 = $('#fullpage .screen-4');
                    if($screen_4.hasClass('off')){
                        break;
                    }else{
                        _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow4',,,false]);
                        $screen_4.addClass('off');
                        $screen_4.find('.title').delay(3000).fadeIn(3500,function(){
                            $(this).fadeOut(2000,function(){
                                $screen_4.find('.title-2').fadeIn(3000,function(){
                                    $screen_4.find('.bottom-icon').fadeIn();
                                });
                                $screen_4.find('.my-slick').fadeIn(2500).slick({
                                    centerMode: true,
                                    centerPadding: '22%',
                                    slidesToShow: 1,
                                    arrows:false,
                                    dots: true
                                });
                            });
                            $screen_4.find('.left-hand').css({
                                transition:'left 5s linear',
                                'left':'-80%'
                            });
                            $screen_4.find('.right-hand').css({
                                transition:'right 5s linear',
                                'right':'-80%'
                            });
                        });
                        $screen_4.find('.left-hand').css({
                            transition:'left 5s linear',
                            'left':0,
                            'display':'block'
                        });
                        $screen_4.find('.right-hand').css({
                            transition:'right 5s linear',
                            'right':0,
                            'display':'block'
                        });
                    }
                    break;
                }
                case 5:{
                    var $screen_5 = $('#fullpage .screen-5');
                    if($screen_5.hasClass('off')){
                        break;
                    }else{
                        _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow5',,,false]);
                        $screen_5.addClass('off');
                        $screen_5.find('a').css({
                            'display':'block'
                        });
                        $screen_5.find('.bottom-icon').css({
                            'margin-left':-$screen_5.find('.bottom-icon').width()/2
                        }).fadeIn();
                    }
                    break;
                }
                case 6:{
                    var $screen_6 = $('#fullpage .screen-6');
                    if($screen_6.hasClass('off')){
                        break;
                    }else{
                        _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow6',,,false]);
                        $screen_6.addClass('off');
                        $screen_6.find('.title').fadeIn(3500);
                        $screen_6.find('.my-slick-2').fadeIn(3500,function(){
                            $(this).next().fadeIn();
                        }).slick({
                            dots: true,
                            infinite: true,
                            arrows: false
                        });
                    }
                    break;
                }
                case 7:{
                    var $screen_7 = $('#fullpage .screen-7');
                    if($screen_7.hasClass('off')){
                        break;
                    }else{
                        $screen_7.addClass('off');
                        setTimeout(showocean,10)
                        $('.screen-7').find('.title').fadeIn(1500,function(){
                            $(this).next().fadeIn(1500,function(){
                                $('.screen-7').find('.title').fadeOut(1000,function(){
                                    $('.screen-7').find('.title-2').fadeIn(1500,function(){
                                        $(this).next().fadeIn(1500,function(){
                                            $(this).off('click').on('click',function(){
                                                _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobShare',,,false]);
                                                $('#share-bg').fadeIn(function(){
                                                    $(this).off('click').on('click',function(){
                                                        $(this).fadeOut()
                                                    })
                                                })
                                            })
                                        })
                                    })
                                })
                            })
                        })
                    }
                    break;
                }
            }
        }
    })
}
function showocean(){
    var canvas = document.getElementById('myCanvas');
    var num = 0;
    var w = canvas.width = window.innerWidth;
    var h = canvas.height = window.innerHeight;
    var ctx = canvas.getContext('2d');
    var img = new Image();
    function changeImg(){
        if(num >= 41){
            return;
        }
        num++;
        img.src = 'firstImg/'+num+'.jpg';
        ctx.drawImage(img,0,0,w,h);
        setTimeout(changeImg,150);
    }
    changeImg();
}
function photo(){
    canvas = document.getElementById("photoCanvas");
    window.stage = new createjs.Stage(canvas);
    createjs.Touch.enable(stage);
    stage.enableMouseOver(10);
    stage.mouseMoveOutside = true;
    createjs.Ticker.setFPS(24);
    createjs.Ticker.on("tick", tick);
    function tick() {
        stage.update();
    }

    var cameraPhoto = new createjs.Container();

    stage.addChild(cameraPhoto);

    var defW,defH,defS,defStyle;

    var testCameraPhoto =function(){
        var scale = cameraPhoto.scaleY;
        var y_test = cameraPhoto.y;
        var x_test = cameraPhoto.x;

        var dx = defW*scale/2;
        var dy = defH*scale/2;

        var tx = 400-(dx-400);
        var ty = 400-(dy-400);

        if(x_test < tx){
            cameraPhoto.x = tx;
        }else if(x_test > dx){
            cameraPhoto.x = dx;
        }

        if(y_test < ty){
            cameraPhoto.y = ty;
        }else if(y_test > dy){
            cameraPhoto.y = dy;
        }

        // $("#cameraTestInput").html("dx:"+dx+"<br>dy:"+dy);
    }

    var addScaleAction = function(bmp){

        var scale = 0.5;
        var defscale = 0.5;
        var isgesture = 0;

        cameraPhoto.on("mousedown", function(evt) {
            if(isgesture == 0){
                this.offset = {x:this.x-evt.stageX, y:this.y-evt.stageY};
            }
            scale = cameraPhoto.scaleY;
        });

        cameraPhoto.on("pressmove", function(evt) {

            if(isgesture == 0){
                this.x = evt.stageX+ this.offset.x;
                this.y = evt.stageY+ this.offset.y;
                testCameraPhoto();
                update = true;
            }
        });

        if(!isIphone){
            var mc = new Hammer(canvas,{touchAction: "pan-y"});
            mc.get('rotate').set({ enable: true });
            mc.on("pan swipe rotate pinch", function(ev) {
                isgesture = 1;
                if(ev.scale != 1){
                    s = ev.scale-1;
                    if(scale+s>=defS){
                        cameraPhoto.scaleX = cameraPhoto.scaleY = scale+s;
                    }else{
                        cameraPhoto.scaleX = cameraPhoto.scaleY = defS;
                    }
                }else{
                    isgesture = 0;
                    testCameraPhoto();
                }
            });
        }else{
            var gesturestart = function(event){
                isgesture = 1;
            };
            var gesturechange = function(event){
                s = event.scale-1;
                if(scale+s>=defS){
                    defscale = cameraPhoto.scaleX = cameraPhoto.scaleY = scale+s;
                }else{
                    defscale = cameraPhoto.scaleX = cameraPhoto.scaleY = defS;
                }
            };
            var gestureend = function(event){
                setTimeout(function(){
                    scale = defscale;
                    isgesture = 0;
                },10)
                testCameraPhoto();
            }
        }
        canvas.addEventListener("gesturestart", gesturestart, true);
        canvas.addEventListener("gesturechange", gesturechange, true);
        canvas.addEventListener("gestureend", gestureend, true);
    }

    window.addImg = function(bigUrl){
        cameraPhoto.removeAllChildren();
        var bmp = new createjs.Bitmap(bigUrl);
        bmp.name = "photoImg";
        var img = new Image();
        img.onload = function(){
            var w = img.width;
            var h = img.height;
            defW = w;
            defH = h;
            cameraPhoto.regX = w/2;
            cameraPhoto.regY = h/2;
            //alert(cameraPhoto.regX);
            if(w < h){
                cameraPhoto.y = h/2-(h-800)/2;
                cameraPhoto.x = w/2-(w-800)/2;
                defS = 800/w;

            }else{
                cameraPhoto.y = w/2-(w-800)/2;
                cameraPhoto.x = h/2-(h-800)/2;
                defS = 800/h;
            }
        }
        img.src = bigUrl;
        cameraPhoto.addChild(bmp);

        addScaleAction(bmp);
    }

    var createImg = function(imgURL,rot){

        var file = filePicture.files[0];
        var mpImg = new MegaPixImage(file);
        var sw = 1200, sh = 1200;
        var img = new Image();
        img.onload = function() {

            var w = img.width, h = img.height;

            if(rot < 5){
                y = sh;
                x = parseInt(sh/h*w);
                setTimeout(function(){
                    mpImg.render(resImg, { width: x, height: y, orientation: rot });
                },200)
            }else{
                y = sw;
                x = parseInt(sw/h*w);
                setTimeout(function(){
                    mpImg.render(resImg, { width: x, height: y, orientation: rot });
                },200)
            }
            $('#photoCanvas').show();
        }
        img.src = imgURL;
    }

    var filePicture = document.getElementById('takepicture');

    filePicture.onchange = function(e){

        var files = e.target.files,
            file;

        if (files && files.length > 0) {
            file = files[0];

            fr = new FileReader;

            fr.onloadend = function() {

                var exif = EXIF.readFromBinaryFile(new BinaryFile(this.result));
                var rot = exif.Orientation;

                try {
                    var URL = window.URL || window.webkitURL;
                    var imgURL = URL.createObjectURL(file);
                    createImg(imgURL,rot);
                    URL.revokeObjectURL(imgURL);
                }
                catch (e) {
                    try {
                        fileReader.onload = function (e) {
                            createImg(event.target.result,rot);
                        };
                        fileReader.readAsDataURL(file);
                    }
                    catch (e) {
                        var error = document.querySelector("#error");
                        if (error) {
                            error.innerHTML = "Neither createObjectURL or FileReader are supported";
                        }
                    }
                }

            };
            fr.readAsBinaryString(file);
        }
    }
}
function bottle(){
    _gaq.push(['_trackEvent','LAMERTmall2015409mob','2015409mobArrow1',,,false]);
    var $bottle = $('#fullpage .screen-1');
    $bottle.find('.bottle-top').fadeIn(function(){
        $(this).css({
            transition:'top 7s',
            top:'19%'
        })
    });
    $bottle.find('.bottle-bottom').fadeIn(function(){
        $(this).css({
            transition:'filter 7s',
            '-webkit-transition':'-webkit-filter 7s',
            '-moz-transition':'-moz-filter 7s',
            '-o-transition':'-o-filter 7s',
            filter:'brightness(1)',
            '-webkit-filter':'brightness(1)',
            '-moz-filter':'brightness(1)',
            '-o-filter':'brightness(1)'
        })
    });
    $bottle.find('.bottle-black').fadeIn(function(){
        $(this).css({
            bottom:'0'
        })
    });
    $bottle.find('.light-s').fadeIn(2000,function(){
        $(this).next('.light-m').fadeIn(3000,function(){
            $(this).next('.light-l').fadeIn(3000);
            $(this).nextAll('.title-s').fadeIn(2000,function(){
                $(this).fadeOut(2000,function(){
                    $(this).next().fadeIn(1500,function(){
                        $(this).next().fadeIn(1500)
                    });
                    $(this).nextAll('.title-b').fadeIn(3000,function(){
                        $('.screen-1 .title-b').prevAll().fadeOut(1000,function(){
                            $('.screen-1').find('.logo').fadeIn(2000,function(){
                                $(this).next().fadeIn(1500)
                            })
                        });
                    });
                })
            });
        })
    });
}
$(function(){
    var imgArr = [];
    for(var m=1;n <= 13; m++ ){
        imgArr.push('loadImg/'+m+'.jpg');
    }
    for(var n=1;n <= 41;n++){
        imgArr.push('firstImg/'+n+'.jpg');
        if(n==41){
            preloadimg(imgArr,function(){
                $('#loading').remove();
                $coverPage = $('#main');
                $coverPage.css({
                    width:window_height*design_width/design_height
                }).fadeIn(fullpageScroll);
                //Doomresize();
                bottle();
                function touches(ev) {
                    if (ev.touches.length == 1) {
                        var oDiv = document.getElementById('myaudio');
                        switch (ev.type) {
                            case 'touchstart':
                                document.getElementById('myaudio').play();
                                document.removeEventListener('touchstart', touches, false);
                                break;
                        }
                    }
                }
                document.addEventListener('touchstart', touches, false);
            })
        }
    }
});
function toggleSound() {
    var music = document.getElementById("myaudio");
    var toggle = document.getElementById("button_music");
    if (music.paused) {
        music.play();
        $('.music').addClass('music-move');
    } else {
        music.pause();
        $('.music').removeClass('music-move');
    }
}

