package com.ethan.myclub.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AnyThread;

import com.ethan.myclub.user.schedule.model.Schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.Buffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/3/5.
 */

public class Utils {

    public static int Str2Int(String s) throws Exception {
        java.util.Map<String, Integer> numMap = new java.util.HashMap<String, Integer>();
        numMap.put("零", 0);
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);
        Integer ret = numMap.get(s);
        if (ret != null)
            return ret;
        throw new Exception("数字转换失败");
    }

    public static Parcel readParcelFromFile(Context context, String fileName) {
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            Parcel parcel = Parcel.obtain();
            if(fin.read(buffer) != -1)
            {
                parcel.unmarshall(buffer, 0, buffer.length);
                parcel.setDataPosition(0); // This is extremely important!
            }
            fin.close();
            return parcel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveParcelToFile(Context context, String fileName, Parcel parcel) {
        try {
            byte[] bytes = parcel.marshall();
            parcel.recycle();

            FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream.write(bytes);
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * RandomAccessFile 获取文件的MD5值
     *
     * @param file 文件路径
     * @return md5
     */
    public static String getFileMd5(File file) {
        MessageDigest messageDigest;
        RandomAccessFile randomAccessFile = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            if (file == null) {
                return "";
            }
            if (!file.exists()) {
                return "";
            }
            randomAccessFile=new RandomAccessFile(file,"r");
            byte[] bytes=new byte[1024*1024*10];
            int len=0;
            while ((len=randomAccessFile.read(bytes))!=-1){
                messageDigest.update(bytes,0, len);
            }
            BigInteger bigInt = new BigInteger(1, messageDigest.digest());
            String md5 = bigInt.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
}
