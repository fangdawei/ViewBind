package club.fdawei.viewbind.processor;

import com.squareup.javapoet.ClassName;

/**
 * Created by david on 2019/3/4.
 */
public class TypeBox {

    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName MOTION_EVENT = ClassName.get("android.view", "MotionEvent");
    public static final ClassName ANDROID_VIEW_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName ANDROID_VIEW_ON_LONG_CLICK_LISTENER = ClassName.get("android.view", "View", "OnLongClickListener");
    public static final ClassName ANDROID_VIEW_ON_TOUCH_LISTENER = ClassName.get("android.view", "View", "OnTouchListener");
    public static final ClassName VIEW_INJECTOR = ClassName.bestGuess("club.fdawei.viewbind.api.ViewInjector");
    public static final ClassName PROVIDER = ClassName.bestGuess("club.fdawei.viewbind.api.provider.Provider");
}
