package com.buaa.food.ui.fragment;

import com.buaa.food.R;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.activity.CopyActivity;


public final class CopyFragment extends AppFragment<CopyActivity> {

    public static CopyFragment newInstance() {
        return new CopyFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.copy_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}