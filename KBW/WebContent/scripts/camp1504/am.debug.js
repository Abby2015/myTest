$(function () {
  window.$ && (window.$.am = {});
//window.$.am = am || {};
  $.am.debug = {
    enable: true,
    count:0,
	init : function() {
	    /*this.enable = (window.localStorage.getItem("kfcmos_debug") == "open");
	    var _this = this;
	    $("#enabledebug").vclick(function () {
	        _this.count++;
	        if (_this.count > 5) {
	            if (_this.enable) {
	                _this.enable = false;
	                window.localStorage.setItem("kfcmos_debug", "close");
	                _this.hide();
	            }else{
	                _this.enable = true;
	                window.localStorage.setItem("kfcmos_debug", "open");
	                _this.log("debug open");
	            }
	            _this.count = 0;
	        }
	    });*/
	//报错时打印到log
    window.onerror = function(msg, url, l) {
      $.am.debug.log("jsError:"+msg + "\n" + url + "\n" + l);
    };
	},
	show : function() {
		var self = this;
		if (this.div) {
			return;
		}
		this.div = $('<div id="am-consolediv" class="am-clickable" style="word-break: break-all; position: absolute; z-index: 1000; background: rgba(0,0,0,0.5); width: 80%; height: 300px; top: 45px; right:0px; color: white; font-size: 9px; overflow: hidden;"></div>');
		this.div.bind('click',function() {
      self.hide();
    });

		$("body").append(this.div);
	},
	hide : function() {
		if (this.div) {
			this.div.remove();
			delete this.div;
		}
	},
	log : function(msg) {
		if (!this.enable) {
			return false;
		}
		if (!this.div) {
			this.show();
		}
		var date = new Date();
		this.div.prepend(date.getMinutes() + ":" + date.getSeconds() + " " + msg + "<br>");
	},

	postLog : function() {
	}
};

  $.am.debug.init();


});