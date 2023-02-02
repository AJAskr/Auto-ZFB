package com.mx.accessibilitydemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.utils.Constants;
import com.molihuan.pathselector.utils.Mtools;
import com.mx.accessibilitydemo.R;
import com.mx.accessibilitydemo.service.MxAccessibilityService;
import com.mx.accessibilitydemo.utils.AccessibilityUtils;
import com.mx.accessibilitydemo.utils.ClearAllDatas;
import com.mx.accessibilitydemo.utils.ExcelUtli.ExcelToInfo;
import com.mx.accessibilitydemo.utils.ExcelUtli.InfoLen;
import com.mx.accessibilitydemo.utils.ExcelUtli.Res_Txt_To_Excel;
import com.mx.accessibilitydemo.utils.HttpUtils;
import com.mx.accessibilitydemo.utils.ShareUtils.Share;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> mFileChoose;
    private String path;
    private String infoPath;
    private String resPath;
    private String infoPath2;
    private String[] split;
    private TextView tvFilePath;//选择文件之后显示路径的文本控件
//    private CFile cFile;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //新建文件夹，保存临时数据
        File dataFile = new File(Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名");
        if (!dataFile.exists()) dataFile.mkdir();

        //动态获取权限
        requestPermission(this);

        infoPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info.txt";
        infoPath2 = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info2.txt";
        resPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/result.txt";

        //1.选择xlsx文件
        //新的文件选择器 使用了github里面找的库
        findViewById(R.id.btn_select_file).setOnClickListener(view -> {
            //ClearAllDatas.clear(getExternalFilesDir("").getPath());
            //如果没有权限会自动申请权限
            PathSelector.build(MainActivity.this, Constants.BUILD_ACTIVITY)//跳转Activity方式
                    .requestCode(10011)//请求码
                    //toolbar选项
                    .setMoreOPtions(new String[]{"选择"},
                            new boolean[]{true},//选择后结束掉Activity结果会给到onActivityResult()
                            (view1, currentPath, fileBeanList, callBackData, tabbarAdapter, fileAdapter, callBackFileBeanList) -> {
                                //for (String callBackDatum : callBackData) {
                                //Mtools.toast(getBaseContext(),callBackDatum);//也可一在这里拿到选择的结果
                                //}
                            }
                    ).start();//开始构建
        });

        //筛选条件的输入框
        EditText editText = findViewById(R.id.et_info);
        //1.1天眼查模板
        findViewById(R.id.tyc_btn).setOnClickListener(v-> editText.setText("3-;-法定代表人-电话-其他电话"));
        //1.2企查查模板
        findViewById(R.id.qcc_btn).setOnClickListener(v-> editText.setText("2-；-法定代表人-电话-更多电话"));
        //1.3爱企查模板
        findViewById(R.id.aqc_btn).setOnClickListener(v-> editText.setText("3-,-法定代表人-电话-更多电话"));
        //1.4进来进往模板
        findViewById(R.id.jljw_btn).setOnClickListener(v-> editText.setText("1-,-法人-联系电话"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            }
        }
        //3.开启新版无障碍
        findViewById(R.id.btn_open_newAccessibility).setOnClickListener(view->{
            String pkgName = "com.aja.newZFBauto";
            try {
                doStartApplicationWithPackageName(pkgName);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "启动失败，可能没有安装新版插件", Toast.LENGTH_SHORT).show();
            }
        });

        //3.开启无障碍
        findViewById(R.id.btn_open_accessibility).setOnClickListener(view -> {
            if (!AccessibilityUtils.isOpenAccessibility(this, MxAccessibilityService.class.getName())) {
                AccessibilityUtils.gotoSettings(this);
            } else {
                Toast.makeText(MainActivity.this, "已经开启无障碍", Toast.LENGTH_SHORT).show();
            }
        });

        //复制id到剪切板
        Button btnCopyID = findViewById(R.id.btn_copy_id);
        btnCopyID.setOnClickListener(v -> {
            String id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("id", id);
            manager.setPrimaryClip(clipData);
            Toast.makeText(MainActivity.this, "ID已复制到剪切板", Toast.LENGTH_SHORT).show();
        });

        //2.对文件筛选并获得预处理信息
        EditText et_info = findViewById(R.id.et_info);
        findViewById(R.id.btn_get_info).setOnClickListener(v -> {
            String info = et_info.getText().toString();
            TextView pathTextView = findViewById(R.id.tv_file_path);
            path = pathTextView.getText().toString();
            split = info.split("-");
            boolean yz = false;
            try {
                if (split.length == 5) {
                    yz = ExcelToInfo.XlsxExcelA(path, infoPath, Integer.parseInt(split[0]), split[1], split[2], split[3], split[4]);
                } else if (split.length == 4) {
                    yz = ExcelToInfo.XlsxExcelA(path, infoPath, Integer.parseInt(split[0]), split[1], split[2], split[3], "");
                }
            } catch (Exception e) {
                Log.e("TAG", e.toString());
                Toast.makeText(MainActivity.this,"失败~",Toast.LENGTH_SHORT).show();
                return;
            }
            if (yz) {
                int line = InfoLen.getLen(infoPath);
                int infoNUmberSum = InfoLen.getNumberSum(infoPath);
                @SuppressLint("DefaultLocale")
                String time = String.format("%.3f", infoNUmberSum * 1.5 / 3600);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("确定", null)
                        .create();
                alertDialog.setTitle("提示");
                alertDialog.setMessage("获取成功\n您需要处理的数据是：" + line + "条数据\n预计时间：" + time + "小时");
                alertDialog.show();
            } else {
                Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
            }
        });

        //4.检查结果数据
        findViewById(R.id.btn_check_result).setOnClickListener(v -> {
            int infoLine = InfoLen.getLen(infoPath.replace("info", "info3"));
            int resLine = InfoLen.getLen(resPath);
            if (infoLine == resLine && infoLine != 0) {
                Toast.makeText(this, "检测成功", Toast.LENGTH_SHORT).show();
            } else {
                boolean flag = true;
                if (resLine > infoLine && infoLine != 0) {
                    int idx = 0;
                    try {
                        List<String> resList = new ArrayList<>();
                        BufferedReader br = new BufferedReader(new FileReader(resPath));
                        String tempLine;
                        while ((tempLine = br.readLine()) != null) {
                            int idx2 = Integer.parseInt(tempLine.split(";")[0]);
                            if (idx2 - 1 == idx) {
                                resList.add(tempLine);
                                idx++;
                            }
                        }
                        br.close();
                        if (infoLine == resList.size()) {
                            FileOutputStream fos = new FileOutputStream(resPath, false);
                            for (String s : resList) {
                                fos.write((s + "\n").getBytes(StandardCharsets.UTF_8));
                            }
                            fos.close();
                        }
                        flag = false;
                        Toast.makeText(MainActivity.this, "检测成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "自动对齐数据失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                if (flag) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("数据有误\ninfo.txt文件行数：" + infoLine + "\nresult.txt文件行数：" + resLine)
                            .setPositiveButton("确定", null)
                            .create().show();
                }
            }
        });

        //5.获取结果文件
        findViewById(R.id.btn_create_result).setOnClickListener(v -> {
            String filesPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/";
            boolean yz = Res_Txt_To_Excel.savaResXlsx(
                    filesPath + "advInfo.txt", resPath, filesPath + "结果表格.xlsx");
            if (yz) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("确定", null)
                        .create();
                alertDialog.setTitle("提示");
                alertDialog.setMessage("处理成功\n文件保存在：" + filesPath + "结果表格.xlsx");
                alertDialog.show();
            } else {
                Toast.makeText(MainActivity.this, "处理失败", Toast.LENGTH_SHORT).show();
            }
        });

        //6.分享结果文件
        findViewById(R.id.btn_view_file).setOnClickListener(v -> {
            ///storage/emulated/0/Download/
//            String path = getExternalFilesDir("").getPath() + "/"+"结果表格.xlsx";
            String resXlsxPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/结果表格.xlsx";
            File file = new File(resXlsxPath);
            if (file.exists()) {
                try {
                    Share.shareFile(resXlsxPath, MainActivity.this);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "分享文件异常~", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "没有找到结果表格~", Toast.LENGTH_SHORT).show();
            }
        });

        //7.清除数据
        findViewById(R.id.clearAll).setOnClickListener(v->{
//            ClearAllDatas.clear();
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("是否清除数据")
                    .setMessage("如果已经操作完一张表并且已经分享,可以清除数据然后对下一张表处理")
                    .setPositiveButton("确定", (dialog, which) -> {
                        //清除数据确定
                        ClearAllDatas.clear(Environment.getExternalStorageDirectory().getPath() + "/支付宝查实名");
                        Toast.makeText(MainActivity.this,"清除完毕",Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("取消",(d,w)->{
                        d.dismiss();
                    }).create();
            alertDialog.show();
        });

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("关于软件")
                    .setMessage("支付宝对应版本：10.2.90.8100及以上\n作者：\n梦雪 QQ/微信2487686673\n蔡佳强 微信/Jude_Cai")
                    .setPositiveButton("确定", null)
                    .create();
            alertDialog.show();
        });
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("请确认是否退出？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("退出", (dialog, which) -> finish())
                    .create();
            alertDialog.show();
        });
        checkPermissions();
        new Thread(() -> {
            String id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String content = HttpUtils.get("https://www.yuque.com/api/docs/share/5faa5b6a-e103-414a-bbdd-8f2c62b58c23?doc_slug=nilgwr&from=https%3A%2F%2Fwww.yuque.com%2Fdocs%2Fshare%2F5faa5b6a-e103-414a-bbdd-8f2c62b58c23%3F%23");
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject data = jsonObject.getJSONObject("data");
                String description = data.getString("description");
                if (description.contains(id)) {
                    btnCopyID.post(() -> {
                        btnCopyID.setVisibility(View.GONE);
                        Toast.makeText(this, "您已拥有使用权限", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    btnCopyID.post(() ->
                            Toast.makeText(MainActivity.this, "请联系作者获取使用权限", Toast.LENGTH_SHORT).show());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkPermissions() {
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null
                    && result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE).equals(true)
                        && result.get(Manifest.permission.READ_EXTERNAL_STORAGE).equals(true)) {
                    Toast.makeText(MainActivity.this, "读写权限申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "读写权限申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }


    //android11+软件接管手机所有文件权限
    private void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //Android11（SDK版本30）
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) { //判断是否获取到“允许管理所有文件”权限
                //这里写获取权限后需要进行的操作，比如查找手机中的 .pdf 文件
            } else {//没有获取到“允许管理所有文件”权限
                //请求“允许管理所有文件”权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 123);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //SDK版本23
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //这里写获取权限后需要进行的操作，比如查找手机中的 .pdf 文件
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        } else {
            //这里写获取权限后需要进行的操作，比如查找手机中的 .pdf 文件
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //这里写获取权限后需要进行的操作，比如查找手机中的 .pdf 文件
            } else {
                Toast.makeText(MainActivity.this, "存储权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //这里写获取权限后需要进行的操作，比如查找手机中的 .pdf 文件
            } else {
                Toast.makeText(MainActivity.this, "存储权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
        //1.2获得选择的路径并且让tvFilePath文本控件显示
        if (requestCode == 10011){
            if (data!=null){
                List<String> pathData = data.getStringArrayListExtra(Constants.CALLBACK_DATA_ARRAYLIST_STRING);//获取数据
                StringBuilder builder = new StringBuilder();
                for (String path : pathData) {
                    builder.append(path).append("");
                }
                Mtools.toast(MainActivity.this,builder.toString());
                tvFilePath = findViewById(R.id.tv_file_path);
                tvFilePath.setText(builder.toString());
            }
        }
    }

    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}