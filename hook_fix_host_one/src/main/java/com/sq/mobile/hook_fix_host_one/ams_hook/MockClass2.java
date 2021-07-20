package com.sq.mobile.hook_fix_host_one.ams_hook;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;


import com.sq.mobile.hook_fix_lib_one.RefInvoke;

import java.util.ArrayList;

class MockClass2 implements Handler.Callback {

    Handler mBase;

    public MockClass2(Handler base) {
        mBase = base;
    }

    /***
     *
     * 具体见：
     * https://android.googlesource.com/platform/frameworks/base
     * /+/d630f105e8bc0021541aacb4dc6498a49048ecea/core/java/android/app/ActivityThread.java
     *
     *         public static final int LAUNCH_ACTIVITY         = 100;
     *
     *         public static final int PAUSE_ACTIVITY          = 101;
     *         public static final int PAUSE_ACTIVITY_FINISHING= 102;
     *         public static final int STOP_ACTIVITY_SHOW      = 103;
     *         public static final int STOP_ACTIVITY_HIDE      = 104;
     *         public static final int SHOW_WINDOW             = 105;
     *         public static final int HIDE_WINDOW             = 106;
     *         public static final int RESUME_ACTIVITY         = 107;
     *         public static final int SEND_RESULT             = 108;
     *         public static final int DESTROY_ACTIVITY         = 109;
     *         public static final int BIND_APPLICATION        = 110;
     *         public static final int EXIT_APPLICATION        = 111;
     *
     *         public static final int NEW_INTENT              = 112;
     *
     *         public static final int RECEIVER                = 113;
     *         public static final int CREATE_SERVICE          = 114;
     *         public static final int SERVICE_ARGS            = 115;
     *         public static final int STOP_SERVICE            = 116;
     *         public static final int REQUEST_THUMBNAIL       = 117;
     *         public static final int CONFIGURATION_CHANGED   = 118;
     *         public static final int CLEAN_UP_CONTEXT        = 119;
     *         public static final int GC_WHEN_IDLE            = 120;
     *         public static final int BIND_SERVICE            = 121;
     *         public static final int UNBIND_SERVICE          = 122;
     *         public static final int DUMP_SERVICE            = 123;
     *         public static final int LOW_MEMORY              = 124;
     *         public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
     *         public static final int RELAUNCH_ACTIVITY       = 126;
     *         public static final int PROFILER_CONTROL        = 127;
     *         public static final int CREATE_BACKUP_AGENT     = 128;
     *         public static final int DESTROY_BACKUP_AGENT    = 129;
     *         public static final int SUICIDE                 = 130;
     *         public static final int REMOVE_PROVIDER         = 131;
     *         public static final int ENABLE_JIT              = 132;
     *         public static final int DISPATCH_PACKAGE_BROADCAST = 133;
     *         public static final int SCHEDULE_CRASH          = 134;
     *         public static final int DUMP_HEAP               = 135;
     *         public static final int DUMP_ACTIVITY           = 136;
     *         public static final int SLEEPING                = 137;
     *         public static final int SET_CORE_SETTINGS       = 138;
     *
     *
     * */
    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            // ActivityThread 里面 "LAUNCH_ACTIVITY" 这个字段的值是100
            // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
            //LAUNCH_ACTIVITY
            case 100:
                handleLaunchActivity(msg);
                break;
            //NEW_INTENT
            case 112:
                handleNewIntent(msg);
                break;
        }

        mBase.handleMessage(msg);
        return true;
    }

    private void handleNewIntent(Message msg) {
        Object obj = msg.obj;
        ArrayList intents = (ArrayList) RefInvoke.getFieldObject(obj, "intents");

        for (Object object : intents) {
            Intent raw = (Intent) object;
            Intent target = raw.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
            if (target != null) {
                raw.setComponent(target.getComponent());

                if (target.getExtras() != null) {
                    raw.putExtras(target.getExtras());
                }

                break;
            }
        }
    }

    private void handleLaunchActivity(Message msg) {
        // 这里简单起见,直接取出TargetActivity;

        Object obj = msg.obj;

        // 把替身恢复成真身
        Intent raw = (Intent) RefInvoke.getFieldObject(obj, "intent");
        Intent target = raw.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
        if (target != null) {
            RefInvoke.setFieldObject(obj, "intent", target);
        }
    }
}
