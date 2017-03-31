package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.common.Utils;
import com.zy.mocknet.common.logger.Logger;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

/**
 * Created by zy on 17-3-5.
 */
public class LogHandler extends Handler {

    @Override
    public Response handle(Request request, HandlerChain chain, int index) {

        Handler handler = chain.getHandler(index + 1);
        if (handler == null) {
            return null;
        }
        Response response = handler.handle(request, chain, index + 1);
        MockConnection connection =
                ConnectionStore.getInstance().getConnection(request.getMethod(), request.getRequestUri());
        if (connection == null || !connection.isLog()) {
            return response;
        }

        StringBuilder builder = new StringBuilder();
        Utils.getInstance().outputTitle("REQUEST");
        builder.append(request.toString()).append("\n");
        Logger.d(builder.toString());
        builder.setLength(0);

        Utils.getInstance().outputTitle("RESPONSE");
        builder.append(response.toString());
        Logger.d(builder.toString());
        return response;
    }
}
