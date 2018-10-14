package com.yuan.blog.controller;

import com.yuan.blog.domain.EsBlog;
import com.yuan.blog.service.EsBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogService esBlogService;

    @GetMapping
    public String listBlogs(@RequestParam(value="order",required=false,defaultValue="new") String order,
                            @RequestParam(value="keyword",required=false,defaultValue = "") String keyword) {
        System.out.print("order:" +order + ";keyword:" +keyword );
        return "redirect:/index?order="+order+"&keyword="+keyword;
    }

/*    @GetMapping
    public List<EsBlog> list(@RequestParam(value="summary",required=false,defaultValue="") String summary,
                             @RequestParam(value="content",required=false,defaultValue="") String content,
                             @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                             @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize){
        // 数据在 Test 里面先初始化了，这里只管取数据
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        Page<EsBlog> page = esBlogService.findBySummaryLikeOrContentLike(summary, content, pageable);
        return page.getContent();
    }*/
}













