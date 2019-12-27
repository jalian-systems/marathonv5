package net.sourceforge.marathon.runtime;

import java.util.ArrayList;
import java.util.List;

import com.jaliansystems.marathonite.api.IAdditionalActionProvider;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import net.sourceforge.marathon.display.DisplayWindow;
import net.sourceforge.marathon.display.MarathonAction;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.Preferences;
import net.sourceforge.marathon.util.Blurb;

public class BrowserActionProvider implements IAdditionalActionProvider {

    @Override
    public List<MarathonAction> getActions(Object o) {
        IEditorProvider editorProvider = (IEditorProvider) o;
        List<MarathonAction> mactions = new ArrayList<>();
        if (Constants.getFramework().equals(Constants.FRAMEWORK_WEB)) {
            ObservableList<Browser> browsers = Browser.getBrowsers();
            final ToggleGroup toggleGroup = new ToggleGroup();
            Object value = Preferences.instance().getValue("project", "browser",
                    System.getProperty(Constants.AUT_WEBAPP_DEFAULT_BROWSER));
            for (Browser browser : browsers) {
                MarathonAction baction = new MarathonAction(browser.getBrowserName(), "Use " + browser.getBrowserName(), "",
                        editorProvider, false, true) {
                    @Override
                    public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                            int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                        Preferences.instance().setValue("project", "browser", browser.getProxy());
                    }

                    @Override
                    public ToggleGroup getButtonGroup() {
                        return toggleGroup;
                    }

                    @Override
                    public boolean isSelected() {
                        return browser.getProxy().equals(value);
                    }

                    @Override
                    public Node getIcon() {
                        HBox b = new HBox(5);
                        Node icon = FXUIUtils.getIcon(browser.getBrowserName());
                        if (icon != null)
                            b.getChildren().add(icon);
                        if (browser.canPlay())
                            b.getChildren().add(FXUIUtils.getIcon("play"));
                        if (browser.canRecord())
                            b.getChildren().add(FXUIUtils.getIcon("record"));
                        return b;
                    }
                };
                baction.setMenuName("Browser");
                mactions.add(baction);
            }
            MarathonAction.SeparatorAction saction = new MarathonAction.SeparatorAction("Browser", false, true);
            saction.setMenuName("Browser");
            mactions.add(saction);
            MarathonAction baction = new MarathonAction("help", "Setting up Browsers", "", editorProvider, false, true,
                    "Setting up Browsers") {
                @Override
                public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                        int endCaretPosition, int startLine) throws Exception {
                    new Blurb("/BrowsersHelp/index", "Setting Up Browsers", false) {
                    };
                }
            };
            baction.setMenuName("Browser");
            mactions.add(baction);
            MarathonAction caction = new MarathonAction("settings", "Configuring browsers to work with Marathonite", "",
                    editorProvider, false, true, "Configure Browsers...") {
                @Override
                public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                        int endCaretPosition, int startLine) throws Exception {
                    BrowserConfigurationStage bcs = new BrowserConfigurationStage(browsers);
                    bcs.getStage().show();
                }
            };
            caction.setMenuName("Browser");
            mactions.add(caction);
        }
        return mactions;
    }

}
