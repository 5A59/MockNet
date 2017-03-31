package com.zy.mocknet.server.bean;

import java.io.*;

/**
 * Created by zy on 17-3-16.
 */
public class ResponseBody {
    private static final int BUFFER_SIZE = 1024;
    private byte[] content;
    private int len;
    private String contentType;
    private File file;

    public ResponseBody() {

    }

    public ResponseBody(byte[] content, int len, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public ResponseBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    public void setContent(byte[] content, int len) {
        this.content = content;
        this.len = len;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public int getContentLen() {
        return len;
    }

    public String decodeContent() {
        return new String(content);
    }

    public String getContentType() {
        return contentType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void writeTo(OutputStream stream) throws IOException {
        if (file == null) {
            if (content == null) {
                return ;
            }
            stream.write(content);
            return ;
        }
        byte[] buf = new byte[BUFFER_SIZE];
        FileInputStream inputStream = new FileInputStream(file);
        int l = -1;
        while ((l = inputStream.read(buf)) != -1) {
            stream.write(buf, 0, l);
        }
        stream.flush();
        inputStream.close();
    }

    @Override
    public String toString() {
        return new String(content);
    }
}
