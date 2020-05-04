package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.service.TodoService;
import com.yuan.blog.timer.ScheduledTask;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.vo.Response;
import com.yuan.blog.vo.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;


    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public String index() {
        return "todo";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> add(@RequestBody Todo todo) {
        try {
            User currentUser = NetUtil.getCurrentUser(true);
            todo.setUserId(currentUser.getId());
            todoService.addTodo(todo);
        } catch (Exception e) {
            return Response.getResponse(false, e.getMessage(), "fail");
        }
        return Response.getResponse(true, "处理成功", "/todo");
    }

    @PostMapping("/query")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> query(@RequestBody Todo todo) {
        List<TodoResponse> todoList;
        try {
            User currentUser = NetUtil.getCurrentUser(true);
            todo.setUserId(currentUser.getId());
            todoList = todoService.queryForNotify(todo);
            if (todoList != null && todoList.size() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                for (TodoResponse td : todoList) {
                    td.setContent(td.getContent() + " -- " + sdf.format(td.getCreateTime()));
                }
            }
        } catch (Exception e) {
            return Response.getResponse(false, e.getMessage(), "fail");
        }
        return Response.getResponse(true, "处理成功", todoList);
    }

    @GetMapping(value = "/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        try {
            User currentUser = NetUtil.getCurrentUser(true);
            Todo todo = new Todo();
            todo.setDeleted(1);
            todo.setId(id);
            todoService.updateTodo(todo, currentUser);
        } catch (Exception e) {
            return Response.getResponse(false, e.getMessage(), "fail");
        }
        return Response.getResponse(true, "处理成功", "/todo");
    }

    @GetMapping(value = "/updateStatus/{id}/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") Long id,
                                                 @PathVariable("status") int status) {
        try {
            User currentUser = NetUtil.getCurrentUser(true);
            Todo todo = new Todo();
            todo.setStatus(status);
            todo.setId(id);
            todoService.updateTodo(todo, currentUser);
        } catch (Exception e) {
            return Response.getResponse(false, e.getMessage(), "fail");
        }
        return Response.getResponse(true, "处理成功", "/todo");
    }
}
