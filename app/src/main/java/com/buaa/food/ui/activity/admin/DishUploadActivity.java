package com.buaa.food.ui.activity.admin;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.manager.InputTextManager;
import com.buaa.food.ui.activity.ImageCropActivity;
import com.buaa.food.ui.activity.ImageSelectActivity;
import com.buaa.food.ui.dialog.SelectDialog;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.view.SubmitButton;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public final class DishUploadActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private AppCompatImageView mImageView;
    private EditText mNameView;
    private EditText mPriceView;
    private EditText mRemainingView;
    private AppCompatTextView mCanteenView;
    private SubmitButton mUploadView;

    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_upload_activity;
    }

    @Override
    protected void initView() {
        mImageView = findViewById(R.id.iv_dish_image);
        mNameView = findViewById(R.id.et_dish_name);
        mPriceView = findViewById(R.id.et_dish_price);
        mRemainingView = findViewById(R.id.et_dish_remaining);
        mCanteenView = findViewById(R.id.tv_canteen);
        mUploadView = findViewById(R.id.btn_dish_upload);

        dataBaseHelper = new DataBaseHelper(this);

        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
            .addView(mNameView)
            .addView(mPriceView)
            .addView(mRemainingView)
            .setMain(mUploadView)
            .build();

        setOnClickListener(mImageView, mCanteenView, mUploadView);
    }

    @Override
    protected void initData() {
        GlideApp.with(getActivity())
            .load(R.drawable.baseline_fastfood_24)
            .placeholder(R.drawable.baseline_fastfood_24)
            .error(R.drawable.baseline_fastfood_24)
            .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
            .into(mImageView);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        String canteen = mCanteenView.getText().toString();

        if (view == mUploadView) {
            String dishName = mNameView.getText().toString();
            String dishPrice = mPriceView.getText().toString();
            String dishRemaining = mRemainingView.getText().toString();

            hideKeyboard(getCurrentFocus()); // 隐藏软键盘

            // TODO: 上传菜品至数据库

            mUploadView.showSucceed();
            Toast.makeText(DishUploadActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
            finish();

        } else if (view == mImageView) {
            ImageSelectActivity.start(this, data -> {
                cropImageFile(new File(data.get(0)));
            });

        } else if (view == mCanteenView) {
            int defaultSelect = 0;
            switch (canteen) {
                case "学三食堂（B1层）":
                    break;
                case "学五食堂（一层）":
                    defaultSelect = 1;
                    break;
                case "学六食堂（二层）":
                    defaultSelect = 2;
                    break;
                case "教工食堂（二层）":
                    defaultSelect = 3;
                    break;
            }

            new SelectDialog.Builder(this)
                .setTitle("请选择菜品食堂")
                .setList("学三食堂（B1层）", "学五食堂（一层）", "学六食堂（二层）", "教工食堂（二层）")
                .setSingleSelect()
                .setSelect(defaultSelect)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        mCanteenView.setText(data.values().iterator().next());
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消选择");
                    }
                })
                .show();
        }

    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                .navigationBarColor(R.color.white)
                .keyboardEnable(true);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    private void cropImageFile(File sourceFile) {
        ImageCropActivity.start(this, sourceFile, 1, 1, new ImageCropActivity.OnCropListener() {

            @Override
            public void onSucceed(Uri fileUri, String fileName) {
                File outputFile;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    outputFile = new FileContentResolver(getActivity(), fileUri, fileName);
                } else {
                    try {
                        outputFile = new File(new URI(fileUri.toString()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        outputFile = new File(fileUri.toString());
                    }
                }
                updateCropImage(outputFile, true);
            }

            @Override
            public void onError(String details) {
                updateCropImage(sourceFile, false);
            }
        });
    }

    private void updateCropImage(File file, boolean deleteFile) {
        Uri mAvatarUrl;
        if (file instanceof FileContentResolver) {
            mAvatarUrl = ((FileContentResolver) file).getContentUri();
        } else {
            mAvatarUrl = Uri.fromFile(file);
        }
        GlideApp.with(getActivity())
            .load(mAvatarUrl)
            .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
            .into(mImageView);

        try {
            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mAvatarUrl);
            this.storeImage(bitmapImage);
        } catch (Exception e) {
            Toast.makeText(this, "图片转换或上传出现问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void storeImage(Bitmap bitmapImage) {
        if (mImageView.getDrawable() != null && bitmapImage != null) {
            dataBaseHelper.updateUserAvatar(UserAuth.getLocalUserPhone(), bitmapImage);
        } else {
            Toast.makeText(this, "图片为空", Toast.LENGTH_SHORT).show();
        }
    }
}