package com.ethan.myclub.user.login.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ethan.myclub.databinding.ActivityLoginRegisterBinding;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.login.model.Valid;
import com.ethan.myclub.user.login.view.RegisterActivity;
import com.ethan.myclub.user.login.view.RegisterActivity2;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CountryListView;
import cn.smssdk.gui.GroupListView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/18.
 */

public class RegisterViewModel {


    private final RegisterActivity mActivity;
    private ActivityLoginRegisterBinding mBinding;
    public ObservableField<String> mCountryName = new ObservableField<>("台湾");
    public ObservableField<String> mCountryCode = new ObservableField<>("886");
    public ObservableField<String> mPhoneNumber = new ObservableField<>();
    public ObservableBoolean mIsSendBtnClickable = new ObservableBoolean(true);
    public ObservableField<String> mSendBtnText = new ObservableField<>("发送验证码");
    public ObservableField<String> mVerifyCode = new ObservableField<>();

    public RegisterViewModel(RegisterActivity registerActivity, ActivityLoginRegisterBinding binding) {
        mActivity = registerActivity;
        mBinding = binding;
        mBinding.setViewModel(this);

        mActivity.getToolbarWrapper()
                .setTitle("验证手机号")
                .showBackIcon()
                .show();
        //Init SMSSDK
        SMSSDK.initSDK(mActivity, "1b8ee6770f538", "3f490908a071256b009580392a5b312a");
    }

    public void selectCountry() {
        mActivity.showWaitingDialog("请稍候", "正在获取国家列表");

        //Log.e("0", "-------线程:" + Thread.currentThread().getName());
        Observable.create(new ObservableOnSubscribe<ArrayList>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList> e) throws Exception {
                SMSSDK.registerEventHandler(new EventHandler() {
                    @Override
                    public void afterEvent(int event, int result, Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                //返回支持发送验证码的国家列表
                                if (data instanceof ArrayList) {
                                    e.onNext((ArrayList) data);
                                }
                            }
                        } else {
                            e.onError((Throwable) data);
                        }
                        SMSSDK.unregisterAllEventHandler();//防止内存泄漏
                    }
                });
                SMSSDK.getSupportedCountries();
            }
        })
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList, HashMap>() {
                    @Override
                    public HashMap apply(ArrayList arrayList) throws Exception {

                        HashMap<String, String> countryRules = new HashMap<String, String>();
                        for (Object countryObject : arrayList) {
                            if (countryObject instanceof HashMap) {
                                Map country = (HashMap) countryObject;
                                String code = (String) country.get("zone");
                                String rule = (String) country.get("rule");
                                if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                                    continue;
                                }
                                countryRules.put(code, rule);
                            }
                        }
                        return countryRules;
                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HashMap>() {
                    @Override
                    public void accept(final HashMap hashMap) throws Exception {
                        //成功
                        Log.e("3", "-------线程:" + Thread.currentThread().getName());
                        final CountryListView countryListView = new CountryListView(mActivity);

                        final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                                .setTitle("请选择国家或地区")
                                .setView(countryListView)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create();

                        countryListView.setOnItemClickListener(new GroupListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(GroupListView groupListView, View view, int group, int position) {
                                if (position >= 0) {
                                    String[] country = countryListView.getCountry(group, position);
                                    if (hashMap != null && hashMap.containsKey(country[1])) {
                                        mActivity.showSnackbar("已经设置您的国家代码为：" + country[1]);
                                        String countryCode = country[1];
                                        String countryName = country[0];
                                        mCountryCode.set(countryCode);
                                        mCountryName.set(countryName);
                                        dialog.dismiss();
                                    } else {
                                        mActivity.showSnackbar("暂时不支持这个国家或地区");
                                    }
                                }
                            }
                        });

                        mActivity.dismissDialog();
                        dialog.show();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //失败
                        throwable.printStackTrace();
                        mActivity.showSnackbar(parseErrorMessage(throwable));
                        mActivity.dismissDialog();
                    }
                });
    }

    public void sendSMS() {

        ApiHelper.getProxyWithoutToken(mActivity)
                .accountValid(mPhoneNumber.get())
                .flatMap(new Function<Valid, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Valid valid) throws Exception {
                        if (valid.valid) {
                            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                                    SMSSDK.registerEventHandler(new EventHandler() {
                                        @Override
                                        public void afterEvent(int event, int result, Object data) {
                                            if (result == SMSSDK.RESULT_COMPLETE) {
                                                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                                    e.onNext((Boolean) data);
                                                }
                                            } else {
                                                e.onError((Throwable) data);
                                            }
                                            SMSSDK.unregisterAllEventHandler();//防止内存泄漏
                                        }
                                    });
                                    SMSSDK.getVerificationCode(mCountryCode.get(), mPhoneNumber.get());
                                }
                            });
                        } else
                            return Observable.error(new Exception("当前手机号已经注册过了"));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mIsSendBtnClickable.set(false);
                        mActivity.showSnackbar("正在发送短信...");
                        mActivity.hideKeyboard();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        //成功
                        if (aBoolean) {
                            mActivity.showSnackbar("您已通过智能验证");
                            Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(@NonNull Long aLong) throws Exception {
                                    startRegisterActivity2();
                                }
                            });
                        } else {
                            mActivity.showSnackbar("短信已发送，请注意查收");
                            startCounting();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //失败
                        mActivity.showSnackbar(parseErrorMessage(e));
                        mIsSendBtnClickable.set(true);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void startCounting() {
        final int countTime = 60;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(Long aLong) throws Exception {
                        return countTime - aLong.intValue();
                    }
                })
                .take(countTime + 1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        mSendBtnText.set("等待" + integer + "秒");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsSendBtnClickable.set(true);
                        mSendBtnText.set("发送短信");
                    }

                    @Override
                    public void onComplete() {
                        mIsSendBtnClickable.set(true);
                        mSendBtnText.set("发送短信");
                    }
                });
    }

    public void nextStep() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(final ObservableEmitter<Object> e) throws Exception {
                //mBtnNext.setClickable(false);
                SMSSDK.registerEventHandler(new EventHandler() {
                    @Override
                    public void afterEvent(int event, int result, Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                e.onNext(data);
                            }
                        } else {
                            e.onError((Throwable) data);
                        }
                        SMSSDK.unregisterAllEventHandler();//防止内存泄漏
                    }
                });
                SMSSDK.submitVerificationCode(mCountryCode.get(), mPhoneNumber.get(), mVerifyCode.get());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(final Object o) throws Exception {
                        //成功验证
                        startRegisterActivity2();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //失败
                        mActivity.showSnackbar(parseErrorMessage(throwable));
                        mBinding.btnNext.setClickable(true);
                    }
                });

    }

    private void startRegisterActivity2() {
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(mActivity,
                        Pair.create((View) mBinding.btnNext, "trans_cv_next"));
        RegisterActivity2.startActivity(mActivity, mPhoneNumber.get(), options.toBundle());

    }

    private String parseErrorMessage(Throwable throwable) {
        String str = throwable.getMessage();
        //throwable.printStackTrace();
        try {
            //is Json?
            JSONObject object = new JSONObject(str);
            String des = object.optString("detail");//错误描述
            int status = object.optInt("status");//错误代码
            if (status > 0 && !TextUtils.isEmpty(des)) {
                return des;
            }
        } catch (Exception e) {
            return str;
        }
        return str;
    }
}
