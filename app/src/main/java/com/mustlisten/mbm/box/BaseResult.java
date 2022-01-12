package com.mustlisten.mbm.box;

/**
 * Created by wuliang on 2017/3/27.
 * 所有返回的json数据的公有格式
 */

public class BaseResult<T> {

//    1）	status: 表成功和失败状态。1表成功，0表失败。
//            2）	errorMessage: 错误信息，当有错误发生时，此errorMessage包含有错误信息
//    3）	errorCode: 错误编码，当有错误发生时，此errorCode包含有错误编码
//    4）	data：返回数据


    private String errmsg;

    private Integer errcode;

    private T data;

    public boolean surcess() {
        return errcode == 0;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
