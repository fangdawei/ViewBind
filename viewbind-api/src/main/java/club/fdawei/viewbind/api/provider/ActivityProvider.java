package club.fdawei.viewbind.api.provider;

import android.app.Activity;
import android.view.View;

/**
 * Created by david on 2019/3/4.
 */
public class ActivityProvider implements Provider {

    @Override
    public View findView(Object source, int id) {
        if (source instanceof Activity) {
            return ((Activity) source).findViewById(id);
        }
        return null;
    }
}
