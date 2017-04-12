package com.ethan.myclub.message.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.view.MessageListActivity;
import com.ethan.myclub.network.ApiHelper;
import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


@Keep
public class MiPushMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MiPushMessageReceiver";
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(final Context context, MiPushMessage message1) {
        Log.i(TAG, "onReceivePassThroughMessage: ");
        final Message message = new Gson().fromJson(message1.getContent(), Message.class);
        MainActivity.needUpdateFlag.userUnreadCount = true;

        switch (message.getItemType()) {
            case 0:
                Intent intent = new Intent(context, MessageListActivity.class);
                //PendingIntent.FLAG_ONE_SHOT
                PendingIntent pendingIntent = PendingIntent.getActivity(context, message.getItemType(), intent, 0);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(message.club + " 通知")
                                .setContentText(message.title)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setDefaults(Notification.DEFAULT_VIBRATE);

                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotifyMgr.notify(message.id, mBuilder.build());
        }
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
//            mUserAccount = message.getUserAccount();
//        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                if (MyApplication.isLogin() && MyApplication.sPushRegID.isEmpty())
                    ApiHelper.getInstance()
                            .submitRegId(cmdArg1)
                            .subscribeOn(Schedulers.io())
                            .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                                @Override
                                public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                                    return throwableObservable.zipWith(Observable.range(1, 3), new BiFunction<Throwable, Integer, Object>() {
                                        @Override
                                        public Object apply(@NonNull Throwable throwable, @NonNull Integer integer) throws Exception {
                                            Log.e(TAG, "onNext: RegId submitted error! test" + integer, throwable);
                                            return integer;
                                        }
                                    });
                                }
                            })
                            .observeOn(Schedulers.io())
                            .subscribe(new Observer<Object>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Object o) {
                                    Log.d(TAG, "onNext: RegId has submitted!");
                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                else
                    MyApplication.sPushRegID = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
    }
}