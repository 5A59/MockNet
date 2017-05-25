package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.common.Utils;
import com.zy.mocknet.common.logger.Logger;
import com.zy.mocknet.server.bean.Parameters;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

import java.util.Map;

/**
 * It verifies the request params key-value.
 * Created by zy on 17-3-21.
 */
public class VerifyParamHandler extends Handler {

    @Override
    public Response handle(Request request, HandlerChain chain, int index) {
        Handler handler = chain.getHandler(index + 1);
        if (handler == null) {
            return null;
        }
        Response response = handler.handle(request, chain, index + 1);

        String url = request.getRequestUri();
        String method = request.getMethod();

        MockConnection connection = ConnectionStore.getInstance().getConnection(method, url);
        if (connection == null || !connection.isVerifyParam()) {
            return response;
        }
        Utils.getInstance().outputTitle("VERIFY PARAMS");
        Parameters parameters = request.getParams();
        StringBuilder validS = new StringBuilder();
        StringBuilder invalidS = new StringBuilder();
        for (Map.Entry<String, String> entry : connection.getReqParams().entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (val.equals(parameters.getParam(key))) {
                validS.append("\"" + key + "\" is valid : " + val);
            }else {
                invalidS.append("\"" + key + "\" is invalid : "
                        + val + " -- " + parameters.getParam(key));
            }
        }
        validS.append("\n");
        invalidS.append("\n");
        Logger.d(validS.toString());
        Logger.d(invalidS.toString());
        return response;
    }
}
