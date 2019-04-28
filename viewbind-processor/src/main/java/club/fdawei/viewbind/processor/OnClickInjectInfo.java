package club.fdawei.viewbind.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Created by david on 2019/3/5.
 */
public class OnClickInjectInfo {

    private ExecutableElement executableElement;
    private int[] viewId;

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    public int[] getViewId() {
        return viewId;
    }

    public void setViewId(int[] viewId) {
        this.viewId = viewId;
    }

    public Name getTargetMethodName() {
        return executableElement.getSimpleName();
    }
}
