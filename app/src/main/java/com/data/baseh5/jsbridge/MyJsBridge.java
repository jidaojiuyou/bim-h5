package com.data.baseh5.jsbridge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.data.baseh5.util.ActivityController;
import com.tencent.smtt.sdk.WebView;

/**
 * js桥
 *
 * @author jidaojiuyou
 * @date 2021-08-20
 * @since 2021-04-23
 */
@SuppressWarnings("unused")
public class MyJsBridge {
    /**
     * Context
     */
    private final Context context;
    /**
     * WebView
     */
    private final WebView webView;

    public MyJsBridge(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }


    /**
     * 打开浏览器
     *
     * @param url 链接
     */
    @JavascriptInterface
    public void openBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 打开拨号界面
     *
     * @param tel 电话号
     */
    @JavascriptInterface
    public void openPhone(String tel) {
        Uri uri = Uri.parse("tel:" + tel);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(intent);
    }

    /**
     * 退出App
     */
    @JavascriptInterface
    public void quit() {
        ActivityController.finishAll();
    }
}