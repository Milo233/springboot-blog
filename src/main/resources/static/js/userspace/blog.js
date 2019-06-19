/*!
 * blog.html 页面脚本.
 */
"use strict";
//# sourceURL=blog.js

// DOM 加载完再执行
$(function () {
    $.catalog("#catalog", ".post-content");

    // 回车事件
    $("#commentContent").keypress(function (e) {
        if (e.which == 13) {
            //submit form via ajax, this is not JS but server side scripting so not showing here
            e.preventDefault();
            $("#submitComment").click();
        }
        /*        if(e.keyCode == 13 && e.ctrlKey){
                    // 这里实现换行
                    document.getElementById("a").value += "\n";}*/
    });

    // 处理删除博客事件
    $(".blog-content-container").on("click", ".blog-delete-blog", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: blogUrl,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 成功后，重定向
                    window.location = data.body;
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 获取评论列表
    function getCommnet(blogId) {

        $.ajax({
            url: '/comments',
            type: 'GET',
            data: {"blogId": blogId},
            success: function (data) {
                $("#mainContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    // 提交评论
    $(".blog-content-container").on("click", "#submitComment", function () {
        var comment = $('#commentContent').val();
        console.log(comment)
        if (!comment || !comment.trim()) {
            return;
        }
        $('#commentContent').val("");//清空输入框，避免重复提交。
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/comments',
            type: 'POST',
            data: {"blogId": blogId, "commentContent": comment},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.code == 0) {
                    // 清空评论框
                    $('#commentContent').val('');
                    // 获取评论列表
                    getCommnet(blogId);
                } else {
                    toastr.error(data.msg);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 删除评论
    $(".blog-content-container").on("click", ".blog-delete-comment", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/comments/' + $(this).attr("commentId") + '?blogId=' + blogId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 获取评论列表
                    getCommnet(blogId);
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 初始化 博客评论
    getCommnet(blogId);

    // 提交点赞
    $(".blog-content-container").on("click", "#submitVote", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/votes',
            type: 'POST',
            data: {"blogId": blogId},
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    $("#submitVote").remove();//
                    $("#comment-vote").append('<button class="btn btn-primary float-right" id="cancelVote" voteid="' +
                        data.body + '">取消点赞</button>')
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 取消点赞
    $(".blog-content-container").on("click", "#cancelVote", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/votes/' + $(this).attr('voteId') + '?blogId=' + blogId,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    $("#cancelVote").remove();
                    $("#comment-vote").append('<button class="btn btn-primary float-right" id="submitVote">点赞</button>')
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });
});