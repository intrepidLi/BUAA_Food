package com.buaa.food.ui.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.activity.ResultActivity;
import com.buaa.food.ui.adapter.TabAdapter;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.HomeActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.widget.view.SwitchButton;

import java.util.ArrayList;

public final class HangWeiFragment extends TitleBarFragment<HomeActivity>
        implements SwitchButton.OnCheckedChangeListener,ViewPager.OnPageChangeListener, TabAdapter.OnTabListener {

    private Toolbar mToolbar;
    private EditText mHintView;
    private AppCompatImageView mSearchView;
    private RecyclerView recyclerView;

    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

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
        recyclerView = findViewById(R.id.rv_hangwei_tab);

        mViewPager = findViewById(R.id.vp_hangwei_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.Hangwei), "新北食堂");
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.Hangwei), "东区食堂");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(getAttachActivity());
        recyclerView.setAdapter(mTabAdapter);

        //ArrayList<String> dishes = createDishesList();
        //recyclerView.setAdapter(new DishAdapter(dishes));
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

        setOnClickListener(mSearchView);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("新北食堂");
        mTabAdapter.addItem("东区食堂");
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mSearchView) {
            Intent intent = new Intent(getActivity(), ResultActivity.class);
            intent.putExtra("searchHint", mHintView.getText().toString());
            startActivity(intent);
            // finish();
            // startActivity(ResultActivity.class);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }
}