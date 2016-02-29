package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.stage.Stage;
import net.sourceforge.marathon.javafxagent.JavaPropertyAccessor;
import net.sourceforge.marathon.javafxagent.WindowTitle;

public class FXStagePropertyAccessor extends JavaPropertyAccessor {

    private Stage stage;

    public FXStagePropertyAccessor(Stage stage) {
        super(stage);
        this.stage = stage;
    }

    public static final List<String> LAST_RESORT_RECOGNITION_PROPERTIES = new ArrayList<String>();

    static {
        LAST_RESORT_RECOGNITION_PROPERTIES.add("type");
        LAST_RESORT_RECOGNITION_PROPERTIES.add("title");
    }

    public final Map<String, String> findURP(List<List<String>> rp) {
        for (List<String> list : rp) {
            Map<String, String> rpValues = findValues(list);
            if (rpValues == null)
                continue;
        }
        return findValues(LAST_RESORT_RECOGNITION_PROPERTIES);
    }

    private Map<String, String> findValues(List<String> list) {
        Map<String, String> rpValues = new HashMap<String, String>();
        for (String attribute : list) {
            String value = getAttribute(attribute);
            if (value == null || "".equals(value)) {
                rpValues = null;
                break;
            }
            rpValues.put(attribute, value);
        }
        return rpValues;
    }

    public String getTitle() {
        return new WindowTitle(stage).getTitle();
    }
    
    public String getType() {
        return stage.getClass().getName();
    }
}
