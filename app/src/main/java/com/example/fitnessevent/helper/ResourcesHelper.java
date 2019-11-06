package com.example.fitnessevent.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.example.fitnessevent.FitnessEventApplication;

public class ResourcesHelper {
    private static DisplayMetrics metrics = new DisplayMetrics();
    private static Point screenSize = new Point();

    static {
        setScreenSize();
    }

    public static void setScreenSize() {
        ((WindowManager) FitnessEventApplication.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(metrics);

        ((WindowManager) FitnessEventApplication.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getSize(screenSize);

    }

    public static Point getScreenSize() {
        return screenSize;
    }

    public static boolean isLandscape() {
        setScreenSize();
        return (screenSize.x > screenSize.y);
    }

    public static DisplayMetrics getMetrics() {
        return metrics;
    }

    public static String getString(int resource) {
        return FitnessEventApplication.getApplication().getString(resource);
    }

    public static String getString(int resource, Object... formatArgs) {
        return FitnessEventApplication.getApplication().getString(resource, formatArgs);
    }

    public static Resources getResources() {
        return FitnessEventApplication.getApplication().getResources();
    }

    public static String[] getStringArray(int resource) {
        return FitnessEventApplication.getApplication().getResources().getStringArray(resource);
    }

    public static int getDimensionPixelSize(int resource) {
        FitnessEventApplication app = FitnessEventApplication.getApplication();
        TypedValue rawValue = new TypedValue();
        app.getResources().getValue(resource, rawValue, true);
        if (rawValue.type == TypedValue.TYPE_FIRST_INT) {
            return app.getResources().getInteger(resource);
        } else {
            return app.getResources().getDimensionPixelSize(resource);
        }
    }

    public static Drawable getDrawable(int resource) {
        return FitnessEventApplication.getApplication().getResources().getDrawable(resource);
    }


}
