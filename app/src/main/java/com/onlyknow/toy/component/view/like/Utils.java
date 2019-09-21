package com.onlyknow.toy.component.view.like;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.onlyknow.toy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 23/12/2015.
 */
public class Utils {
    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

    public static List<Icon> getIcons() {
        List<Icon> icons = new ArrayList<>();
        icons.add(new Icon(R.drawable.ok_like_heart_on, R.drawable.ok_like_heart_off, IconType.Heart));
        icons.add(new Icon(R.drawable.ok_like_star_on, R.drawable.ok_like_star_off, IconType.Star));
        icons.add(new Icon(R.drawable.ok_like_thumb_on, R.drawable.ok_like_thumb_off, IconType.Thumb));

        return icons;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable resizeDrawable(Context context, Drawable drawable, int width, int height) {
        Bitmap bitmap = getBitmap(drawable, width, height);
        return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable, width, height);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable, width, height);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
