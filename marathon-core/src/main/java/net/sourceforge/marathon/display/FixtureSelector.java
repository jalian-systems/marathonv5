package net.sourceforge.marathon.display;

import javax.swing.JFrame;

public class FixtureSelector {

    public String selectFixture(JFrame parent, String[] fixtures, String fixture) {
        FixtureSelectionDialog dialog = new FixtureSelectionDialog(parent, fixtures, fixture == null ? "default" : fixture);
        dialog.setVisible(true);
        String selectedFixture = dialog.getSelectedFixture();
        return selectedFixture;
    }

}
