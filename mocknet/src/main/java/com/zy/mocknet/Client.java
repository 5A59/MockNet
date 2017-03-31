package com.zy.mocknet;

import java.io.*;
import java.net.Socket;

/**
 * Created by zy on 17-3-17.
 */
public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8088);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String content = "GET / HTTP/1.1\n" +
                    "Host: www.baidu.com\n" +
                    "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                    "Accept-Language: zh,en-US;q=0.7,en;q=0.3\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Content-Length: 10" +
                    "Connection: keep-alive\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "\r\n" +
                    "aaaaaaaaaa";
            writer.write(content);
            writer.flush();

//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                Logger.d(line);
//            }
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
