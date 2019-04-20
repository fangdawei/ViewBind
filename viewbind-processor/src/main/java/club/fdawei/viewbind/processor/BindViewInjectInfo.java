package club.fdawei.viewbind.processor;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by david on 2019/3/4.
 */
public class BindViewInjectInfo {

    private VariableElement variableElement;
    private int viewId;

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public void setVariableElement(VariableElement variableElement) {
        this.variableElement = variableElement;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public TypeMirror getTargetFieldType() {
        return variableElement.asType();
    }

    public Name getTargetFieldName() {
        return variableElement.getSimpleName();
    }
}
