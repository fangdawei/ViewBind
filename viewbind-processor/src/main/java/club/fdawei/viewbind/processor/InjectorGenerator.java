package club.fdawei.viewbind.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2019/3/4.
 */
public class InjectorGenerator {

    private Logger logger;
    private IUtilProvider utilProvider;
    private TypeElement typeElement;
    private List<BindViewInjectInfo> bindViewInjectInfoList = new ArrayList<>();
    private List<ListenerInjectInfo> listenerInjectInfoList = new ArrayList<>();

    public InjectorGenerator(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setUtilProvider(IUtilProvider utilProvider) {
        this.utilProvider = utilProvider;
    }

    public void addBindViewInjectInfo(BindViewInjectInfo bindViewInjectInfo) {
        if (bindViewInjectInfo != null) {
            bindViewInjectInfoList.add(bindViewInjectInfo);
        }
    }

    public void addListenerInjectInfo(ListenerInjectInfo listenerInjectInfo) {
        if (listenerInjectInfo != null) {
            listenerInjectInfoList.add(listenerInjectInfo);
        }
    }

    public void genJavaFile(Filer filer) {
        ClassName targetClzName = ClassName.get(typeElement);

        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Object.class, "target", Modifier.FINAL)
                .addParameter(Object.class, "source")
                .addParameter(TypeBox.PROVIDER, "provider");

        final String targetObj = "targetObj";
        injectMethodBuilder.beginControlFlow("if (!(target instanceof $T))", targetClzName)
                .addStatement("return")
                .endControlFlow()
                .addStatement("final $T $L = ($T) target", targetClzName, targetObj, targetClzName);

        for(BindViewInjectInfo bindViewInjectInfo : bindViewInjectInfoList) {
            injectMethodBuilder.addStatement(bindViewInjectInfo.buildCode(targetObj, "source"));
        }

        for(ListenerInjectInfo listenerInjectInfo : listenerInjectInfoList) {
            List<CodeBlock> codeBlocks = listenerInjectInfo.buildCode(targetObj, "source");
            for(CodeBlock codeBlock : codeBlocks) {
                injectMethodBuilder.addStatement(codeBlock);
            }
        }

        String pkgName = utilProvider.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        String injectorClzName = getInjectorClassName();
        TypeSpec injector = TypeSpec.classBuilder(injectorClzName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeBox.VIEW_INJECTOR)
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
        String binaryName = utilProvider.getElementUtils().getBinaryName(typeElement).toString();
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
