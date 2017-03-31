package com.zy.mocknet.common.logger;

/**
 * Created by zy on 17-3-10.
 */
public class JavaPrinter implements Printer {

    public JavaPrinter() {
    }

    @Override
    public void v(String tag, String msg) {
        print(tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        print(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        print(tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        print(tag, msg);
    }

    private void print(String tag, String msg) {
        String[] spMsg = formatString(msg);
        for (String s : spMsg) {
            printTag(tag);
            System.out.println(msg);
        }
    }

    @Override
    public void exception(String tag, Exception exception) {
        printTag(tag);
        System.out.println(exception.getMessage());
    }

    public void printTag(String tag) {
        if (tag == null) {
            return ;
        }
        System.out.print(tag + " : ");
    }

    private static String[] formatString(String msg) {
        if (msg == null) {
            return new String[] {"null"};
        }
        return msg.split("\n");
    }
}
