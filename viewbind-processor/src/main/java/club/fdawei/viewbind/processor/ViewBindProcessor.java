package club.fdawei.viewbind.processor;

import club.fdawei.viewbind.annotation.BindView;
import club.fdawei.viewbind.annotation.OnClick;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

@AutoService(Processor.class)
public class ViewBindProcessor extends AbstractProcessor {

    private IUtilBox mUtilBox;
    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;
    private Types mTypeUtils;

    private Map<TypeElement, InjectorClassInfo> mInjectorClassInfoMap = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
        mTypeUtils = processingEnvironment.getTypeUtils();

        mUtilBox = new IUtilBox() {
            @Override
            public Elements getElementUtils() {
                return mElementUtils;
            }

            @Override
            public Types getTypeUtils() {
                return mTypeUtils;
            }
        };
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        annotations.add(OnClick.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        collectBindView(roundEnvironment);
        collectOnClick(roundEnvironment);
        generateClass();
        mInjectorClassInfoMap.clear();
        return true;
    }

    private void collectBindView(RoundEnvironment roundEnvironment) {
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

    private void collectOnClick(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsOnClickWith = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for(Element element : elementsOnClickWith) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            InjectorClassInfo injectorClassInfo = getInjectorClassInfo(typeElement);
            OnClick onClick = executableElement.getAnnotation(OnClick.class);
            int[] viewId = onClick.id();
            injectorClassInfo.addOnClickInjectInfo(executableElement, viewId);
        }
    }

    private InjectorClassInfo getInjectorClassInfo(TypeElement typeElement) {
        InjectorClassInfo injectorClassInfo = mInjectorClassInfoMap.get(typeElement);
        if (injectorClassInfo == null) {
            injectorClassInfo = new InjectorClassInfo(typeElement);
            injectorClassInfo.setUtilBox(mUtilBox);
            mInjectorClassInfoMap.put(typeElement, injectorClassInfo);
        }
        return injectorClassInfo;
    }

    private void generateClass() {
        for (InjectorClassInfo injectorClassInfo : mInjectorClassInfoMap.values()) {
            injectorClassInfo.genJavaFile(mFiler);
        }
    }
}
