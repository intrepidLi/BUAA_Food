package com.buaa.food.ui.activity.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.ui.activity.ImageCropActivity;
import com.buaa.food.ui.activity.ImageSelectActivity;
import com.buaa.food.ui.dialog.InputDialog;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.RegexEditText;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class UsersManageActivity extends AppActivity {

    private RegexEditText mHintView;
    private AppCompatImageView mAvatarView;
    private SettingBar mPhoneView;
    private SettingBar mNameView;
    private SettingBar mPasswordView;

    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.users_manage_activity;
    }

    @Override
    protected void initView() {
        mHintView = findViewById(R.id.tv_user_hint);
        mAvatarView = findViewById(R.id.iv_user_avatar);
        mPhoneView = findViewById(R.id.sb_user_phone);
        mNameView = findViewById(R.id.sb_user_name);
        mPasswordView = findViewById(R.id.sb_user_password);

        dataBaseHelper = new DataBaseHelper(this);

        setOnClickListener(mAvatarView, mPhoneView, mNameView, mPasswordView);

        mHintView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mHintView.getText() == null) {
                    return;
                }
                String hintPhone = mHintView.getText().toString();
                if (hintPhone.length() == 11 && dataBaseHelper.checkPhone(hintPhone)) {
                    byte[] image = dataBaseHelper.getUserAvatar(hintPhone);
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
                    mPhoneView.setRightText(hintPhone);
                    mNameView.setRightText(dataBaseHelper.getUsername(hintPhone));
                    mPasswordView.setRightText(dataBaseHelper.getUserPassword(hintPhone));
                } else {
                    mAvatarView.setImageResource(R.drawable.avatar_placeholder_ic);
                    mPhoneView.setRightText("");
                    mNameView.setRightText("");
                    mPasswordView.setRightText("");
                }
            }
        });
    }

    @Override
    protected void initData() {
        mAvatarView.setImageResource(R.drawable.avatar_placeholder_ic);

        mPhoneView.setRightText("");
        mNameView.setRightText("");
        mPasswordView.setRightText("");
    }

    @Override
    public void onClick(View view) {
        String hintPhone = Objects.requireNonNull(mHintView.getText()).toString();

        if (view == mPasswordView) {
            AtomicBoolean isContentChanged = new AtomicBoolean(false);
            CharSequence oldName = mPasswordView.getRightText();

            new InputDialog.Builder(this)
                .setTitle("设置用户密码")
                .setContent(mPasswordView.getRightText())
                .setHint("设置用户密码")
                .setConfirm("确定")
                .setCancel("取消")
                .setAutoDismiss(true)
                .setListener((dialog, content) -> {
                    if (!mPasswordView.getRightText().equals(content)) {
                        mPasswordView.setRightText(content);

                        if (mPasswordView.getRightText().toString().isEmpty()) {
                            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                            mPasswordView.setRightText(oldName);
                        } else {
                            isContentChanged.set(true);
                            Toast.makeText(this, "密码已修改", Toast.LENGTH_SHORT).show();
                            dataBaseHelper.updatePassword(hintPhone, mPasswordView.getRightText().toString());
                        }
                    }
                })
                .show();
        } else if (view == mAvatarView) {
            ImageSelectActivity.start(this, data -> {
                cropImageFile(new File(data.get(0)));
            });
        } else if (view == mNameView) {
            AtomicBoolean isContentChanged = new AtomicBoolean(false);
            CharSequence oldName = mNameView.getRightText();

            new InputDialog.Builder(this)
                .setTitle(getString(R.string.personal_data_name_hint))
                .setContent(mNameView.getRightText())
                .setHint(getString(R.string.personal_data_name_hint))
                .setConfirm("确定")
                .setCancel("取消")
                .setAutoDismiss(true)
                .setListener((dialog, content) -> {
                    if (!mNameView.getRightText().equals(content)) {
                        mNameView.setRightText(content);

                        if (mNameView.getRightText().toString().isEmpty()) {
                            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                            mNameView.setRightText(oldName);
                        } else {
                            isContentChanged.set(true);
                            Toast.makeText(this, "用户名已修改", Toast.LENGTH_SHORT).show();
                            dataBaseHelper.updateUsername(hintPhone, mNameView.getRightText().toString());
                        }
                    }
                })
                .show();
        }
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
                .into(mAvatarView);

        try {
            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mAvatarUrl);
            this.storeImage(bitmapImage);
        } catch (Exception e) {
            Toast.makeText(this, "图片转换或上传出现问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void storeImage(Bitmap bitmapImage) {
        String hintPhone = Objects.requireNonNull(mHintView.getText()).toString();
        if (mAvatarView.getDrawable() != null && bitmapImage != null) {
            dataBaseHelper.updateUserAvatar(hintPhone, bitmapImage);
        } else {
            Toast.makeText(this, "图片为空", Toast.LENGTH_SHORT).show();
        }
    }
}