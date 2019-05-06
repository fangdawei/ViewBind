package club.fdawei.viewbind.processor;


import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by david on 2019/05/04.
 */
public class ViewIdFinder {

    private Trees trees;
    private Logger logger;

    public ViewIdFinder(Trees trees) {
        this.trees = trees;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ViewId findViewId(Element element, Class<? extends Annotation> annoClz, int value) {
        return findViewIds(element, annoClz, new int[]{value})[0];
    }

    public ViewId[] findViewIds(Element element, Class<? extends Annotation> annoClz, int[] values) {
        final Map<Integer, ViewId> idMap = new LinkedHashMap<>();
        AnnotationMirror annoMirror = getAnnoMirror(element, annoClz);
        if (annoMirror != null) {
            JCTree jcTree = (JCTree) trees.getTree(element, annoMirror);
            jcTree.accept(new TreeScanner() {
                @Override
                public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
                    Symbol symbol = jcFieldAccess.sym;
                    if (symbol.getEnclosingElement() == null
                            || symbol.getEnclosingElement().getEnclosingElement() == null
                            || symbol.getEnclosingElement().getEnclosingElement().enclClass() == null) {
                        return;
                    }
                    Integer id = (Integer) ((Symbol.VarSymbol) symbol).getConstantValue();
                    idMap.put(id, new ViewId(id, symbol));
                }

                @Override
                public void visitLiteral(JCTree.JCLiteral jcLiteral) {
                    Integer id = (Integer) jcLiteral.value;
                    idMap.put(id, new ViewId(id));
                }
            });
        }
        for (int id : values) {
            idMap.putIfAbsent(id, new ViewId(id));
        }
        return idMap.values().toArray(new ViewId[0]);
    }

    private static AnnotationMirror getAnnoMirror(Element element, Class<? extends Annotation> clz) {
        List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();
        for (AnnotationMirror mirror : mirrors) {
            if (TypeName.get(mirror.getAnnotationType()).equals(TypeName.get(clz))) {
                return mirror;
            }
        }
        return null;
    }
}
