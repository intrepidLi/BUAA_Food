package com.buaa.food.http.model;

import com.buaa.food.other.AppConfig;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;


public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return AppConfig.getHostUrl();
    }

    @Override
    public String getPath() {
        return "api/";
    }

    @Override
    public BodyType getType() {
        // 以表单的形式提交参数
        return BodyType.FORM;
    }
}