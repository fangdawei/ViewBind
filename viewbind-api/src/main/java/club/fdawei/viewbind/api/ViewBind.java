package club.fdawei.viewbind.api;

import android.app.Activity;
import android.view.View;
import club.fdawei.viewbind.api.provider.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 2019/3/4.
 */
public class ViewBind {

    private static final String IMPL_SUFFIX = "_ViewInjector";

    private static final Map<String, ViewInjector> viewInjectorMap = new HashMap<>();

    public static void bind(View view) {
        bind(view, view, ProviderStore.VIEW_PROVIDER);
    }

    public static void bind(Activity activity) {
        bind(activity, activity, ProviderStore.ACTIVIRT_PROVIDER);
    }

    private static void bind(Object target, Object source, Provider provider) {
        ViewInjector viewInjector = getViewInjector(target);
        if (viewInjector != null) {
            viewInjector.inject(target, source, provider);
        }
    }

    private static ViewInjector getViewInjector(Object target) {
        final String clzName = target.getClass().getName() + IMPL_SUFFIX;
        ViewInjector viewInjector = viewInjectorMap.get(clzName);
        if (viewInjector == null) {
            try {
                Class clazz = Class.forName(clzName);
                viewInjector = (ViewInjector) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return viewInjector;
    }
}
