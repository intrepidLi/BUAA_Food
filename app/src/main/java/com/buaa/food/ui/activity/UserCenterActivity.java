package com.buaa.food.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.ui.dialog.InputDialog;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.layout.SettingBar;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public final class UserCenterActivity extends AppActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIdView;
    private SettingBar mNameView;
    private SettingBar mPasswordView;
    private DataBaseHelper dataBaseHelper;


    /** 头像地址 */
    private Uri mAvatarUrl;
    private Bitmap bitmapImage;
    private ImageView uploadImage;

    @Override
    protected int getLayoutId() {
        return R.layout.user_center_activity;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIdView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
        mPasswordView = findViewById(R.id.sb_person_data_password);
        setOnClickListener(mPasswordView, mAvatarLayout, mAvatarView, mNameView);
        dataBaseHelper = new DataBaseHelper(this);
    }

    @Override
    protected void initData() {
        // 设置头像
        byte[] image = dataBaseHelper.getUserAvatar(UserAuth.getLocalUserPhone());
        if (image != null && image.length > 0) {
            Bitmap avatar = BitmapFactory.decodeByteArray(image, 0, image.length);

            GlideApp.with(getActivity())
                    .load(avatar)
                    .placeholder(R.drawable.avatar_placeholder_ic)
                    .error(R.drawable.avatar_placeholder_ic)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mAvatarView);
        } else {
            GlideApp.with(getActivity())
                    .load(R.drawable.avatar_placeholder_ic)
                    .placeholder(R.drawable.avatar_placeholder_ic)
                    .error(R.drawable.avatar_placeholder_ic)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mAvatarView);
        }
        // DataBaseHelper dataBaseHelper = new DataBaseHelper(this.getContext());
        mIdView.setRightText(dataBaseHelper.getUserId(UserAuth.getLocalUserPhone()));
        mNameView.setRightText(dataBaseHelper.getUsername(UserAuth.getLocalUserPhone()));
        mPasswordView.setRightText("******");
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mAvatarLayout) {
            ImageSelectActivity.start(this, data -> {
                // 裁剪头像
                cropImageFile(new File(data.get(0)));
            });
        } else if (view == mPasswordView) {
            startActivity(PasswordResetActivity.class);
        }else if (view == mAvatarView) {
            if (mAvatarUrl != null) {
                // 查看头像
                ImagePreviewActivity.start(getActivity(), mAvatarUrl.toString());
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (view == mNameView) {
            AtomicBoolean isContentChanged = new AtomicBoolean(false);
            CharSequence oldName = mNameView.getRightText();

            new InputDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(getString(R.string.personal_data_name_hint))
                    .setContent(mNameView.getRightText())
                    .setHint(getString(R.string.personal_data_name_hint))
                    .setConfirm("确定")
                    // 设置 null 表示不显示取消按钮
                    .setCancel("取消")
                    // 设置点击按钮后不关闭对话框
                    .setAutoDismiss(true)
                    .setListener((dialog, content) -> {
                        if (!mNameView.getRightText().equals(content)) {
                            mNameView.setRightText(content);

                            if (mNameView.getRightText().toString().isEmpty()) {
                                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                                mNameView.setRightText(oldName);
                            } else {
                                // Toast.makeText(this, "用户名已修改", Toast.LENGTH_SHORT).show();
                                isContentChanged.set(true);
                                Toast.makeText(this, "用户名已修改1", Toast.LENGTH_SHORT).show();
                                // DataBaseHelper dataBaseHelper = new DataBaseHelper(this.getContext());
                                dataBaseHelper.updateUsername(UserAuth.getLocalUserPhone(),
                                        mNameView.getRightText().toString());
                            }
                        }
                    })
                    .show();
        }

    }

    /**
     * 裁剪图片
     */
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
                // 没有的话就不裁剪，直接上传原图片
                // 但是这种情况极其少见，可以忽略不计
                updateCropImage(sourceFile, false);
            }
        });
    }

    /**
     * 上传裁剪后的图片
     */
    private void updateCropImage(File file, boolean deleteFile) {
        Toast.makeText(this, "要上传图片1", Toast.LENGTH_SHORT).show();
        Timber.tag("updateCropImage").d("要上传图片1");
        if (true) {
            if (file instanceof FileContentResolver) {
                mAvatarUrl = ((FileContentResolver) file).getContentUri();
            } else {
                mAvatarUrl = Uri.fromFile(file);
            }
            GlideApp.with(getActivity())
                    .load(mAvatarUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mAvatarView);
            // return;
        }
        try {
            Toast.makeText(this, "要上传图片", Toast.LENGTH_SHORT).show();
            bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mAvatarUrl);
            this.storeImage();
            // uploadImage.setImageBitmap(bitmapImage);
        } catch (Exception e) {
            Toast.makeText(this, "图片转换或上传出现问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }




//        EasyHttp.post(this)
//                .api(new UpdateImageApi()
//                        .setImage(file))
//                .request(new HttpCallback<HttpData<String>>(this) {
//
//                    @Override
//                    public void onSucceed(HttpData<String> data) {
//                        mAvatarUrl = Uri.parse(data.getData());
//                        GlideApp.with(getActivity())
//                                .load(mAvatarUrl)
//                                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
//                                .into(mAvatarView);
//                        if (deleteFile) {
//                            file.delete();
//                        }
//                    }
//                });
    }

    private void storeImage() {

        if (mAvatarView.getDrawable() != null && bitmapImage != null) {
            // DataBaseHelper dataBaseHelper = new DataBaseHelper(this.getContext());
            dataBaseHelper.updateUserAvatar(UserAuth.getLocalUserPhone(), bitmapImage);
        } else {
            Toast.makeText(this, "图片为空", Toast.LENGTH_SHORT).show();
        }
    }
}