package net.sourceforge.marathon.kbmap;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.CardLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class BannerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private CardLayout cardlayout = new CardLayout();

    public BannerPanel() {
        setLayout(cardlayout);
    }

    public void addSheet(Sheet sheet, String name) {
        add(sheet, name);
    }

    public void showSheet(String name) {
        cardlayout.show(this, name);
    }

    public static class Sheet extends JPanel {
        private static final long serialVersionUID = 1L;
        private String title;
        private String[] lines;
        private ImageIcon imageIcon;

        public Sheet(String title, String[] lines, ImageIcon imageIcon) {
            this.title = title;
            this.lines = lines;
            this.imageIcon = imageIcon;
            initComponents();
        }

        public Sheet(String title, String[] lines) {
            this(title, lines, null);
        }

        private void initComponents() {
            StringBuilder rowSpec = new StringBuilder("7dlu, pref, 2dlu");
            for (int i = 0; i < lines.length; i++) {
                rowSpec.append(", pref");
            }
            rowSpec.append(",7dlu");
            FormLayout formLayout = new FormLayout("7dlu, pref:grow, 4dlu, pref, 7dlu", rowSpec.toString());
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16));
            PanelBuilder panelBuilder = new PanelBuilder(formLayout);
            CellConstraints cc = new CellConstraints();
            panelBuilder.add(titleLabel, cc.xy(2, 2));
            for (int i = 0; i < lines.length; i++) {
                panelBuilder.add(new JLabel(lines[i]), cc.xy(2, (i + 4)));
            }
            if (imageIcon != null) {
                panelBuilder.add(new JLabel(imageIcon),
                        cc.xywh(4, 1, 1, 4 + lines.length, CellConstraints.CENTER, CellConstraints.TOP));
            }
            JPanel subPanel = panelBuilder.getPanel();
            subPanel.setBackground(UIManager.getColor("TextField.background"));
            panelBuilder = new PanelBuilder(new FormLayout("pref:grow", "pref, pref"), this);
            panelBuilder.add(subPanel, cc.xy(1, 1));
            panelBuilder.addSeparator("", cc.xy(1, 2));
        }
    }
}
