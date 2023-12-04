package com.buaa.food.ui.activity.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.aop.Log;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.manager.InputTextManager;
import com.buaa.food.ui.activity.HomeActivity;
import com.buaa.food.ui.activity.ImageCropActivity;
import com.buaa.food.ui.activity.ImageSelectActivity;
import com.buaa.food.ui.activity.LoginActivity;
import com.buaa.food.ui.activity.SettingActivity;
import com.buaa.food.ui.dialog.SelectDialog;
import com.buaa.food.ui.fragment.MineFragment;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.view.SubmitButton;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public final class DishUploadActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private int dishId;

    private AppCompatImageView mImageView;
    private EditText mNameView;
    private EditText mPriceView;
    private EditText mRemainingView;
    private AppCompatTextView mCanteenView;
    private AppCompatTextView mWindowView;
    private SubmitButton mUploadView;

    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_upload_activity;
    }

    @Override
    protected void initView() {
        dishId = getIntent().getIntExtra("dishId", -1);

        mImageView = findViewById(R.id.iv_dish_image);
        mNameView = findViewById(R.id.et_dish_name);
        mPriceView = findViewById(R.id.et_dish_price);
        mRemainingView = findViewById(R.id.et_dish_remaining);
        mCanteenView = findViewById(R.id.tv_canteen);
        mWindowView = findViewById(R.id.tv_window);
        mUploadView = findViewById(R.id.btn_dish_upload);

        dataBaseHelper = new DataBaseHelper(this);

        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
            .addView(mNameView)
            .addView(mPriceView)
            .addView(mRemainingView)
            .setMain(mUploadView)
            .build();

        setOnClickListener(mImageView, mCanteenView, mWindowView, mUploadView);
    }

    @Override
    protected void initData() {
        if (dishId == -1) {
            // 添加菜品
            GlideApp.with(getActivity())
                .load(R.drawable.baseline_fastfood_24)
                .placeholder(R.drawable.baseline_fastfood_24)
                .error(R.drawable.baseline_fastfood_24)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mImageView);
        } else {
            // 修改菜品
            byte[] image = dataBaseHelper.getDishImage(dishId);
            if (image != null && image.length > 0) {
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                GlideApp.with(getActivity())
                    .load(imgBitmap)
                    .placeholder(R.drawable.baseline_fastfood_24)
                    .error(R.drawable.baseline_fastfood_24)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mImageView);
            } else {
                GlideApp.with(getActivity())
                    .load(R.drawable.baseline_fastfood_24)
                    .placeholder(R.drawable.baseline_fastfood_24)
                    .error(R.drawable.baseline_fastfood_24)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mImageView);
            }

            mNameView.setText(dataBaseHelper.getDishName(dishId));
            mPriceView.setText(String.valueOf(dataBaseHelper.getDishPrice(dishId)));
            mRemainingView.setText(String.valueOf(dataBaseHelper.getDishRemaining(dishId)));
            mCanteenView.setText(dataBaseHelper.getDishCanteen(dishId));
            mWindowView.setText(dataBaseHelper.getDishWindow(dishId));
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        String canteen = mCanteenView.getText().toString();

        if (view == mUploadView) {

            String dishName = mNameView.getText().toString();
            float dishPrice = Float.parseFloat(mPriceView.getText().toString());
            int dishRemaining = Integer.parseInt(mRemainingView.getText().toString());
            int canteenId = dataBaseHelper.getCanteenId(canteen);
            int windowId = dataBaseHelper.getWindowId(mWindowView.getText().toString());

            hideKeyboard(getCurrentFocus()); // 隐藏软键盘

            if (dishId == -1) {
                dataBaseHelper.insertDish(dishName, dishPrice, dishRemaining, windowId, canteenId);
                Toast.makeText(DishUploadActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
            } else {
                dataBaseHelper.updateDish(dishId, dishName, dishPrice, dishRemaining, windowId, canteenId);
                Toast.makeText(DishUploadActivity.this, "Edit Successfully", Toast.LENGTH_SHORT).show();
            }

            mUploadView.showSucceed();
            finish();

        } else if (view == mImageView) {
            ImageSelectActivity.start(this, data -> {
                cropImageFile(new File(data.get(0)));
            });

        } else if (view == mCanteenView) {
//            mWindowView.setText("选择窗口");

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

        } else if (view == mWindowView) {
            if (canteen.equals("选择食堂")) {
                toast("请先选择食堂");
            } else {
                switch (canteen) {
                    case "学三食堂（B1层）":
                        new SelectDialog.Builder(this)
                            .setTitle("请选择菜品窗口")
                            .setList(
                                    "三颗糖",
                                    "不止一面",
                                    "原蛊蒸饭",
                                    "新疆炒米粉",
                                    "日鉴一面（牛肉面）",
                                    "炉石披萨",
                                    "炸鸡汉堡",
                                    "猪肚鸡",
                                    "王宏龙馋嘴鱼",
                                    "米大碗",
                                    "缘味先石锅饭",
                                    "胡椒小屋"
                            )
                            .setSingleSelect()
                            .setSelect(0)
                            .setListener(new SelectDialog.OnListener<String>() {

                                @Override
                                public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                    mWindowView.setText(data.values().iterator().next());
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消选择");
                                }
                            })
                            .show();
                        break;
                    case "学五食堂（一层）":
                        new SelectDialog.Builder(this)
                            .setTitle("请选择菜品窗口")
                            .setList(
                                    "主食",
                                    "卤肉饭",
                                    "基本伙",
                                    "小碗菜",
                                    "渔粉",
                                    "滋补汤品",
                                    "熟食",
                                    "精品菜",
                                    "航味菜",
                                    "轻食套餐",
                                    "铁板拌饭",
                                    "面条",
                                    "麻辣香锅冒菜"
                            )
                            .setSingleSelect()
                            .setSelect(0)
                            .setListener(new SelectDialog.OnListener<String>() {

                                @Override
                                public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                    mWindowView.setText(data.values().iterator().next());
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消选择");
                                }
                            })
                            .show();
                        break;
                    case "学六食堂（二层）":
                        new SelectDialog.Builder(this)
                            .setTitle("请选择菜品窗口")
                            .setList(
                                    "一品粥饼",
                                    "五谷渔粉",
                                    "千里香馄饨",
                                    "咖喱饭",
                                    "广式烧腊",
                                    "桂林米粉",
                                    "湖南小碗菜",
                                    "炒河粉",
                                    "煎饼豆浆",
                                    "猛火炒饭",
                                    "石锅拌饭",
                                    "酸菜鱼",
                                    "铁板饭",
                                    "闽南浇汁拌饭",
                                    "鱼籽饭",
                                    "黄焖鸡米饭"
                            )
                            .setSingleSelect()
                            .setSelect(0)
                            .setListener(new SelectDialog.OnListener<String>() {

                                @Override
                                public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                    mWindowView.setText(data.values().iterator().next());
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消选择");
                                }
                            })
                            .show();
                        break;
                    case "教工食堂（二层）":
                        new SelectDialog.Builder(this)
                            .setTitle("请选择菜品窗口")
                            .setList(
                                    "统一窗口"
                            )
                            .setSingleSelect()
                            .setSelect(0)
                            .setListener(new SelectDialog.OnListener<String>() {

                                @Override
                                public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                    mWindowView.setText(data.values().iterator().next());
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消选择");
                                }
                            })
                            .show();
                        break;
                }
            }
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
            dataBaseHelper.updateDishImage(dishId, bitmapImage);
        } else {
            Toast.makeText(this, "图片为空", Toast.LENGTH_SHORT).show();
        }
    }
}