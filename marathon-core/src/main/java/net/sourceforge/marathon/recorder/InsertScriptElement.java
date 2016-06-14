package net.sourceforge.marathon.recorder;

import net.sourceforge.marathon.action.AbstractScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class InsertScriptElement extends AbstractScriptElement {
    private static final long serialVersionUID = 1L;
    private String function;
    private String pkg = null;

    public InsertScriptElement(WindowId windowId, String function) {
        super(null, windowId);
        this.function = ScriptModel.getModel().getFunctionFromInsertDialog(function);
        this.pkg = ScriptModel.getModel().getPackageFromInsertDialog(function);
    }

    public String toScriptCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(Indent.getIndent()).append(function).append("\n");
        return sb.toString();
    }

    public String getImportStatement() {
        return ScriptModel.getModel().getScriptCodeForImportAction(pkg, function);
    }

}
