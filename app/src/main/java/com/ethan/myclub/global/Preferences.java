package com.ethan.myclub.global;

import android.os.Environment;

import java.io.File;

/**
 * Created by ethan on 2017/3/5.
 */

public class Preferences {

    static public String sToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjA5MDk3NDc2ODMiLCJvcmlnX2lhdCI6MTQ4OTU0MzQxOCwidXNlcl9pZCI6NiwiZW1haWwiOm51bGwsImV4cCI6MTQ5MDE0ODIxOH0.iqqu5FUDZF_BECi9ljzygdC0KW8cGEQ_SMHKhzJXSi8";
    //static public String sToken;

    static public boolean isLogined() {
        return (sToken != null && !sToken.isEmpty());
    }

}
