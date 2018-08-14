(function(w, $) {
	var nativeUi = {
		"_history" : {},
		"_uiCache" : [],
		"event" : {
			left : function() {
				smartMobile.appManager.switchApp({
					appName : "HomeApp"
				});
			},
			right : function() {
				console.log("right")
			},
			/**
			 * tabBar button
			 */
			tabitem1 : function() {
				console.log("tabitem1");
			},
			tabitem2 : function() {
				console.log("tabitem2");
			},
			tabitem3 : function() {
				console.log("tabitem3");
			},
			tabitem4 : function() {
				console.log("tabitem4");
			},
			tabitem5 : function() {
				console.log("tabitem5");
			},
			tabitem6 : function() {
				console.log("tabitem6");
			},
			tabitem7 : function() {
				console.log("tabitem7");
			},
			tabitem8 : function() {
				console.log("tabitem8");
			},
			tabitem9 : function() {
				console.log("tabitem9");
			},
			tabitem10 : function() {
				console.log("tabitem10");
			},
			tabitem11 : function() {
				console.log("tabitem11");
			},
			tabitem12 : function() {
				console.log("tabitem12");
			}
		},
		nameEventMap : {
			left : "left",
			right : "right",
			tabitem1 : "tabitem1",
			tabitem2 : "tabitem2",
			tabitem3 : "tabitem3",
			tabitem4 : "tabitem4",
			tabitem5 : "tabitem5",
			tabitem6 : "tabitem6",
			tabitem7 : "tabitem7",
			tabitem8 : "tabitem8",
			tabitem9 : "tabitem9",
			tabitem10 : "tabitem10",
			tabitem11 : "tabitem11",
			tabitem12 : "tabitem12"
		},
		change : function(uis, options) {

			if (!uis)
				return;
			var _options = options ? options : {};
			$.extend(uis, _options);

			var args = this.historyfilter(uis);
			for (var $id in args) {
				if (args[$id]) {
					var paras = {};
					for (var key in args[$id]) {
						if (key == "onclick")
							continue;
						paras[key] = args[$id][key];
					}

					if (!$.isEmptyObject(paras)) {

						smartMobile.nativeUI.setAttribute({
							id : $id.toString(),
							parameters : paras
						});
					}
				}
			}
			//args
			$.extend(true, this._history, uis);
		},
		update : function(uis, options) {
			if (!uis)
				return;
			var _options = options ? options : {};
			$.extend(uis, _options);
		},
		//        reset: function (id) {
		//            var page = $.am.pages[id];
		//            if (page) {
		//                page.nativeUi = $.extend(true, {}, this._uiCache[id]);
		//            }
		//        },
		historyfilter : function(uis) {
			var options = {};
			for (var id in uis) {
				var style = uis[id], _laststyle = this._history[id] ? this._history[id] : {};
				var empty = {};
				empty[id] = {};
				//$.extend(options, empty);
				for (var key in style) {
					if (key == "onclick") {
						this.event[this.nameEventMap[id]] = style[key];
						continue;
					}
					if (_laststyle[key] && (style[key] == _laststyle[key]))
						continue;

					empty[id][key] = style[key];
				}

				$.extend(options, empty);
			}

			return options;
		},
		setUiEventMap : function(option) {
			var _option = option ? option : {};
			$.extend(true, this.nameEventMap, _option);
		}
		//        addCache: function (page) {
		//            if (page && page.id && page.nativeUi) {
		//                this._uiCache[page.id] = $.extend(true, {}, page.nativeUi); ;
		//            }
		//        }
	}
	$.am.nativeUi = {
		setUiEventMap : function(option) {
			nativeUi.setUiEventMap(option);
		},
		reset : function(id) {
			nativeUi.reset(id);
		},
		change : function(uis, options) {
			nativeUi.change(uis, options);
		},
		event : nativeUi.event
		//        addCache:function(page){
		//            nativeUi.addCache(page);
		//        }
	}

})(window, jQuery)
