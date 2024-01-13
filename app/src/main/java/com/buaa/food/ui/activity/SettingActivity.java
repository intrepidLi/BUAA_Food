package com.buaa.food.ui.activity;

import android.view.View;
import com.buaa.food.ui.activity.admin.AdminActivity;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.api.LogoutApi;
import com.buaa.food.http.model.HttpData;
import com.buaa.food.manager.ActivityManager;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;
import com.buaa.food.DataBaseHelper;
import com.buaa.food.UserAuth;

public final class SettingActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

//    private SettingBar mNameView;
//    private SettingBar mPasswordView;

    private SettingBar mSettingBar;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    protected void initView() {
//        mNameView = findViewById(R.id.sb_setting_userName);
//        mPasswordView = findViewById(R.id.sb_setting_password);
        dataBaseHelper = new DataBaseHelper(this);
        mSettingBar = findViewById(R.id.sb_admin_enter);
        String phone = UserAuth.getLocalUserPhone();
        if (!dataBaseHelper.getUsername(phone).equals("admin")) {
            mSettingBar.setLeftText("联系管理员");
        }

        setOnClickListener(
                R.id.sb_setting_agreement,
                R.id.sb_setting_about,
                R.id.sb_admin_enter,
                R.id.sb_setting_exit
        );
    }

    @Override
    protected void initData() {
//        dataBaseHelper = new DataBaseHelper(this.getContext());
//        String username = dataBaseHelper.getUsername(UserAuth.getLocalUserPhone());
//
//        // 获取应用缓存大小
//        mNameView.setRightText(username);
//        mPasswordView.setRightText("密码强度较低");
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
//        if (viewId == R.id.sb_setting_userName) {
//
//            new SafeDialog.Builder(this)
//                    .setListener((dialog, phone, code) -> PhoneResetActivity.start(getActivity(), code))
//                    .show();
//
//        } else if (viewId == R.id.sb_setting_password) {
//
//            new SafeDialog.Builder(this)
//                    .setListener((dialog, phone, code) -> PasswordResetActivity.start(getActivity(), phone, code))
//                    .show();
//
//        }  else if (viewId == R.id.sb_setting_about) {
        if (viewId == R.id.sb_setting_about) {
            startActivity(AboutActivity.class);

        } else if (viewId == R.id.sb_setting_agreement) {

            BrowserActivity.start(this, "https://github.com/intrepidLi/BUAA_Food");

        } else if (viewId == R.id.sb_admin_enter) {

            String phone = UserAuth.getLocalUserPhone();
            if (dataBaseHelper.getUsername(phone).equals("admin")) {
                startActivity(AdminActivity.class);
            } else {
                toast("管理员电话：13910188707");
            }

        } else if (viewId == R.id.sb_setting_exit) {

            if (true) {
                startActivity(LoginActivity.class);
                // 进行内存优化，销毁除登录页之外的所有界面
                ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                return;
            }

            // 退出登录
            EasyHttp.post(this)
                    .api(new LogoutApi())
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                        }
                    });

        }
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        toast(checked);
    }
}