package club.fdawei.viewbind.api;

import club.fdawei.viewbind.api.provider.Provider;

/**
 * Created by david on 2019/3/4.
 */
public interface ViewInjector {
    void inject(Object target, Object source, Provider provider);
}
