package com.ethan.myclub.message.viewmodel;

import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.view.MessageDetailClubActivity;
import com.ethan.myclub.databinding.ActivityMessageDetailClubBinding;
import com.ethan.myclub.network.ApiHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MessageDetailClubViewModel {

    private MessageDetailClubActivity mActivity;
    private ActivityMessageDetailClubBinding mBinding;

    public Message mMessage;

    public MessageDetailClubViewModel(MessageDetailClubActivity activity, ActivityMessageDetailClubBinding binding, Message message) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMessage = message;
        mActivity.getToolbarWrapper()
                .setTitle("消息详情")
                .showBackIcon()
                .show();
        if (!message.isChecked)
            setChecked();
    }

    private void setChecked() {
        ApiHelper.getProxy(mActivity)
                .setUserReadStatus(String.valueOf(mMessage.id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (mMessage.getItemType() == 0)
                            mActivity.showSnackbar("消息已阅");
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