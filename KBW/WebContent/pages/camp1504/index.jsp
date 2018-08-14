<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String base_ctx = request.getContextPath();
	session.getServletContext().setAttribute("base_ctx", base_ctx);
%>
<!DOCTYPE html>
<html>
<head>
<title>肯德基“鸡”密大放送，不看后悔</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="format-detection" content="telephonAe=no" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<link rel="stylesheet" type="text/css" href="http://t.a.kfc.com.cn/brandkfc/styles/camp1504/am.css" />
<link rel="stylesheet" type="text/css" href="http://t.a.kfc.com.cn/brandkfc/styles/camp1504/common.css?v=1.3" />
<link rel="stylesheet" type="text/css" href="http://t.a.kfc.com.cn/brandkfc/styles/camp1504/fool.css?v=1.1" />
<link rel="stylesheet" type="text/css" href="http://t.a.kfc.com.cn/brandkfc/styles/camp1504/animate.css" />
<link rel="stylesheet" type="text/css" href="http://t.a.kfc.com.cn/brandkfc/styles/camp1504/style.css" />
<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/jquery-1.8.0.min.js"></script>

<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/amWidget.js?v=1.1"></script>
<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/apiRemote.js?v=1.1"></script>


<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/pageSwitch.js"></script>
<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/sdc_MobWeb.js?v=1.0"></script>

<script type="text/javascript">
	$(function() {
		$(".formText input").focus(function() {
			//$(this).next().hide();
			//alert("===开始输入手机号码===");
			$(this).css("background","none");
		});
		var titleArr = ["肯德基“鸡”密大放送，不看后悔",
		                "转疯了：那些不为人知的“鸡”密",
		                "如果你觉得愚人节很没劲，那这个故事一定要看",
		                "KFC的愚人节玩笑，你们感受一下"];
		var titleIndex = Math.floor(Math.random()*4);
		$("title").html(titleArr[titleIndex]);
	});
</script>

<script type="text/javascript" src="http://t.a.kfc.com.cn/brandkfc/scripts/camp1504/april_index.js?v=2.5"></script>



