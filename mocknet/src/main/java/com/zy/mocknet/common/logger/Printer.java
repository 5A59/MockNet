package com.zy.mocknet.common.logger;

/**
 * Created by zy on 17-3-10.
 */
public interface Printer {
    void v(String tag, String msg);

    void d(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);

    void exception(String tag, Exception exception);
}
