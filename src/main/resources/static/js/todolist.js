$(function () {
    // 渲染
    load();
    // 数量更新
    nowNum();

    // 键盘回车事件
    $('#txt').on('keydown', function (event) {
        // 判断是否为回车
        if (event.keyCode === 13) {
            // 判断内容是否为空
            if ($(this).val() == '') {
                alert('请先输入内容！')
            } else {
                // 获得数据
                var local = getData();
                // 更改数据 增加
                local.push({title: $(this).val(), done: false});
                // 存储数据
                saveData(local);
                // 重新渲染
                load();
                // 数量更新
                nowNum();
                $(this).val("")
            }
        }

    });

    // 删除操作 删除事件
    $(".todoList").find('ul,ol').on('click', 'a', function () {
        // 读取数据
        var data = getData();
        // 拿到自定义索引
        var index = $(this).attr('index');
        // 删除当前项数据
        data.splice(index, 1);
        // 更新数据
        saveData(data);
        // 重新渲染
        load();
        // 数量更新
        nowNum();
    });

    // 读取本地存储的数据函数
    function getData() {
        // 取得本地数据 字符串形式
        var data = localStorage.getItem('todo');
        if (data !== null) {
            // 传出数组格式的数据 JSON.parse()将字符串转换为数组
            console.log(JSON.parse(data))
            return JSON.parse(data);
        } else {
            return [];
        }
    }

    // 存储数据函数
    function saveData(data) {
        // 存储数据 JSON.stringify() 将数组转换为字符串
        localStorage.setItem('todo', JSON.stringify(data));
    }

    // 渲染加载数据函数
    function load() {
        // 获取数据
        var data = getData();
        // 清空ol 和 ul 的儿子
        $(".todoList").find('ul,ol').empty();
        // 遍历 数据
        $.each(data, function (i, n) {
            // 判断条件 数据中的done 属性的值 为true
            if (n.done) {
                // 将生成的li 添加到 ol中(已完成)
                $(".todoList").find('ol').append("<li><input type='checkbox' checked ><span>" + n.title + "</span><a href='javascript:;'" + " index= " + i + " style='float:right'>删除</a></li>")
                // 否则 添加到 ul 中(未完成)
            } else {
                $(".todoList").find('ul').append("<li><input type='checkbox'><span>" + n.title + "</span><a href='javascript:;'" + " index= " + i + " style='float:right'>删除</a></li>")
            }

        })
    }

    // 复选框操作
    $(".todoList").find('ul,ol').on('click', 'input', function () {
        // 获取数据
        var data = getData();
        // 通过兄弟 获得索引
        var index = $(this).siblings('a').attr('index');
        // 修改对应索引的数据
        data[index].done = $(this).prop('checked');
        // 数据更新
        saveData(data);
        // 重新渲染
        load();
        // 数量更新
        nowNum();
    });

    // 数量更新函数
    function nowNum() {
        $('.todo').html($(".todoList").find('ul li').length);
        $('.done').html($(".todoList").find('ol li').length);
    }
});