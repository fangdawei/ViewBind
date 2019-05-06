package club.fdawei.viewbind.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.sun.tools.javac.code.Symbol;

/**
 * Create by david on 2019/05/04.
 */
public class ViewId {

    private int value;
    private CodeBlock codeBlock;

    public ViewId(int value) {
        this(value, null);
    }

    public ViewId(int value, Symbol symbol) {
        this.value = value;
        if (symbol == null) {
            this.codeBlock = CodeBlock.of("$L", value);
        } else {
            String rPkgName = symbol.packge().getQualifiedName().toString();
            ClassName r = ClassName.get(rPkgName, "R", symbol.enclClass().name.toString());
            String resName = symbol.name.toString();
            this.codeBlock = CodeBlock.of("$T.$N", r, resName);
        }
    }

    public int getValue() {
        return value;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }
}
