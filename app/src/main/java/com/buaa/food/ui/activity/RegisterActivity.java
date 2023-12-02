package com.buaa.food.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.ui.fragment.MineFragment;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.buaa.food.R;
import com.buaa.food.aop.Log;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.api.GetCodeApi;
import com.buaa.food.http.api.RegisterApi;
import com.buaa.food.http.model.HttpData;
import com.buaa.food.manager.InputTextManager;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;
import com.hjq.widget.view.SubmitButton;
import com.buaa.food.UserAuth;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 注册界面
 */
public final class RegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_PHONE = "phone";
    private static final String INTENT_KEY_PASSWORD = "password";

    @Log
    public static void start(BaseActivity activity, String phone, String password, OnRegisterListener listener) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_PHONE), data.getStringExtra(INTENT_KEY_PASSWORD));
            } else {
                listener.onCancel();
            }
        });
    }

    private EditText mPhoneView;
    // private CountdownView mCountdownView;

    // private EditText mCodeView;

    private EditText mFirstPassword;
    private EditText mSecondPassword;
    private EditText mUserNameView;

    private SubmitButton mCommitView;
    private SubmitButton mReturnView;

    // 连接数据库
    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_register_phone);
        mUserNameView = findViewById(R.id.et_register_username);
        // mCountdownView = findViewById(R.id.cv_register_countdown);
        // mCodeView = findViewById(R.id.et_register_code);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);
        mReturnView = findViewById(R.id.btn_return_login);

        // Log.e("Enter Register InitView!!!!");
        setOnClickListener(mReturnView, mCommitView);

        mSecondPassword.setOnEditorActionListener(this);
        dataBaseHelper = new DataBaseHelper(this);

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
                .addView(mPhoneView)
                // .addView(mCodeView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        // 自动填充手机号和密码
        mPhoneView.setText(getString(INTENT_KEY_PHONE));
        mFirstPassword.setText(getString(INTENT_KEY_PASSWORD));
        mSecondPassword.setText(getString(INTENT_KEY_PASSWORD));


    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommitView) {
            String username = mUserNameView.getText().toString();
            String password = mFirstPassword.getText().toString();
            String secondPassword = mSecondPassword.getText().toString();
            String phone = mPhoneView.getText().toString();

            hideKeyboard(getCurrentFocus()); // 隐藏软键盘

            if (phone.length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (!password.equals(secondPassword)) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }

            if (dataBaseHelper.checkPhone(phone)) {
                Toast.makeText(RegisterActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                mCommitView.showError(3000);
            }

            if (dataBaseHelper.insert(username, password, phone)) {
                Toast.makeText(RegisterActivity.this, "SignUp Successfully", Toast.LENGTH_SHORT).show();
                mCommitView.showSucceed();
                UserAuth.setLocalUserPhone(phone);
                HomeActivity.start(getContext(), MineFragment.class);
            } else {
                Toast.makeText(RegisterActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
                mCommitView.showError(3000);
            }
        }

        if (view == mReturnView) {
            startActivity(LoginActivity.class);
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone             手机号
         * @param password          密码
         */
        void onSucceed(String phone, String password);

        /**
         * 取消注册
         */
        default void onCancel() {}
    }
}