package com.buaa.food.ui.activity;

import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.http.api.UpdateImageApi;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.http.model.HttpData;
import com.buaa.food.ui.dialog.AddressDialog;
import com.buaa.food.ui.dialog.InputDialog;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.layout.SettingBar;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class UserCenterActivity extends AppActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIdView;
    private SettingBar mNameView;
    private SettingBar mPasswordView;
    private DataBaseHelper dataBaseHelper;


    /** 头像地址 */
    private Uri mAvatarUrl;

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
        GlideApp.with(getActivity())
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mAvatarView);

        // DataBaseHelper dataBaseHelper = new DataBaseHelper(this.getContext());
        mIdView.setRightText(dataBaseHelper.getUserId(UserAuth.getLocalUserPhone()));
        mNameView.setRightText(dataBaseHelper.getUsername(UserAuth.getLocalUserPhone()));
        mPasswordView.setRightText("***");
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
            new InputDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(getString(R.string.personal_data_name_hint))
                    .setContent(mNameView.getRightText())
                    //.setHint(getString(R.string.personal_data_name_hint))
                    //.setConfirm("确定")
                    // 设置 null 表示不显示取消按钮
                    //.setCancel("取消")
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener((dialog, content) -> {
                        if (!mNameView.getRightText().equals(content)) {
                            mNameView.setRightText(content);
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
            return;
        }

        EasyHttp.post(this)
                .api(new UpdateImageApi()
                        .setImage(file))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        mAvatarUrl = Uri.parse(data.getData());
                        GlideApp.with(getActivity())
                                .load(mAvatarUrl)
                                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                .into(mAvatarView);
                        if (deleteFile) {
                            file.delete();
                        }
                    }
                });
    }
}