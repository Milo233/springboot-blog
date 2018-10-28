/*!
 * blogedit.html 页面脚本.
 */
"use strict";
//# sourceURL=blogedit.js
// DOM 加载完再执行
$(function() {

	// 初始化 md 编辑器
    $("#md").markdown({
        language: 'zh',
        fullscreen: {
            enable: true
        },
        resize:'vertical',
        localStorage:'md',
        imgurl: 'http://localhost:8081',
        base64url: 'http://localhost:8081'
        // todo 干嘛的？？
    });

    // 初始化标签控件
    $('.form-control-tag').tagEditor({
        initialTags: [],
        maxTags: 5,
        delimiter: ', ',
        forceLowercase: false,
        animateDelete: 0,
        placeholder: '请输入标签'
    });
        
    $('.form-control-chosen').chosen();
    
    
    $("#uploadImage").click(function() {
        $.ajax({
            url: fileServerUrl,
            type: 'POST',
            cache: false,
            data: new FormData($('#uploadformid')[0]),
            processData: false,
            contentType: false,
            success: function(data){
                var mdcontent=$("#md").val();
                 $("#md").val(mdcontent + "\n![]("+data +") \n");

             }
        }).done(function(res) {
            $('#file').val('');
        }).fail(function(res) {});
    })

});