package com.pa.common;

import android.util.Log;

public class Tracer
{
    public static final boolean isDebugMode     = true;
    private static final int     DEBUG_LEVEL     = 1;

    public static final int      LV1_DUMP        = 1;
    public static final int      LV2_VAR_CHECK   = 2;
    public static final int      LV3_LOOP        = 3;
    public static final int      LV4_METHOD_CALL = 4;
    public static final int      LV5_TAG         = 5;
    public static void d(String msg)
    {
        if (isDebugMode)
            Log.d("System.out.println", msg);
    }
    public static void d(String tag, String msg)
    {
        if (isDebugMode)
            Log.d(tag, msg);
    }

    public static void d(int debug_level, String tag, String msg)
    {
        if (isDebugMode)
        {
            if (debug_level >= DEBUG_LEVEL)
            {
                Log.d(debug_level + "-" + tag, msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr)
    {
        if (isDebugMode)
            Log.e(tag, msg, tr);
    }

    public static void w(String tag, String msg, Throwable tr)
    {
        if (isDebugMode)
            Log.w(tag, msg, tr);
    }

    public static void w(String tag, String msg)
    {
        Log.w(tag, msg);
    }
}
