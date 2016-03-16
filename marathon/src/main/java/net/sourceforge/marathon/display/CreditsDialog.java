package net.sourceforge.marathon.display;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class CreditsDialog extends EscapeDialog {
    private static final long serialVersionUID = 1L;
    private JButton okButton;

    public CreditsDialog(JDialog parent) {
        super(parent, "Credits", true);
        setLocation(parent.getLocation().x + 50, parent.getLocation().y + 50);
        FormLayout layout = new FormLayout("fill:pref:grow", "fill:pref:grow, 3dlu, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints constraints = new CellConstraints();
        JEditorPane ep = new JEditorPane("text/html", "");
        ep.setEditable(false);
        ep.setText(getCreditContent());
        builder.add(ep, constraints.xy(1, 1));
        okButton = UIUtils.createOKButton();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bbuilder = ButtonBarFactory.buildRightAlignedBar(okButton);
        bbuilder.setBackground(new Color(255, 255, 255));
        builder.add(bbuilder, constraints.xy(1, 3));
        builder.getPanel().setBackground(new Color(255, 255, 255));
        getContentPane().add(builder.getPanel());
        pack();
    }

    private String getCreditContent() {
        // @formatter:off
        String prefix = "<html><body>" +
        		            "<center><h1>Credits</h1></center>" +
                            "<p><table width=\"100%\" border=\"1\" align=\"center\" cellpadding=\"3\" cellspacing=\"0\">" +
                            "<tr bgcolor=\"#c3d9ff\">" + "<th>Package</th>" + "<th>Blurb</th>" + "<th>Web Site</th>" + "</tr>";
        
        String suffix =      "</table></p>" +
        		        "</body></html>";
        // @formatter:on
        StringBuffer content = new StringBuffer(prefix);
        content.append(getCredit("Eclipse",
                "Great platform to work with.<br>The navigator and junit interfaces as well as most of the icons<br> are picked up from eclipse package. Hopefully, we will have Marathon as eclipse package sometime",
                "http://eclipse.org"));
        content.append(getCredit("JRuby",
                "JRuby is an 100% pure-Java implementation of the Ruby programming language.<br>Marathon Ruby scripting model uses JRuby.",
                "http://jruby.codehaus.org"));
        content.append(getCredit("jEdit",
                "Programmer's text editor.<br>Marathon uses jEdit's excellent text area component to provide a comprehensive editing environment.",
                "http://www.jedit.org"));
        content.append(getCredit("Looks and Forms", "The good looks of Marathon are derived from these two packages from jgoodies.",
                "http://www.jgoodies.com"));
        content.append(getCredit("JUnit", "The original Java unit testing framework", "http://www.junit.org"));
        content.append(getCredit("VL Docking",
                "VLDocking Framework is a set of Java Swing Components providing a simple and coherent API to bring docking capabilities to "
                        + "any Swing application. "
                        + "<p>VLDocking Framework (www.vlsolutions.com), is the property of VLSolutions, and "
                        + "is distributed under the terms of its commercial license. It can only be used in the context of the marathon project."
                        + "<p>For any other usage, please refer to the open source (GPL compatible) version available from VLSolutions web site.",
                "http://www.vlsolutions.com"));
        content.append(suffix);
        return content.toString();
    }

    private String getCredit(String name, String blurb, String website) {
        String string = "<tr>" + "<td valign=\"center\" nowrap>" + name + "</td>" + "<td width=\"250px\" valign=\"center\">" + blurb
                + "</td>" + "<td valign=\"center\" nowrap><a href=\"" + website + "\">" + website + "</a>" + "</tr>";
        return string;
    }

    @Override public JButton getOKButton() {
        return okButton;
    }

    @Override public JButton getCloseButton() {
        return okButton;
    }
}
