package club.fdawei.viewbind.processor;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;

/**
 * Created by david on 2019/3/4.
 */
public class BindViewInjectInfo {

    private VariableElement variableElement;
    private ViewId viewId;

    public BindViewInjectInfo(VariableElement variableElement, ViewId viewId) {
        this.variableElement = variableElement;
        this.viewId = viewId;
    }

    public CodeBlock buildCode(String target, String source) {
        return CodeBlock.of("$L.$N = ($T) provider.findView($L, $L)",
                target, variableElement.getSimpleName(), variableElement.asType(), source, viewId.getCodeBlock());
    }
}
