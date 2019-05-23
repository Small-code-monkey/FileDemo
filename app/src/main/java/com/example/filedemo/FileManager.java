package com.example.filedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filedemo.fileutils.MyAdapter;
import com.example.filedemo.fileutils.MyFileAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author
 */
public class FileManager extends Activity {
    /**
     * Called when the activity is first created.
     */
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_files);
        findvied();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getFilesDir(rootPath);
            }
        });
        onclicks();
    }

    private int rerequestCode;
    private Button backButton;
    private String[] arrayL;
    private String[] arrayDir;
    private String rootPath = "/sdcard";
    private ListView listV;
    private List<String> items = null;
    private List<String> paths = null;
    private String inpath;
    private View delView;
    private View renView;
    private EditText delEdie;
    private EditText reEdiet;
    private TextView text;
    private TextView delText;
    private Button butt1;
    private Button butt2;
    private LinearLayout linear;
    private LinearLayout buttonLinear;
    private DisplayMetrics dm;
    private int wHeight;
    private LayoutParams li;
    private int wWidth;
    MyFileAction fileAction;
    private String currentPath = "";

    private void findvied() {
        listV = findViewById(R.id.listV);
        text = findViewById(R.id.text);
        butt1 = findViewById(R.id.butt1);
        butt2 = findViewById(R.id.butt2);
        arrayL = getResources().getStringArray(R.array.arrayM);
        arrayDir = getResources().getStringArray(R.array.mkdir);
        linear = findViewById(R.id.linear);
        buttonLinear = findViewById(R.id.buttonLinear);
        buttonLinear.setVisibility(View.GONE);
        backButton = findViewById(R.id.back_button);
    }

    /**
     * 复制或移动时按钮浮现
     */
    private void lineraManage() {
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        wHeight = dm.heightPixels;
        wWidth = dm.widthPixels;
        int bWidth = wWidth / 2;
        butt1.setWidth(bWidth);
        butt1.setHeight(50);
        butt2.setWidth(bWidth);
        butt2.setHeight(50);
        li = linear.getLayoutParams();
        li.height = wHeight - (50 * 2);
    }

    /**
     * 文件遍历并显示
     *
     * @param filePath
     */
    public void getFilesDir(String filePath) {
        items = new ArrayList<>();
        paths = new ArrayList<>();
        text.setText(filePath);
        inpath = filePath;
        File file = new File(filePath);
        File[] listFile = file.listFiles();
        List<File> fileList = new ArrayList<>();
        List<File> floderList = new ArrayList<>();
        if (listFile != null) {
            for (File f : listFile) {
                if (f.isDirectory()) {
                    floderList.add(f);
                } else {
                    fileList.add(f);
                }
            }
        }
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });
        Collections.sort(floderList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });

        String upPath = file.getParent();
        if (!filePath.equals(rootPath)) {
            items.add("up");
            paths.add(upPath);
        }
        for (File f : floderList) {
            if (!".android_secure".equals(f.getName())) {
                items.add(f.getName());
                paths.add(f.getPath());
            }
        }
        for (File f : fileList) {
            if (!".android_secure".equals(f.getName())) {
                items.add(f.getName());
                paths.add(f.getPath());
            }
        }
        listV.setAdapter(new MyAdapter(FileManager.this, items, paths));
    }

    private void onclicks() {
        // 单击事件
        listV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                File file = new File(paths.get(arg2));
                if (arg2 == 0) {
                    // 返回到/sdcard目录
                    currentPath = "/sdcard";
                } else {
                    currentPath = paths.get(arg2);
                    System.out.println("paths------->" + paths.get(arg2));
                    System.out
                            .println("currentPath3333-------->" + currentPath);
                }
                if (file.isDirectory()) {
                    // 进入文件夹

                    getFilesDir(paths.get(arg2));
                } else {
                    // 打开文件
                    Intent it = new Intent();
                    it.putExtra("path", currentPath);
                    setResult(RESULT_OK, it);
                    finish();
                }
                System.out.println("currentPath222---------->" + currentPath);
            }
        });
        // 长按事件
        listV.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
                final File file = new File(paths.get(arg2));
                if ("up".equals(items.get(arg2))) {
                    getFilesDir(file.getPath());
                } else {
                    new AlertDialog.Builder(FileManager.this)
                            .setTitle(getString(R.string.options))
                            .setItems(arrayL, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    switch (which) {
                                        case 0:
                                            // 重命名
                                            reName(file);
                                            break;
                                        case 1:
                                            // 删除
                                            delDir(file);
                                            break;
                                        case 2:
                                            // 复制
                                            buttonLinear.setVisibility(1);
                                            fileCopy(file);
                                            break;
                                        case 3:
                                            // 移动
                                            buttonLinear.setVisibility(1);
                                            removeFile(file);
                                            break;
                                        case 4:
                                            // 属性
                                            fileAction = new MyFileAction();
                                            try {
                                                fileAction.attribute(
                                                        FileManager.this,
                                                        file);
                                            } catch (IOException e) {
                                                // block
                                                e.printStackTrace();
                                            }
                                            break;
                                        default:
                                            break;
                                    }

                                }

                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface arg0, int arg1) {
                                }
                            }).show();
                }
                return true;
            }
        });

        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                backClick();
            }
        });
    }

    private void backClick() {
        System.out.println("currentPath---------->" + currentPath);
        if (!"".equals(currentPath)) {
            if ("/sdcard".equals(currentPath) || "/".equals(currentPath)) {
                Intent it = new Intent();
                it.putExtra("path", currentPath);
                setResult(RESULT_OK, it);
                finish();
            } else {
                toParent();
            }
        } else {
            currentPath = "/sdcard";
            Intent it = new Intent();
            it.putExtra("path", currentPath);
            setResult(RESULT_OK, it);
            finish();
        }
    }

    private void toParent() {
        // 回到父目录
        File file = new File(currentPath);
        File parent = file.getParentFile();
        if (parent == null) {
            getFilesDir(currentPath);
        } else {
            currentPath = parent.getAbsolutePath();
            getFilesDir(currentPath);
        }
    }

    /**
     * 文件重命名
     *
     * @param file
     */
    private void reName(final File file) {
        LayoutInflater inflater = LayoutInflater.from(FileManager.this);
        renView = inflater.inflate(R.layout.rename_dilog, null);
        reEdiet = renView.findViewById(R.id.edit1);
        new AlertDialog.Builder(FileManager.this)
                .setTitle(getString(R.string.rename))
                .setView(renView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = reEdiet.getText().toString();
                        // 取得要命名文件的路径
                        String pFile = file.getParent() + "/";
                        String newPath = pFile + name;
                        if ("".equals(name)) {
                            Toast.makeText(FileManager.this, getString(R.string.file_name_can_not_to_be_empty),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // 判断是否为文件夹
                            if (file.isDirectory()) {
                                // 判断文件夹名是否重名
                                if (new File(newPath).exists()) {
                                    new AlertDialog.Builder(FileManager.this)
                                            .setTitle(getString(R.string.warning))
                                            .setMessage(getString(R.string.file_name_can_not_to_be_repeat))
                                            .setPositiveButton(
                                                    getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            // Auto-generated
                                                            // method stub

                                                        }
                                                    }).show();
                                }
                                // 对文件夹重命名
                                else {
                                    file.renameTo(new File(newPath));
                                    getFilesDir(inpath);
                                }
                            }
                            // 对文件重命名
                            else {
                                String fistName = file.getName();
                                String lastName = fistName.substring(
                                        fistName.indexOf("."));
                                String pa = pFile + name;
                                // 判断文件是否重名
                                if (new File(pa + lastName).exists()) {
                                    new AlertDialog.Builder(FileManager.this)
                                            .setTitle(getString(R.string.warning))
                                            .setMessage(getString(R.string.file_name_can_not_to_be_repeat))
                                            .setPositiveButton(
                                                    getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            // Auto-generated
                                                            // method stub

                                                        }
                                                    }).show();
                                } else {
                                    file.renameTo(new File(pa + lastName));
                                    getFilesDir(inpath);
                                }
                            }
                        }
                    }

                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    /**
     * 删除文件
     *
     * @param file
     */
    private void delDir(final File file) {
        fileAction = new MyFileAction();
        new AlertDialog.Builder(FileManager.this).setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.is_delect))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (file.isDirectory()) {
                            fileAction.deleteDir(file);
                        } else {
                            file.delete();
                        }
                        getFilesDir(inpath);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * 复制
     *
     * @param file
     */
    private void fileCopy(final File file) {
        lineraManage();
        butt1.setText(getString(R.string.paste));
        fileAction = new MyFileAction();
        butt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File plasPath = new File(inpath + "/" + file.getName());
                if (plasPath.exists()
                        && !file.getPath().equals(plasPath.getPath())) {
                    Toast.makeText(FileManager.this, getString(R.string.file_name_can_not_to_be_repeat),
                            Toast.LENGTH_LONG).show();
                    li.height = wHeight - 65;
                    buttonLinear.setVisibility(View.GONE);
                } else {
                    if (file.isDirectory()) {

                        String[] cutFile = file.getPath().split("/");
                        String[] cutPlasPath = plasPath.getPath().split("/");
                        String eCutFile = file.getPath().substring(0, 8)
                                + cutFile[2];
                        String pCutPlasPath = plasPath.getPath()
                                .substring(0, 8) + cutPlasPath[2];
                        if (eCutFile.equals(pCutPlasPath)) {
                            File newplasPath = new File("/sdcard/1234567890_qwertyuiopasdfghjklzxcvbnm0987654321");
                            newplasPath.mkdir();
                            File nPlasPath = new File(newplasPath.getPath() + "/" + file.getName());
                            fileAction.copyDir(file, nPlasPath);
                            fileAction.copyDir(nPlasPath, plasPath);
                            fileAction.deleteDir(newplasPath);
                            getFilesDir(inpath);
                        } else {
                            fileAction.copyDir(file, plasPath);
                            getFilesDir(inpath);
                        }
                    } else {
                        fileAction.copyFile(file, plasPath);
                        getFilesDir(inpath);
                    }
                    li.height = wHeight - 65;
                    buttonLinear.setVisibility(View.GONE);
                }

            }
        });
        butt2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                li.height = wHeight - 65;
                buttonLinear.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 文件移动
     *
     * @param file
     */
    private void removeFile(final File file) {
        lineraManage();
        butt1.setText(getString(R.string.remove1));
        fileAction = new MyFileAction();
        butt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File plasPath = new File(inpath + "/" + file.getName());
                if (plasPath.exists()
                        && !file.getPath().equals(plasPath.getPath())) {
                    Toast.makeText(FileManager.this, getString(R.string.file_name_can_not_to_be_repeat),
                            Toast.LENGTH_LONG).show();
                    li.height = wHeight - 65;
                    buttonLinear.setVisibility(View.GONE);
                } else {
                    if (file.isDirectory()) {
                        String[] cutFile = file.getPath().split("/");
                        String[] cutPlasPath = plasPath.getPath().split("/");
                        String eCutFile = file.getPath().substring(0, 8)
                                + cutFile[2];
                        String pCutPlasPath = plasPath.getPath()
                                .substring(0, 8) + cutPlasPath[2];
                        /*
                         * 判断是否在同一个文件夹中进行文件夹移动， 如果是将使用中间文件夹作为过度，文件夹移动，否则将出现错误
                         */
                        if (eCutFile.equals(pCutPlasPath)) {
                            int fl = file.getPath().length();
                            int pl = plasPath.getPath().length();
                            // 新建中间文件，存放要移动的文件夹
                            File newplasPath = new File("/sdcard/1234567890_qwertyuiopasdfghjklzxcvbnm0987654321");
                            newplasPath.mkdir();
                            File nPlasPath = new File(newplasPath.getPath()
                                    + "/" + file.getName());
                            // 把要移动的文件夹复制到中间文件夹中
                            fileAction.copyDir(file, nPlasPath);
                            // 从中间文件夹中复制目标文件夹到所指定路径，到此完成了目标文件夹复制到指定路径中
                            fileAction.copyDir(nPlasPath, plasPath);
                            // 删除中间文件夹
                            fileAction.deleteDir(newplasPath);
                            /*
                             * 如果源文件夹路径比指定路径短，也就是说此时的源文件夹路径是指定路径的父路径
                             * 此时删除源文件夹，将连移动文件夹一同删除
                             */
                            if (fl > pl) {
                                // 删除源文件夹
                                fileAction.deleteDir(file);
                            }
                            getFilesDir(inpath);
                        } else {
                            fileAction.copyDir(file, plasPath);
                            getFilesDir(inpath);
                        }
                    } else {
                        fileAction.copyFile(file, plasPath);
                        file.delete();
                        getFilesDir(plasPath.getParent());
                    }
                    li.height = wHeight - 65;
                    buttonLinear.setVisibility(View.GONE);
                }
            }
        });
        butt2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                li.height = wHeight - 65;
                buttonLinear.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 打开文件操作
     *
     * @param file
     */
    protected void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }

    private String getMIMEType(File file) {
        String type = "";
        String fName = file.getName();
        String endName = (String) fName.subSequence(fName.lastIndexOf(".") + 1,
                fName.length());
        if (!"apk".equals(endName)) {
            switch (endName) {
                case "mp3":
                case "m4a":
                case "mid":
                case "xmf":
                case "ogg":
                case "wav":
                    type = "audio";
                    break;
                case "3gp":
                case "mp4":
                    type = "video";
                    break;
                case "jpg":
                case "gif":
                case "png":
                case "jpeng":
                case "bmp":
                    type = "image";
                    break;
                default:
                    type = "*";
                    break;
            }
            type += "/*";
        } else {
            type = "application/vnd.android.package-archive";
        }
        return type;
    }

    /**
     * menu菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, getString(R.string.new_folder));
        menu.add(1, 1, 1, getString(R.string.retrun_root_directory));
        menu.add(2, 2, 2, getString(R.string.refresh_load));
        menu.add(3, 3, 3, getString(R.string.about1));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File file = new File(inpath);
        switch (item.getItemId()) {
            case 0:
                mkDir(file);
                break;
            case 1:
                getFilesDir(rootPath);
                break;
            case 2:
                getFilesDir(inpath);
                break;
            case 3:
                about();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void about() {
        new AlertDialog.Builder(FileManager.this).setTitle(getString(R.string.about1))
                .setMessage(getString(R.string.practice))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void mkDir(final File file) {
        LayoutInflater inflater = LayoutInflater.from(FileManager.this);
        delView = inflater.inflate(R.layout.newdir_dilog, null);
        delEdie = delView.findViewById(R.id.edit);
        delText = delView.findViewById(R.id.tex);

        new AlertDialog.Builder(FileManager.this)
                .setTitle(getString(R.string.newly_build))
                .setItems(arrayDir, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        switch (arg1) {
                            case 0:
                                delText.setText(getString(R.string.new_folder_name));
                                new AlertDialog.Builder(FileManager.this)
                                        .setTitle(getString(R.string.new_folder))
                                        .setView(delView)
                                        .setPositiveButton(
                                                getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {
                                                        // method stub
                                                        String dirname = String
                                                                .valueOf(delEdie
                                                                        .getText());
                                                        String dirPath = file
                                                                .getPath()
                                                                + "/" + dirname;
                                                        File dirFile = new File(
                                                                dirPath);
                                                        if (dirFile.exists()) {
                                                            Toast.makeText(
                                                                    FileManager.this,
                                                                    getString(R.string.file_name_can_not_to_be_repeat),
                                                                    Toast.LENGTH_LONG)
                                                                    .show();
                                                        } else {
                                                            dirFile.mkdir();
                                                            getFilesDir(file.getPath());
                                                        }
                                                    }
                                                })
                                        .setNegativeButton(
                                                getString(R.string.cancel),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {
                                                        // method stub

                                                    }
                                                }).show();
                                break;
                            default:
                                delText.setText(getString(R.string.new_folder_name));
                                new AlertDialog.Builder(FileManager.this)
                                        .setTitle(getString(R.string.new_folder))
                                        .setView(delView)
                                        .setPositiveButton(
                                                getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        String filename = String.valueOf(delEdie.getText());
                                                        String filePath = file.getPath() + "/" + filename;
                                                        File dirFile = new File(filePath);
                                                        if (dirFile.exists()) {
                                                            Toast.makeText(FileManager.this,
                                                                    getString(R.string.file_name_can_not_to_be_repeat),
                                                                    Toast.LENGTH_LONG).show();
                                                        } else {
                                                            try {
                                                                dirFile.createNewFile();
                                                                getFilesDir(file.getPath());
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                })
                                        .setNegativeButton(
                                                getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {
                                                    }
                                                }).show();
                                break;
                        }

                    }
                })
                .setPositiveButton(getString(R.string.cancel), null).show();
    }

    public static void jump(Fragment fragment) {
        Intent it = new Intent(fragment.getActivity(), FileManager.class);
        fragment.startActivityForResult(it, 1);
    }

    public static void jump(Activity activity) {
        Intent it = new Intent(activity, FileManager.class);
        activity.startActivityForResult(it, 2);
    }

    @Override
    public void onBackPressed() {
        backClick();
    }
}