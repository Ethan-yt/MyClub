package com.ethan.myclub.user.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/3/4.
 */

public class Token implements Parcelable {

    @SerializedName("user_id")
    @Expose
    public String uid;

    @SerializedName("access_token")
    @Expose
    public String mAccessToken;

    @SerializedName("token_type")
    @Expose
    public String mTokenType;

    @SerializedName("expires_in")
    @Expose
    public int mExpiresIn;

    @SerializedName("refresh_token")
    @Expose
    public String mRefreshToken;

    @SerializedName("scope")
    @Expose
    public String mScope;

    public Token() {

    }

    protected Token(Parcel in) {
        mAccessToken = in.readString();
        mTokenType = in.readString();
        mExpiresIn = in.readInt();
        mRefreshToken = in.readString();
        mScope = in.readString();
        uid = in.readString();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAccessToken);
        dest.writeString(mTokenType);
        dest.writeInt(mExpiresIn);
        dest.writeString(mRefreshToken);
        dest.writeString(mScope);
        dest.writeString(uid);
    }
}