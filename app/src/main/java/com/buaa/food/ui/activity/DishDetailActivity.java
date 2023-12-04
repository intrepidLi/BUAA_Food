package com.buaa.food.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.ui.activity.admin.DishUploadActivity;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.widget.layout.SettingBar;
import com.umeng.commonsdk.debug.D;


public final class DishDetailActivity extends AppActivity {

    private int dishId;

    private AppCompatImageView mDishImageView;
    private AppCompatTextView mDishNameView;
    private AppCompatTextView mDishPriceView;
    private AppCompatTextView mDishRemainingView;
    private AppCompatTextView mDishCanteenView;
    private AppCompatTextView mDishWindowView;
    private SettingBar mCommentEnterBar;
    private AppCompatButton mBuyBtn;
    private AppCompatImageView mCollectionView;

    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_detail_activity;
    }

    @Override
    protected void initView() {
        dishId = getIntent().getIntExtra("dishId", 0);

        mDishImageView = findViewById(R.id.iv_dish_image);
        mDishNameView = findViewById(R.id.tv_dish_name);
        mDishPriceView = findViewById(R.id.tv_dish_price);
        mDishRemainingView = findViewById(R.id.tv_dish_remaining);
        mDishCanteenView = findViewById(R.id.tv_dish_canteen);
        mDishWindowView = findViewById(R.id.tv_dish_window);
        mCommentEnterBar = findViewById(R.id.sb_comment_enter);
        mBuyBtn = findViewById(R.id.btn_buy);
        mCollectionView = findViewById(R.id.iv_collection);

        setOnClickListener(mBuyBtn, mCollectionView, mCommentEnterBar);

        dataBaseHelper = new DataBaseHelper(this);
    }

    @Override
    protected void initData() {
        byte[] imgByte = dataBaseHelper.getDishImage(dishId);
        if (imgByte != null && imgByte.length > 0) {
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

            GlideApp.with(getActivity())
                .load(imgBitmap)
                .placeholder(R.drawable.dish_1)
                .error(R.drawable.dish_1)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mDishImageView);
        } else {
            GlideApp.with(getActivity())
                .load(R.drawable.dish_1)
                .placeholder(R.drawable.dish_1)
                .error(R.drawable.dish_1)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mDishImageView);
        }

        mDishNameView.setText(dataBaseHelper.getDishName(dishId));
        mDishPriceView.setText(String.valueOf(dataBaseHelper.getDishPrice(dishId)));
        mDishRemainingView.setText(String.valueOf(dataBaseHelper.getDishRemaining(dishId)));
        mDishCanteenView.setText(dataBaseHelper.getDishCanteen(dishId));
        mDishWindowView.setText(dataBaseHelper.getDishWindow(dishId));
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mBuyBtn) {
            dataBaseHelper.updateDishRemaining(dishId, dataBaseHelper.getDishRemaining(dishId) - 1);
            dataBaseHelper.updateDishViewed(dishId, dataBaseHelper.getDishViewed(dishId) + 1);

            Toast.makeText(DishDetailActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
            finish();

        } else if (view == mCommentEnterBar) {


        } else if (view == mCollectionView) {


        }
    }
}
