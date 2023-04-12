package com.techguy.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("rawtypes")
public class ResultVo<T> {


    private boolean success;


    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errCode;


    private String errMsg;


    private T data;

    public static ResultVo success() {
        return success(null);
    }

    public static <E> ResultVo<E> success(E data) {
        ResultVo<E> result = new ResultVo<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <E> ResultVo<E> fail(String errMsg) {
        return fail(500, errMsg);
    }

    public static <E> ResultVo<E> fail(int errCode, String errMsg) {
        ResultVo<E> result = new ResultVo<>();
        result.setSuccess(false);
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
