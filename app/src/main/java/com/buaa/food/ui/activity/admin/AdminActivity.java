package com.buaa.food.ui.activity.admin;

import android.widget.LinearLayout;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;

public final class AdminActivity extends AppActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.admin_activity;
    }

    @Override
    protected void initView() {
        LinearLayout mManageDishes = findViewById(R.id.ll_manage_dishes);
        LinearLayout mManageUsers = findViewById(R.id.ll_manage_users);

        mManageDishes.setOnClickListener(v -> startActivity(DishesManageActivity.class));
        mManageUsers.setOnClickListener(v -> startActivity(UsersManageActivity.class));
    }

    @Override
    protected void initData() {}
}