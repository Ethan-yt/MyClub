package com.ethan.myclub.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AnyThread;

import com.ethan.myclub.user.schedule.model.Schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
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

}
