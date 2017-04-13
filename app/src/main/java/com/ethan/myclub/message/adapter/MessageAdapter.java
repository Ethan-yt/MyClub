package com.ethan.myclub.message.adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.view.MessageAnalysisActivity;
import com.ethan.myclub.message.view.MessageDetailClubActivity;
import com.ethan.myclub.message.viewmodel.MessageListViewModel;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.ImageUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/4/11.
 */

public class MessageAdapter extends BaseMultiItemQuickAdapter<Message, BaseViewHolder> {

    private final ViewBinderHelper binderHelper;

    private BaseActivity mBaseActivity;
    private MessageListViewModel mMessageListViewModel;
    private MyClub mMyClub;
    public boolean mAnalysisMode = false;
    public boolean mDeleteMode = false;

    public MessageAdapter(List<Message> data, @Nullable MyClub myclub, BaseActivity baseActivity, MessageListViewModel messageListViewModel) {
        super(data);
        addItemType(0, R.layout.item_message);
        binderHelper = new ViewBinderHelper();
        binderHelper.setOpenOnlyOne(true);
        mBaseActivity = baseActivity;
        mMessageListViewModel = messageListViewModel;
        mMyClub = myclub;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Message item) {
        String typeId = item.type;
        SwipeRevealLayout swipeRevealLayout = helper.getView(R.id.swipeLayout);
        if (mMyClub != null)
            swipeRevealLayout.setLockDrag(true);
        else
            binderHelper.bind(swipeRevealLayout, String.valueOf(item.id));
        String msgContent;
        helper.setText(R.id.tv_title, item.generateTitle());
        helper.setText(R.id.tv_content, item.generateContent());
        helper.setText(R.id.tv_time, item.standardTime);
        helper.getView(R.id.iv_not_read).setVisibility(item.isChecked ? View.INVISIBLE : View.VISIBLE);
        ImageUtils.loadImageUrl((ImageView) helper.getView(R.id.iv_image), item.image);
        helper.getView(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMessage(String.valueOf(item.id));
            }
        });

        switch (typeId) {
            case "0": //社团通知
                helper.getView(R.id.cv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnalysisMode) {
                            MessageAnalysisActivity.start(mBaseActivity, mMyClub, item);
                        } else if (mDeleteMode) {
                            new AlertDialog.Builder(mBaseActivity)
                                    .setTitle("提示")
                                    .setMessage("确定要将这条公告删除吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            removeClubMessage(String.valueOf(item.contentId));
                                        }
                                    })
                                    .setNeutralButton("点错了", null)
                                    .show();
                        } else {
                            MessageDetailClubActivity.start(mBaseActivity, item);
                        }

                    }
                });
                break;
            case "1": //申请加入社团

                helper.getView(R.id.cv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper.getView(R.id.iv_not_read).setVisibility(View.INVISIBLE);
                        setChecked(item);
                        NotificationManager nm = (NotificationManager) mBaseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.cancel(item.id);

                        new AlertDialog.Builder(mBaseActivity)
                                .setTitle("请处理")
                                .setMessage(item.generateContent())
                                .setPositiveButton("通过", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        manageApply(String.valueOf(item.clubId), "1", String.valueOf(item.senderId));
                                    }
                                })
                                .setNeutralButton("取消", null)
                                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        manageApply(String.valueOf(item.clubId), "0", String.valueOf(item.senderId));
                                    }
                                })
                                .show();
                    }
                });
                break;
            case "2": //加入通过或拒绝
                NotificationManager nm = (NotificationManager) mBaseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(item.id);
                helper.getView(R.id.iv_not_read).setVisibility(View.INVISIBLE);
                setChecked(item);
                break;
        }
    }

    private void manageApply(String clubId, String passed, String userId) {
        ApiHelper.getProxy(mBaseActivity)
                .manageApply(clubId, userId, passed)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        mBaseActivity.showSnackbar("操作成功！");
                        mMessageListViewModel.update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void removeMessage(String msgId) {
        ApiHelper.getProxy(mBaseActivity)
                .removeUserMessage(msgId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        mBaseActivity.showSnackbar("删除成功！");
                        mMessageListViewModel.update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void removeClubMessage(String contentId) {
        ApiHelper.getProxy(mBaseActivity)
                .deleteClubMessage(String.valueOf(mMyClub.clubId), contentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        mMessageListViewModel.update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBaseActivity.showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void setChecked(Message msg) {
        ApiHelper.getProxy(mBaseActivity)
                .setUserReadStatus(String.valueOf(msg.id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

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
