package com.ethan.myclub.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.util.Utils;

import org.w3c.dom.Text;

import okhttp3.Credentials;

/**
 * Created by ethan on 2017/3/5.
 */

public class Preferences {

    public static final String FILE_NAME_TOKEN = "Token.dat";
    private static final String TAG = "Preferences";

    static public SharedPreferences sSharedPreferences;

    static public void initPreferencesEngine(Context context)
    {
        sSharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        Parcel parcel = Utils.readParcelFromFile(context,FILE_NAME_TOKEN);
        if (parcel != null) {
            sToken = Token.CREATOR.createFromParcel(parcel);
            parcel.recycle();
        }

        if(sToken != null && !TextUtils.isEmpty(sToken.mAccessToken))
            sIsLogin = true;
        Log.d(TAG, "initPreferencesEngine: is login :" + isLogin());
    }

    final static private String CLIENT_ID = "N4KeCoCo530CIotQW9QL7LaOxudoOs5a7STrrb4Q";

    final static private String CLIENT_SECRET = "sSrIu0NTlCtljOBRcv0otpHZmdDpbpNq4l1svvTIYbXDXHcEsq8ujuFIhuUWwSQ24hmdu3ou3LWGQ1vLqTJaiZxJ33LiZbxh7dWLCdzgi6taCEp0DmjTBNYXKAIHOlvu";

    final static public String CLIENT_CREDENTIALS = Credentials.basic(CLIENT_ID, CLIENT_SECRET);

    static private Token sToken;


    public static void setToken(Context context,Token token) {
        sIsLogin = token != null;
        Parcel parcel = Parcel.obtain();
        if(token != null)
            token.writeToParcel(parcel,0);
        Utils.saveParcelToFile(context, FILE_NAME_TOKEN,parcel);

        sToken = token;
    }

    public static Token getToken() {
        return sToken;
    }

    static private boolean sIsLogin;

    public static boolean isLogin() {
        return sIsLogin;
    }


}
