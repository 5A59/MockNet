package com.zy.mocknet.server.bean;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Random;

/**
 * Created by zy on 17-3-16.
 */
public class RequestBody {
    private static int MAX_BUFFER_IN_MEMORY = 2048;
    private long contentLen;
    private long validLen;
    private byte[] content;
    private String contentType;

    private RandomAccessFile contentFile; // put body to file while size is too large
    private String filename;
    private Random random; // using making body file name

    public RequestBody(long contentLen) throws FileNotFoundException {
        this.contentLen = contentLen;
        if (contentLen > MAX_BUFFER_IN_MEMORY) {
            filename = getContentFileName();
            contentFile = new RandomAccessFile(filename, "rw");
            random = new Random();
        }else {
            content = new byte[(int) contentLen];
        }
        validLen = 0;
    }

    private String getContentFileName() {
        StringBuilder builder = new StringBuilder(String.valueOf(new Date().getTime()));
        builder.append("_");
        while (true) {
            File file = new File(builder.toString());
            if (!file.exists()) {
                break;
            }
            builder.append(random.nextInt());
        }
        return builder.toString();
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLen() {
        return contentLen;
    }

    public boolean appendContent(byte[] content, int off, int len) {
        if (len - off > contentLen - validLen) {
            return false;
        }
        if (contentFile != null) {
            try {
                contentFile.write(content, off, len);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }else {
            System.arraycopy(content, off, this.content, (int) validLen, len);
        }
        validLen += len - off;
        return true;
    }

    public ByteBuffer getContentByteBuffer() {
        if (contentFile != null) {
            try {
                contentFile.seek(0);
                return contentFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, contentLen);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        ByteBuffer buffer = ByteBuffer.wrap(content);
        return buffer;
    }

    public void destroy() {
        try {
            if (contentFile != null) {
                contentFile.close();
                File file = new File(filename);
                file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (contentFile != null) {
            return filename;
        }
        return new String(content);
    }
}
