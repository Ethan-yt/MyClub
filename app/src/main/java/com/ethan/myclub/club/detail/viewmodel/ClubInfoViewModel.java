package com.ethan.myclub.club.detail.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.detail.view.ActivityDetailActivity;
import com.ethan.myclub.club.detail.adapter.ClubDetailActivityAdapter;
import com.ethan.myclub.club.edit.view.ClubInfoEditActivity;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.model.Tag;
import com.ethan.myclub.club.detail.view.ClubInfoActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.discover.activity.adapter.ActivityAdapter;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.ToolbarWrapper;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.Utils;
import com.google.android.flexbox.FlexboxLayout;

import org.w3c.dom.Text;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClubInfoViewModel {


    private final ClubDetailActivityAdapter mAdapter;
    private final TextView mEmptyView;
    private ClubInfoActivity mActivity;
    private ActivityClubInfoBinding mBinding;

    public ObservableField<Club> mClub = new ObservableField<>();

    public MyClub mMyClub;

    public ClubInfoViewModel(ClubInfoActivity activity, ActivityClubInfoBinding binding, final MyClub myclub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myclub;
        ToolbarWrapper.Builder builder = new ToolbarWrapper.Builder(mActivity)
                .setTitle("社团简介", true)
                .setColor(Color.WHITE)
                .transparent()
                .showBackIcon()
                .target(mBinding.constraintLayout);
        if (myclub.checkPermission(1))
            builder.setMenu(R.menu.toolbar_edit_white, new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ClubInfoEditActivity.startForResult(mActivity, mClub.get(), ClubInfoActivity.REQUEST_EDIT_CLUB_INFO);
                    return false;
                }
            });
        builder.show();

        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateClubDetail();
                updateClubActivity();
            }
        });
        updateClubDetail();


        mAdapter = new ClubDetailActivityAdapter(mActivity, null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityDetailActivity.start(mActivity, (ActivityResult) adapter.getData().get(position));
            }
        });


        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.list.setAdapter(mAdapter);
        mEmptyView = new TextView(mActivity);
        mEmptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mEmptyView.setGravity(Gravity.CENTER);
        updateClubActivity();
    }

    private void updateClubActivity() {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getClubActivity(String.valueOf(mMyClub.clubId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ActivityResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEmptyView.setText("加载中");
                        mAdapter.setNewData(null);
                        mBinding.list.setLayoutFrozen(true);
                        mAdapter.setEmptyView(mEmptyView);
                    }

                    @Override
                    public void onNext(List<ActivityResult> activityResults) {
                        for (ActivityResult activityResult : activityResults) {
                            activityResult.homePageImg += "?imageView2/0/w/300/h/300";
                        }

                        if (activityResults.size() == 0) {
                            mEmptyView.setText("当前社团还未发布任何活动");
                            mAdapter.setNewData(null);
                            mBinding.list.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            mBinding.list.setLayoutFrozen(false);
                            formatOrder(activityResults);
                            mAdapter.setNewData(activityResults);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mEmptyView.setText("发生了错误");
                        mAdapter.setNewData(null);
                        mBinding.list.setLayoutFrozen(true);
                        mAdapter.setEmptyView(mEmptyView);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void formatOrder(List<ActivityResult> activityList) {
        ActivityResult specialActivity = null;
        for (ActivityResult activity : activityList) {
            if (activity.isSpecial) {
                specialActivity = activity;
                activityList.remove(activity);
                break;
            }
        }
        if (specialActivity != null)
            activityList.add(0, specialActivity);
    }

    public void updateClubDetail() {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getClub(String.valueOf(mMyClub.clubId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(Club club) {
                        club.badge = club.badge + "?imageView2/0/w/300/h/300";
                        mClub.set(club);
                        mBinding.flTags.removeAllViews();
                        for (Tag tag : club.tag) {
                            TextView tv = new TextView(mActivity);
                            tv.setText(tag.tagName);
                            tv.setBackgroundResource(R.drawable.bg_tag);
                            mBinding.flTags.addView(tv);
                            FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) tv.getLayoutParams();
                            lp.setMargins(10, 10, 10, 10);
                            lp.order = tag.tagName.length();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public void add() {
        ApiHelper.getProxy(mActivity)
                .joinClub(String.valueOf(mClub.get().id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object club) {
                        mActivity.showSnackbar("请求已提交");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}