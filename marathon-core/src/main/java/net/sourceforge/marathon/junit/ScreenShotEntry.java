package net.sourceforge.marathon.junit;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import com.google.common.io.Files;

import net.sourceforge.marathon.runtime.api.UsedAssertion;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.MakeAttachmentEvent;

public class ScreenShotEntry {

    private String message;
    private String fileName;
    private List<UsedAssertion> assertions;

    public ScreenShotEntry(String message, String fileName, List<UsedAssertion> assertions) {
        this.message = message;
        this.fileName = fileName;
        this.assertions = assertions;
    }

    private MakeAttachmentEvent makeAttachmentEvent() {
        if (assertions != null && assertions.size() > 0) {
            return makeAttachmentEventWithAssertions();
        }
        try {
            File file = new File(fileName);
            MakeAttachmentEvent event = new MakeAttachmentEvent(Files.toByteArray(file), message, "image/png");
            file.delete();
            return event;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MakeAttachmentEvent makeAttachmentEventWithAssertions() {
        File file = new File(fileName);
        try {
            String encoded = Base64.getEncoder().encodeToString(Files.toByteArray(file));
            StringBuffer b = new StringBuffer();
            b.append("<!DOCTYPE html>\n");
            b.append("<html>\n");
            b.append("<head>\n");
            b.append("<style>\n");
            b.append("table {\n");
            b.append("    font-family: arial, sans-serif;\n");
            b.append("    border-collapse: collapse;\n");
            b.append("    width: 100%;\n");
            b.append("}\n");
            b.append("\n");
            b.append("td, th {\n");
            b.append("    border: 1px solid #dddddd;\n");
            b.append("    text-align: left;\n");
            b.append("    padding: 8px;\n");
            b.append("}\n");
            b.append("\n");
            b.append("tr:nth-child(even) {\n");
            b.append("    background-color: #dddddd;\n");
            b.append("}\n");
            b.append("</style>\n");
            b.append("</head>\n");
            b.append("<body>\n");
            b.append("\n");
            b.append("<img src=\"data:image/png;base64,").append(encoded).append("\" />\n");
            b.append("<table>\n");
            b.append("  <tr>\n");
            b.append("    <th>Component</th>\n");
            b.append("    <th>Property</th>\n");
            b.append("    <th>Expected</th>\n");
            b.append("    <th>Actual</th>\n");
            b.append("    <th>Status</th>\n");
            b.append("  </tr>\n");
            for (UsedAssertion usedAssertion : assertions) {
                usedAssertion.makeTableEntry(b);
            }
            b.append("</table>\n");
            b.append("</body>\n");
            b.append("</html>\n");
            return new MakeAttachmentEvent(b.toString().getBytes(), message, "text/html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fire(Allure lifecycle) {
        MakeAttachmentEvent event = makeAttachmentEvent();
        if (event != null)
            lifecycle.fire(event);
    }

}
