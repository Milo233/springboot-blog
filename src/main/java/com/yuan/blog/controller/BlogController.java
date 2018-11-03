package com.yuan.blog.controller;

import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.EsBlog;
import com.yuan.blog.domain.User;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.service.CatalogService;
import com.yuan.blog.service.EsBlogService;
import com.yuan.blog.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogService esBlogService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BlogService blogService;

    /*@GetMapping
    public String listEsBlogs(
            @RequestParam(value="order",required=false,defaultValue="new") String order,
            @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
            @RequestParam(value="async",required=false) boolean async,
            @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
            @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
            Model model) {

        Page<EsBlog> page = null;
        List<EsBlog> list = null;
        boolean isEmpty = true; // 系统初始化时，没有博客数据
        try {
            if (order.equals("hot")) { // 最热查询
                Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
                Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
                page = esBlogService.listHotestEsBlogs(keyword, pageable);
            } else if (order.equals("new")) { // 最新查询
                Sort sort = new Sort(Sort.Direction.DESC,"createTime");
                Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
                page = esBlogService.listNewestEsBlogs(keyword, pageable);
            }

            isEmpty = false;
        } catch (Exception e) {
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = esBlogService.listEsBlogs(pageable);
        }

        list = page.getContent();   // 当前所在页面数据列表

        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);

        // 首次访问页面才加载
        if (!async && !isEmpty) {
            List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();
            model.addAttribute("newest", newest);
            List<EsBlog> hotest = esBlogService.listTop5HotestEsBlogs();
            model.addAttribute("hotest", hotest);
            List<TagVO> tags = esBlogService.listTop30Tags();
            model.addAttribute("tags", tags);
            List<User> users = esBlogService.listTop12Users();
            model.addAttribute("users", users);
        }

        return (async?"index :: #mainContainerRepleace":"index");
    }*/

    @GetMapping
    public String listBlogsByOrder(@RequestParam(value="order",required=false,defaultValue="hot") String order,
                                   @RequestParam(value="categoryId",required=false ) Long categoryId,
                                   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
                                   @RequestParam(value="async",required=false) boolean async,
                                   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                                   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
                                   Model model) {
       /* User  user = new User(11111L,"","","");
        Page<Blog> page = null;
        if (categoryId != null &&categoryId > 0) {
            Optional<Catalog> optionalCatalog = catalogService.getCatalogById(categoryId);
            Catalog catalog = null;
            if (optionalCatalog.isPresent()) {
                catalog = optionalCatalog.get();
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                page = blogService.listBlogsByCatalog(catalog, pageable);
                order = "";
            }
        }
        if (order.equals("hot")) { // 最热查询 阅读/评论/点赞量
            Sort sort = new Sort(Sort.Direction.DESC,"reading","comments","likes");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByKeywordByHot(keyword, pageable);
        }
        if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByKeywordByTime( keyword, pageable);
        }

        List<Blog> list = page.getContent();	// 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("keyword",keyword);
        model.addAttribute("blogList", list);
        return (async?"index :: #mainContainerRepleace":"index");*/

        return "index";
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













