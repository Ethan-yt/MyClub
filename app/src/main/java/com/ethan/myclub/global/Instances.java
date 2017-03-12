package com.ethan.myclub.global;

import android.os.Environment;

import java.io.File;

/**
 * Created by ethan on 2017/3/12.
 */

public class Instances {

    /**
     * 获得本应用的外部存储文件夹
     * @return
     */
    public static File sExternalStorageDirectory() {
        return ExternalStorageDirectoryHolder.sExternalStorageDirectory;
    }

    private static class ExternalStorageDirectoryHolder {
        static File sExternalStorageDirectory;

        static {
            sExternalStorageDirectory = new File(Environment.getExternalStorageDirectory(), "com.ethan.myclub");
            if(!sExternalStorageDirectory.exists())
                if(!sExternalStorageDirectory.mkdirs())
                    throw new RuntimeException("无法创建外部存储文件夹");
        }
    }


}
