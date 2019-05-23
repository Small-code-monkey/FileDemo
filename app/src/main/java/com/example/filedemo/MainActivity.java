package com.example.filedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.filedemo.bean.GeoJson;
import com.example.filedemo.excelutils.ExcelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 首页
 *
 * @author user
 */
public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //动态读取内存权限
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
    }

    private void initView() {
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManager.jump(MainActivity.this);
            }
        });
    }

    /**
     * 页面返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String filePath = (String) data.getExtras().get("path");
                        if (!filePath.equals("/sdcard")) {
                            if (filePath.toLowerCase().endsWith(".geojson")) {
                                parseJsonTemporary(filePath);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        R.string.please_choice_geojson, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    ArrayList<ArrayList<String>> recordList = new ArrayList<>();

    private void parseJsonTemporary(final String path) {
        List<GeoJson> geoJsons = new ArrayList<>();
        try {
            //读取json文件
            BufferedReader br = new BufferedReader(new FileReader(path));
            String result = null;
            while ((result = br.readLine()) != null) {
                try {
                    JSONObject dataJson = new JSONObject(result);
                    JSONArray features = dataJson.getJSONArray("features");

                    for (int i = 0; i < features.length(); i++) {
                        GeoJson geoJson = new GeoJson();
                        JSONObject info = features.getJSONObject(i).getJSONObject("geometry");
                        if (info.has("geometry")) {
                            JSONObject geometry = info.getJSONObject("geometry");
                            geoJson.setCoordinates(geometry.getString("coordinates"));
                            geoJson.setGeometryType(geometry.getString("type"));
                        }
                        if (info.has("properties")) {
                            JSONObject properties = info.getJSONObject("properties");
                            geoJson.setName(properties.getString("name"));
                            geoJson.setNote(properties.getString("note"));
                        }
                        geoJson.setType(info.getString("type"));
                        geoJsons.add(geoJson);

                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(geoJsons.get(i).getCoordinates());
                        arrayList.add(geoJsons.get(i).getGeometryType());
                        arrayList.add(geoJsons.get(i).getName());
                        arrayList.add(geoJsons.get(i).getNote());
                        arrayList.add(geoJsons.get(i).getType());
                        recordList.add(arrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.reverse(recordList);
        exportExcel();
    }

    /**
     * 导出数据形成Excel
     */
    private void exportExcel() {
        //填充数据导出Excel表
        String[] colName = new String[5];
        colName[0] = "coordinates";
        colName[1] = "type";
        colName[2] = "name";
        colName[3] = "note";
        colName[4] = "type";

        String excelName = "测试";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        ExcelUtils.initExcel(file.toString() + "/" + excelName + ".xls", colName, "测试");
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + excelName + ".xls";
        ExcelUtils.writeObjListToExcel(recordList, fileName, getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                //动态权限判断
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意权限,执行我们的操作
                } else {
                    //用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                }
                break;
            default:
                break;
        }
    }
}
