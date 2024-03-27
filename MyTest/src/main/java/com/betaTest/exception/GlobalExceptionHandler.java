package com.betaTest.exception;

import com.betaTest.domain.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <p>
 *  全局异常处理器
 * </p>
 *
 * @author LMX
 * @date 2024/03/27/10:42
 */
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 参数转换异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String InvalidFormatException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return "参数无效";
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public String businessException(CustomException e) {
        log.info(e.getMessage(), e);
        if (null == e.getCode()) {
            return e.getMessage();
        }
        return e.getMessage();
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error(e.getMessage(), e);
        return e.getMessage();
    }
}
