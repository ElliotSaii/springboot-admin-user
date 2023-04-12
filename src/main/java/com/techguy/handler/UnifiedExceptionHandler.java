package com.techguy.handler;

import com.techguy.api.vo.ResultVo;
import com.techguy.exception.AccessLimitExceededException;
import com.techguy.exception.AuthorizationException;
import com.techguy.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;

@ResponseStatus(HttpStatus.OK)
@RestControllerAdvice
public class UnifiedExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(UnifiedExceptionHandler.class);

   @ExceptionHandler(BusinessException.class)
    public ResultVo handleBusinessException(BusinessException ex, HttpServletRequest request){
     log.info(">> business exception: {}, {}, {}",request.getRequestURI(),ex.getCode(), ex.getMessage());
       String errMessage = ex.getMessage();
       if(StringUtils.isBlank(errMessage)){
          errMessage = "The server is busy";
          log.warn(">>  business exception error not set",ex);
       }
       return ResultVo.fail(ex.getCode(), errMessage);
   }

    @ExceptionHandler(AuthorizationException.class)
    public ResultVo handleTokenAuthorizationException(AuthorizationException ex, HttpServletRequest request) {
        log.info(">> invalid authorization error: {} {}", request.getRequestURI(), ex.getMessage());
        String errMessage =StringUtils.isBlank(ex.getMessage()) ? "Authentication failed" : ex.getMessage();
        return ResultVo.fail(HttpStatus.UNAUTHORIZED.value(), errMessage);
    }
    @ExceptionHandler(AccessLimitExceededException.class)
    public ResultVo handleAccessitExceededException(AccessLimitExceededException ex, HttpServletRequest request) {
        log.info(">> current access limit is exceeded: {}", request.getRequestURI());
        return ResultVo.fail(12345, "Frequently requests");
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultVo handleMaxUploadSizedExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.info(">> upload file size is exceeded: {}, limit: {}", request.getRequestURI(), ex.getMessage());
        String errMessage = "Upload file is too large";
        return ResultVo.fail(HttpStatus.BAD_REQUEST.value(), errMessage);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo handleArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info(">> argument not valid error: {} {}", request.getRequestURI(), ex.getMessage());
        String field = fieldError == null ? "unknown" : fieldError.getField();
        String message = fieldError == null ? "null" : fieldError.getDefaultMessage();
        return ResultVo.fail(HttpStatus.BAD_REQUEST.value(), message);
        //return ResultVo.fail(HttpStatus.BAD_REQUEST.value(), "invalid request parameters" + ex.getParameter().getParameterName());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.info(">> method not supported error: {} {}, expected: {}",
                ex.getMethod(), request.getRequestURI(), ex.getSupportedHttpMethods());
        return ResultVo.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), "Doesn't support this" + ex.getMethod() + "request");
    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultVo handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String detail = ex.getClass().getName();
        if (!ex.getSupportedMediaTypes().isEmpty()) {
            detail = MediaType.toString(ex.getSupportedMediaTypes());
            detail = "support content-type: " + detail;
        }
        log.info(">> http media type not supported error: {} {}, expected: {}",
                request.getContentType(), request.getRequestURI(), detail);
        return ResultVo.fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Doesn't support Content-Type: " + request.getContentType());
    }
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResultVo handleMessageConversionException(HttpMessageConversionException ex, HttpServletRequest request) {
        log.info(">> message conversion error: {} {}", request.getRequestURI(), ex.getMessage());
        return ResultVo.fail(HttpStatus.BAD_REQUEST.value(), "Unable to parse request message");
    }
    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ResultVo handleMissingServletRequestPartException(Exception ex, HttpServletRequest request) {
        log.info(">> missing servlet request part/param error: {} {}", request.getRequestURI(), ex.getMessage());
        String paranmName = "";
        if (ex instanceof MissingServletRequestPartException) {
            paranmName = ((MissingServletRequestPartException) ex).getRequestPartName();
        } else if (ex instanceof MissingServletRequestParameterException) {
            paranmName = ((MissingServletRequestParameterException) ex).getParameterName();
        }
        return ResultVo.fail(HttpStatus.BAD_REQUEST.value(), "Missing parameters" + paranmName);
    }
    @ExceptionHandler(DataAccessException.class)
    public ResultVo handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        log.error(">> data access failure: " + request.getRequestURI(), ex);
        return ResultVo.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "The server is busy");
    }

    @ExceptionHandler(value = Exception.class)
    public ResultVo defaultErrorHandler(Exception ex, HttpServletRequest request) {
        log.error(">> internal server error: " + request.getRequestURI(), ex);
        return ResultVo.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "The server is busy");
    }
}
