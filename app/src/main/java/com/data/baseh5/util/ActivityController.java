package com.data.baseh5.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动控制器<br/>
 * 用于特殊情况下,需要退出APP,此时可以全部退栈
 *
 * @author jidaojiuyou
 * @date 2021-08-20
 * @since 2021-04-28
 */
public class ActivityController {
    public static List<Activity> activities = new ArrayList<>();

    /**
     * 将activity加入栈
     *
     * @param activity activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 将activity退栈
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 全部finish
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}