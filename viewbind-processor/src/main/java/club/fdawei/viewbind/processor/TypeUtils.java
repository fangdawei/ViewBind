package club.fdawei.viewbind.processor;

import com.squareup.javapoet.ClassName;

/**
 * Created by david on 2019/3/4.
 */
public class TypeUtils {

    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName ANDROID_VIEW_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName VIEW_INJECTOR = ClassName.bestGuess("club.fdawei.viewbind.api.ViewInjector");
    public static final ClassName PROVIDER = ClassName.bestGuess("club.fdawei.viewbind.api.provider.Provider");
}
