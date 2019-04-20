package club.fdawei.viewbind.processor;

import club.fdawei.viewbind.annotation.BindView;
import club.fdawei.viewbind.annotation.OnClick;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class ViewBindProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;

    private Map<TypeElement, InjectorClassInfo> mInjectorClassInfoMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "ViewBindProcessor start");
        mInjectorClassInfoMap.clear();
        processBindView(roundEnvironment);
        processOnClick(roundEnvironment);
        for (InjectorClassInfo injectorClassInfo : mInjectorClassInfoMap.values()) {
            injectorClassInfo.genJavaFile(mFiler);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "ViewBindProcessor end");
        return true;
    }

    private void processBindView(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsBindViewWith = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsBindViewWith) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            InjectorClassInfo injectorClassInfo = getInjectorClassInfo(typeElement);
            BindView bindView = variableElement.getAnnotation(BindView.class);
            int viewId = bindView.id();
            injectorClassInfo.addBindViewInjectInfo(variableElement, viewId);
        }
    }

    private void processOnClick(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsOnClickWith = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for(Element element : elementsOnClickWith) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            InjectorClassInfo injectorClassInfo = getInjectorClassInfo(typeElement);
            OnClick onClick = executableElement.getAnnotation(OnClick.class);
            int viewId = onClick.id();
            injectorClassInfo.addOnClickInjectInfo(executableElement, viewId);
        }
    }

    private InjectorClassInfo getInjectorClassInfo(TypeElement typeElement) {
        InjectorClassInfo injectorClassInfo = mInjectorClassInfoMap.get(typeElement);
        if (injectorClassInfo == null) {
            String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            injectorClassInfo = new InjectorClassInfo(packageName, typeElement);
            mInjectorClassInfoMap.put(typeElement, injectorClassInfo);
        }
        return injectorClassInfo;
    }
}
