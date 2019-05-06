package club.fdawei.viewbind.processor;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by david on 2019/05/05.
 */
public class Logger {

    private Messager messager;

    public void setMessager(Messager messager) {
        this.messager = messager;
    }

    public void i(String tag, String format, Object... args) {
        if (messager == null) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, String.format("%s " + format, tag, args));
    }

    public void w(String tag, String format, Object... args) {
        if (messager == null) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.WARNING, String.format("%s " + format, tag, args));
    }

    public void e(String tag, String format, Object... args) {
        if (messager == null) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s " + format, tag, args));
    }
}
