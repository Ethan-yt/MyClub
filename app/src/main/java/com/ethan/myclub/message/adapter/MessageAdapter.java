package com.ethan.myclub.message.adapter;

import android.support.annotation.Nullable;
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

    public MessageAdapter(List<Message> data, @Nullable MyClub myclub, BaseActivity baseActivity, MessageListViewModel messageListViewModel) {
        super(data);
        addItemType(0, R.layout.item_message_club);
        binderHelper = new ViewBinderHelper();
        binderHelper.setOpenOnlyOne(true);
        mBaseActivity = baseActivity;
        mMessageListViewModel = messageListViewModel;
        mMyClub = myclub;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Message item) {
        int typeId = item.getItemType();
        SwipeRevealLayout swipeRevealLayout = helper.getView(R.id.swipeLayout);
        if(mMyClub != null)
            swipeRevealLayout.setLockDrag(true);

        switch (typeId) {
            case 0:
                helper.setText(R.id.tv_clubname, item.club);
                helper.setText(R.id.tv_time, item.standardTime);
                helper.setText(R.id.tv_brief, item.title);
                helper.setVisible(R.id.iv_not_read, !item.isChecked);
                ImageUtils.loadImageUrl((ImageView) helper.getView(R.id.iv_avatar), item.image);
                binderHelper.bind(swipeRevealLayout, String.valueOf(item.id));
                helper.getView(R.id.cv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper.setVisible(R.id.iv_not_read, false);
                        MessageDetailClubActivity.start(mBaseActivity, item);
                    }
                });
                helper.getView(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeMessage(String.valueOf(item.id));
                    }
                });
        }
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
                        mMessageListViewModel.update();
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
