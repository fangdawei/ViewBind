package club.fdawei.viewbind.api.provider;

import android.view.View;

/**
 * Created by david on 2019/3/4.
 */
public interface Provider {

    View findView(Object source, int id);
}
