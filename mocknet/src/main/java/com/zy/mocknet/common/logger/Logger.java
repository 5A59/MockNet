package com.zy.mocknet.common.logger;

/**
 * Created by zy on 17-3-5.
 */
public class Logger {

    private static final String DEFAULT_TAG = "MOCK_NET";
    private static String tag = DEFAULT_TAG;
    private static volatile Printer printer = new MockPrinter();

    private Logger() {

    }

    public static synchronized void init() {
        printer = new JavaPrinter();
    }

    public static synchronized void init(String tag) {
        Logger.tag = tag;
        printer = new JavaPrinter();
    }

    public static synchronized void init(Printer printer) {
        if (printer == null) {
            return ;
        }
        Logger.printer = printer;
    }

    public static void v(String msg) {
        printer.v(tag, msg);
    }

    public static void v(String tag, String msg) {
        printer.v(tag, msg);
    }

    public static void d(String msg) {
        d(tag, msg);
    }

    public static void d(String tag, String msg) {
        printer.d(tag, msg);
    }

    public static void w(String msg) {
        w(tag, msg);
    }

    public static void w(String tag, String msg) {
        printer.w(tag, msg);
    }

    public static void e(String msg) {
        e(tag, msg);
    }

    public static void e(String tag, String msg) {
        printer.e(tag, msg);
    }

    public static void exception(Exception e) {
        exception(tag, e);
    }

    public static void exception(String tag, Exception e) {
        printer.exception(tag, e);
    }

}
