package com.example.filedemo.fileutils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.filedemo.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class MyFileAction {
    private String spaces = null;
    private double space = 0;
    private double count = 0;

    // 文件复制
    public void copyFile(File file, File plasPath) {
        try {
            FileInputStream fileInput = new FileInputStream(file);
            BufferedInputStream inBuff = new BufferedInputStream(fileInput);
            FileOutputStream fileOutput = new FileOutputStream(plasPath);
            BufferedOutputStream outBuff = new BufferedOutputStream(fileOutput);
            byte[] b = new byte[1025 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
            inBuff.close();
            outBuff.close();
            fileOutput.close();
            fileInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 文件夹复制，包括文件夹里面的文件复制
    public void copyDir(File file, File plasPath) {
        plasPath.mkdir();
        File[] f = file.listFiles();
        for (File newFile : f) {
            if (newFile.isDirectory()) {
                File files = new File(file.getPath() + "/" + newFile.getName());
                File plasPaths = new File(plasPath.getPath() + "/"
                        + newFile.getName());
                copyDir(files, plasPaths);
            } else {
                String newPath = plasPath.getPath() + "/" + newFile.getName();
                File newPlasFile = new File(newPath);
                copyFile(newFile, newPlasFile);
            }
        }

    }

    // 文件夹删除，包括文件夹里面的文件
    public void deleteDir(File delFile) {
        File[] f = delFile.listFiles();// 取得文件夹里面的路径
        File upFile = delFile;
        if (f.length == 0 && upFile.getParent().equals("/sdcard")) {
            delFile.delete();
        } else if (f.length == 0 && !upFile.getParent().equals("/sdcard")) {
            delFile.delete();
        } else {
            for (File nFile : f) {
                if (nFile.isDirectory()) {
                    deleteDir(nFile);

                } else {
                    nFile.delete();
                }
            }
            delFile.delete();
        }
        delFile.delete();
    }

    // 属性
    public void attribute(Context context, File file) throws IOException {
        LayoutInflater inflater = LayoutInflater.from(context);
        View atView = inflater.inflate(R.layout.attribute_dilog, null);
        TextView atText1 = atView.findViewById(R.id.name);
        TextView atText2 = atView.findViewById(R.id.space);
        TextView atText3 = atView.findViewById(R.id.date);
        String sum = null;
        if (file.isFile()) {
            attFile(file);
            sum = spaces;
        } else {
            attDir(file);
            if (count > 1048576) {
                sum = String.valueOf(count / 1048576).substring(0,
                        String.valueOf(count / 1048576).lastIndexOf(".") + 4)
                        + "MB";
            } else {
                sum = String.valueOf(count / 1024).substring(0,
                        String.valueOf(count / 1024).lastIndexOf(".") + 2)
                        + "KB";
            }
        }
        atText1.setText(file.getName());
        atText2.setText(sum);
        String date = String.valueOf(new Timestamp(file.lastModified()));
        String mydate = date.substring(0, date.lastIndexOf(":"));
        atText3.setText(mydate);
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.attribute)).setView(atView)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).show();
    }

    private void attDir(File file) throws IOException {
        File[] liFile = file.listFiles();
        for (File nFile : liFile) {
            if (nFile.isFile()) {
                attFile(nFile);
                count += space;
            } else {
                attDir(nFile);
            }
        }
    }

    private void attFile(File file) throws IOException {
        FileInputStream fi = new FileInputStream(file);
        space = fi.available();
        if (space > 1048576) {
            spaces = String.valueOf(space / 1048576).substring(0,
                    String.valueOf(space / 1048576).lastIndexOf(".") + 4)
                    + "MB";
        } else {
            spaces = String.valueOf(space / 1024).substring(0,
                    String.valueOf(space / 1024).lastIndexOf(".") + 2)
                    + "KB";
        }
    }
}