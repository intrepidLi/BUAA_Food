package com.buaa.food.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.UserAuth;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.dialog.InputDialog;
import com.buaa.food.ui.fragment.CommentsFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.bar.TitleBar;
import com.hjq.base.FragmentPagerAdapter;

public class CommentActivity extends AppActivity implements TextView.OnEditorActionListener{
    private int dishId;
    private int commentId;
    private TitleBar mTitleBar;
    private LinearLayout mLinearLayout;
    private LinearLayout mCommentLinearLayout;
    private TextView commentView;
    private TextView userView;
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private DataBaseHelper dataBaseHelper;
    private AppCompatButton mCommentButton;

    protected int getLayoutId() {
        return R.layout.comment_activity;
    }

    protected void initView() {
        dataBaseHelper = new DataBaseHelper(this);

        dishId = getIntent().getIntExtra("dishId", 0);
        commentId = getIntent().getIntExtra("commentId", 0);

        mLinearLayout = findViewById(R.id.comment_activity);
        mCommentLinearLayout = findViewById(R.id.one_comment);

        mTitleBar = findViewById(R.id.comment);
        mViewPager = findViewById(R.id.vp_comment_pager);
        mPagerAdapter = new FragmentPagerAdapter<>(this);

        if (dishId != 0 && commentId == 0) {
            mTitleBar.setTitle("一级评论");
            mPagerAdapter.addFragment(CommentsFragment.newInstance(dishId, CommentsFragment.StatusType.CommentOne));
            mLinearLayout.removeView(mCommentLinearLayout);
        } else if (dishId == 0 && commentId != 0) {
            mTitleBar.setTitle("二级评论");
            mPagerAdapter.addFragment(CommentsFragment.newInstance(commentId, CommentsFragment.StatusType.CommentTwo));
            String comment = dataBaseHelper.getComment(commentId);
            String userName = dataBaseHelper.getCommentUserName(commentId);
            String time = dataBaseHelper.getCommentTime(commentId);
            commentView = findViewById(R.id.tv_comment_content);
            userView = findViewById(R.id.tv_comment_time);
            commentView.setText(comment);
            userView.setText(userName + "  " + time);
        }

        mViewPager.setAdapter(mPagerAdapter);

        mCommentButton = findViewById(R.id.btn_comment);

        setOnClickListener(mCommentButton);

        this.dataBaseHelper = new DataBaseHelper(this);
    }


    protected void initData() {
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommentButton) {
            new InputDialog.Builder(this)
                    .setTitle("评论")
                    .setContent("请输入评论内容")
                    .setHint("评论内容")
                    .setConfirm("确定")
                    .setCancel("取消")
                    .setAutoDismiss(true)
                    .setListener((dialog, content) -> {
                        if (content.isEmpty()) {
                            toast("请输入评论内容");
                        }
                        if (dishId != 0 && commentId == 0) {
                            dataBaseHelper.insertComment(dishId, content, dataBaseHelper
                                    .getUserId(UserAuth.getLocalUserPhone()));
                            toast("评论成功");
                            finish();
                        } else if (dishId == 0 && commentId != 0) {
                            dataBaseHelper.insertSecondComment(commentId, content, dataBaseHelper
                                    .getUserId(UserAuth.getLocalUserPhone()));
                            toast("评论成功");
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }
}
