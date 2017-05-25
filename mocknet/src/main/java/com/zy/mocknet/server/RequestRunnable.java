package com.zy.mocknet.server;

import com.zy.mocknet.common.logger.Logger;
import com.zy.mocknet.server.bean.Headers;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.RequestBody;
import com.zy.mocknet.server.bean.Response;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class finishes the jobs of parsing http data. <br>
 * It will run in thread.
 * Created by zy on 17-3-5.
 */
public class RequestRunnable implements Runnable {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    private static final String TAG_BOUNDARY = "boundary=";
    private static final String TAG_CHARSET = "charset=";

    private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_FORM_DATA = "multipart/form-data";

    private static final int HEADER_SIZE = 8192;
    private static final int BUFFER_SIZE = 4096;
    private Socket socket;
    private RequestExecutor executor;

    public RequestRunnable(Socket socket, RequestExecutor executor) {
        this.socket = socket;
        this.executor = executor;
    }

    @Override
    public void run() {
        Request request = new Request();
        try {
            Logger.d("request runnable run start");
            // get request from socket
            InputStream inputStream = socket.getInputStream();

            handleRequest(request, new BufferedInputStream(inputStream));

            if (executor == null) {
                socket.close();
                return ;
            }

            // execute request
            Response response = executor.execute(request);
            if (response != null) {
                OutputStream outputStream = socket.getOutputStream();
                response.writeTo(outputStream);
            }
        } catch (IOException e) {
            Logger.exception(e);
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
                request.destory();
            } catch (IOException e) {
                Logger.d("socket close error");
                Logger.exception(e);
            }
        }
    }

    private int findHeaderEnd(byte[] buffer, int len) {
        for (int i = 0; i < len - 3; i ++) {
            if (buffer[i] == '\r'
                    && buffer[i + 1] == '\n'
                    && buffer[i + 2] == '\r'
                    && buffer[i + 3] == '\n') {
                return i + 3;
            }
        }
        return -1;
    }

    private void handleRequest(Request request, BufferedInputStream inputStream) throws IOException {
        int allLen = 0;
        int len = 0;
        byte[] buffer = new byte[HEADER_SIZE];

        inputStream.mark(HEADER_SIZE);
        if ((len = inputStream.read(buffer)) <= 0) {
            return ;
        }

        int headerEnd = -1;
        while (len > 0) {
            allLen += len;
            headerEnd = findHeaderEnd(buffer, len);
            if (headerEnd > 0) {
                break;
            }
            len = inputStream.read(buffer, allLen, HEADER_SIZE - allLen);
        }
        if (headerEnd > allLen || headerEnd <= 0 || allLen <= 0) {
            return ;
        }
        inputStream.reset();
        inputStream.skip(headerEnd + 1);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buffer, 0, headerEnd)));
        handleRequestLine(request, reader);
        handleHeaders(request, reader);
        handleUriParams(request);
        handleBody(request, inputStream);
        handleBodyParams(request);
    }

    private boolean handleRequestLine(Request request, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            return false;
        }
        String[] lineArray = line.split(" ");
        if (lineArray == null || lineArray.length < 3) {
            return false;
        }
        String method = lineArray[0];
        String uri = lineArray[1];
        String httpVersion = lineArray[2];
        request.setMethod(method);
        request.setHttpVresion(httpVersion);
        request.setRequestUri(uri);
        return true;
    }

    private boolean handleHeaders(Request request, BufferedReader reader) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            handleHeader(request, line);
        }
        return true;
    }

    private boolean handleHeader(Request request, String line) {
        String[] lineArray = line.split(":", 2);
        if (lineArray == null || lineArray.length < 2) {
            return false;
        }
        String name = lineArray[0];
        String content = lineArray[1].trim();
        request.addHeader(name, content);
        return true;
    }

    private boolean handleBody(Request request, BufferedInputStream inputStream) throws IOException {
        String ssize = request.getHeader().getHeader(HEADER_CONTENT_LENGTH);
        long size = ssize != null && !ssize.isEmpty() ? Long.valueOf(ssize) : -1;
        if (size == -1) {
            return true;
        }
        int len = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        RequestBody body = new RequestBody(size);
        StringBuilder builder = new StringBuilder();
        while ((len = inputStream.read(buffer, 0, (int) Math.min(size, BUFFER_SIZE))) > 0
                && size > 0) {
            size -= len;

            if (len > 0) {
                body.appendContent(buffer, 0, len);
            }
        }
        request.setBody(body);
        return true;
    }

    private void handleUriParams(Request request) {
        String uri = request.getRequestUri();
        if (!uri.contains("?")) {
            return ;
        }
        int startIndex = uri.indexOf("?") + 1;
        request.setRequestUri(uri.substring(0, startIndex - 1));
        String params = uri.substring(startIndex >= uri.length() ? uri.length(): startIndex);
        parseParams(request, params);
    }

    private void parseParams(Request request, String params) {
        String[] param = params.split("&");
        for (String p : param) {
            String[] nameVal = p.split("=");
            if (nameVal != null && nameVal.length == 2) {
                request.addParam(nameVal[0], nameVal[1]);
            }
        }
    }

    private void handleBodyParams(Request request) throws IOException {
        String conType = request.getHeader().getHeader(HEADER_CONTENT_TYPE);
        if (conType == null || conType.isEmpty()) {
            return ;
        }
        String charset = getCharset(conType);
        charset = charset == null ? "utf-8" : charset;

        if (conType.contains(CONTENT_TYPE_FORM_URLENCODED)) {
            ByteBuffer byteBuffer = request.getBody().getContentByteBuffer();
            CharBuffer charBuffer =
                    Charset.forName(charset).newDecoder().decode(byteBuffer.asReadOnlyBuffer());
            String body = charBuffer.toString();
            parseParams(request, body);
        }else if (conType.contains(CONTENT_TYPE_FORM_DATA)) {
            int index = conType.indexOf(TAG_BOUNDARY);
            if (index == -1) {
                Logger.d("No Boundary Tag In Form-Data");
                return ;
            }
            String boundary = "--" + conType.substring(index + TAG_BOUNDARY.length());
            long tmpConLen = request.getBody().getContentLen();
            long conLen = tmpConLen;
            ByteBuffer byteBuffer = request.getBody().getContentByteBuffer();
            List<Long> boundaryIndexs = new ArrayList<>(5);
            byte[] buffer = new byte[BUFFER_SIZE];
            // find all boundary position
            while (conLen > 0) {
                int len = byteBuffer.remaining() < BUFFER_SIZE ? byteBuffer.remaining() : BUFFER_SIZE;
                byteBuffer.get(buffer, 0, len);
                long startIndex = tmpConLen - conLen;
                matchString(buffer, len, startIndex, boundary, boundaryIndexs);
                conLen -= len;
                if (conLen > 0) {
                    conLen += boundary.length();
                }
                byteBuffer.position((int) (tmpConLen - conLen));
            }
            byteBuffer.rewind();

            byte[] buf = new byte[HEADER_SIZE];
            // handle part of form data
            for (int j = 0; j < boundaryIndexs.size() - 1; j ++) {
                long i = boundaryIndexs.get(j);
                // handle part header
                byteBuffer.limit((int) ((boundaryIndexs.get(j + 1)) - 2));
                byteBuffer.position((int) (i + boundary.length() + 2));

                int l = byteBuffer.remaining() < HEADER_SIZE ? byteBuffer.remaining() : HEADER_SIZE;
                byteBuffer.get(buf, 0, l);
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, l)));
                // MAY_ERROR: 感觉这里有问题，第一行一定是内容么
                String line = null;
                Headers headers = new Headers();
                int headerLen = 0;
                // skip boundary line
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    String name = line.substring(0, line.indexOf(":"));
                    String val = line.substring(line.indexOf(":") + 1).trim();
                    headers.addHeader(name, val);
                    // calculate header size
                    while (buf[headerLen] != '\r' && buf[headerLen + 1] != '\n') {
                        headerLen ++;
                    }
                    headerLen += 2;
                }
                // handle part body
                String disposition = headers.getHeader(HEADER_CONTENT_DISPOSITION);
                String type = headers.getHeader(HEADER_CONTENT_TYPE);
                if (disposition == null || disposition.isEmpty()) {
                    continue;
                }
                String name = null;
                String filename = null;
                // find name
                String nameRe = "name=[\"](.*?)[\"]";
                Pattern namePattern = Pattern.compile(nameRe);
                Matcher matcher = namePattern.matcher(disposition);
                if (matcher.find()) {
                    name = matcher.group(1);
                }

                String filenameRe = "filename=[\"](.*?)[\"]";
                Pattern filenamePattern = Pattern.compile(filenameRe);
                Matcher fileMatcher = filenamePattern.matcher(disposition);
                if (fileMatcher.find()) {
                    filename = fileMatcher.group(1);
                }

                // handle body byte
                int headerEndPos = -1;
                for (int bufI = 0; bufI < l - 4; bufI ++) {
                    if (buf[bufI] == '\r'
                            && buf[bufI + 1] == '\n'
                            && buf[bufI + 2] == '\r'
                            && buf[bufI + 3] == '\n') {
                        headerEndPos = bufI + 4;
                        break;
                    }
                }
                if (headerEndPos == -1) {
                    Logger.d("header size over MAX_HEADER_SIZE");
                    continue;
                }
                byteBuffer.position((int) (i + boundary.length() + 2 + headerEndPos));

                OutputStream outputStream;
                // TODO: what if filename is null but this part is a file ?
                if (isFile(type, filename)) {
                    outputStream = new FileOutputStream(filename);
                }else {
                    outputStream = new ByteArrayOutputStream();
                }
                while (byteBuffer.remaining() > 0) {
                    int len = byteBuffer.remaining() < HEADER_SIZE ? byteBuffer.remaining() : HEADER_SIZE;
                    byteBuffer.get(buf, 0, len);
                    outputStream.write(buf, 0, len);
                }
                if (!isFile(type, filename)) {
                    String cs = getCharset(type);
                    String val = "";
                    if (cs == null) {
                        val = new String(((ByteArrayOutputStream) outputStream).toByteArray());
                    }else {
                        val = new String(((ByteArrayOutputStream) outputStream).toByteArray(), cs);
                    }
                    request.addParam(name, val);
                }else {
                    request.addParam(name, filename);
                }
            }

        }
    }

    private String getCharset(String conType) {
        if (!conType.contains(TAG_CHARSET)) {
            return null;
        }
        int index = conType.indexOf(TAG_CHARSET) + TAG_CHARSET.length();
        return conType.substring(index);
    }

    private boolean isFile(String type, String filename) {
        return (filename != null && !filename.isEmpty());
    }

    private long matchString(byte[] buffer, int len, long startIndex, String s, List<Long> indexs) {
        for (int i = 0; i < len - s.length(); i ++) {
            for (int j = 0, tmpI = i; j < s.length(); j ++, tmpI ++) {
                if (buffer[tmpI] == s.charAt(j)) {
                    if (j == s.length() - 1) {
                        indexs.add(startIndex + i);
                    }
                }else {
                    break;
                }
            }
        }
        return -1;
    }
}
