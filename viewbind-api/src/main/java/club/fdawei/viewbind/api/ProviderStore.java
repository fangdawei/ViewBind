package club.fdawei.viewbind.api;

import club.fdawei.viewbind.api.provider.ActivityProvider;
import club.fdawei.viewbind.api.provider.Provider;
import club.fdawei.viewbind.api.provider.ViewProvider;

/**
 * Created by david on 2019/3/4.
 */
public class ProviderStore {
    public static final Provider ACTIVIRT_PROVIDER = new ActivityProvider();
    public static final Provider VIEW_PROVIDER = new ViewProvider();
}
