package com.yuan.blog;

import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import com.yuan.blog.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;

/**
 * Controller统一异常处理
 */
@ControllerAdvice(basePackages = "com.yuan.blog.controller")
public class ExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    /*此方法返回值，可以是Controller可以返回的任何值*/
    /**
     * 全局异常处理 访问没有权限也可以捕获处理
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, WebRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType)) {//Ajax请求
            if (e instanceof ConstraintViolationException) {
                return ResponseEntity.ok().body(new Response(false,
                        ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) e)));
            } else if (e instanceof TransactionSystemException) {
                Throwable t = e.getCause();
                while ((t != null) && !(t instanceof ConstraintViolationException)) {
                    t = t.getCause();
                }
                if (t != null) {
                    return ResponseEntity.ok().body(
                            new Response(false, ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t)));
                }
            }else if(e instanceof DataIntegrityViolationException){
                return ResponseEntity.ok().body(
                        // 不完全正确，可能是其他 表的数据重复了，所以还是要在代码里做控制 判断
                        new Response(false, "账号或邮箱重复！"));
            }else if(e instanceof AccessDeniedException){
                return ResponseEntity.ok().body(
                        new Response(false, "操作失败，请尝试先登录！"));
            }
            logger.error("【系统异常】={}", e.getMessage());
            return ResponseEntity.ok().body(
                    new Response(false, parseException(e).getMessage()));

        } else {//非Ajax请求
            //此时requestType为null
            logger.error("【系统异常】={}", e.getMessage());
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMsg", e.getMessage());
            return modelAndView;
        }
    }

    //获得最内层异常
    private static Throwable parseException(Throwable e) {
        Throwable tmp = e;
        int breakPoint = 0;
        while (tmp.getCause() != null) {
            if (tmp.equals(tmp.getCause())) {
                break;
            }
            tmp = tmp.getCause();
            breakPoint++;
            if (breakPoint > 1000) {
                break;
            }
        }
        return tmp;
    }
}
