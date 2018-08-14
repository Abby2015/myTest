/** ***********@mobileAPI js *************** */

(function() {

	/*
	 * 
	 * @param {Object} target 目标对象。
	 * 
	 * @param {Object} source 源对象。
	 * 
	 * @param {boolean} deep 是否复制(继承)对象中的对象。
	 * 
	 * @returns {Object} 返回继承了source对象属性的新对象。
	 * 
	 */

	var extend = function(target, /* optional */source, /* optional */deep) {
		target = target || {};

		var i = 0;

		if (typeof source !== 'object'
				&& Object.prototype.toString.call(source) !== '[object Function]')

			source = {};

		while (i <= 2) {
			option = i === 1 ? target : source;

			if (option != null) {

				for ( var name in option) {

					var src = target[name], copy = option[name];

					if (target === copy)

						continue;

					if (deep && copy && typeof copy === 'object'
							&& !copy.nodeType)

						target[name] = extend(src
								|| (copy.length != null ? [] : {}), copy, deep);

					else if (copy !== undefined)

						target[name] = copy;

				}

			}
			i++;

		}

		return target;

	};
	var mobileApi = {

		webService : {
		    execServiceConnector : function(option, successCallback,
					failedCallback) {

		        var ajaxOption = { "type": "POST" };
		        try{
		            if (config && config.server && config.protocol && config.port && config.virtualDirectory) {
		                ajaxOption["url"] = config.protocol + "://" + config.server + ":" + config.port + "/" +config.virtualDirectory+ "/mas/mobile/invokeService";
		            }else{
		                ajaxOption["url"] = "http://16.156.254.90:8081/mos2server/mas/mobile/invokeService";
		            }
		        } catch (e) {
		            ajaxOption["url"] = "http://16.156.254.90:8081/mos2server/mas/mobile/invokeService";
		        }
				var data = {
				    "deviceType":"Phone",
                    "language":"zh",
                    "platform": device.platform || "Android",
                    "mobilet":"ORDERING",
                    "dataType":"JSON",
                    "resouceVersion":smartMobile.app.resourceVersion || "2.032",
                    "apkVersion":smartMobile.app.apkVersion || "3",

                    deviceId: smartMobile.app.deviceId || "00000000000",
                    mobilet: "KFC_mos3",
                    versionFlag: "P",
                    macAddress: smartMobile.app.macAddress || "0000-0000-0000-0000",
				    "data": option
				}
				typeof(option.timeOut)=="number" && (ajaxOption["timeout"] = option.timeOut);
				if(!ajaxOption["timeout"]){
					ajaxOption["timeout"]=30*1000
				}
				ajaxOption["data"] = JSON.stringify(data);
				ajaxOption["success"] = function (ret) {
				    var res = ret;
				    if(typeof(ret) == "string"){
				        try{
				            res = JSON.parse(ret);
				        } catch (e) {
				            console.log(e);
				        }
				    }
				    successCallback(res);
				};
				ajaxOption["error"] = function (ret) {
				    failedCallback(ret);
				};
				ajaxOption.contentType = 'application/json; charset=UTF-8';
				$.ajax(ajaxOption);
			}
		},

		nativeUI : {
			setAttribute : function(option, successCallback, failedCallback) {
			    mobileApi.tab.setAttribute(option, successCallback, failedCallback);
			}
		},
		webtrend :{
		  pushEvent : function(option, successCallback, failedCallback) {
			try{
				yum.webtrend.pushEvent(option, successCallback, failedCallback);
			}catch(e){
			}

		  }
		},

		nativeUIWidget : {
			confirm : function(options, successCallback, failedCallback) {
				//mobileApi.execNativeAPI("nativeUIWidget", "confirm", options,
			    //		successCallback, failedCallback);
			    navigator.notification.confirm(options.description, function (ret) {
			        if (ret == 1) {
			            successCallback();
			        } else {
			            failedCallback();
			        }
			    }, options.caption, [options.okCaption, options.cancelCaption])
			},
			datePicker : function(options, successCallback, failedCallback) {
				try{
				    yum.nativeUIWidget.showTimePicker(options, function (ret) {
				        //alert(JSON.stringify(ret));
				        var content;
				        if (typeof ret.content == "string") {
				            content = JSON.parse(ret.content);
				        } else if (typeof ret.content == "undefined") {
				            successCallback(JSON.stringify(ret));
				            return;
				        }else{
				            content = ret.content;
				        }
				        ret.hourOfDay = content.hourOfDay;
				        ret.minute = content.minute;
						successCallback(JSON.stringify(ret));
					}, function(ret){
					    failedCallback(JSON.stringify(ret));
					});
				}catch(e){
					console.log(e);
				}
			},
			citySelecter: function (options, successCallback, failedCallback) {
			    smartmobile.nativeUIWidget.openSearchList(options, function (ret) {
			        if (ret.result == 0 && ret.content) {
			            successCallback(JSON.stringify(ret.content));
			        } else if (typeof (ret.result) == "undefined") {
			            successCallback(JSON.stringify(ret));
			        }else{
			            failedCallback();
			        }
			    }, function (ret) {
			        failedCallback(ret);
			    });
			},
			startBusy: function (option) {
			    smartmobile.nativeUIWidget.startBusy(option || {});
			},
			stopBusy : function() {
			    smartmobile.nativeUIWidget.stopBusy();
			},
			showMap : function(option) {

				//mobileApi.execNativeAPI("nativeUIWidget", "showMap", option);

			},
			showInstantMessage : function(option) {
				smartmobile.nativeUIWidget.showInstantMessage(option);

			},
			showMessageBox : function(option, successCallback, failedCallback) {
			    navigator.notification.alert(option.content, option.title, '确定');
			},
			selectPicture : function(successCallback, failedCallback, option) {
			    alert("selectPicture");
			},
			showPopupMenu : function(option, successCallback, failedCallback) {
			    smartmobile.nativeUIWidget.showPopupMenu(option, function (ret) {
			        //alert(JSON.stringify(ret));
			        if (ret && ret.result == 0) {
			            var i = ret.content;
			            eval(option.items[i].action);
			        } else {

			        }
			    });

			},
			readBarCode : function(successCallback, failedCallback) {
			    alert("readBarCode");
			},
			readRQcode : function(successCallback, failedCallback) {
			    alert("readRQcode");
			},
			openUrl : function(option, successCallback, failedCallback) {
			    alert("openUrl");
			},
			viewFile : function(option, successCallback, failedCallback) {
			    alert("viewFile");
			},
			popupWebsite : function(option, successCallback, failedCallback) {
			    alert("popupWebsite");
			},
			showComboBox: function (option, successCallback, failedCallback) {
			    try{
			        smartmobile.nativeUIWidget.showComboBox(option, function (ret) {
			            console.log(JSON.stringify(ret));
			            if (ret && ret.result == 0) {
			                var i = ret.content;
			                successCallback({
			                    result:0,
			                    selected:i
			                });
			            } 
			        });
			    } catch (e) {
			        alert("error:"+e);
			    }
			},
			openSearchList : function(option, successCallback, failedCallback) {
			    alert("openSearchList");
			},
		},

		media : {
		    photoToAlbum: function (option, successCallback, failedCallback) {
		        try{
		            smartmobile.media.photoToAlbum(option, successCallback, failedCallback);
		        } catch (e) {
		            alert(e);
		        }
			}
		},

		phoneService : {
			dial : function(option, successCallback, failedCallback) {
			    alert("dial");
			},
			tel : function(option, successCallback, failedCallback) {
			    alert("tel");
			},

			sms : function(option, successCallback, failedCallback) {
			    smartmobile.phoneService.sms(option, successCallback, failedCallback);
			},
			email: function (option, successCallback, failedCallback) {
			    smartmobile.phoneService.email(option, successCallback, failedCallback);
			}

		},
		appManager : {

			switchApp : function(option, successCallback, failedCallback) {

			    //mobileApi.execNativeAPI("appManager", "switchApp", option);
			    location.href = "."+option.page;

			}
		},

		network : {

			isReachable : function(callback) {

				//mobileApi.execNativeAPI("network", "isReachable", null,
				//		callback, callback);

			}
		},

		userInfo : {

			userName : ""

		},

		download : {
		    download: function (option, successCallback, failedCallback) {
		        //alert(JSON.stringify(option));
		        smartmobile.download.download(option, function (ret) {
		            //alert(JSON.stringify(ret));
			        try {
			            typeof (ret) == "string" && (ret = JSON.parse(ret));
			            if (!ret.responseData) {
			                var res = {
			                    result: ret.result,
			                    message: ret.message,
			                    responseData: {
			                        filename: ret.content.filename,
			                        relativePath: ret.content.relativePath
			                    }
			                };
			                successCallback(JSON.stringify(res));
			            } else {
			                if (ret.status == "ok") {
			                    successCallback(JSON.stringify(ret));
			                } else {
			                    failedCallback(JSON.stringify(ret));
			                }
			            }
			        } catch (e) {
			            alert(e);
			        }
			        
			    }, function (err) {
			        //console.log("fail download:"+JSON.stringify(err));
			        var res = {
			            result: err.result,
			            message: err.message,
			        };

			        failedCallback(JSON.stringify(res));
			    });

			},
			getProgress : function(option, successCallback, failedCallback) {
				//mobileApi.execNativeAPI("download", "getProgress", option,
				//		successCallback, failedCallback);
			},
			deleteDownloadedFile : function(option, successCallback,
					failedCallback) {
				//mobileApi.execNativeAPI("download", "deleteDownloadedFile",
				//		option, successCallback, failedCallback);
			},
			cancel : function(option, successCallback, failedCallback) {
				//mobileApi.execNativeAPI("download", "cancel", option,
				//		successCallback, failedCallback);
			},
			upload : function(option, successCallback, failedCallback) {
				//mobileApi.execNativeAPI("download", "upload", option,
				//		successCallback, failedCallback);
			}
		},
		execNativeAPI : function(serviceName, actionName, option,
				successCallback, failedCallback) {
		    //alert("execNativeAPI:" + serviceName);
			if (!window.device) {

				console.log("yqa===>smartMobileApi未初始化");

				return false;

			}

			// alert(window.device.platform);

			switch (window.device.platform) {

			case "iPad":

			case "iPhone":

			case "iPod touch":

			case "iPhone Simulator":

			case "iPad Simulator":

				// alert("ios: " + serviceName + " | " + actionName + " | " +
				// JSON.stringify(option) + " | " + successCallback + " | " +
				// failedCallback);

				PhoneGap.exec(serviceName + "." + actionName, option,
						GetFunctionName(successCallback),
						GetFunctionName(failedCallback))

				break;

			case "Android":

				// alert("Android: " + serviceName + " | " + actionName + " | "
				// + JSON.stringify(option) + " | " + successCallback + " | " +
				// failedCallback)

				PhoneGap.exec(successCallback, failedCallback, serviceName,
						actionName, [ option ]);

				break;

			case "BlackBerry":

				break;

			}

		},
		// 支付宝
		pay : {
		    securepay: function (option, successCallback, failedCallback) {
		        try {
		            yum.pay.securepay(function (ar) {
		                typeof (ar) == "string" && (ar = JSON.parse(ar));
		                if (ar.content) {
		                    successCallback(ar.content);
		                } else if (typeof ar.isInstalled != "undefined") {
		                    successCallback(ar);
		                } else {
		                    failedCallback(ar);
		                }
		            }, function (ar) {
		                failedCallback(ar);
		            });
		        } catch (e) {
		            alert(e);
		        }
			},
		    prepareinfo: function (option, successCallback, failedCallback) {
		        //alert("prepareinfo");
		        try{
		            yum.pay.prepareinfo(option, successCallback, failedCallback);
		        } catch (e) {
		            alert(e);
		        }
			},
		    reqpay: function (option, successCallback, failedCallback) {
		        try {
		            yum.pay.reqpay(option, function (ar) {
		                //alert(JSON.stringify(ar));
		                successCallback(ar);
		            }, function (ar) {
		                //alert(JSON.stringify(ar));
		                failedCallback(ar);
		            });
		        } catch (e) {
		            alert("d" + e);
		        }
			}
		},
		social : {
		    shareTo: function (option, successCallback, failedCallback) {
		        try{
		            smartmobile.social.shareTo(option, successCallback, failedCallback);
		        } catch (e) {
		            alert(e);
		        }
			},
			bind : function(option, successCallback, failedCallback) {
				//for ( var i in option) {
				//	console.log(i + ":" + option[i]);
				//}
				//mobileApi.execNativeAPI("social", "bind", option,
				//		successCallback, failedCallback);
			    try {
			        smartmobile.social.bind(option, successCallback, failedCallback);
			    } catch (e) {
			        alert(e);
			    }
			},
			unbind : function(option, successCallback, failedCallback) {
			    try {
			        smartmobile.social.unbind(option, successCallback, failedCallback);
			    } catch (e) {
			        alert(e);
			    }
			},

			follow : function(option, successCallback, failedCallback) {
			    try {
			        smartmobile.social.follow(option, successCallback, failedCallback);
			    } catch (e) {
			        alert(e);
			    }
			},
			isBound : function(option, successCallback, failedCallback) {
			    try {
			        smartmobile.social.isBound(option, successCallback, failedCallback);
			    } catch (e) {
			        alert(e);
			    }
			}
		},
		sys : {
			postLog : function(option, successCallback, failedCallback) {
			    try {
			        yum.sys.postLog(option, successCallback, failedCallback);
			    } catch (e) {
			        //alert(e);
			    }
			},
			syncTime : function(successCallback, failedCallback) {
			    try {
			        yum.sys.syncTime("", successCallback, failedCallback);
			    } catch (e) {
			        //alert(e);
			    }
			},
			appLoadReady : function(successCallback, failedCallback) {
				//mobileApi.execNativeAPI("system", "appLoadReady",
				//		successCallback, failedCallback);

			},
			locale : function(option, successCallback, failedCallback) {
			    try {
			        yum.sys.locale(option, successCallback, failedCallback);
			    } catch (e) {
			        //alert(e);
			    }
			}

		},
		usageProfile : {
			trackPage : function(option, successCallback, failedCallback) {
				//for ( var i in option) {
				//	console.log(i + ":" + option[i]);
				//}
				//mobileApi.execNativeAPI("usageProfile", "trackPage", option,
				//		successCallback, failedCallback);

			}
		},
		// 注册apiReady事件，该事件在PhoneGap的deviceready事件执行之后执行，

		// mobilet开发人员需要将所有涉及取smartMobile数据的请求放置在apiReady事件内执行

		Init : function() {
			window.smartMobile.container = {version:"1.0"};
			window.smartMobile.app = {id:"Mock",sourceRoot:""};
		},
		apiReady : function(callback) {
			if (typeof callback == "function") {
			    mobileApi.Init();
				this._apiReady = callback;
			}
		},
		extend : extend
	}


	document.addEventListener("deviceready", function () {
	    mobileApi.tab = new Tab("bottomBarPH");

	    yum.sys.getInfo(function (ret) {
            if (typeof ret == "string") {
                try{
                    ret = JSON.parse(ret);
                } catch (e) {
                    alert("yum.sys.getInfo error:" + e);
                }
            }
            if (ret && ret.result == 0) {
                mobileApi.containerInfo = ret.content;
                mobileApi.container.version = ret.content.apkVersion;
                window.smartMobile.app = extend(window.smartMobile.app, ret.content);
            } else {

            }

            if (typeof mobileApi._apiReady == "function") {
                mobileApi._apiReady();
            }
        }, function (ret) {
            if (typeof mobileApi._apiReady == "function") {
                mobileApi._apiReady();
            }
        });
	    try{
	        yum.appInfo.getAppInfo(function (ret) {
	            if (ret && ret.result == 0) {
	                window.smartMobile.app = extend(window.smartMobile.app, ret.content);
	                /*if (device.platform == "Android") {
	                    window.smartMobile.app.version = "A_" + window.smartMobile.app.version;
	                } else {
	                    window.smartMobile.app.version = "I_" + window.smartMobile.app.version;
	                }*/
	            } else{
	                if (typeof ret.content == "string") {
	                    content = JSON.parse(ret.content);
	                }

	                window.smartMobile.app = extend(window.smartMobile.app, ret);
	                /*if (device.platform == "Android") {
	                    window.smartMobile.app.version = "A_" + window.smartMobile.app.version;
	                } else {
	                    window.smartMobile.app.version = "I_" + window.smartMobile.app.version;
	                }*/
	            }
	        }, function (ret) {
	            if (typeof mobileApi._apiReady == "function") {

	            }
	        });
	    } catch (e) {
	        alert(e);
	    }
	}, false);

	window.smartMobile = mobileApi;


	var Tab = function (id) {
	    this.$ = $("#" + id);
	    this.$.find("li").addClass("am-clickable").each(function (i,item) {
	        $(this).vclick(function () {
	            var f = $.am.nativeUi.event["tabitem" + (i + 1)];
	            typeof(f)=="function" && f();
	        });
	    });
	}
	Tab.prototype = {
	    setAttribute: function (opt, cb, fb) {
	        var id = opt.id;
	        var option = opt.parameters;
	        var $c = $("#" + id);
	        if (option.image && option.selectedImage) {
	            $c.attr("bg", option.image);
	            $c.attr("sbg", option.selectedImage);

	            if ($c.hasClass("selected")) {
	                if (option.selectedImage.indexOf("2x") == -1) {
	                    $c.find("div.childItem").css("background-image", "url(" + option.selectedImage.replace(".png", "@2x.png") + ")");
	                } else {
	                    $c.find("div.childItem").css("background-image", "url(" + option.selectedImage + ")");
	                }
	            } else {
	                if (option.image.indexOf("2x") == -1) {
	                    $c.find("div.childItem").css("background-image", "url(" + option.image.replace(".png", "@2x.png") + ")");
	                } else {
	                    $c.find("div.childItem").css("background-image", "url(" + option.image + ")");
	                }
	            }
	        }
	        if (option.visible == "true") {
	            $c.show();
	        } else if (option.visible == "false") {
	            $c.hide();
	        }
	        if (option.title) {
	            $c.find(".childItem").text(option.title);
	        }
	        
	        if (option.badgeValue) {
	            $c.find(".badge").remove();
	            $c.append('<div class="badge">'+option.badgeValue+'</div>');
	        } else if (option.badgeValue == "") {
	            $c.find(".badge").remove();
	        } else {
	            //$c.find(".badge").remove();
	        }

	        if (option.newMsg == "true") {
	            $c.append('<div class="newMsg"></div>');
	        } else if (option.newMsg == "false") {
	            $c.find(".newMsg").remove();
	        } else {

	        }

	        if (option.onclick) {
	            var n = $c.parent().children().index($c);
	            $.am.nativeUi.event["tabitem" + (n + 1)] = option.onclick;
	        }
	        console.log(JSON.stringify(opt));
	        if (option.selected == "true") {
	            $c.addClass("selected");
	            if ($c.attr("bg") && $c.attr("sbg")) {
	                var sbg = $c.attr("sbg");
	                if (sbg.indexOf("2x") == -1) {
	                    $c.find("div.childItem").css("background-image", "url(" + sbg.replace(".png", "@2x.png") + ")");
	                } else {
	                    $c.find("div.childItem").css("background-image", "url(" + sbg + ")");
	                }
	            }
	        } else if (option.selected == "false") {
	            $c.removeClass("selected");
	            if ($c.attr("bg") && $c.attr("sbg")) {
	                var sbg = $c.attr("bg");
	                if (sbg.indexOf("2x") == -1) {
	                    $c.find("div.childItem").css("background-image", "url(" + sbg.replace(".png", "@2x.png") + ")");
	                } else {
	                    $c.find("div.childItem").css("background-image", "url(" + sbg + ")");
	                }
	            }
	        }
	        cb && cb();
	    }
	}
})();
