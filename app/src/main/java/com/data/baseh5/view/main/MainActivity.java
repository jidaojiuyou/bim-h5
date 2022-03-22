package com.data.baseh5.view.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.data.baseh5.R;
import com.data.baseh5.base.BaseAppCompatActivity;
import com.data.baseh5.config.Constant;
import com.data.baseh5.jsbridge.MyJsBridge;
import com.data.baseh5.util.ActivityController;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 主活动
 *
 * @author jidaojiuyou
 * @date 2021-08-20
 * @since 2021-04-28
 */
public class MainActivity extends BaseAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView webView;
    private Context context;
    /**
     * 文件提供器
     */
    private CaptureStrategy captureStrategy;
    private ValueCallback<Uri[]> uploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO 此处需要将值设为自己的 包名.fileprovider
        captureStrategy = new CaptureStrategy(true, "com.data.baseh5.fileprovider");
        getPermission();
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        initView();
        bindEvent();
    }

    private void initView() {
        webView = findViewById(R.id.webView);
        context = this;
    }

    private void bindEvent() {
        webView.loadUrl(Constant.WEBVIEW_URL);
        setting();
        MyJsBridge myJsBridge = new MyJsBridge(context, webView);
        webView.addJavascriptInterface(myJsBridge, "android");
        setWebViewClient();
        setWebChromeClient();
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
    }

    @SuppressWarnings({"SetJavaScriptEnabled", "deprecated", "deprecation"})
    public void setting() {
        WebSettings settings = webView.getSettings();
        IX5WebSettingsExtension settingsExtension = webView.getSettingsExtension();
        // 允许JS
        settings.setJavaScriptEnabled(true);
        //关闭密码保存
        settings.setSavePassword(false);
        // 显示缩放按钮
        settings.setBuiltInZoomControls(false);
        // 是否允许缩放
        settings.setSupportZoom(false);
        // 使用预览模式
        settings.setLoadWithOverviewMode(true);
        // 设置WebView是否使用viewport
        settings.setUseWideViewPort(true);
        // 重写缓存
        settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        // 设置是否开启数据库存储API权限
        settings.setDatabaseEnabled(true);
        // 设置是否开启DOM存储API权限
        settings.setDomStorageEnabled(true);
        // 设置允许app缓存
        settings.setAppCacheEnabled(true);
        // 允许弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (settingsExtension != null) {
            Log.d(TAG, "setting: ");
            // 对于无法缩放的页面当用户双指缩放时会提示强制缩放，再次操作将触发缩放功能
            settingsExtension.setForcePinchScaleEnabled(false);
            // 对于刘海屏机器如果webview被遮挡会自动padding
            settingsExtension.setDisplayCutoutEnable(true);
        }
    }

    public void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient());
    }

    public void setWebChromeClient() {
        // 文件选择
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
                return true;
            }
        });
    }

    /**
     * 退出
     */
    @Override
    protected void onDestroy() {
        /// 清除缓存操作
        // QbSdk.clearAllWebViewCache(context, true);
        super.onDestroy();
    }

    /**
     * 重写后退方法
     */
    @Override
    public void onBackPressed() {
        MessageDialog
                .build()
                .setTitle(getString(R.string.tips))
                .setCancelable(true)
                .setMessageTextInfo(new TextInfo().setGravity(Gravity.CENTER))
                .setMessage(getString(R.string.will_quit))
                .setOkButton(getString(R.string.ok), (baseDialog, v) -> {
                    ActivityController.finishAll();
                    return false;
                })
                .setCancelButton(getString(R.string.cancel))
                .show(MainActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IX5WebViewExtension x5WebViewExtension = webView.getX5WebViewExtension();
        if (x5WebViewExtension != null) {
            Log.d(TAG, "X5内核加载成功");
        } else {
            Log.e(TAG, "X5内核加载失败");
        }
    }

    public void getPermission() {
        XXPermissions.with(this)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                .permission(Permission.CAMERA)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        // TODO 判断权限是否成功获取
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        // TODO 用户拒绝授权的后续操作
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.Group.STORAGE)) {
                // 成功获取权限
            } else {
                // 获取失败的后续操作
            }
        } else if (requestCode == Constant.FILE_CHOOSER_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                //给文件选择的ValueCallback设置onReceiveValue值
                List<Uri> list = data == null ? new ArrayList<>() : Matisse.obtainResult(data);
                Uri[] uris = new Uri[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    uris[i] = list.get(i);
                }
                uploadMessage.onReceiveValue(uris);
            } else if (resultCode == RESULT_CANCELED) {
                //给文件选择的ValueCallback设置null值
                uploadMessage.onReceiveValue(null);
            }
            uploadMessage = null;
        }
    }

    /**
     * 选择图片的方法
     */
    private void openImageChooserActivity() {
        // 这个组件时知乎的Matisse，具体使用方法请自行百度
        Matisse
                .from(MainActivity.this)
                // 照片视频全部显示
                .choose(MimeType.ofAll())
                // 启用相机
                .capture(true)
                // 设置文件提供者
                .captureStrategy(captureStrategy)
                //有序选择图片
                .countable(true)
                // 最大选择数量为9
                .maxSelectable(9)
                // 图像选择和预览活动所需的方向。
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                // 主题
                .theme(R.style.Matisse_Zhihu)
                // 加载方式
                .imageEngine(new GlideEngine())
                // 略缩图比例
                .thumbnailScale(0.75f)
                // 不显示预览，防止华为等ANR
                .showPreview(false)
                // 请求码
                .forResult(Constant.FILE_CHOOSER_RESULT_CODE);
    }
}