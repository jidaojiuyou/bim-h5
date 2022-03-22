package com.data.baseh5.base;

import android.app.Application;
import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.IOSStyle;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;
import java.util.Map;


/**
 * @author jidaojiuyou
 * @date 2021-08-20
 * @since 2021-04-22
 */
public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        // dex2oat
        Map<String, Object> map = new HashMap<>(2);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        // 允许流量下载核心
        QbSdk.setDownloadWithoutWifi(true);
        // 加载内核回调
        QbSdk.PreInitCallback preInitCallback = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.i(TAG, "内核核心初始化完成");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e(TAG, "内核初始化成功:" + b);
            }
        };
        // 加载内核
        if (!QbSdk.isTbsCoreInited()) {
            QbSdk.initX5Environment(this, preInitCallback);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        // 初始化弹窗组件
        DialogX.init(this);
        // 配置dialogX
        settingDialogX();
    }


    /**
     * 配置dialogX
     */
    private void settingDialogX() {
        //开启调试模式，在部分情况下会使用 Log 输出日志信息
        DialogX.DEBUGMODE = true;
        //设置亮色/暗色（在启动下一个对话框时生效）
        DialogX.globalTheme = DialogX.THEME.LIGHT;
        //设置主题样式
        DialogX.globalStyle = new IOSStyle();
        //限制 PopTip 一次只显示一个实例（关闭后可以同时弹出多个 PopTip）
        DialogX.onlyOnePopTip = true;
        //设置默认对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
        DialogX.cancelable = false;
        // 禁止震动
        DialogX.useHaptic = false;
        //设置对话框最大宽度（单位为像素）
        DialogX.dialogMaxWidth = 1920;
        //设置 InputDialog 自动弹出键盘
        DialogX.autoShowInputKeyboard = true;
        //设置默认对话框背景颜色（值为ColorInt，为-1不生效）
        DialogX.backgroundColor = Color.WHITE;
        //设置默认提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
        DialogX.cancelableTipDialog = false;
        // 使用沉浸式适配
        DialogX.useActivityLayoutTranslationNavigationBar = true;
    }
}