package club.fdawei.viewbind.api;

import club.fdawei.viewbind.api.provider.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 2019/3/4.
 */
public class ViewBind {

    private static final String INJECTOR_NAME_SUFFIX = "_ViewInjector";

    private static final ProviderStore providerStore = new ProviderStore();
    private static final Map<String, ViewInjector> viewInjectorMap = new HashMap<>();

    public static void registerProvider(Class<?> clz, Provider provider) {
        providerStore.registerProvider(clz, provider);
    }

    public static void unregisterProvider(Class<?> clz) {
        providerStore.unregisterProvider(clz);
    }

    public static void bind(Object obj) {
        bind(obj, obj);
    }

    public static void bind(Object target, Object source) {
        if (target == null || source == null) {
            return;
        }
        Provider provider = providerStore.getProvider(source);
        if (provider == null) {
            return;
        }
        bindReal(target, source, provider);
    }

    private static void bindReal(Object target, Object source, Provider provider) {
        ViewInjector viewInjector = getViewInjector(target);
        if (viewInjector != null) {
            viewInjector.inject(target, source, provider);
        }
    }

    private static ViewInjector getViewInjector(Object target) {
        final String clzName = target.getClass().getName() + INJECTOR_NAME_SUFFIX;
        ViewInjector viewInjector = viewInjectorMap.get(clzName);
        if (viewInjector == null) {
            synchronized (viewInjectorMap) {
                viewInjector = viewInjectorMap.get(clzName);
                if (viewInjector == null) {
                    try {
                        Class clazz = Class.forName(clzName);
                        viewInjector = (ViewInjector) clazz.newInstance();
                        viewInjectorMap.put(clzName, viewInjector);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return viewInjector;
    }
}
