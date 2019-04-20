package club.fdawei.viewbind.processor;

import club.fdawei.viewbind.annotation.OnClick;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2019/3/4.
 */
public class InjectorClassInfo {

    private TypeElement typeElement;
    private String packageName;
    private List<BindViewInjectInfo> bindViewInjectInfoList = new ArrayList<>();
    private List<OnClickInjectInfo> onClickInjectInfoList = new ArrayList<>();

    public InjectorClassInfo(String packageName, TypeElement typeElement) {
        this.packageName = packageName;
        this.typeElement = typeElement;
    }

    public void addBindViewInjectInfo(VariableElement variableElement, int viewId) {
        BindViewInjectInfo bindViewInjectInfo = new BindViewInjectInfo();
        bindViewInjectInfo.setVariableElement(variableElement);
        bindViewInjectInfo.setViewId(viewId);
        bindViewInjectInfoList.add(bindViewInjectInfo);
    }

    public void addOnClickInjectInfo(ExecutableElement executableElement, int viewId) {
        OnClickInjectInfo onClickInjectInfo = new OnClickInjectInfo();
        onClickInjectInfo.setExecutableElement(executableElement);
        onClickInjectInfo.setViewId(viewId);
        onClickInjectInfoList.add(onClickInjectInfo);
    }

    public void genJavaFile(Filer filer) {
        ClassName targetClzName = ClassName.bestGuess(typeElement.getQualifiedName().toString());

        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(targetClzName, "target", Modifier.FINAL).build())
                .addParameter(Object.class, "source")
                .addParameter(TypeUtils.PROVIDER, "provider");

        for(BindViewInjectInfo bindViewInjectInfo : bindViewInjectInfoList) {
            int viewId = bindViewInjectInfo.getViewId();
            Name fieldName = bindViewInjectInfo.getTargetFieldName();
            TypeMirror fieldType = bindViewInjectInfo.getTargetFieldType();
            injectMethodBuilder.addStatement("target.$N = ($T) provider.findView(source, $L)", fieldName, fieldType, viewId);
        }

        for(OnClickInjectInfo onClickInjectInfo : onClickInjectInfoList) {
            int viewId = onClickInjectInfo.getViewId();
            MethodSpec onClickMethod = MethodSpec.methodBuilder("onClick")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(TypeUtils.ANDROID_VIEW, "view")
                    .addStatement("target.$N()", onClickInjectInfo.getTargetMethodName())
                    .build();
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtils.ANDROID_VIEW_ON_CLICK_LISTENER)
                    .addMethod(onClickMethod)
                    .build();
            String viewName = "view_" + viewId;
            injectMethodBuilder.addStatement("$T $L = provider.findView(source, $L)", TypeUtils.ANDROID_VIEW, viewName, viewId);
            injectMethodBuilder.beginControlFlow("if ($L != null)", viewName);
            injectMethodBuilder.addStatement("$L.setOnClickListener($L)", viewName, listener);
            injectMethodBuilder.endControlFlow();
        }

        String injectorClzName = typeElement.getSimpleName().toString() + "_ViewInjector";
        TypeSpec injector = TypeSpec.classBuilder(injectorClzName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtils.VIEW_INJECTOR, targetClzName))
                .addMethod(injectMethodBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, injector)
                .addFileComment("Generated automatically. Do not modify!")
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
