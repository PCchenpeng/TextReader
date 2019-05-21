var publicURL='https://app.pythe.cn';//线上
//var publicURL='https://check.pythe.cn';//测试
var publicImgUrl='https://app.pythe.cn:446';
var publicImgUrl2='https://app.pythe.cn:446';
var pytheInfoObj={
    setTime:2000,
    tips:'加载中'
};
var pytheLayOutObj3={};
pytheLayOutObj3.pytheLayOutFun3=function(opt){
    opt=opt||pytheInfoObj ;
    var loaddingData2=$("<div class='turnToLogin lay-centent-content'>"+opt.tips+"</div>");
    $('body').append(loaddingData2);
    setTimeout(function(){
        $('.turnToLogin').hide();
        $('.turnToLogin').remove();
    },opt.setTime);

};

var toDowmload=function pytheDownload(){
        var userAgentInfo = navigator.userAgent.toLowerCase();
        if (userAgentInfo.indexOf('android') > 0)
        {
            location.href='http://app.qq.com/#id=detail&appid=1106272259';
        }else if(userAgentInfo.indexOf('win') > 0){
            location.href='http://sj.qq.com/myapp/detail.htm?apkName=com.dace.textreader';
        }else if(userAgentInfo.indexOf('iphone') > 0){
            location.href="https://itunes.apple.com/cn/app/%E6%B4%BE%E7%9F%A5%E9%98%85%E8%AF%BB/id1264419204?mt=8";
            //alert('在开发中，带来不便请谅解');
        }else if(userAgentInfo.indexOf('ipad') > 0){
            alert('暂无ipad版');
        }else if(userAgentInfo.indexOf('ipod') > 0){
            alert('暂无ipod版');
        }else{
            alert('暂无'+userAgentInfo+'版');
        }

        //else if(userAgentInfo.indexOf('windows phone') > 0){
        //alert('暂无电脑版');
        //}

  };








