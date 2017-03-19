package com.ethan.myclub.user.login.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/3/4.
 */

public class Token {
    @SerializedName("access_token")
    public String mAccessToken;

    @SerializedName("token_type")
    public String mTokenType;

    @SerializedName("expires_in")
    public int mExpiresIn;

    @SerializedName("refresh_token")
    public String mRefreshToken;

    @SerializedName("scope")
    public String mScope;
}