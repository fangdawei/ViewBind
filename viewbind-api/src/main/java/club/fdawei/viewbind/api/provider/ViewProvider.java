package club.fdawei.viewbind.api.provider;

import android.view.View;

/**
 * Created by david on 2019/3/4.
 */
public class ViewProvider implements Provider {

    @Override
    public View findView(Object source, int id) {
        if (source instanceof View) {
            return ((View) source).findViewById(id);
        }
        return null;
    }
}
