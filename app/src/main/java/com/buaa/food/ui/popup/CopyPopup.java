package com.buaa.food.ui.popup;

import android.content.Context;

import com.hjq.base.BasePopupWindow;
import com.buaa.food.R;


public final class CopyPopup {

    public static final class Builder
            extends BasePopupWindow.Builder<Builder> {

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.copy_popup);
        }
    }
}