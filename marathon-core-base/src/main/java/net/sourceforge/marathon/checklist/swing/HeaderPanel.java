package net.sourceforge.marathon.checklist.swing;

import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.checklist.CheckList.CheckListItem;
import net.sourceforge.marathon.checklist.CheckList.Header;

public class HeaderPanel extends CheckListItemPanel {
    private Header item;

    public HeaderPanel(Header item) {
        this.item = item;
    }

    @Override
    protected JPanel createPanel(boolean selectable, boolean editable) {
        FormLayout layout = new FormLayout("pref:grow", "pref");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.appendSeparator(item.getLabel());
        builder.appendUnrelatedComponentsGapRow();

        JPanel panel = builder.getPanel();
        return panel;
    }

    @Override
    public CheckListItem getItem() {
        return item;
    }

}
