package com.ethan.myclub.activity.detail.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.detail.adapter.ActivityContentAdapter;
import com.ethan.myclub.activity.detail.model.ActivityContent;
import com.ethan.myclub.activity.detail.model.LikeStatus;
import com.ethan.myclub.activity.detail.view.ActivityDetailActivity;
import com.ethan.myclub.activity.model.Activity;
import com.ethan.myclub.club.model.Tag;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.databinding.ActivityActivityDetailBinding;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.exception.ApiException;
import com.ethan.myclub.user.collection.view.UserCollectionActivity;
import com.ethan.myclub.util.ImageUtils;
import com.ethan.myclub.util.Utils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ActivityDetailViewModel {

    private final ActivityContentAdapter mAdapter;
    private final ViewGroup mTagsFlow;
    private EmptyView mEmptyView;
    private ActivityDetailActivity mActivity;
    private ActivityActivityDetailBinding mBinding;

    public ObservableField<ActivityResult> mActivityResult = new ObservableField<>();
    public ObservableField<Activity> mActivityDetail = new ObservableField<>();
    public ObservableField<String> mTime = new ObservableField<>();

    public ActivityDetailViewModel(ActivityDetailActivity activity, ActivityActivityDetailBinding binding, ActivityResult activityResult) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mActivityResult.set(activityResult);
        mTime.set(Utils.getStandardDate(activityResult.publishTime));
        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ActivityContentAdapter(mActivity, null);
        mAdapter.openLoadAnimation();
        mBinding.list.setAdapter(mAdapter);
        mTagsFlow = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.view_tags_flow, (ViewGroup) mBinding.list.getParent(), false);
        mAdapter.addHeaderView(mTagsFlow);


        mEmptyView = new EmptyView(mActivity);

        final String url;
        if (activityResult.isSpecial) {
            url = activityResult.specialIndexImage + "?imageView2/0/w/720/h/1080";
        } else {
            ViewGroup.LayoutParams layoutParams = mBinding.appbar.getLayoutParams();
            layoutParams.height = Utils.dp2px(mActivity, 300);
            url = activityResult.homePageImg;
        }
        update();
        Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                        Glide.with(mActivity)
                                .load(url)
                                .listener(new RequestListener<Object, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        e.printStackTrace();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        mBinding.appbar.setExpanded(true);
                                        return false;
                                    }
                                })
                                .crossFade()
                                .into(mBinding.ivActivityLogo);
                    }
                });
    }

    private static final int GET_ACTIVITY_RESULT_OK = -1;
    private static final int GET_ACTIVITY_RESULT_NOT_LOGIN = 2;
    private static final int GET_ACTIVITY_RESULT_ERROR = 3;
    private static final int GET_ACTIVITY_RESULT_NO_NETWORK = 4;

    private void notifyFinished(Activity activity, int resultCode) {

        if (resultCode == GET_ACTIVITY_RESULT_OK) {
            mBinding.list.setLayoutFrozen(false);

            activity.club.badge += "?imageView2/0/w/300/h/300";
            mActivityDetail.set(activity);

            if (activity.likeStatus)
                mBinding.fab.setImageResource(R.drawable.ic_like_red);
            else
                mBinding.fab.setImageResource(R.drawable.ic_like_white);

            List<ActivityContent> list = new ArrayList<>();
            int size1 = activity.contentImages.size();
            int size2 = activity.contentTexts.size();
            if (size1 != size2) {
                mAdapter.setNewData(null);
                mBinding.list.setLayoutFrozen(true);
                mAdapter.setEmptyView(mEmptyView);
                mEmptyView.showEmptyView("内容出错了", "内容的文字和照片数量不匹配");
                return;
            }
            if (size1 == 0) {
                mAdapter.setNewData(null);
                mBinding.list.setLayoutFrozen(true);
                mAdapter.setEmptyView(mEmptyView);
                mEmptyView.showEmptyView("还没有内容", "活动发布者还未上传活动详情");
                return;
            }

            for (int i = 0; i < size1; i++) {
                ActivityContent content = new ActivityContent(activity.contentImages.get(i), activity.contentTexts.get(i));
                content.url += "?imageView2/0/w/1080/h/720";
                list.add(content);
            }


            mAdapter.setNewData(list);
            mTagsFlow.removeAllViews();
            for (Tag tag : activity.tag) {
                TextView tv = new TextView(mActivity);
                tv.setText(tag.tagName);
                tv.setBackgroundResource(R.drawable.bg_tag);
                mTagsFlow.addView(tv);
                FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) tv.getLayoutParams();
                lp.setMargins(10, 10, 10, 10);
                lp.order = tag.tagName.length();
            }


        } else {
            switch (resultCode) {
                case GET_ACTIVITY_RESULT_ERROR:
                    mEmptyView.showErrorView(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update();
                        }
                    });
                    break;
                case GET_ACTIVITY_RESULT_NO_NETWORK:
                    mEmptyView.showNoNetWorkError();
                    break;
                case GET_ACTIVITY_RESULT_NOT_LOGIN:
                    mEmptyView.showNotLoginView();
                    break;
            }
            mAdapter.setNewData(null);
            mBinding.list.setLayoutFrozen(true);
            mAdapter.setEmptyView(mEmptyView);
        }
    }

    public void update() {
        ApiHelper.getProxy(mActivity)
                .getActivity(String.valueOf(mActivityResult.get().id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Activity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEmptyView.showLoadingView();
                        mAdapter.setNewData(null);
                        mBinding.list.setLayoutFrozen(true);
                        mAdapter.setEmptyView(mEmptyView);
                    }

                    @Override
                    public void onNext(Activity activity) {
                        notifyFinished(activity, GET_ACTIVITY_RESULT_OK);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ApiException && ((ApiException) e).getCode() == ApiException.NETWORK_ERROR)
                            notifyFinished(null, GET_ACTIVITY_RESULT_NO_NETWORK);
                        else
                            notifyFinished(null, GET_ACTIVITY_RESULT_ERROR);
                    }

                    @Override
                    public void onComplete() {
                        if (!MyApplication.isLogin())
                            notifyFinished(null, GET_ACTIVITY_RESULT_NOT_LOGIN);
                    }
                });
    }


    public void like() {
        mActivity.setResult(UserCollectionActivity.RESULT_CHANGED);
        if (mActivityDetail.get() == null)
            return;
        mActivityDetail.get().likeStatus = !mActivityDetail.get().likeStatus;
        if (mActivityDetail.get().likeStatus)
            mBinding.fab.setImageResource(R.drawable.ic_like_red);
        else
            mBinding.fab.setImageResource(R.drawable.ic_like_white);
        ApiHelper.getProxy(mActivity)
                .changeLikeStatus(String.valueOf(mActivityResult.get().id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LikeStatus>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LikeStatus likeStatus) {
                        mActivityResult.get().likeNum = likeStatus.likeNumber;
                        mActivityResult.notifyChange();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}