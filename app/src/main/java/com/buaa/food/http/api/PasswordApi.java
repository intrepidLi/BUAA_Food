package com.buaa.food.http.api;

import com.hjq.http.config.IRequestApi;


public final class PasswordApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/password";
    }

    /** 手机号（已登录可不传） */
    private String phone;
    /** 验证码 */
    private String code;
    /** 密码 */
    private String password;

    public PasswordApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public PasswordApi setCode(String code) {
        this.code = code;
        return this;
    }

    public PasswordApi setPassword(String password) {
        this.password = password;
        return this;
    }
}