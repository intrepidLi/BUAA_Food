package com.buaa.food.other;


import com.buaa.food.action.ToastAction;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.config.IToastInterceptor;

import timber.log.Timber;


public final class ToastLogInterceptor implements IToastInterceptor {

    @Override
    public boolean intercept(CharSequence text) {
        if (AppConfig.isLogEnable()) {
            // 获取调用的堆栈信息
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            // 跳过最前面两个堆栈
            for (int i = 2; stackTrace.length > 2 && i < stackTrace.length; i++) {
                // 获取代码行数
                int lineNumber = stackTrace[i].getLineNumber();
                // 获取类的全路径
                String className = stackTrace[i].getClassName();
                if (lineNumber <= 0 || className.startsWith(ToastUtils.class.getName()) ||
                        className.startsWith(ToastAction.class.getName())) {
                    continue;
                }

                Timber.tag("ToastUtils");
                Timber.i("(" + stackTrace[i].getFileName() + ":" + lineNumber + ") " + text.toString());
                break;
            }
        }
        return false;
    }
}