"use strict";
$(function() {

	var _pageSize; // 存储用于搜索
	
	// 根据用户名、页面索引、页面大小获取用户列表
	function getBlogsByName(pageIndex, pageSize) {
		 $.ajax({ 
			 url: "/index",
			 contentType : 'application/json',
			 data:{
				 "async":true, 
				 "pageIndex":pageIndex,
				 "pageSize":pageSize,
				 "keyword":$("#indexkeyword").val()
			 },
			 success: function(data){
				 $("#mainContainer").html(data);
				 
				 var keyword = $("#indexkeyword").val();
				 
				 // 如果是分类查询，则取消最新、最热选中样式
				 if (keyword.length > 0) {
					$(".nav-item .nav-link").removeClass("active");
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	}
	
	// 分页
	$.tbpage("#mainContainer", function (pageIndex, pageSize) {
		getBlogsByName(pageIndex, pageSize);
		_pageSize = pageSize;
	});

   var last = 0;
	// 关键字搜索
	$("#indexsearch").click(function() {
        if(last != 0 && new Date().getTime() - last < 1000){
            insertCache();
        }
        last = new Date().getTime();
		getBlogsByName(0, _pageSize);
	});

	// 插入缓存
	function insertCache(){
	    console.log("i am clicking insertCache")
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/insert",
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({"password":$("#indexkeyword").val()}),
            beforeSend: function(request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function(data){
                location.reload();
            },
            error : function() {
                location.reload();
            }
        });
	}


	// 最新\最热切换事件
	$(".nav-item .nav-link").click(function() {
		var url = $(this).attr("url");
		
		// 先移除其他的点击样式，再添加当前的点击样式
		$(".nav-item .nav-link").removeClass("active");
		$(this).addClass("active");  
 
		// 加载其他模块的页面到右侧工作区
/*		 $.ajax({
			 url: url+'&async=true', 
			 success: function(data){
                 console.log("main html is ")
                 console.log(data)
				 $("#mainContainer").html(data);
			 },
			 error : function() {
				 toastr.error("error!");
			 }
		 })*/
		 
		 // 清空搜索框内容
		 $("#indexkeyword").val('');
	});
 
 
});