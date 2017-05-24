package com.zy.mocknet.common.logger;

import android.util.Log;

/**
 * Created by zy on 17-3-10.
 */
public class AndroidPrinter implements Printer {

    public AndroidPrinter() {

    }

    @Override
    public void v(String tag, String msg) {
        String[] spMsg = formatString(msg);
        for (String s : spMsg) {
            Log.v(tag, s);
        }
    }

    @Override
    public void d(String tag, String msg) {
        String[] spMsg = formatString(msg);
        for (String s : spMsg) {
            Log.d(tag, s);
        }
    }

    @Override
    public void w(String tag, String msg) {
        String[] spMsg = formatString(msg);
        for (String s : spMsg) {
            Log.w(tag, s);
        }
    }

    @Override
    public void e(String tag, String msg) {
        String[] spMsg = formatString(msg);
        for (String s : spMsg) {
            Log.e(tag, s);
        }

    }

    @Override
    public void exception(String tag, Exception exception) {
        exception.printStackTrace();
    }

    private static String[] formatString(String msg) {
        if (msg == null) {
            return new String[] {"null"};
        }
        return msg.split("\n");
    }
}
