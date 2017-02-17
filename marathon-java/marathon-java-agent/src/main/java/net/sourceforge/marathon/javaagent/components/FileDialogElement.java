package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.awt.FileDialog;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.ChooserHelper;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class FileDialogElement extends AbstractJavaElement {

    private JWindow dialog;

    public static class FileChooserComponent extends Component {

        private static final long serialVersionUID = 1L;
    }

    public FileDialogElement(JWindow dialog, IJavaAgent driver, JWindow window) {
        super(new FileChooserComponent(), driver, window);
        this.dialog = dialog;
    }

    @Override public void sendKeys(CharSequence... keysToSend) {
        FileDialog fileDialog = (FileDialog) dialog.getWindow();
        String filePath = (String) keysToSend[0];
        String setPath = "";
        if (filePath != null && !"".equals(filePath)) {
            setPath = ChooserHelper.decodeFile(filePath).getPath();
        }
        fileDialog.setFile(setPath);
        fileDialog.setVisible(false);
    }

}
