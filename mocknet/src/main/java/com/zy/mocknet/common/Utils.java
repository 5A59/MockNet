package com.zy.mocknet.common;

import com.zy.mocknet.common.logger.Logger;

/**
 * Created by zy on 17-3-16.
 */
public class Utils {

    private static volatile Utils instance;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }
        return instance;
    }

    public boolean stringEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        return false;
    }

    public void outputTitle(String title) {
        StringBuilder builder = new StringBuilder();
        builder.append("=============================================\n");
        builder.append("=                  " + title + "                   \n");
        builder.append("=============================================\n");
        Logger.d(builder.toString());
    }

}
