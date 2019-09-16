package com.cs.ping.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileUtil {
    private String storagePath;

    public FileUtil() {
        storagePath = Environment.getExternalStorageDirectory().getPath();
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    // 创建文件
    public File createFile(String fileName) {
        File file = new File(storagePath, fileName);
        if (!file.exists() || file.isDirectory()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    // 创建目录
    public File createDir(String dirName) {
        File dir = new File(storagePath, dirName);
        if (!dir.exists() || dir.isFile()) {
            dir.mkdirs();
        }
        return dir;
    }
    // 文件是否存在
    public boolean exists(String fileName){
        File file = new File(storagePath,fileName);
        return file.exists();
    }

    //  把数据写到文件中去
    public File write2File(String path, String fileName, InputStream in){
        File file = null;
        OutputStream ou = null;
        setStoragePath(path);
        try{
            createDir(path);
            file = createFile(fileName);
            ou = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((in.read(buffer)) != -1){
                ou.write(buffer);
            }
            ou.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                ou.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    // 追加到文件上
    public File append2File(String path,String fileName,String str){
        File file = null;
        FileOutputStream fos = null;
        setStoragePath(path);
        try{
            createDir(path);
            file = createFile(fileName);
            fos= new FileOutputStream(file,true);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(str);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }
}

