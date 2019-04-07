/*!
 * blogedit.html 页面脚本.
 */
"use strict";
//# sourceURL=blogedit.js
// DOM 加载完再执行
$(function () {

    // 初始化 md 编辑器
    $("#md").markdown({
        language: 'zh',
        fullscreen: {
            enable: true
        },
        resize: 'vertical',
        localStorage: 'md',
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

    // 编辑界面插入图片
    $("#uploadImage").click(function () {
        //这里唯一需要注意的就是这个form-add的id
        // form必须有action
        var url = $("#uploadFormId").attr("action");
        var formData = new FormData($("#uploadFormId")[0]);
        $.ajax({
            //接口地址
            url: url,
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
                //成功的回调  ![](图片地址 ''图片title'')
                if (data.success) {
                    var text = $("#md").val()
                    var image = "![](" + data.body + " '图片title')"
                    if (text) {
                        text = (text + "\r\n" + image)
                    } else {
                        text = image
                    }
                    $("#md").val(text);
                } else {
                    alert("图片上传失败，请稍后重试！");
                }
            },
            error: function (returndata) {
                //请求异常的回调
                alert("图片上传失败，请稍后重试！");
            }
        });
    });

});