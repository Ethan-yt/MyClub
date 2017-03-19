package com.ethan.myclub.global;

import com.ethan.myclub.user.login.model.Token;

import okhttp3.Credentials;

/**
 * Created by ethan on 2017/3/5.
 */

public class Preferences {

    static public Token sToken;
    //static public String sToken;

    static public boolean isLogined() {
        return (sToken != null);
    }

    final static private String CLIENT_ID = "N4KeCoCo530CIotQW9QL7LaOxudoOs5a7STrrb4Q";

    final static private String CLIENT_SECRET = "sSrIu0NTlCtljOBRcv0otpHZmdDpbpNq4l1svvTIYbXDXHcEsq8ujuFIhuUWwSQ24hmdu3ou3LWGQ1vLqTJaiZxJ33LiZbxh7dWLCdzgi6taCEp0DmjTBNYXKAIHOlvu";

    final static public String CLIENT_CREDENTIALS = Credentials.basic(CLIENT_ID,CLIENT_SECRET);

}
