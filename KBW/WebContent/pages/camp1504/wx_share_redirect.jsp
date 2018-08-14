<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<% 
String base_ctx = request.getContextPath();
session.getServletContext().setAttribute("base_ctx", base_ctx);
%>
<html>
<head>
    <title>肯德基“鸡”密大放送，不看后悔</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport"content="initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
    <meta name="format-detection" content="telephone=no" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
</head>
<body>
	<div id='wx_logo' style='margin:0 auto;display:none;'>
		<img src='${base_ctx}/images/camp1504/linkPic.jpg' />
	</div>
	<div id="base_ctx" style="display: none">${base_ctx }</div>
	
	<a href="#" style="display:none;">服务器忙，请稍后重试~</a>
	<script type="text/javascript" src="${base_ctx}/scripts/camp1504/jquery-1.8.0.min.js" defer></script>
	<script type="text/javascript" src="${base_ctx}/scripts/camp1504/amWidget.js?v=1" defer></script>
	<script type="text/javascript" src="${base_ctx}/scripts/camp1504/apiRemote.js?v=1" defer></script>
	<script type="text/javascript" src="${base_ctx}/scripts/camp1504/wx_share_redirect.js?v=2.5" defer></script>
</body>
</html>