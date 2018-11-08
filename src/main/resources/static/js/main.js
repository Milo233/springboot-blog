"use strict";
// DOM 加载完再执行
$(function() {
    // 弹窗插件设置
    toastr.options = {
        "closeButton": true, //是否显示关闭按钮
        "debug": false, //是否使用debug模式
        "positionClass": "toast-bottom-right",//弹出窗的位置
        "showDuration": "300",//显示的动画时间
        "hideDuration": "300",//消失的动画时间
        "timeOut": "2500", //展现时间
        "extendedTimeOut": "1000",//加长展示时间
        "showEasing": "swing",//显示时的动画缓冲方式
        "hideEasing": "linear",//消失时的动画缓冲方式
        "showMethod": "fadeIn",//显示时的动画方式
        "hideMethod": "fadeOut" //消失时的动画方式
    };

    // 返回顶部的效果事件
    NProgress.start();

    $(window).scroll(function(){  //只要窗口滚动,就触发下面代码
        var scrollt = document.documentElement.scrollTop + document.body.scrollTop; //获取滚动后的高度
        if( scrollt >200 ){  //判断滚动后高度超过200px,就显示
            $("#goToTop").fadeIn(400); //淡出
        }else{
            $("#goToTop").stop().fadeOut(400); //如果返回或者没有超过,就淡入.必须加上stop()停止之前动画,否则会出现闪动
        }
    });
    $("#goToTop").click(function(){ //当点击标签的时候,使用animate在200毫秒的时间内,滚到顶部
        $("html,body").animate({scrollTop:"0px"},200);
    });
    NProgress.done();
});