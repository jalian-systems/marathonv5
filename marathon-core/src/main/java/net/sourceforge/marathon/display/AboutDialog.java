package net.sourceforge.marathon.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.marathon.Version;
import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AboutDialog extends EscapeDialog {
    private static final long serialVersionUID = 1L;
    private JButton okButton;

    public AboutDialog() {
    }

    public void display() {
        setResizable(false);
        setModal(true);
        FormLayout layout = new FormLayout("pref", "fill:pref, pref, 3dlu, pref, 3dlu, pref, pref, pref, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.border(Borders.EMPTY);
        JLabel image = new JLabel(SplashScreen.SPLASH);
        image.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        CellConstraints constraints = new CellConstraints();
        builder.add(image, constraints.xy(1, 1));
        JPanel versionPanel = new JPanel();
        versionPanel.setAlignmentX(0.5f);
        JLabel version = new JLabel("Version: " + Version.id());
        version.setFont(version.getFont().deriveFont(11.0f));
        versionPanel.add(version);
        builder.add(versionPanel, constraints.xy(1, 2));
        builder.addSeparator(Version.blurbTitle(), constraints.xy(1, 4));
        builder.add(new JLabel("    " + Version.blurbCompany()), constraints.xy(1, 6));
        builder.add(new JLabel("    " + Version.blurbWebsite()), constraints.xy(1, 7));
        builder.add(new JLabel("    " + Version.blurbCredits()), constraints.xy(1, 8));
        JButton creditsButton = UIUtils.createCreditsButton();
        creditsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreditsDialog(AboutDialog.this).setVisible(true);
            }
        });
        okButton = UIUtils.createOKButton();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bbuilder = ButtonBarFactory.buildRightAlignedBar(creditsButton, okButton);
        bbuilder.setBackground(new Color(255, 255, 255));
        builder.getPanel().setBackground(new Color(255, 255, 255));
        builder.add(bbuilder, constraints.xy(1, 9));
        getContentPane().add(builder.getPanel());
        pack();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((size.width - getSize().width) / 2, (size.height - getSize().height) / 2);
        setVisible(true);
    }

    @Override public JButton getOKButton() {
        return okButton;
    }

    @Override public JButton getCloseButton() {
        return okButton;
    }

}
