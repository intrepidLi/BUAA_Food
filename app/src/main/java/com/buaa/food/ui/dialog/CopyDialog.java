package com.buaa.food.ui.dialog;

import android.content.Context;
import android.view.Gravity;

import com.hjq.base.BaseDialog;
import com.buaa.food.R;


public final class CopyDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder> {

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.copy_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);
            setGravity(Gravity.BOTTOM);
        }
    }
}