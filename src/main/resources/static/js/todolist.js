$(function () {
    // 渲染
    getData()

    // 键盘回车事件
    $('#content').on('keydown', function (event) {
        // 判断是否为回车
        if (event.keyCode === 13) {
            // 判断内容是否为空
            if ($(this).val() == '') {
                alert('请先输入内容！')
            } else {
                // 获得数据
                var local = getData();
                // 更改数据 增加
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                $.ajax({
                    url: '/todo/add',
                    type: 'POST',
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        "content": $('#content').val(),
                        "type": $("#type").val(),
                        "notify": $('#notify').is(':checked') ? 1 : 0
                    }),
                    beforeSend: function (request) {
                        request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                    },
                    success: function (data) {
                        if (data.success) {
                            // 成功后，重定向
                            toastr.info("保存成功！");
                            getData()
                        } else {
                            toastr.error("error!" + data.message);
                        }
                    },
                    error: function () {
                        toastr.error("error!");
                    }
                })
                // 数量更新
                $(this).val("")
            }
        }

    });

    // 删除操作 删除事件
    $(".todoList").find('ul,ol').on('click', 'a', function () {
        // 拿到自定义索引
        var index = $(this).attr('index');
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/todo/delete/' + index,
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getData();
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 读取本地存储的数据函数
    function getData() {
        // 取得本地数据 字符串形式
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/todo/query',
            type: 'POST',
            async: false,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "type": $("#type").val(),
                "notify": $('#notify').is(':checked') ? 1 : 0
            }),
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    load(data.body)
                } else {
                    load([]);
                }
            },
            error: function () {
                    load([]);
//                toastr.error("error!");
            }
        })
    }

    // 渲染加载数据函数
    function load(data) {
        // 清空ol 和 ul 的儿子
        $(".todoList").find('ul,ol').empty();
        // 遍历 数据
        $.each(data, function (i, n) {
            // 判断条件 数据中的done 属性的值 为true
            if (n.status == 1) {
                // 将生成的li 添加到 ol中(已完成)
                $(".todoList").find('ol').append("<li><input type='checkbox' checked ><span>" + n.content + "</span><a href='javascript:;'" + " index= " + n.id + " status= " + 1 + " style='float:right'>删除</a></li>")
                // 否则 添加到 ul 中(未完成)
            } else {
                $(".todoList").find('ul').append("<li><input type='checkbox'><span>" + n.content + "</span><a href='javascript:;'" + " index= " + n.id + " style='float:right'>删除</a></li>")
            }
        })
        nowNum();
    }

    // 复选框操作
    $(".todoList").find('ul,ol').on('click', 'input', function () {
        var data = getData();
        // 通过兄弟 获得索引
        var index = $(this).siblings('a').attr('index');
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/todo/updateStatus/' + index + "/" + ($(this).prop('checked') ?  1 : 0),
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getData();
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 数量更新函数
    function nowNum() {
        $('.todo').html($(".todoList").find('ul li').length);
        $('.done').html($(".todoList").find('ol li').length);
    }
});