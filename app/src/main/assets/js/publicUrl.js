var publicURL = "https://app.pythe.cn",//线上
//var publicURL = "https://check.pythe.cn",//测试
	publicImgUrl = "https://app.pythe.cn:446",
	pytheInfoObj = {
		setTime: 2000,
		tips: "加载中"
	},
	pytheLayOutObj = {},
	pytheLayOutObj2 = {};
	pytheLayOutObj.pytheLayOutFun = function(opt) {
		opt = opt || pytheInfoObj;
		var loaddingData = $("<div class='lay-out-key' id='lay-out-key' style='display:block'><div class='lay-center-flex'><div class='lay-centent-content'>" + opt.tips + "</div></div></div>");
		if(!$("#lay-out-key").hasClass("lay-out-key")){
		     $("body").append(loaddingData);
		     setTimeout(function() {
				 $(".lay-out-key").hide();
				 $(".lay-out-key").remove()
			 }, opt.setTime);
		}
		  
	};

	 pytheLayOutObj2.pytheLayOutFun2 = function(opt) {
		opt = opt || pytheInfoObj;
		var loaddingData2 = $("<div class='turnToLogin lay-centent-content' style='position:fixed;top:200px;left:50%;'>" + opt.tips + "</div>");
		if(!$(".lay-centent-content").hasClass("turnToLogin")){
			$("body").append(loaddingData2);
			setTimeout(function() {
				 $(".turnToLogin").hide();
				 $(".turnToLogin").remove()
			}, opt.setTime);
	    }
	};
var loadTips = $("<div class='lay-out-load' style='display:block'><div class='lay-center-flex'><div class='lay-centent-content'>加载中，请稍后...</div></div></div>"),
	toDowmload = function() {
		var userAgentInfo = navigator.userAgent.toLowerCase();
		0 < userAgentInfo.indexOf("android") ? location.href = "http://app.qq.com/#id=detail&appid=1106272259" : 0 < userAgentInfo.indexOf("win") ? location.href = "http://sj.qq.com/myapp/detail.htm?apkName=com.dace.textreader" : 0 < userAgentInfo.indexOf("iphone") ? location.href = "https://itunes.apple.com/cn/app/%E6%B4%BE%E7%9F%A5%E9%98%85%E8%AF%BB/id1264419204?mt=8" : 0 < userAgentInfo.indexOf("ipad") ? alert("暂无ipad版") : 0 < userAgentInfo.indexOf("ipod") ? alert("暂无ipod版") : alert("暂无" + userAgentInfo + "版")
	};