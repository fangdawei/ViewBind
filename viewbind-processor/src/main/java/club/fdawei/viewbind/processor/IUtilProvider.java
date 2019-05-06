package club.fdawei.viewbind.processor;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by david on 2019/04/28.
 */
public interface IUtilProvider {

    Elements getElementUtils();

    Types getTypeUtils();
}
