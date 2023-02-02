package com.mx.accessibilitydemo.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mx.accessibilitydemo.R;
import com.mx.accessibilitydemo.utils.HttpUtils;
import com.mx.accessibilitydemo.utils.PackageManagerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MxAccessibilityService extends AccessibilityService {
    private static final String TAG = MxAccessibilityService.class.getName();
    private boolean isRunner;
    private boolean isInput;
    private Button btnStart;
    private TextView tvResult;
    private List<AccessibilityNodeInfo> pinfan;
    private List<AccessibilityNodeInfo> verify;
    private final InnerHandler mHandler = new InnerHandler(this);
    private String activityName;

    private static class InnerHandler extends Handler {
        private final WeakReference<MxAccessibilityService> mService;

        public InnerHandler(MxAccessibilityService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MxAccessibilityService mxAccessibilityService = mService.get();
            if (mxAccessibilityService != null) {
                switch (msg.what) {
                    case 1:
                        TextView tvResult = mxAccessibilityService.tvResult;
                        int scrollAmount = tvResult.getLayout()
                                .getLineTop(tvResult.getLineCount()) - tvResult.getHeight();
                        tvResult.scrollTo(0, Math.max(scrollAmount, 0));
                        tvResult.append(msg.obj.toString());
                        break;
                    case 2:
                        Toast.makeText(mxAccessibilityService, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        mxAccessibilityService.btnStart.setText("运行完毕");
                        break;
                    case 6:
                    case 4:
                        Toast.makeText(mxAccessibilityService, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        mxAccessibilityService.btnStart.setText(msg.obj.toString());
                        break;
                    case 5:
                        mxAccessibilityService.btnStart.setText(msg.obj.toString());
                        Toast.makeText(mxAccessibilityService, "开始运行", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        AlertDialog alertDialog = new AlertDialog.Builder(mxAccessibilityService)
                                .setTitle("提示")
                                .setMessage(msg.obj.toString())
                                .setNegativeButton("确定", null)
                                .create();
                        alertDialog.show();
                        break;
                }
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "无障碍服务开启", Toast.LENGTH_SHORT).show();
        floatingWindow();
    }

    /**
     * 创建悬浮窗
     */
    private void floatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            WindowManager windowManager =
                    (WindowManager) getSystemService(AccessibilityService.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutParams.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(this).inflate(R.layout.service_window, null);
            btnStart = view.findViewById(R.id.btn_start);
            btnStart.setOnClickListener(v -> {
                if (btnStart.getText().toString().equals("开始运行")) {
                    if (activityName.contains("NebulaActivity$Main")) {
                        new Thread(() -> {
                            String id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            String content = HttpUtils.get("https://www.yuque.com/api/docs/share/5faa5b6a-e103-414a-bbdd-8f2c62b58c23?doc_slug=nilgwr&from=https%3A%2F%2Fwww.yuque.com%2Fdocs%2Fshare%2F5faa5b6a-e103-414a-bbdd-8f2c62b58c23%3F%23");
                            try {
                                JSONObject jsonObject = new JSONObject(content);
                                JSONObject data = jsonObject.getJSONObject("data");
                                String description = data.getString("description");
                                Message obtain = Message.obtain();
                                if (description.contains(id)) {
                                    //Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名"
                                    String infoPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info.txt";
                                    String resultPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/result.txt";
                                    String infoPath2 = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info2.txt";
                                    if (new File(resultPath).exists()) {
                                        try {
                                            BufferedReader br0 = new BufferedReader(new FileReader(infoPath));
                                            BufferedReader br2 = new BufferedReader(new FileReader(resultPath));
                                            FileOutputStream fos2 = new FileOutputStream(infoPath2, true);
                                            String tempLine = "";
                                            String endLine = "";
                                            while ((tempLine = br2.readLine()) != null) {
                                                endLine = tempLine;
                                            }
                                            String resIndex = endLine.split(";")[0];
                                            tempLine = "";
                                            while ((tempLine = br0.readLine()) != null) {
                                                if (Integer.parseInt(tempLine.split(";")[0]) > Integer.parseInt(resIndex)) {
                                                    fos2.write((tempLine + "\n").getBytes(StandardCharsets.UTF_8));
                                                }
                                            }
                                            boolean delete = new File(infoPath).delete();
                                            boolean rename = new File(infoPath2).renameTo(
                                                    new File(Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info.txt"));
                                            br0.close();
                                            br2.close();
                                            fos2.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    isRunner = true;
                                    obtain.what = 5;
                                    obtain.obj = "暂停运行";
                                } else {
                                    obtain.what = 4;
                                    obtain.obj = "无权限使用";
                                }
                                mHandler.sendMessage(obtain);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        Message obtain = Message.obtain();
                        obtain.what = 6;
                        obtain.obj = "您不能在当前界面开始运行\n请前往转账界面点击开始运行!!!";
                        mHandler.sendMessage(obtain);
                    }
                } else {
                    isRunner = false;
                    btnStart.setText("开始运行");
                    Toast.makeText(MxAccessibilityService.this, "暂停运行", Toast.LENGTH_SHORT).show();
                }
            });
            view.findViewById(R.id.cl).setOnTouchListener(new View.OnTouchListener() {
                int lastX = 0;
                int lastY = 0;
                int paramX = 0;
                int paramY = 0;

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = (int) motionEvent.getRawX();
                            lastY = (int) motionEvent.getRawY();
                            paramX = layoutParams.x;
                            paramY = layoutParams.y;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int dx = (int) motionEvent.getRawX() - lastX;
                            int dy = (int) motionEvent.getRawY() - lastY;
                            layoutParams.x = paramX + dx;
                            layoutParams.y = paramY + dy;
                            windowManager.updateViewLayout(view, layoutParams);
                            break;
                    }
                    return true;
                }
            });
            view.findViewById(R.id.btn_close_service).setOnClickListener(v -> {
                disableSelf();
            });
            tvResult = view.findViewById(R.id.tv_result);
            windowManager.addView(view, layoutParams);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getPackageName() == null) return;
        if (accessibilityEvent.getPackageName().equals("com.eg.android.AlipayGphone")) {
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow == null) return;
            switch (accessibilityEvent.getEventType()) {
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    pinfan = rootInActiveWindow.findAccessibilityNodeInfosByText("频繁");
                    verify = rootInActiveWindow.findAccessibilityNodeInfosByText("验证");
                    break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    if (isRunner) {
                        findRealName(rootInActiveWindow);
                    }
                    break;
            }
        }
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            activityName = PackageManagerUtils.getActivityName(this, accessibilityEvent);
        }
    }

    @Override
    public void onInterrupt() {

    }


    /**
     * 查找支付宝实名的逻辑处理
     *
     * @param rootInActiveWindow 根节点信息
     */
    private void findRealName(AccessibilityNodeInfo rootInActiveWindow) {
        String infoPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info.txt";
        String resultPath = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/result.txt";
        new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(infoPath));
                FileOutputStream fos = new FileOutputStream(resultPath, true);
                String temp = br.readLine();
                String savaStr;
                while (temp != null) {
                    savaStr = "false";
                    if (isRunner) {
                        if (verify.size() != 0 || !activityName.contains("MainSearchActivity")) {
                            if (!activityName.contains("SoftInputWindow")) {
                                isRunner = false;
                                Message message = Message.obtain();
                                message.what = 3;
                                message.obj = "开始运行";
                                mHandler.sendMessage(message);
                            }
                        }
                        if (pinfan.size() != 0) {
                            isRunner = false;
                            Message message = Message.obtain();
                            message.what = 5;
                            message.obj = "当前账号已频繁，请更换支付宝账号！";
                            mHandler.sendMessage(message);
                            break;
                        }
                        String[] split = temp.split(";");
                        for (int j = 0; j < split.length - 2; j++) {
                            String number = split[j + 2];
                            String name = onResult(rootInActiveWindow, number);
                            if (name.equals("null")) {
                                savaStr = "false";
                            } else {
                                name = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                                if ((split[1].charAt(split[1].length() - 1) ==
                                        name.charAt(name.length() - 1)) &&
                                        (split[1].length() == name.length()) && (!name.equals("未实名"))) {
                                    savaStr = j + "";
                                    break;
                                }
                            }
                        }
                        //保存对应号码的索引到文本后面
                        String str2 = temp + savaStr + "\n";
                        Message obtain = Message.obtain();
                        obtain.what = 1;
                        obtain.obj = str2;
                        mHandler.sendMessage(obtain);
                        fos.write(str2.getBytes());
                        temp = br.readLine();
                    } else {
                        br.close();
                        fos.close();

                        String infoPath2 = Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info2.txt";
                        BufferedReader br0 = new BufferedReader(new FileReader(infoPath));
                        BufferedReader br2 = new BufferedReader(new FileReader(resultPath));
                        FileOutputStream fos2 = new FileOutputStream(infoPath2, true);
                        String tempLine = "";
                        String endLine = "";
                        while ((tempLine = br2.readLine()) != null) {
                            endLine = tempLine;
                        }
                        String resIndex = endLine.split(";")[0];
                        tempLine = "";
                        while ((tempLine = br0.readLine()) != null) {
                            if (Integer.parseInt(tempLine.split(";")[0]) > Integer.parseInt(resIndex)) {
                                fos2.write((tempLine + "\n").getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        boolean delete = new File(infoPath).delete();
                        boolean rename = new File(infoPath2).renameTo(
                                new File(Environment.getExternalStorageDirectory().getPath()+"/支付宝查实名" + "/info.txt"));
                        br0.close();
                        br2.close();
                        fos2.close();
                        break;
                    }
                }
                br.close();
                fos.close();
                isRunner = false;
                Message obtain = Message.obtain();
                obtain.what = 2;
                obtain.obj = "完成";
                mHandler.sendMessage(obtain);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取支付宝用户名
     *
     * @param rootInActiveWindow 根节点信息
     */
    private String getUsername(AccessibilityNodeInfo rootInActiveWindow) {
        if (isInput) {
            List<AccessibilityNodeInfo> nodeInfos = rootInActiveWindow.findAccessibilityNodeInfosByText("(");
            if (nodeInfos != null && nodeInfos.size() != 0) {
                String name = nodeInfos.get(0).getText().toString();
                isInput = false;
                return name;
            }
        }
        return "null";
    }

    /**
     * 向输入框输入文本并获取结果
     *
     * @param rootInActiveWindow 根节点信息
     * @param number             号码
     * @return 支付宝实名
     */
    private String onResult(AccessibilityNodeInfo rootInActiveWindow, String number) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, number);
        for (int i = 0; i < rootInActiveWindow.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootInActiveWindow.getChild(i);
            if (child == null) continue;
            CharSequence className = child.getClassName();
            if (className != null && className.equals("android.widget.EditText")) {
                child.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                isInput = true;
                break;
            }
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getUsername(rootInActiveWindow);
    }

}
