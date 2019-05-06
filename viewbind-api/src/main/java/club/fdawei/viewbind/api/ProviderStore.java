package club.fdawei.viewbind.api;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import club.fdawei.viewbind.api.provider.ActivityProvider;
import club.fdawei.viewbind.api.provider.Provider;
import club.fdawei.viewbind.api.provider.ViewProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by david on 2019/3/4.
 */
public class ProviderStore {

    private static final Provider ACTIVITY_PROVIDER = new ActivityProvider();
    private static final Provider VIEW_PROVIDER = new ViewProvider();

    private final Map<Class, Provider> providerMap = new ConcurrentHashMap<>();

    public void registerProvider(Class<?> clz, Provider provider) {
        if (clz == null || provider == null) {
            return;
        }
        providerMap.put(clz, provider);
    }

    public void unregisterProvider(Class<?> clz) {
        if (clz == null) {
            return;
        }
        providerMap.remove(clz);
    }

    @Nullable
    public Provider getProvider(Object source) {
        if (source == null) {
            return null;
        }
        if (source instanceof View) {
            return VIEW_PROVIDER;
        } else if (source instanceof Activity) {
            return ACTIVITY_PROVIDER;
        } else {
            return providerMap.get(source.getClass());
        }
    }
}
