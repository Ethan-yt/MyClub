package com.ethan.myclub.club.member.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.ethan.myclub.R;
import com.ethan.myclub.club.member.viewmodel.ClubMemberListViewModel;
import com.ethan.myclub.club.model.MemberResult;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.detail.view.UserDetailActivity;
import com.ethan.myclub.util.ImageUtils;

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

/**
 * Created by ethan on 2017/4/9.
 */

public class MemberAdapter extends BaseQuickAdapter<MemberResult, BaseViewHolder> {

    private BaseActivity mBaseActivity;
    private MyClub mMyClub;
    private ClubMemberListViewModel mViewModel;

    public MemberAdapter(int layout, List<MemberResult> data, BaseActivity baseActivity, MyClub myClub, ClubMemberListViewModel viewModel) {
        super(layout, data);
        mBaseActivity = baseActivity;
        mMyClub = myClub;
        mViewModel = viewModel;
    }

    @Override
    protected void convert(BaseViewHolder helper, final MemberResult item) {
        String name = item.name;
        if (TextUtils.isEmpty(name))
            name = "无名氏";
        helper.setText(R.id.tv_username, name);
        helper.setText(R.id.tv_title_name, mMyClub.getTitleNameFromMemberResult(item));
        helper.setText(R.id.tv_nickname, "昵称 " + item.nickname);

        ImageUtils.loadImageUrl((ImageView) helper.getView(R.id.iv_avatar), item.avatar);


        helper.getView(R.id.cv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserDetailActivity.start(mBaseActivity, item);
                    }
                });

        if (mMyClub.isCreator) {
            final SwipeRevealLayout swipeLayout = helper.getView(R.id.swipeLayout);
            helper.getView(R.id.btn_give)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(mBaseActivity)
                                    .setTitle("提示")
                                    .setMessage("转让社团后你将失去社长身份")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new AlertDialog.Builder(mBaseActivity)
                                                    .setTitle("再确认一下")
                                                    .setMessage("你真的想转让社团吗？")
                                                    .setPositiveButton("真的", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            give(item.userAccount);
                                                        }
                                                    })
                                                    .setNeutralButton("点错了", null)
                                                    .show();
                                        }
                                    })
                                    .setNeutralButton("点错了", null)
                                    .show();
                            swipeLayout.close(true);
                        }
                    });

            helper.getView(R.id.btn_remove)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(mBaseActivity)
                                    .setTitle("提示")
                                    .setMessage("确定要将" + item.nickname + "请出社团？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            remove(item.userAccount);
                                        }
                                    })
                                    .setNeutralButton("点错了", null)
                                    .show();
                            swipeLayout.close(true);
                        }
                    });

            helper.getView(R.id.btn_grant)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> titleNames = new ArrayList<>();
                            int selectedIndex = 0;
                            for (int i = 0; i < mMyClub.titleTable.size(); i++) {
                                if (mMyClub.titleTable.get(i).id.equals(item.title))
                                    selectedIndex = i;
                                titleNames.add(mMyClub.titleTable.get(i).titleName);
                            }

                            final int[] finalSelectedIndex = {selectedIndex};
                            new AlertDialog.Builder(mBaseActivity)
                                    .setSingleChoiceItems(titleNames.toArray(new String[0]), selectedIndex, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finalSelectedIndex[0] = which;
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            grant(item.userAccount, String.valueOf(mMyClub.titleTable.get(finalSelectedIndex[0]).id));
                                        }
                                    })
                                    .setNeutralButton("取消", null)
                                    .show();
                            swipeLayout.close(true);
                        }
                    });
        }

    }

    private void give(String userId) {
        ApiHelper.getProxy(mBaseActivity)
                .changeCreator(String.valueOf(mMyClub.clubId), userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBaseActivity.showWaitingDialog("请稍候", "转让中", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mBaseActivity.showSnackbar("操作成功！");
                        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {
                                        MainActivity.needUpdateFlag.clubList = true;
                                        MainActivity.startActivity(mBaseActivity, MainActivity.REQUEST_GIVE_CLUB, Activity.RESULT_OK);
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.dismissDialog();
                        mBaseActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBaseActivity.dismissDialog();
                    }
                });
    }

    private void remove(String userId) {
        ApiHelper.getProxy(mBaseActivity)
                .removeClubMember(String.valueOf(mMyClub.clubId), userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBaseActivity.showWaitingDialog("请稍候", "操作中", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mBaseActivity.showSnackbar("操作成功！");
                        mViewModel.update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.dismissDialog();
                        mBaseActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBaseActivity.dismissDialog();
                    }
                });
    }

    private void grant(String userId, String titleId) {
        ApiHelper.getProxy(mBaseActivity)
                .grantClubTitle(String.valueOf(mMyClub.clubId), userId, titleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBaseActivity.showWaitingDialog("请稍候", "操作中", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mBaseActivity.showSnackbar("操作成功！");
                        mViewModel.update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.dismissDialog();
                        mBaseActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBaseActivity.dismissDialog();
                    }
                });
    }
}
