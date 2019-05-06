package club.fdawei.viewbind.processor;

import club.fdawei.viewbind.annotation.OnClick;
import club.fdawei.viewbind.annotation.OnLongClick;
import club.fdawei.viewbind.annotation.OnTouch;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by david on 2019/3/5.
 */
public class ListenerInjectInfo {

    private Class<? extends Annotation> annoClz;
    private ExecutableElement executableElement;
    private ViewId[] viewIds;

    public ListenerInjectInfo(Class<? extends Annotation> annoClz, ExecutableElement executableElement, ViewId[] viewIds) {
        this.annoClz = annoClz;
        this.executableElement = executableElement;
        this.viewIds = viewIds;
    }

    public List<CodeBlock> buildCode(String target, String source) {
        List<CodeBlock> codeBlocks = new LinkedList<>();
        if (annoClz == OnClick.class) {
            buildOnClickListener(target, source, codeBlocks);
        } else if (annoClz == OnLongClick.class) {
            buildOnLongClickListener(target, source, codeBlocks);
        } else if (annoClz == OnTouch.class) {
            buildOnTouchListener(target, source, codeBlocks);
        }
        return codeBlocks;
    }

    private void buildOnClickListener(String target, String source, List<CodeBlock> codeBlocks) {
        if (viewIds == null) {
            return;
        }
        for (ViewId viewId : viewIds) {
            MethodSpec method = MethodSpec.methodBuilder("onClick")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(TypeBox.ANDROID_VIEW, "view")
                    .addStatement("$L.$N(view)", target, executableElement.getSimpleName())
                    .build();
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeBox.ANDROID_VIEW_ON_CLICK_LISTENER)
                    .addMethod(method)
                    .build();
            codeBlocks.add(CodeBlock.of("provider.findView($L, $L).setOnClickListener($L)",
                    source, viewId.getCodeBlock(), listener));
        }
    }

    private void buildOnLongClickListener(String target, String source, List<CodeBlock> codeBlocks) {
        if (viewIds == null) {
            return;
        }
        for (ViewId viewId : viewIds) {
            MethodSpec method = MethodSpec.methodBuilder("onLongClick")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.BOOLEAN)
                    .addParameter(TypeBox.ANDROID_VIEW, "view")
                    .addStatement("return $L.$N(view)", target, executableElement.getSimpleName())
                    .build();
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeBox.ANDROID_VIEW_ON_LONG_CLICK_LISTENER)
                    .addMethod(method)
                    .build();
            codeBlocks.add(CodeBlock.of("provider.findView($L, $L).setOnLongClickListener($L)",
                    source, viewId.getCodeBlock(), listener));
        }
    }

    private void buildOnTouchListener(String target, String source, List<CodeBlock> codeBlocks) {
        if (viewIds == null) {
            return;
        }
        for (ViewId viewId : viewIds) {
            MethodSpec method = MethodSpec.methodBuilder("onTouch")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.BOOLEAN)
                    .addParameter(TypeBox.ANDROID_VIEW, "view")
                    .addParameter(TypeBox.MOTION_EVENT, "event")
                    .addStatement("return $L.$N(view, event)", target, executableElement.getSimpleName())
                    .build();
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeBox.ANDROID_VIEW_ON_TOUCH_LISTENER)
                    .addMethod(method)
                    .build();
            codeBlocks.add(CodeBlock.of("provider.findView($L, $L).setOnTouchListener($L)",
                    source, viewId.getCodeBlock(), listener));
        }
    }
}
