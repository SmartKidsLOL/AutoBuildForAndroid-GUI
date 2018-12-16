package com.wjr.andToZip.utils;

/**
 * Created by WangJinRui on 2018/3/13.
 */

public class StringUtils {

    public static boolean isEmptys(String... strs) {

        for (String str : strs) {
            if (str == null || str.trim().equals("")) {
                return true;
            }
        }

        return false;

    }

}
