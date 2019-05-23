package com.example.filedemo.fileutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filedemo.R;

import java.io.File;
import java.util.List;

/**
 * 文件列表适配器
 *
 * @author user
 */
public class MyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> items = null;
    private List<String> paths = null;
    private Bitmap back02;
    private Bitmap fold;
    private Bitmap wenjian;
    private Bitmap txt;
    private Bitmap xls;
    private Bitmap mp3;
    private Bitmap word;
    private Bitmap mp4;

    public MyAdapter(Context context, List<String> it, List<String> pa) {
        inflater = LayoutInflater.from(context);
        items = it;
        paths = pa;
        back02 = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.back02);
        fold = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.folder);
        wenjian = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.wenjian);
        txt = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.txt);
        xls = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.excel);
        mp3 = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.mp3);
        word = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.word);
        mp4 = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_action_video);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.text);
            holder.img = convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = new File(paths.get(position));
        if (file == null || items.get(position).equals("up")) {
            holder.text.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
        } else {
            holder.text.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.VISIBLE);
            holder.text.setText(items.get(position));
            if (file.isDirectory()) {
                holder.img.setImageBitmap(fold);
            } else {
                String hz = getFileType(file + "");
                switch (hz) {
                    case "txt":
                        holder.img.setImageBitmap(txt);
                        break;
                    case "xls":
                        holder.img.setImageBitmap(xls);
                        break;
                    case "mp3":
                        holder.img.setImageBitmap(mp3);
                        break;
                    case "word":
                        holder.img.setImageBitmap(word);
                        break;
                    case "wma":
                    case "mp4":
                    case "rmvb":
                    case "rm":
                    case "flash":
                    case "3GP":
                    case "AVI":
                        holder.img.setImageBitmap(mp4);
                        break;
                    default:
                        holder.img.setImageBitmap(wenjian);
                        break;
                }
            }
        }
        return convertView;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return 文件后缀名
     */
    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                return fileType;
            }
        }
        return "";
    }

    /**
     * 根据后缀名判断是否是图片文件
     *
     * @param type
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String type) {
        return type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"));
    }

    public class ViewHolder {
        TextView text;
        ImageView img;
    }
}