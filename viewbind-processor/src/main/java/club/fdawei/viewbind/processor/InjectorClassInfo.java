package club.fdawei.viewbind.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2019/3/4.
 */
public class InjectorClassInfo {

    private IUtilBox utilBox;
    private TypeElement typeElement;
    private List<BindViewInjectInfo> bindViewInjectInfoList = new ArrayList<>();
    private List<OnClickInjectInfo> onClickInjectInfoList = new ArrayList<>();

    public InjectorClassInfo(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public void setUtilBox(IUtilBox utilBox) {
        this.utilBox = utilBox;
    }

    public void addBindViewInjectInfo(VariableElement variableElement, int viewId) {
        BindViewInjectInfo bindViewInjectInfo = new BindViewInjectInfo();
        bindViewInjectInfo.setVariableElement(variableElement);
        bindViewInjectInfo.setViewId(viewId);
        bindViewInjectInfoList.add(bindViewInjectInfo);
    }

    public void addOnClickInjectInfo(ExecutableElement executableElement, int[] viewId) {
        OnClickInjectInfo onClickInjectInfo = new OnClickInjectInfo();
        onClickInjectInfo.setExecutableElement(executableElement);
        onClickInjectInfo.setViewId(viewId);
        onClickInjectInfoList.add(onClickInjectInfo);
    }

    public void genJavaFile(Filer filer) {
        ClassName targetClzName = ClassName.get(typeElement);

        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Object.class, "target", Modifier.FINAL)
                .addParameter(Object.class, "source")
                .addParameter(TypeUtils.PROVIDER, "provider");

        final String targetObj = "targetObj";
        injectMethodBuilder.beginControlFlow("if (!(target instanceof $T))", targetClzName)
                .addStatement("return")
                .endControlFlow()
                .addStatement("final $T $L = ($T) target", targetClzName, targetObj, targetClzName);

        for(BindViewInjectInfo bindViewInjectInfo : bindViewInjectInfoList) {
            int viewId = bindViewInjectInfo.getViewId();
            Name fieldName = bindViewInjectInfo.getTargetFieldName();
            TypeMirror fieldType = bindViewInjectInfo.getTargetFieldType();
            injectMethodBuilder.addStatement("$L.$N = ($T) provider.findView(source, $L)", targetObj, fieldName, fieldType, viewId);
        }

        for(OnClickInjectInfo onClickInjectInfo : onClickInjectInfoList) {
            int[] viewIds = onClickInjectInfo.getViewId();
            if (viewIds == null) {
                continue;
            }
            for(int viewId : viewIds) {
                MethodSpec onClickMethod = MethodSpec.methodBuilder("onClick")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addParameter(TypeUtils.ANDROID_VIEW, "view")
                        .addStatement("$L.$N(view)", targetObj, onClickInjectInfo.getTargetMethodName())
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
        }

        String pkgName = utilBox.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        String injectorClzName = getInjectorClassName();
        TypeSpec injector = TypeSpec.classBuilder(injectorClzName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeUtils.VIEW_INJECTOR)
                .addMethod(injectMethodBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder(pkgName, injector)
                .addFileComment("Generated automatically. Do not modify!")
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getInjectorClassName() {
        String binaryName = utilBox.getElementUtils().getBinaryName(typeElement).toString();
        int index = binaryName.lastIndexOf('.');
        String binaryNameWithoutPkg;
        if (index >= 0 && index < binaryName.length() - 1) {
            binaryNameWithoutPkg = binaryName.substring(index + 1);
        } else {
            binaryNameWithoutPkg = binaryName;
        }
        return binaryNameWithoutPkg.concat("_ViewInjector");
    }
}
