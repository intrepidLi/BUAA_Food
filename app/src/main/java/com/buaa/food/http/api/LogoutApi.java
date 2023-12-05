package com.buaa.food.http.api;

import com.hjq.http.config.IRequestApi;


public final class LogoutApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/logout";
    }
}