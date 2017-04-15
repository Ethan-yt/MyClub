package com.ethan.myclub.main;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Parcel;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.ethan.myclub.BuildConfig;
import com.ethan.myclub.user.model.Token;
import com.ethan.myclub.user.model.Profile;
import com.ethan.myclub.util.Utils;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by ethan on 2017/4/6.
 */

public class MyApplication extends Application {

    private static final String APP_ID = "2882303761517560870";

    private static final String APP_KEY = "5211756044870";

    public static final String TAG = "MyApplication";
    public static final String MIPUSH_TAG = "MIPUSH";

    public static final String FILE_NAME_TOKEN = "Token.dat";

    public static String sOldUID = "";

    static private Token sToken;
    @Nullable
    static public Profile sProfile;

    public static void setToken(Context context, Token token) {
        Parcel parcel = Parcel.obtain();
        if (token != null)
            token.writeToParcel(parcel, 0);// 保存token
        Utils.saveParcelToFile(context, FILE_NAME_TOKEN, parcel);
        if (sToken != null)
            sOldUID = sToken.uid;
        else
            sOldUID = "anonymous";
        sToken = token;

        resetUID(context);
    }

    public static void resetUID(Context context) {
        if (isLogin()) {
            Log.i(TAG, "readToken: Finished!: token is: " + sToken.mAccessToken);
            MiPushClient.unsetAlias(context, sOldUID, null);
            MiPushClient.setAlias(context, sToken.uid, null);
            Log.e(TAG, "Try to unset UID: " + sOldUID + " set UID: " + sToken.uid);
        } else {
            MiPushClient.unsetAlias(context, sOldUID, null);
            MiPushClient.setAlias(context, "anonymous", null);
            Log.e(TAG, "Try to unset UID: " + sOldUID + " set UID: anonymous");
        }
    }

    public static Token getToken() {
        return sToken;
    }

    public static boolean isLogin() {
        return (sToken != null && !TextUtils.isEmpty(sToken.uid));
    }


    public void readToken(Context context) {
        Parcel parcel = Utils.readParcelFromFile(context, FILE_NAME_TOKEN);
        if (parcel == null) {
            Log.e(TAG, "readToken: Failed!,parcel = null!");
            return;
        }
        sToken = Token.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        Log.i(TAG, "readToken: Finished!: token is: " + sToken.mAccessToken);
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this); //启用Stetho的Debug模式
            MobclickAgent.setDebugMode(true);//启用umeng统计的Debug模式
        } else {
            Bugly.init(this, "ff50329b00", false); //若非Debug模式记录Bugly日志
        }
        super.onCreate();

        readToken(getApplicationContext());//读取登录状态

        initMiPush();//初始化小米推送
    }

    private void initMiPush() {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(MIPUSH_TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(MIPUSH_TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        if (!BuildConfig.DEBUG) {
            // 安装tinker
            Beta.installTinker();
        }

    }
}
