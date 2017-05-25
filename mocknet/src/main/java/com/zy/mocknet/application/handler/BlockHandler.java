package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.common.Utils;
import com.zy.mocknet.common.logger.Logger;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

/**
 * It will block some time when the request is received.
 * Created by zy on 17-3-28.
 */
public class BlockHandler extends Handler {

    @Override
    public Response handle(Request request, HandlerChain chain, int index) {
        Handler handler = chain.getHandler(index + 1);
        if (handler == null) {
            return null;
        }
        Response response = handler.handle(request, chain, index + 1);
        MockConnection connection =
                ConnectionStore.getInstance().getConnection(request.getMethod(), request.getRequestUri());
        if (connection != null) {
            int time = connection.getBlockTime();
            if (time == MockConnection.NO_BLOCK) {
                return response;
            }
            Utils.getInstance().outputTitle("BLOCKING");
            Logger.d(String.format("START BLOCKING %d ms", time));
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.d(String.format("BLOCKING END", time));
        }
        return response;
    }
}
