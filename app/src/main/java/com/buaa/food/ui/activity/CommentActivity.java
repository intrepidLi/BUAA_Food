package com.buaa.food.ui.activity;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.base.FragmentPagerAdapter;

public class CommentActivity extends AppActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private DataBaseHelper dataBaseHelper;
    private AppCompatButton mCommentButton;

    protected int getLayoutId() {
        return R.layout.comment_activity;
    }

    protected void initView() {
        mViewPager = findViewById(R.id.vp_comment_pager);
        mPagerAdapter = new FragmentPagerAdapter<>(this);

        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.History));

        mViewPager.setAdapter(mPagerAdapter);

        mCommentButton = findViewById(R.id.btn_comment);

        setOnClickListener(mCommentButton);

        this.dataBaseHelper = new DataBaseHelper(this);
    }


    protected void initData() {
    }
}
