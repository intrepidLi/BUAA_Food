package com.buaa.food.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.hjq.widget.layout.SettingBar;

public final class DishDetailActivity extends AppActivity {

    private int dishId;
    boolean isCollected = false;

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

        setOnClickListener(mCommentEnterBar, mBuyBtn, mCollectionView, mCommentEnterBar);

        dataBaseHelper = new DataBaseHelper(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        byte[] imgByte = dataBaseHelper.getDishImage(dishId);
        if (imgByte != null && imgByte.length > 0) {
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

            GlideApp.with(getActivity())
                .load(imgBitmap)
                .placeholder(R.drawable.dish_1)
                .error(R.drawable.dish_1)
                .transform(new MultiTransformation<>(new CenterCrop()))
                .into(mDishImageView);
        } else {
            GlideApp.with(getActivity())
                .load(R.drawable.default1)
                .placeholder(R.drawable.dish_1)
                .error(R.drawable.dish_1)
                .transform(new MultiTransformation<>(new CenterCrop()))
                .into(mDishImageView);
        }

        mDishNameView.setText(dataBaseHelper.getDishName(dishId));
        mDishPriceView.setText(dataBaseHelper.getDishPrice(dishId) + " 元");
        mDishRemainingView.setText(dataBaseHelper.getDishRemaining(dishId) + " 份");
        mDishCanteenView.setText(dataBaseHelper.getDishCanteen(dishId));
        mDishWindowView.setText(dataBaseHelper.getDishWindow(dishId));

        // TODO : init mCollectionView ==> has done
        // isCollected = dataBaseHelper.isDishCollected(dishId);
        int curViewed = dataBaseHelper.getDishViewed(dishId);
        if (curViewed != -1) {
            dataBaseHelper.updateDishViewed(dishId, curViewed + 1);
        }

        isCollected = dataBaseHelper.isFavorite(dishId);

        if (isCollected) {
            mCollectionView.setImageResource(R.drawable.collected);
        } else {
            mCollectionView.setImageResource(R.drawable.collection_icon);
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mBuyBtn) {
            if (dataBaseHelper.checkDishRemaining(dishId)) {
                int curRemain = dataBaseHelper.getDishRemaining(dishId);
                int curOrdered = dataBaseHelper.getDishOrdered(dishId);
                if (curOrdered != -1 && curRemain != -1) {
                    dataBaseHelper.updateDishRemaining(dishId, curRemain - 1);
                    dataBaseHelper.updateDishOrdered(dishId, curOrdered + 1);
                    Toast.makeText(DishDetailActivity.this, "菜品购买成功", Toast.LENGTH_SHORT).show();
                    dataBaseHelper.uploadHistory(dishId);
                } else {
                    Toast.makeText(DishDetailActivity.this, "菜品购买失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DishDetailActivity.this, "购买失败，菜品余量不足", Toast.LENGTH_SHORT).show();
            }

            // dataBaseHelper.updateDishViewed(dishId, dataBaseHelper.getDishViewed(dishId) + 1);

            // TODO : Add to user's history ==> has done
            finish();

        } else if (view == mCommentEnterBar) {
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            intent.putExtra("dishId", dishId);
            intent.putExtra("commentId", 0);
            startActivity(intent);
        } else if (view == mCollectionView) {
            // dataBaseHelper.uploadFavorite(dishId);

            // TODO : Add to user's collection or cancel collection ==> has done

            if (isCollected) {
                dataBaseHelper.deleteCollection(dishId);
                toast("取消收藏");
                mCollectionView.setImageResource(R.drawable.collection_icon);
                isCollected = false;
            } else {
                dataBaseHelper.uploadFavorite(dishId);
                toast("收藏成功");
                mCollectionView.setImageResource(R.drawable.collected);
                isCollected = true;
            }

        }
    }
}
