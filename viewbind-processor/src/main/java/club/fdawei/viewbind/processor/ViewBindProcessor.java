package club.fdawei.viewbind.processor;

import club.fdawei.viewbind.annotation.BindView;
import club.fdawei.viewbind.annotation.OnClick;
import club.fdawei.viewbind.annotation.OnLongClick;
import club.fdawei.viewbind.annotation.OnTouch;
import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;

@AutoService(Processor.class)
public class ViewBindProcessor extends AbstractProcessor {

    private IUtilProvider mUtilProvider;
    private Filer mFiler;
    private Elements mElementUtils;
    private Types mTypeUtils;
    private Logger mLogger = new Logger();
    private ViewIdFinder mViewIdFinder;

    private Map<TypeElement, InjectorGenerator> mInjectorClassInfoMap = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mTypeUtils = processingEnvironment.getTypeUtils();

        mLogger.setMessager(processingEnvironment.getMessager());

        mUtilProvider = new IUtilProvider() {
            @Override
            public Elements getElementUtils() {
                return mElementUtils;
            }

            @Override
            public Types getTypeUtils() {
                return mTypeUtils;
            }
        };

        mViewIdFinder = new ViewIdFinder(Trees.instance(processingEnvironment));
        mViewIdFinder.setLogger(mLogger);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        annotations.add(OnClick.class.getCanonicalName());
        annotations.add(OnLongClick.class.getCanonicalName());
        annotations.add(OnTouch.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        collectBindView(roundEnvironment);
        collectListener(roundEnvironment);
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
            InjectorGenerator injectorGenerator = getInjectorGenerator(typeElement);
            BindView anno = variableElement.getAnnotation(BindView.class);
            int id = anno.value();
            ViewId viewId = mViewIdFinder.findViewId(variableElement, BindView.class, id);
            BindViewInjectInfo bindViewInjectInfo = new BindViewInjectInfo(variableElement, viewId);
            injectorGenerator.addBindViewInjectInfo(bindViewInjectInfo);
        }
    }

    private void collectListener(RoundEnvironment roundEnvironment) {
        collectListener(roundEnvironment, OnClick.class);
        collectListener(roundEnvironment, OnLongClick.class);
        collectListener(roundEnvironment, OnTouch.class);
    }

    private void collectListener(RoundEnvironment roundEnvironment, Class<? extends Annotation> annoClz) {
        Set<? extends Element> elementsAnnoWith = roundEnvironment.getElementsAnnotatedWith(annoClz);
        for(Element element : elementsAnnoWith) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            InjectorGenerator injectorGenerator = getInjectorGenerator(typeElement);
            Annotation anno = executableElement.getAnnotation(annoClz);
            int[] ids = null;
            if (anno instanceof OnClick) {
                ids = new int[]{((OnClick) anno).value()};
            } else if (anno instanceof OnLongClick) {
                ids = new int[]{((OnLongClick) anno).value()};
            } else if (anno instanceof OnTouch) {
                ids = new int[]{((OnTouch) anno).value()};
            }
            if (ids != null) {
                ViewId[] viewIds = mViewIdFinder.findViewIds(executableElement, annoClz, ids);
                ListenerInjectInfo listenerInjectInfo = new ListenerInjectInfo(annoClz, executableElement, viewIds);
                injectorGenerator.addListenerInjectInfo(listenerInjectInfo);
            }
        }
    }

    private InjectorGenerator getInjectorGenerator(TypeElement typeElement) {
        InjectorGenerator injectorGenerator = mInjectorClassInfoMap.get(typeElement);
        if (injectorGenerator == null) {
            injectorGenerator = new InjectorGenerator(typeElement);
            injectorGenerator.setLogger(mLogger);
            injectorGenerator.setUtilProvider(mUtilProvider);
            mInjectorClassInfoMap.put(typeElement, injectorGenerator);
        }
        return injectorGenerator;
    }

    private void generateClass() {
        for (InjectorGenerator injectorGenerator : mInjectorClassInfoMap.values()) {
            injectorGenerator.genJavaFile(mFiler);
        }
    }
}
