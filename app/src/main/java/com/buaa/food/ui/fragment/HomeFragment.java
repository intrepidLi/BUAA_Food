package com.buaa.food.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.DishPreview;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.ui.activity.DishDetailActivity;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.buaa.food.R;
import com.buaa.food.app.AppFragment;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.HomeActivity;
import com.buaa.food.ui.adapter.TabAdapter;
import com.buaa.food.widget.XCollapsingToolbarLayout;
import com.umeng.commonsdk.debug.D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class HomeFragment extends TitleBarFragment<HomeActivity>
        implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {

    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private TextView mHintView;

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    private DataBaseHelper dataBaseHelper;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.ctl_home_bar);
        mToolbar = findViewById(R.id.tb_home_title);

        mHintView = findViewById(R.id.tv_home_hint);

        mTabView = findViewById(R.id.rv_home_tab);
        mViewPager = findViewById(R.id.vp_home_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.HotRank), "餐品热度排行榜");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(getAttachActivity());
        mTabView.setAdapter(mTabAdapter);

        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);

        // 初始化三餐推荐
        dataBaseHelper = new DataBaseHelper(this.getContext());
        initRecommend();
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("餐品热度排行榜");
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

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

    /**
     * CollapsingToolbarLayout 渐变回调
     *
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {
        getStatusBarConfig().statusBarDarkFont(shown).init();
        mHintView.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        mHintView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.white60));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }

    private int breakfastId;
    private int lunchId;
    private int dinnerId;

    private void initRecommend() {
        LinearLayout mBreakfastLayout = findViewById(R.id.ll_breakfast_line);
        mBreakfastLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getAttachActivity(), DishDetailActivity.class);
            intent.putExtra("dishId", breakfastId);
            startActivity(intent);
        });
        AppCompatImageView mBreakfastImage = findViewById(R.id.iv_breakfast_image);
        AppCompatTextView mBreakfastName = findViewById(R.id.tv_breakfast_name);

        LinearLayout mLunchLayout = findViewById(R.id.ll_lunch_line);
        mLunchLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getAttachActivity(), DishDetailActivity.class);
            intent.putExtra("dishId", lunchId);
            startActivity(intent);
        });
        AppCompatImageView mLunchImage = findViewById(R.id.iv_lunch_image);
        AppCompatTextView mLunchName = findViewById(R.id.tv_lunch_name);

        LinearLayout mDinnerLayout = findViewById(R.id.ll_dinner_line);
        mDinnerLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getAttachActivity(), DishDetailActivity.class);
            intent.putExtra("dishId", dinnerId);
            startActivity(intent);
        });
        AppCompatImageView mDinnerImage = findViewById(R.id.iv_dinner_image);
        AppCompatTextView mDinnerName = findViewById(R.id.tv_dinner_name);

        Random random = new Random();

        // randomly select a breakfast from database
        List<DishPreview> breakfasts = dataBaseHelper.fetchAllBreakfast();
        DishPreview breakfast = breakfasts.get(random.nextInt(breakfasts.size()));
        breakfastId = breakfast.getDishId();
        setRecommendLine(breakfast, mBreakfastImage, mBreakfastName);

        // randomly select a lunch and a dinner from database and collection and history
        List<DishPreview> allDishes = dataBaseHelper.fetchAllDishes();
        List<DishPreview> history = dataBaseHelper.fetchHistorys();
        List<DishPreview> collection = dataBaseHelper.fetchFavorites();

        List<DishPreview> lunches = getRandomListItem(allDishes, 10);
        lunches.addAll(getRandomListItem(history, 10));
        lunches.addAll(getRandomListItem(collection, 10));
        DishPreview lunch = lunches.get(random.nextInt(lunches.size()));
        lunchId = lunch.getDishId();
        setRecommendLine(lunch, mLunchImage, mLunchName);

        List<DishPreview> dinners = getRandomListItem(allDishes, 10);
        dinners.addAll(getRandomListItem(history, 10));
        dinners.addAll(getRandomListItem(collection, 10));
        DishPreview dinner = dinners.get(random.nextInt(dinners.size()));
        dinnerId = dinner.getDishId();
        setRecommendLine(dinner, mDinnerImage, mDinnerName);
    }

    private void setRecommendLine(DishPreview dishPreview, AppCompatImageView imageView, AppCompatTextView textView) {
        byte[] imgByte = dataBaseHelper.getDishImage(dishPreview.getDishId());
        if (imgByte != null && imgByte.length > 0) {
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            GlideApp.with(getContext())
                    .load(imgBitmap)
                    .placeholder(R.drawable.dish_1)
                    .error(R.drawable.dish_1)
                    .transform(new MultiTransformation<>(new CenterInside()))
                    .into(imageView);
        } else {
            GlideApp.with(getContext())
                    .load(R.drawable.default1)
                    .placeholder(R.drawable.dish_1)
                    .error(R.drawable.dish_1)
                    .transform(new MultiTransformation<>(new CenterInside()))
                    .into(imageView);
        }
        textView.setText(dishPreview.getDishName());
    }

    public static <T> List<T> getRandomListItem(List<T> list, int count) {
        List<T> olist = new ArrayList<>();
        if (list.size() <= count) {
            return list;
        } else {
            Random random = new Random();
            for (int i = 0; i < count; i++){
                int intRandom = random.nextInt(list.size() - 1);
                olist.add(list.get(intRandom));
                list.remove(list.get(intRandom));
            }
            return olist;
        }
    }
}