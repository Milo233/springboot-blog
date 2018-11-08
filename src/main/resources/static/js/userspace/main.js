"use strict";
//# sourceURL=main.js

// DOM 加载完再执行
$(function() {

	var avatarApi;
	// 获取编辑用户头像的界面
	$(".blog-content-container").on("click",".blog-edit-avatar", function () {
        console.log("获取编辑用户头像的界面")
		avatarApi = "/u/"+$(this).attr("userName")+"/avatar";
		console.log("avatarApi" + avatarApi)
		$.ajax({
			 url: avatarApi,
			 success: function(data){
			 	console.log("success of get user head!!")
				 $("#avatarFormContainer").html(data);
		     },
		     error : function() {
                 console.log("error of get user head!!")
		    	 toastr.error("error!");
		     }
		 });
	});

	/**
	 * 将以base64的图片url数据转换为Blob
	 * @param urlData
	 *            用url方式表示的base64图片数据
	 */
	function convertBase64UrlToBlob(urlData){
        console.log("将以base64的图片url数据转换为Blob  ")
		console.log(urlData)
	    var bytes=window.atob(urlData.split(',')[1]); //去掉url的头，并转换为byte

	    //处理异常,将ascii码小于0的转换为大于0
	    var ab = new ArrayBuffer(bytes.length);
	    var ia = new Uint8Array(ab);
	    for (var i = 0; i < bytes.length; i++) {
	        ia[i] = bytes.charCodeAt(i);
	    }

	    return new Blob( [ab] , {type : 'image/png'});
	}

	// 提交用户头像的图片数据
    $("#submitEditAvatar").on("click", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content")

/*
        console.log("提交用户头像的图片数据")
        var form = $('#avatarformid')[0];
        var formData = new FormData(form);
        // var base64Codes = $("#cropImg").find("img").attr("src");
        console.log("base64Codes" + $("#testArea").val())
        formData.append("uploaded_file[]",convertBase64UrlToBlob($("#testArea").val()));
        console.log("提交用户头像的图片数据")
*/
        console.log("提交用户头像的图片数据11111")
        var fileObj = document.getElementById("FileUpload").files[0]; // js 获取文件对象
        if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
            alert("请选择图片");
            return;
        }
        console.log("提交用户头像的图片数据333")
        var formFile = new FormData();
        formFile.append("action", "UploadVMKImagePath");
        formFile.append("file", fileObj); //加入文件对象
        var data1 = formFile;
        console.log("提交用户头像的图片数据444")
        console.log("avatarApi" + avatarApi)
        $.ajax({
            url: avatarApi,
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: data1,
            beforeSend: function(request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function(data){
                if (data.success) {
                    // 成功后，置换头像图片
                    $(".blog-avatar").attr("src", data.avatarUrl);
                } else {
                    toastr.error("error!"+data.message);
                }

            },
            error : function() {
                toastr.error("error!");
            }
        });
    });
	// 使用mongodb的上传方法
	$("#submitEditAvatarVersoin1").on("click", function () {
        console.log("提交用户头像的图片数据")
		var form = $('#avatarformid')[0];
	    var formData = new FormData(form);
	    // var base64Codes = $("#cropImg").find("img").attr("src");
	    console.log("base64Codes" + $("#testArea").val())
 	    formData.append("file",convertBase64UrlToBlob($("#testArea").val()));
        console.log("提交用户头像的图片数据")
 	    $.ajax({
		    url: fileServerUrl,  // 文件服务器地址 fileServerUrl
		    type: 'POST',
		    cache: false,
		    data: formData,
		    processData: false,
		    contentType: false,
		    success: function(data){

		    	var avatarUrl = data;

				// 获取 CSRF Token
				var csrfToken = $("meta[name='_csrf']").attr("content");
				var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		    	// 保存头像更改到数据库
				$.ajax({
					 url: avatarApi,
					 type: 'POST',
					 contentType: "application/json; charset=utf-8",
					 data: JSON.stringify({"id":Number($("#userId").val()),
						 	"avatar":avatarUrl}),
					 beforeSend: function(request) {
		                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
		             },
					 success: function(data){
						 if (data.success) {
							// 成功后，置换头像图片
							 $(".blog-avatar").attr("src", data.avatarUrl);
						 } else {
							 toastr.error("error!"+data.message);
						 }

				     },
				     error : function() {
				    	 toastr.error("error!");
				     }
				 });
	        },
		    error : function() {
		    	 toastr.error("error!");
		    }
		})
	});
});