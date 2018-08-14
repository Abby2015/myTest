$(function(){
	var controller = {
			
			init: function() {
//				alert("===mobile.js=== \n" + JSON.stringify(location));
				var _this = this;
				
				this.touch4back = $(".back_btn");
				
				this.touch4back.bind("click",function(){
					location.href= amGloble.config.selfRoot + "pages/dbwy1506/boy.html?foid="+localStorage.getItem("openid")+"&tag="+sessionStorage.getItem("tag")+"&normalShare=1";
				});
			}
	};
	controller.init();
});