package com.ethan.myclub.user.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ethan.myclub.R;
import com.ethan.myclub.user.login.models.Valid;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.main.SnackbarActivity;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends SnackbarActivity {
    //private ProgressDialog mProgressDialog;
    private TextView mTvCountryCode;
    private TextView mTvCountryName;
    private Button mBtnSendSMS;
    private EditText mEtPhoneNumber;
    private CardView mBtnNext;
    private TextView mVerifyCode;
    private CardView mCvInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
            }
        });

        mTvCountryCode = (TextView) findViewById(R.id.tv_country_code);
        mTvCountryName = (TextView) findViewById(R.id.tv_country_name);
        mVerifyCode = (TextView) findViewById(R.id.et_verify_code);
        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mBtnSendSMS = (Button) findViewById(R.id.btn_sendSMS);
        mBtnNext = (CardView) findViewById(R.id.btn_next);
        mCvInput = (CardView) findViewById(R.id.cv_input);
        //Init SMSSDK
        SMSSDK.initSDK(this, "1b8ee6770f538", "3f490908a071256b009580392a5b312a");
        initCountry();
        initSendSMS();
        initSubmit();

    }

    private void initSubmit() {
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        SMSSDK.submitVerificationCode(mTvCountryCode.getText().toString(), mEtPhoneNumber.getText().toString(), mVerifyCode.getText().toString());
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(final Object o) throws Exception {
                                //成功验证
                                Intent intent = new Intent();
                                intent.putExtra("username", mEtPhoneNumber.getText().toString());
                                intent.setClass(RegisterActivity.this, RegisterActivity2.class);

                                ActivityOptionsCompat options = ActivityOptionsCompat
                                        .makeSceneTransitionAnimation(RegisterActivity.this,
                                                Pair.create((View) mCvInput, "trans_cv_input"),
                                                Pair.create((View) mBtnNext, "trans_cv_next"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    startActivity(intent, options.toBundle());
                                } else {
                                    startActivity(intent);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                //失败
                                showSnackbar(parseErrorMessage(throwable));
                                mBtnNext.setClickable(true);
                            }
                        });
            }
        });


    }

    private void initSendSMS() {
        mBtnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mBtnSendSMS.setClickable(false);
                final String phoneNumber = mEtPhoneNumber.getText().toString();
                ApiHelper.getProxyWithoutToken(RegisterActivity.this)
                        .accountValid(phoneNumber)
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
                                            SMSSDK.getVerificationCode(mTvCountryCode.getText().toString(), phoneNumber);
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
                                showSnackbar("正在发送短信...");
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                //成功
                                showSnackbar("短信已发送，请注意查收");
                                startCounting();
//                                if (b) {
//                                    //通过智能验证
//                                } else {
//                                    //依然走短信验证
//                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                //失败
                                showSnackbar(parseErrorMessage(e));
                                mBtnSendSMS.setClickable(true);

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
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

    private void initCountry() {

        mTvCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog("请稍候", "正在获取国家列表");

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
                                final CountryListView countryListView = new CountryListView(RegisterActivity.this);

                                final AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
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
                                                showSnackbar("已经设置您的国家代码为：" + country[1]);
                                                String countryCode = country[1];
                                                String countryName = country[0];
                                                mTvCountryCode.setText(countryCode);
                                                mTvCountryName.setText(countryName);
                                                dialog.dismiss();
                                            } else {
                                                showSnackbar("暂时不支持这个国家或地区");
                                            }
                                        }
                                    }
                                });

                                dismissDialog();
                                dialog.show();


                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                //失败
                                throwable.printStackTrace();
                                showSnackbar(parseErrorMessage(throwable));
                                dismissDialog();
                            }
                        });
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
                        mBtnSendSMS.setText("等待" + integer + "秒");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBtnSendSMS.setClickable(true);
                        mBtnSendSMS.setText("发送短信");
                    }

                    @Override
                    public void onComplete() {
                        mBtnSendSMS.setClickable(true);
                        mBtnSendSMS.setText("发送短信");
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = findViewById(R.id.container);
    }
}