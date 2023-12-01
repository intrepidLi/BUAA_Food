package com.buaa.food.ui.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.ui.adapter.DishAdapter;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.HomeActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.widget.view.CountdownView;
import com.hjq.widget.view.SwitchButton;

import java.util.ArrayList;

import javax.xml.transform.sax.SAXResult;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 发现 Fragment
 */

public final class HangWeiFragment extends TitleBarFragment<HomeActivity>
        implements SwitchButton.OnCheckedChangeListener {

    private Toolbar mToolbar;
    private EditText mHintView;
    private AppCompatImageView mSearchView;
    private RecyclerView recyclerView;

    public static HangWeiFragment newInstance() {
        return new HangWeiFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hangwei_fragment;
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.tb_hangwei_title);
        mHintView = findViewById(R.id.tv_hangwei_hint);
        mSearchView = findViewById(R.id.iv_hangwei_search);
        recyclerView = findViewById(R.id.hangwei_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<String> dishes = createDishesList();
        recyclerView.setAdapter(new DishAdapter(dishes));
        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        mHintView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        toast(checked);
    }

    private ArrayList<String> createDishesList() {
        ArrayList<String> dishList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            dishList.add("菜品名称 " + i);
        }
        return dishList;
    }
}