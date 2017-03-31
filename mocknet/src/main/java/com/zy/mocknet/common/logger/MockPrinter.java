package com.zy.mocknet.common.logger;

/**
 * Created by zy on 17-3-17.
 */
public class MockPrinter implements Printer {

    @Override
    public void v(String tag, String msg) {
        System.out.println("please init logger");
    }

    @Override
    public void d(String tag, String msg) {
        System.out.println("please init logger");
    }

    @Override
    public void w(String tag, String msg) {
        System.out.println("please init logger");
    }

    @Override
    public void e(String tag, String msg) {
        System.out.println("please init logger");
    }

    @Override
    public void exception(String tag, Exception exception) {
        System.out.println("please init logger");
    }
}
