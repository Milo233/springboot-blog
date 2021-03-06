package com.yuan.blog.listener;

import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import com.yuan.blog.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Controller统一异常处理
 */
@ControllerAdvice(basePackages = "com.yuan.blog.controller")
public class ExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

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
            } else if (e instanceof AccessDeniedException) {
                return ResponseEntity.ok().body(
                        new Response(false, "操作失败，请尝试先登录！"));
            }
            logger.error("【系统异常】={}", e.getMessage() + getStackTrace(e));
            return ResponseEntity.ok().body(
                    new Response(false, parseException(e).getMessage()));

        } else {//非Ajax请求
            //此时requestType为null
            logger.error("【系统异常】={}", e.getMessage() + getStackTrace(e));
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

    /**
     * 获取出错的栈信息
     */
    private String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