</head>
<body>
	<div id="base_ctx" style="display: none">${base_ctx }</div>

	<div  class="catoon1" style="display: none">
			<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
        <div id="pages1">
            <div class="page page1_1">
            	
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV1.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV2.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV3.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV4.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV5.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/fuguV6.jpg" alt="" />
            </div>
            <div class="page page18">
            	<!--复古餐厅-选择靠谱,不靠谱 Begain-->
				<div class="am-page Cloud" style="display:block">
					<div class="am-header">
						<div class="logo"></div>
						<div class="help"></div>
					</div>
					<div class="am-body-wrap am-touchable">
						<div class="am-body-inner">
							<div class="chinaStyle">
								<div class="img">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/china.png" alt="厦门中国风" />
								</div>
								<div class="animated tada question_mark"></div>
							</div>
							<ul class="Button">
								<li class="bukaopu animated"></li>
								<li class="kaopu animated"></li>
							</ul>
							<div class="percentage">
								<dl class="clearfix tuotuo">
									<dt>
										<!-- <p>
											0<sup>%</sup>
										</p> -->
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/num.png" alt=""/>
									</dt>
									<dd>
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/tuotuo.png" alt="" />
									</dd>
								</dl>
								<dl class="clearfix cdz">
									<dt>
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/cdz.png" alt="" />
									</dt>
									<dd>
										<!-- <p>
											0<sup>%</sup>
										</p> -->
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/num1.png"alt=""/>
									</dd>
								</dl>
								<dl class="clearfix nochoose">
									<dt>
										<!-- <p>
											0<sup>%</sup>
										</p> -->
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/num2.png" alt=""/>
									</dt>
									<dd>
										<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/choose.png" alt="" />
									</dd>
								</dl>
								<div class="tips"></div>
							</div>
						</div>
					</div>
				</div>
				<!--复古餐厅-选择靠谱,不靠谱 End-->
            </div>
        </div>
    	<div class="arrowB" style="display: block"></div>
    </div>
    
	<div  class="catoon2" style="display: none">
            	<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
        <div id="pages2">
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV1.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV2.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV3.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV4.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV5.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijingV6.jpg" alt="" />
            </div>
            <div class="page page28">
            	<!--北京烤鸭-选择相信,不相信 Begain-->
			<div class="am-page Cloud kaoya" style="display:block">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="duckStyle">
							<div class="img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck.png" alt="北京烤鸭" />
							</div>
							<div class="animated tada question_mark"></div>
						</div>
						<ul class="Button">
							<li class="Donot_believeBtn animated"></li>
							<li class="believeBtn animated"></li>
						</ul>
						<div class="percentage">
							<dl class="clearfix duck_p">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijing50.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_p.png" alt="" />
								</dd>
							</dl>
							<dl class="clearfix duck_p1">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijing18.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_p1.png" alt="" />
								</dd>
							</dl>
							<dl class="clearfix duck_p2">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/beijing32.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_p2.png" alt="" />
								</dd>
							</dl>
							<div class="tip"></div>
						</div>
					</div>
				</div>
			</div>
			<!--北京烤鸭-选择相信,不相信 End-->
            </div>
        </div>
   		<div class="arrowB" style="display: block"></div>
    </div>
    
	<div  class="catoon3" style="display: none">
		<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
        <div id="pages3">
            <div class="page page1_1">
            	
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/newmenuV1.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/newmenuV2.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/newmenuV3.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/newmenuV4.jpg" alt="" />
            </div>
            <div class="page page1_1">
            	<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/newmenuV5.jpg" alt="" />
            </div>
            <div class="page page37">
            	<!--华丽变身-选择信,不信 Begain-->
			<div class="am-page Cloud newMenu" style="display:block">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="changeStyle">
							<div class="img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_believe.png" alt="华丽变身" />
							</div>
							<div class="animated tada question_mark"></div>
						</div>
						<ul class="Button">
							<li class="Donot_believeBtn1 animated"></li>
							<li class="believeBtn animated"></li>
						</ul>
						<div class="percentage">
							<dl class="clearfix change_p">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change70.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_p.png" alt="" />
								</dd>
							</dl>
							<dl class="clearfix change_p1">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_p1.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change25.png" alt="" />
								</dd>
							</dl>
							<dl class="clearfix change_p2">
								<dt>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change5.png" alt="" />
								</dt>
								<dd>
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_p2.png" alt="" />
								</dd>
							</dl>
							<div class="tip"></div>
						</div>
					</div>
				</div>
			</div>
			<!--华丽变身-选择信,不信 End-->
            </div>
        </div>
        <div class="arrowB" style="display: block"></div>
    </div>

	<div class="am-app all" style="display: none">
		
		<div class="fugu">
			<!--复古餐厅-看穿啦 Begain-->
			<div class="am-page yes">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="see_pic">
							<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/kfc.jpg" alt="被看穿啦" />
						</div>
						<div class="coinsbg">
							<div class="rewardBtn animated gelatine"></div>
						</div>
					</div>
				</div>
			</div>
			<!--复古餐厅-看穿啦 End-->

			<!--复古餐厅-别不信 Begain-->
			<div class="am-page no">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="see_pic">
							<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/kfc1.jpg" alt="别不信" />
						</div>
						<div class="unbelief">
							<div class="share_Btn animated gelatine"></div>
							<div class="refuse_Btn animated lightSpeedIn"></div>
						</div>
					</div>
				</div>
			</div>
			<!--复古餐厅-别不信 End-->
		</div>

		<div class="kaoya">
			<!--北京烤鸭-bingo答对啦 Begain-->
			<div class="am-page Cloud yes">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="bingo">
							<div class="duck_word">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/bingo.png" alt="bingo答对啦" />
							</div>
							<div class="duck_img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_bingo.png" alt="" />
							</div>
							<div class="duck_bj">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_word.png" alt="" />
							</div>
							<div class="duck_reward">
								<div class="rewardBtn animated gelatine"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--北京烤鸭-bingo答对啦 End-->

			<!--北京烤鸭-这是真的哟 Begain-->
			<div class="am-page Cloud no">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="bingo">
							<div class="duck_realy">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/realy.png" alt="bingo答对啦" />
							</div>
							<div class="duck_img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_bingo.png" alt="" />
							</div>
							<div class="duck_bj">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/duck_word1.png" alt="" />
							</div>
							<div class="share">
								<div class="share_Btn animated gelatine"></div>
								<div class="refuse_Btn animated lightSpeedIn"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--北京烤鸭-这是真的哟 End-->
		</div>

		<div class="newMenu">
			<!--华丽变身-美味翻新 Begain-->
			<div class="am-page yes">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="Morph">
							<div class="morph_word">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_yami.png" alt="美味翻新" />
							</div>
							<div class="morph_img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change.jpg" alt="" />
							</div>
							<div class="morph_bj">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_word.png" alt="" />
							</div>
							<div class="morph_reward">
								<div class="rewardBtn animated gelatine"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--华丽变身-美味翻新 End-->

			<!--华丽变身-答错了真可惜 Begain-->
			<div class="am-page Cloud no">
				<div class="am-header">
					<div class="logo"></div>
					<div class="help"></div>
				</div>
				<div class="am-body-wrap am-touchable">
					<div class="am-body-inner">
						<div class="Morph">
							<div class="morph_word">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_word1.png" alt="答错了真可惜" />
							</div>
							<div class="morph_img">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change.jpg" alt="" />
							</div>
							<div class="morph_bj">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/change_word2.png" alt="" />
							</div>
							<div class="share">
								<div class="share_Btn animated gelatine"></div>
								<div class="refuse_Btn animated lightSpeedIn"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--华丽变身-答错了真可惜 End-->
		</div>

		<!--优惠券第一张 Begain-->
		<div class="am-page Cloud First">
			<div class="am-header">
				<div class="logo"></div>
				<div class="help"></div>
			</div>
			<div class="am-body-wrap am-touchable">
				<div class="am-body-inner">
					<div class="area">
						<div class="set_meal">
							<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/taocan.jpg" alt="" />
							<div class="arrow"></div>
						</div>
						<div class="word">
							<div class="word_click animated rubberBand">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/clickme.png" />
							</div>
							<div class="word_double">
								<div class="double animated zoomInDown">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/double.png" alt="" />
								</div>
							</div>
						</div>
						<div class="click_share animated bounceIn">
							<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/clickshare.png" alt="" class="animated tada" />
						</div>
						<ul class="Button">
							<li class="once_againRed animated lightSpeedIn"></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!--优惠券第一张 End-->
		
		<!--奖励翻倍再来一题 Begain-->
		<div class="am-page Cloud Double">
			<div class="am-header">
				<div class="logo"></div>
				<div class="help"></div>
			</div>
			<div class="am-body-wrap am-touchable">
				<div class="am-body-inner">
					<div class="coupnTransforms">
						<div class="coupnTransform">
							<div class="magic magic_one">
								<div class="quan animated slideInDown">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/kfc_two.jpg" alt="" />
								</div>
								<div class="arrow_down animated arrowInDown"><img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/arrow.png" alt="" /></div>
								<p class="wordInDown animated">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/quan.png" alt="" />
								</p>
							</div>
							<div class="magic magic_Copy animated">
								<div class="quan">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/kfc_two1.jpg" alt="" />
								</div>
								<div class="arrow_down"><img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/arrow.png" alt="" /></div>
								<p class="animated">
									<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/quan_f.png" alt="" />
								</p>
							</div>
						</div>
						<div class="Says_to_do">
							<div class="todo">
								<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/happy.png" alt="" />
							</div>
							<div class="againBtn_red animated gelatine"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--奖励翻倍再来一题 End-->

		<!--送50话费 Begain-->
		<div class="am-page Cloud Bill">
			<div class="am-header">
				<div class="logo"></div>
				<div class="help"></div>
			</div>
			<div class="am-body-wrap am-touchable">
				<div class="am-body-inner">
					<div class="isYou">
						<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/foryou88.png" alt="就是你" />
					</div>
					<div class="formText">
						<input id="telNo" type="text"  name="" value="" placeholder="" /> 
						<!-- <span class="placeholder"></span> -->
					</div>
					<ul class="Button">
						<li class="submitBtn animated gelatine"></li>
					</ul>
				</div>
			</div>
		</div>
		<!--送50话费 End-->
		
		<!--确认手机号码 Begain-->
		<div class="am-page Cloud Bill2">
			<div class="am-header">
				<div class="logo"></div>
				<div class="help"></div>
			</div>
			<div class="am-body-wrap am-touchable">
				<div class="am-body-inner">
					<div class="isYou" style="font-weight: bold;color: #3F96BE;padding: 70px 0 25px;">
						请确认中奖手机号码,小肯会为你第一时间充值哦!
					</div>
					<div class="formText">
						<input id="telNo2" type="text" name="" value="" placeholder="" /> 
						<!-- <span class="placeholder"></span> -->
					</div>
					
					<div id="telTips" style="font-weight: bold;color: #3F96BE;padding: 0px 0 25px; display:none;text-align: center;" >
						填写手机号码成功!
					</div>
					<ul class="Button">
						<li class="submitBtn animated gelatine2"></li>
					</ul>
				</div>
			</div>
		</div>
		<!--确认手机号码 End-->
		
		
		<!--中奖手机号码 Begain-->
		<div class="am-page Cloud Bill3">
			<div class="am-header">
				<div class="logo"></div>
				<div class="help"></div>
			</div>
			<div class="am-body-wrap am-touchable">
				<div class="am-body-inner">
					<div id="showPhone" class="isYou" style="font-weight: bold;color: #3F96BE;padding: 70px 0 25px;">
						
					</div>
					<ul class="Button">
						<li class="rewardBtn animated gelatine3"></li>
					</ul>
				</div>
			</div>
		</div>
		<!--中奖手机号码  End-->
		
	</div>
	
	
	<!--蒙层弹窗 Begain-->
	<div class="overlay"></div>
	<div class="pop_up">
		<div class="content">
			<div class="close"></div>
			<div class="guys_xm">
				<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/guy.png" alt="" />
			</div>
			<div class="reward_more">
				<img src="http://t.a.kfc.com.cn/brandkfc/images/camp1504/want.png" alt="" />
			</div>
			<div class="friendBtn animated gelatine"></div>
			<div class="againBtn_blue animated lightSpeedIn"></div>
			<div class="coin_l"></div>
			<div class="coin_r">
				<div class="coin_r1"></div>
			</div>

		</div>
	</div>
	<!--蒙层弹窗 End-->

	<!--蒙层活动介绍 Begain-->
	<div class="Masking">
		<div class="logo"></div>
		<div class="popup">
			<div class="close"></div>
			<ul class="Button">
				<li class="activity_ruleBtn now"></li>
				<li class="activity_nameBtn"></li>
			</ul>
			<div class="container rule" style="display: block;font-family: 'hychengxingjregular';">
				<div class="count">
					<dl class="clearfix">
						<dt>活动时间：</dt>
						<dd class="blue">2015年4月1日-4月3日</dd>
					</dl>
					<dl class="clearfix">
						<dt>参与方式：</dt>
						<dd>
							活动期间，关注肯德基官方微信，进入“<span class="blue">愚乐‘鸡’秘知多少</span>”活动页面，浏览漫画故事并回答趣味问题，回答正确即可参与抽奖并获得奖品一份，奖品内容为以下二项其一，随机发放：
							<ul>
								<li>肯德基新品优惠券一张</li>
								<li>50元手机话费一份</li>
							</ul>
							注：同一微信账号不可重复获得手机话费奖品；同一微信账号每天最多抽奖3次。
						</dd>
					</dl>
					<dl class="clearfix">
						<dt>奖项设置：</dt>
						<dd>50元手机话费：每天100份，活动3天共300份。</dd>
					</dl>
					<dl class="clearfix">
						<dt>领奖须知：</dt>
						<dd>
							<ul>
								<li>手机话费的中奖名单将于活动结束后一周内公布在活动网站。</li>
								<li>手机话费的领取：主办方将根据中奖用户填写的手机号码，在活动结束后1个月内，为该手机号码充入50元手机话费。届时请中奖用户留意查看话费余额。中奖用户请务必按照页面提示正确填写手机号码，如因中奖者提供个人信息不全或有误，导致奖品无法递送，主办方为此不承担任何责任。</li>
								<li>本活动奖项一经确认不得转换、转让或折换现金。</li>
							</ul>
						</dd>
					</dl>
					<dl class="clearfix">
						<dt>其他：</dt>
						<dd>
							<ul>
								<li>本次活动的主办方为百胜餐饮集团中国事业部肯德基品牌（统一简称“主办方”）。</li>
								<li>主办方及其广告公司、网络合作伙伴的员工不可参加此次活动，以示公允。
									对于任何通过不正当手段参加活动者，主办方有权在不事先通知的前提下取消其参加活动及获奖资格。</li>
								<li>主办方不对因网络传输原因而导致参加者提交的信息错误承担任何责任。</li>
								<li>如遇不可抗力因素，本活动因故无法进行时，主办方在法律允许的范围内有权决定取消、终止、修改或暂停本活动。 </li>
							</ul>
						</dd>
					</dl>
				</div>
				<div class="arrow_tip"></div>
			</div>
			<div class="container winlist">
				<div class="count">
					<dl class="clearfix phone_num">
						<!-- <dt>敬请期待！</dt> -->
						<dt>中奖手机号码公示如下：</dt>
						<dd>
							<ul class="clearfix">
								<li>1376***2009</li>
								<li>1565***1181</li>
								<li>1346***9900</li>
								<li>1775***6545</li>
								<li>1366***4228</li>
								<li>1886***7239</li>
								<li>1563***7966</li>
								<li>1365***1228</li>
								<li>1881***9839</li>
								<li>1771***8545</li>
							</ul>
						</dd>
					</dl>
				</div>
				<div class="arrow_tip"></div>
			</div>
		</div>
	</div>
	<!--蒙层活动介绍 End-->

	<!--差一步分享浮层 Begain-->
	<div class="share_tip"><div class="logo"></div></div>
	<!--差一步分享浮层 End-->
	
	<!--差一步分享浮层 Begain-->
	<div class="share_tip2"><div class="logo"></div></div>
	<!--差一步分享浮层 End-->
	
	<!--show一下也是极好的 Begain-->
	<div class="share_showtip"><div class="logo"></div></div>
	<!--show一下也是极好的 End-->
	
	<!--图片展示弹出 Begain-->
	<div class="pop">
		<div class="coupons_popUpBox">
			<div class="close_popimg"></div>
			<div class="popUpBoxImg"><img src="" alt=""/></div>
		</div>
	</div>
	<!--图片展示弹出 End-->
</body>
</html>
