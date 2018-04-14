package com.androidmediacode;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * Created by zhengheng on 18/4/13.
 */
public class TypefaceUtil {

    public static void replaceSystemDefaultFont(@NonNull Context context, @NonNull String fontPath) {
        replaceTypefaceField("MONOSPACE", createTypeface(context, fontPath));
    }

    /**
     * <p>Replace field in class Typeface with reflection.</p>
     */
    private static void replaceTypefaceField(String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Typeface createTypeface(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }
}
