package com.ethan.myclub.user.schedule;

/**
 * Created by ethan on 2017/2/11.
 */

public final class Utils {

    public final static int Str2Int(String s) throws Exception {
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
        if(ret != null)
            return ret;
        throw new Exception("数字转换失败");
    }

}
