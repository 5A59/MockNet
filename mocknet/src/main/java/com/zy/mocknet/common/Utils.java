package com.zy.mocknet.common;

import com.zy.mocknet.common.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Some common methods.
 * Created by zy on 17-3-16.
 */
public class Utils {

    private static volatile Utils instance;
    private File tmpDir;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }
        return instance;
    }

    public boolean stringEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean setTmpDir(File file) {
        if (file == null || !file.isDirectory()) {
            return false;
        }
        tmpDir = file;
        return true;
    }

    public void outputTitle(String title) {
        StringBuilder builder = new StringBuilder();
        builder.append(" \n");
        builder.append("=============================================\n");
        builder.append("=                  " + title + "                   \n");
        builder.append("=============================================\n");
        Logger.d(builder.toString());
    }

    public void outputLine() {
        String s = "---------------------------------------------\n";
        Logger.d(s);
    }

    public RandomAccessFile getTmpRandomAccessFile(String filename, String mode)
            throws FileNotFoundException {
        if (filename == null || filename.isEmpty() || mode == null || mode.isEmpty()) {
            return null;
        }
        String parent = tmpDir == null ?
                System.getProperty("java.io.tmpdir") : tmpDir.getAbsolutePath();
        return new RandomAccessFile(new File(parent, filename).getAbsolutePath(), mode);
    }

    public void rmTmpRandomAccessFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ;
        }
        String parent = tmpDir == null ? "" : tmpDir.getAbsolutePath();
        File file = new File(parent, filename);
        file.delete();
    }

    public String getSystem() {
        return System.getProperty("os.name");
    }
}
