package club.fdawei.viewbind.api;

import club.fdawei.viewbind.api.provider.Provider;

/**
 * Created by david on 2019/3/4.
 */
public interface ViewInjector<T> {

    void inject(T target, Object source, Provider provider);
}
