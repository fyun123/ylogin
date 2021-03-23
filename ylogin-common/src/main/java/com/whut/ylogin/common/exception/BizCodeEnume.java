package com.whut.ylogin.common.exception;

public enum BizCodeEnume {

    LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION(15003,"账号或密码错误");



    private int code;
    private String msg;
    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
