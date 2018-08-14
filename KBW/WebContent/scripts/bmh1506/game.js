var Game = {
	initParams : function () {
		Game.Target = {
			startX : 0,	
			shakeTime:200,
			movedDuration : 1,	//篮子移动时长	
			moveStartTime:0//目标每次移动的开始时间
		};	
		Game.Clock = {
			updateTimer : null,
			timeInfo :[0,0]
		};
		Game.Ball = {
			originPosition:{x:0,y:0},	
			startPosition:{},
			movePosition:{},
			timer : 0,	//球计数器	
			total:10,	//总球数	
			speed:200,
			duration:0,
			radian:0,
			distance:0,
			deg:0,
			success : false,
			direction:'left',
			levelUnit : 0,
		};

		Game.Result = {
			score:0,
			time:0,
			flag:0
		};
		
		this.setUnit1();
	},
	initTarget : function () {
		Game.Target.startX = $('.page3 .ice').offset().left;
		//动态生成动画
		var targetMoveToleft = window.innerWidth - $('.page3 .ice').width();
		targetAnimation = new JS2CSSKeyframes('targetAnimation',{
	       from:'transform:translate(0px,0px);',
	       to:'transform:translate('+targetMoveToleft+'px,0px);'
	    });
		
		$('.page3 .ice').css({
			'-webkit-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
			'-moz-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
			'-o-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
			'-ms-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
			'animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite'
		});
		$('.page3 .ice').unbind('touchstart').unbind('touchmove').unbind('touchend');
		$('.page3 .ice').bind('touchstart',function(e){
			event.preventDefault();
		}).bind('touchmove',function(e){
			event.preventDefault();
		}).bind('touchend',function(e){
			event.preventDefault();
		});
	},
	initClock : function () {
		//计时器
		Game.Clock.updateTimer = setInterval(function(){
			Game.Clock.timeInfo[1]++;
			if (Game.Clock.timeInfo[1]>9) {
				Game.Clock.timeInfo[0]++;
				Game.Clock.timeInfo[1] = 0;
			}	
			$('.page3 .time_button>span').text(Game.Clock.timeInfo.join(':'));
		}, 100);  //1s更新一次
	},
	initBallAndBow : function () {		
		var startX = startY = endTouchPageX = endTouchPageY = 0;
		var lineLength = $('.ballmain .r').height(), ballWidth = $('.page3 .ballmain .ball').width();
		Game.Ball.originPosition.x = $('.ballmain .l').height();
		Game.Ball.originPosition.top = $('.ballmain .l').offset().top;
		//console.log(Game.Ball.originPosition.top);
		//var startTime = 0;
		$('.page3 .slingshot').unbind('touchstart').unbind('touchmove').unbind('touchend');		
		$('.page3 .slingshot').bind('touchstart',function(e){
			event.preventDefault();
			Game.Ball.startPosition.x=startX=event.touches[0].pageX;
			Game.Ball.startPosition.y=startY=event.touches[0].pageY;
			/*var d = new Date();
			startTime = d.getTime();*/
			$('.page3 .hand').hide();
		}).bind('touchmove',function(e){
			event.preventDefault();
			
			endTouchPageX = event.touches[0].pageX;
			endTouchPageY = parseInt(event.touches[0].pageY);
			if (endTouchPageY > startY) {
				Game.Ball.movePosition.x=endTouchPageX - startX + Game.Ball.originPosition.x ;
				Game.Ball.movePosition.y=endTouchPageY - startY + Game.Ball.originPosition.y ;
				Game.Ball.movePosition.rotateL=Math.atan2(Game.Ball.movePosition.y,Game.Ball.movePosition.x)*180/Math.PI-90;
				Game.Ball.movePosition.rotateR=(Math.atan2(Game.Ball.movePosition.y,2*lineLength-Game.Ball.movePosition.x)*180/Math.PI-90)*-1;
				
				$('.page3 .ballmain .ball').css('transform','translate('+(Game.Ball.movePosition.x)+'px,'+Game.Ball.movePosition.y+'px)');
				$('.ballmain .l').css({
					'height':Math.sqrt(Math.pow(Game.Ball.movePosition.x,2)+Math.pow(Game.Ball.movePosition.y,2))-5,
					'transform':'rotate('+Game.Ball.movePosition.rotateL+'deg)',
					'-webkit-transform':'rotate('+Game.Ball.movePosition.rotateL+'deg)',
					'-moz-transform':'rotate('+Game.Ball.movePosition.rotateL+'deg)',
					'-o-transform':'rotate('+Game.Ball.movePosition.rotateL+'deg)',
					'-ms-transform':'rotate('+Game.Ball.movePosition.rotateL+'deg)'
				});
				$('.ballmain .r').css({
					'height':Math.sqrt(Math.pow(2*lineLength-Game.Ball.movePosition.x,2)+Math.pow(Game.Ball.movePosition.y,2))-5,
					'transform':'rotate('+Game.Ball.movePosition.rotateR+'deg)',
					'-webkit-transform':'rotate('+Game.Ball.movePosition.rotateR+'deg)',
					'-moz-transform':'rotate('+Game.Ball.movePosition.rotateR+'deg)',
					'-o-transform':'rotate('+Game.Ball.movePosition.rotateR+'deg)',
					'-ms-transform':'rotate('+Game.Ball.movePosition.rotateR+'deg)'
				});
			}
		}).bind('touchend',function(e){
			event.preventDefault();
			if ((endTouchPageX != 0 || endTouchPageY != 0) && (endTouchPageY > startY)) {//在爆米花以下方向才能正常发射
				Game.drawDirection(startX,startY,endTouchPageX,endTouchPageY);
				//Game.forcastResultByTime(startX, endTouchPageX,endTouchPageY);
				Game.forcastResult(startX, startY, endTouchPageX,endTouchPageY, lineLength);
				Game.updateStatus();
			} else {
				$('.page3 .hand').show();
			}
		});
	},
	init: function () {
		this.initParams();
		this.initTarget();
		this.initClock();
		this.initBallAndBow();
	},
	setUnit : function () {
		var deviceHeight = window.innerHeight, levelUnit = speedUnit = timeUnit = 0;
		if (deviceHeight <= 480) {	//iphone 4  320*480
			levelUnit = 40;
			speedUnit = 20;
			timeUnit = 10;
		} else if (deviceHeight <= 568) {	//iphone 5  320*568
			levelUnit = 30;
			speedUnit = 35;
			timeUnit = 20;
		} else if (deviceHeight <= 667)  {	//iphone 6  375*667
			levelUnit = 25;
			speedUnit = 45;
			timeUnit = 30;
		} else if (deviceHeight <= 736) {	//iphone 6 plus  414*716
			levelUnit = 20;
			speedUnit = 50;
			timeUnit = 40;
		}
		Game.Ball.levelUnit = levelUnit;
		Game.Ball.speedUnit = speedUnit;
		Game.Ball.timeUnit = timeUnit;
		//console.log('levelUnit='+Game.Ball.levelUnit+','+'speedUnit='+Game.Ball.speedUnit+','+'timeUnit='+Game.Ball.timeUnit);
	},
	setUnit1 : function () {
		Game.Ball.levelUnit = Math.ceil(window.innerHeight/10);
		Game.Ball.speedUnit = (window.innerHeight - $('.page3 .ice').offset().top)/6;
		Game.Ball.timeUnit = Math.floor($('.page3 .ballmain').height()/8);
		//console.log('levelUnit='+Game.Ball.levelUnit+','+'speedUnit='+Game.Ball.speedUnit+','+'timeUnit='+Game.Ball.timeUnit);
	},
	setBallSpeed : function(startX,startY,endX,endY){
		var d = Math.sqrt(Math.pow(endX-startX, 2) + Math.pow(endY-startY, 2));
		var level = parseInt(d/Game.Ball.levelUnit);	//档位
		if ( level > 8) {
			level = 8;
		} else if (level < 1) {
			level = 1;
		}
		Game.Ball.speed = level * Game.Ball.speedUnit;	
	},
	setBallFlyingTime : function (startX,startY,endX,endY) {
		Game.Ball.duration = parseInt(Math.abs(endY - startY)/Game.Ball.timeUnit);
		
		//console.log('speed='+Game.Ball.speed+','+'duration='+Game.Ball.duration);
	},
	drawDirection : function (startX,startY,endX,endY) {
		//获取角度；
		var radian = Math.atan(Math.abs(startY-endY)/Math.abs(startX-endX));
		var deg = Math.round(radian * (180/Math.PI));
		var direction = 'left';
		if (endX > startX ) {//向左
			direction = 'left';
		} else if (endX == startX) {	//向下
			direction = 'up';
		} else {	//向右
			direction = 'right';
		}
		Game.Ball['direction'] = direction;
		Game.Ball['radian'] = radian;
		Game.Ball['deg'] = deg;
		
		//根据拉升的距离判断速度；根据拉升的垂直距离判断时间；
		this.setBallSpeed(startX, startY, endX, endY);
		this.setBallFlyingTime(startX, startY, endX, endY);		
	},
	forcastResult : function (startX, startY, endTouchPageX,endTouchPageY, lineLength) {
		var speed = Game.Ball.speed, flyDuration = Game.Ball.duration, newBallX = newBallY = 0 ;
		Game.Ball.distance = flyDuration * speed;
		var moveToLeft = Game.Ball.distance * Math.cos(Game.Ball['radian']),
			moveToTop = - (Game.Ball.distance * Math.sin(Game.Ball['radian'])),		//计算子弹圆心
			ballCenterX = endTouchPageX + Math.round($('.ballmain .ball').width()/2),
			ballCenterY = endTouchPageY + Math.round($('.ballmain .ball').width()/2);
		if (Game.Ball.direction == 'left') {
			moveToLeft = -moveToLeft;
		} 
		newBallX = ballCenterX + moveToLeft;
		newBallY = ballCenterY + moveToTop;
		
		//判断是否
		if (newBallX>$('.page3 .target').offset().left && newBallX < $('.page3 .target').offset().left + $('.page3 .target').width()
				&& newBallY > $('.page3 .target').offset().top && newBallY < $('.page3 .target').offset().top + $('.page3 .target').height()
		) {
			Game.Ball.success = true;			
		} else {
			Game.Ball.success = false;
		}
		this.ballFlying(moveToLeft, moveToTop, endTouchPageX, endTouchPageY, startX, startY, lineLength);
	},
	shakeTarget: function(){
		//获取当前坐标
		var successX = $('.page3 .ice').offset().left;
		//取消当前动画
		$('.page3 .ice').removeAttr('style');
		 
		//切换为新的动画
		$('.page3 .ice').css({
		  'left':successX+'px',	
		  '-webkit-animation' : 'shake-opacity 1s linear alternate 1',
	      '-moz-animation' : 'shake-opacity 1s linear alternate 1',
	      '-o-animation' : 'shake-opacity 1s linear alternate 1',
	      '-ms-animation' : 'shake-opacity 10s linear alternate 1',
	      'animation' : 'shake-opacity 1s linear alternate 1'
		});
		
		setTimeout(function(){
			var d = new Date();
			Game.Target.moveStartTime =  d.getTime();
			$('.page3 .ice').css({
				'left' : Game.Target.startX + 'px',
				'-webkit-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
				'-moz-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
				'-o-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
				'-ms-animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite',
				'animation' : 'targetAnimation '+Game.Target.movedDuration+'s linear alternate infinite'
			});
		},1000);
	},
	ballFlying : function (moveToLeft, moveToTop, endTouchPageX, endTouchPageY, startX, startY, lineLength) {
		//动态生成动画
		new JS2CSSKeyframes('fly',{
	       from:'transform:translate(0px,0px);',
	       to:'transform:translate('+moveToLeft+'px,'+moveToTop+'px);'
	    });
		var width = $('.ballmain .ball').width();
		$('.ballmain .ball').hide();
		$('.flyball').show().css({
			'left':(endTouchPageX - width/2) + 'px',
			'top':(endTouchPageY - width/2)+'px',
			'-webkit-animation' : 'fly 1300ms linear normal 1',
			'-moz-animation' : 'fly 1300ms linear normal 1',
			'-o-animation' : 'fly 1300ms linear normal 1',
			'-ms-animation' : 'fly 1300ms linear normal 1',
			'animation' : 'fly 1300ms linear normal 1',
		});
		Game.resetBow(startX, startY, lineLength);
		setTimeout(function(){
			if (Game.Ball.success == true) {
				Game.shakeTarget();
			}
			Game.resetBall();
		},1300);
	},
	
	resetBow : function (startX, startY, lineLength) {
		//$('.ballmain .l,.ballmain .r, .ballmain .ball').removeAttr('style');
		//$('.ballmain .l,.ballmain .r').removeAttr('style');
		Game.Ball.bowMovePosition = {};
		var bowTimer = null, ballWidth = $('.page3 .ballmain .ball').width();
		//1秒内收弓 动态旋转
		bowTimer = setInterval(function(){
			//$('.flyball').offset().top;
			//console.log('left='+$('.flyball').offset().left+',top='+$('.flyball').offset().top);
			
			var endTouchPageX = $('.flyball').offset().left + ballWidth/2 , endTouchPageY =  $('.flyball').offset().top + ballWidth ;
			//console.log('startX='+startX+',startY='+startY);
			//console.log('left='+$('.flyball').offset().left+',top='+$('.flyball').offset().top);
			//console.log('endTouchPageX='+endTouchPageX+',endTouchPageY='+endTouchPageY);
			
			Game.Ball.bowMovePosition.x=endTouchPageX - startX + Game.Ball.originPosition.x ;
			Game.Ball.bowMovePosition.y=endTouchPageY - startY + Game.Ball.originPosition.y ;
			Game.Ball.bowMovePosition.rotateL=Math.atan2(Game.Ball.bowMovePosition.y,Game.Ball.bowMovePosition.x)*180/Math.PI-90;
			Game.Ball.bowMovePosition.rotateR=(Math.atan2(Game.Ball.bowMovePosition.y,2*lineLength-Game.Ball.bowMovePosition.x)*180/Math.PI-90)*-1;
			
			$('.ballmain .l').css({
				'height':Math.sqrt(Math.pow(Game.Ball.bowMovePosition.x,2)+Math.pow(Game.Ball.bowMovePosition.y,2))-2,
				'transform':'rotate('+Game.Ball.bowMovePosition.rotateL+'deg)',
				'-webkit-transform':'rotate('+Game.Ball.bowMovePosition.rotateL+'deg)',
				'-moz-transform':'rotate('+Game.Ball.bowMovePosition.rotateL+'deg)',
				'-o-transform':'rotate('+Game.Ball.bowMovePosition.rotateL+'deg)',
				'-ms-transform':'rotate('+Game.Ball.bowMovePosition.rotateL+'deg)'
			});
			$('.ballmain .r').css({
				'height':Math.sqrt(Math.pow(2*lineLength-Game.Ball.bowMovePosition.x,2)+Math.pow(Game.Ball.bowMovePosition.y,2))-2,
				'transform':'rotate('+Game.Ball.bowMovePosition.rotateR+'deg)',
				'-webkit-transform':'rotate('+Game.Ball.bowMovePosition.rotateR+'deg)',
				'-moz-transform':'rotate('+Game.Ball.bowMovePosition.rotateR+'deg)',
				'-o-transform':'rotate('+Game.Ball.bowMovePosition.rotateR+'deg)',
				'-ms-transform':'rotate('+Game.Ball.bowMovePosition.rotateR+'deg)'
			});
			
			if ($('.flyball').offset().top <= Game.Ball.originPosition.top) {
				clearInterval(bowTimer);
				$('.ballmain .l,.ballmain .r').removeAttr('style');
			}
		},10);
	},
	resetBall : function () {
		$('.ballmain .ball').removeAttr('style');
		Game.Ball.startPosition = Game.Ball.movePosition = {};
		$('.page3 .hand').show();
		$('.flyball').removeAttr('style');
	},
	updateStatus : function () {
		if (Game.Ball.success == true) {	//击中
			$('.page3 .number').text().replace('x','');
			var num = parseInt($('.page3 .number').text().replace('x',''))+1;
			$('.page3 .number').text('x'+num);
		}
		$('.page3 .bar').children('.icon').eq(Game.Ball.timer++).hide();
		if (Game.Ball.timer == 10) {	//最后一个球,结束飞行后，然后跳转
			setTimeout(function(){
				Game.end();
			},1000);
		}
	},
	end: function () {	//游戏结束
		var successNum = parseInt($('.page3 .number').text().replace('x',''));
		Game.Result.score = successNum;
		localStorage.setItem("gameScore", successNum);
		
		//调整流程后伪装
		successNum = 9;
		
		var spendTime = Game.Clock.timeInfo[0];//游戏耗费时间
		//调用接口跳转页面---api/bmh1506/ask
		var opt = {
			sid:	Bmh.getSid(),
			channelType: Bmh.getChannelType(),
			userId: Bmh.getOpenid(),
			deviceType: Bmh.getDeviceType(),
			hitCount: successNum
    	};
    	
		if (amGloble.config.debug == true) alert("[-a02- input]"+JSON.stringify(opt));
    	amGloble.api.a02.post(opt, function (ret) {
    		if (amGloble.config.debug == true) alert("[-a02- return]" + JSON.stringify(ret));
    		if (ret.content.errCode != 0 || ret.content.data == null) {
    			if (amGloble.config.debug == true)	alert(JSON.stringify(ret));
    		} else {
				var gameResult = ret.content.data.result;
				var pageIndex = 7;
				Game.Result.flag = gameResult;
				if (gameResult == 0) {
					pageIndex = 6;	//有券
					sessionStorage.setItem("promoCode", ret.content.data.promoCode);
					localStorage.setItem("localPromoCode", ret.content.data.promoCode);
					//$('title').html('我用'+spendTime+'s制造了'+successNum+'次邂逅！半价畅享夏日甜蜜！你也可以哦！');
				} else if (gameResult == 1) {
					pageIndex = 7;	//没券
					sessionStorage.setItem("promoCode", "");
					//$('title').html('我用'+spendTime+'s制造了'+successNum+'次邂逅！半价畅享夏日甜蜜！你也可以哦！');
				} else {
					pageIndex = 8;	//失败
					sessionStorage.setItem("promoCode", "");
					//$('title').html('制造甜蜜邂逅，分分钟体验半价新享法。骚年！等你来哦！');
				}
				//调整文案
				if (localStorage.getItem("gameScore")>=3) {
					$('title').html('我用'+spendTime+'s制造了'+Game.Result.score+'次邂逅！半价畅享夏日甜蜜！你也可以哦！');
				} else {
					$('title').html('制造甜蜜邂逅，分分钟体验半价新享法。骚年！等你来哦！');
				}
				Bmh.goToPage(pageIndex);
    		}
	    }, "application/json; charset=utf-8");
		//初始化
		this.reset();
	},
	reset : function(){	//清理游戏的所有信息
		//计时器清零并初始化
		if (Game.Clock.updateTimer) clearInterval(Game.Clock.updateTimer);
		Game.Clock.timeInfo = [0,0];
		$('.page3 .time_button>span').text(Game.Clock.timeInfo.join(':'));
		//重新添加子弹
		$('.page3 .bar .icon').show();
		$('.page3 .number').text('x0');
		
		Game.Ball.timer = 0;
	}
};