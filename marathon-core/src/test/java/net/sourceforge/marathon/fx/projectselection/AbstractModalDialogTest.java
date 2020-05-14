package net.sourceforge.marathon.fx.projectselection;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.javafxagent.components.JavaFXElementTest;

public abstract class AbstractModalDialogTest extends JavaFXElementTest {

    @Override
    protected Pane getMainPane() {
        return getRootPane();
    }

    protected BorderPane getRootPane() {
        ModalDialog<?> modalDialog = getModalDialog();
        BorderPane sceneContent = new BorderPane();
        if (modalDialog != null) {
            String title = modalDialog.getTitle();
            String subTitle = modalDialog.getSubTitle();
            Node icon = modalDialog.getIcon();
            if (title != null && !"".equals(title)) {
                VBox titleBox = new VBox();
                Label titleLabel = new Label(title, icon);
                titleLabel.getStyleClass().add("modaldialog-title");
                titleBox.getChildren().add(titleLabel);
                if (subTitle != null) {
                    Label subTitleLabel = new Label(subTitle);
                    subTitleLabel.getStyleClass().add("modaldialog-subtitle");
                    if (icon != null)
                        subTitleLabel.setPadding(new Insets(0, 0, 0, 20));
                    titleBox.getChildren().add(subTitleLabel);
                }
                titleBox.getChildren().add(new Separator());
                sceneContent.setTop(titleBox);
            }
            sceneContent.setCenter(modalDialog.getContentPane());
        }
        return sceneContent;

    }

    protected abstract ModalDialog<?> getModalDialog();

}