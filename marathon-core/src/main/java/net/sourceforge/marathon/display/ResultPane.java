/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.runtime.api.Failure;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.SourceLine;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.google.inject.Inject;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.toolbars.ToolBarConstraints;
import com.vlsolutions.swing.toolbars.ToolBarContainer;
import com.vlsolutions.swing.toolbars.ToolBarPanel;
import com.vlsolutions.swing.toolbars.VLToolBar;

public class ResultPane implements Dockable {
    private static final Icon ICON_RESULTPANE = new ImageIcon(
            TextAreaOutput.class.getResource("/net/sourceforge/marathon/display/icons/enabled/showreport.gif"));

    private static final DockKey DOCK_KEY = new DockKey("Results", "Results", "Test results", ICON_RESULTPANE);

    static class PlaybackResultList {
        private List<PlaybackResult> resultList = new ArrayList<PlaybackResult>();

        public int failureCount() {
            int count = 0;
            for (PlaybackResult pr : resultList) {
                count += pr.failureCount();
            }
            return count;
        }

        public Failure getFailureAt(int selectedRow) {
            for (PlaybackResult pr : resultList) {
                if (selectedRow < pr.failureCount()) {
                    return pr.failures()[selectedRow];
                } else
                    selectedRow -= pr.failureCount();
            }
            return null;
        }

        public void add(PlaybackResult result) {
            resultList.add(result);
        }

        public void clear() {
            resultList.clear();
        }
    }

    PlaybackResultList pbResult = new PlaybackResultList();

    private ResultTableModel resultTableModel;
    private JTable resultTable;
    public Failure selectedResult;
    private JLabel label;

    private ToolBarContainer panel = ToolBarContainer.createDefaultContainer(true, false, false, false, FlowLayout.TRAILING);

    private EventListenerList listeners = new EventListenerList();

    public static interface IResultPaneSelectionListener extends EventListener {
        public void resultSelected(SourceLine line);
    }

    public ResultPane() {
        initUI();
    }

    public void addSelectionListener(IResultPaneSelectionListener l) {
        listeners.add(IResultPaneSelectionListener.class, l);
    }

    /* default */int getEventSize() {
        return resultTableModel.getRowCount();
    }

    private void initUI() {
        resultTableModel = new ResultTableModel();
        resultTable = new JTable(resultTableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        resultTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableCellRenderer renderer = resultTable.getTableHeader().getDefaultRenderer();
        if (renderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) renderer).setHorizontalAlignment(SwingConstants.LEFT);
        }
        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow >= 0 && pbResult != null && selectedRow < pbResult.failureCount()) {
                    selectedResult = pbResult.getFailureAt(selectedRow);
                } else
                    selectedResult = null;
            }
        });
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int clickCount = e.getClickCount();
                if (clickCount > 1) {
                    SourceLine[] failure = selectedResult.getTraceback();
                    if (failure.length > 0)
                        fireResultPaneSelectedEvent(failure[0]);
                }
            }
        });
        resultTable.getSelectionModel().setSelectionInterval(0, 0);
        JPanel panel1 = new JPanel(new BorderLayout());
        label = new JLabel(getMessageText());
        label.setForeground(Color.GREEN);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        panel1.add(label, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel1.add(scrollPane, BorderLayout.CENTER);
        panel.add(panel1);
        ToolBarPanel barPanel = panel.getToolBarPanelAt(BorderLayout.NORTH);
        VLToolBar toolBar = new VLToolBar();
        JButton clear = UIUtils.createClearButton();
        clear.setToolTipText("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        JButton showError = UIUtils.createShowMessageButton();
        showError.setToolTipText("Show message");
        showError.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = resultTable.getSelectedRow();
                if (row == -1)
                    return;
                Failure failure = pbResult.getFailureAt(row);
                if (failure != null)
                    showMessage(failure);
            }
        });
        toolBar.add(showError);
        toolBar.add(clear);
        barPanel.add(toolBar, new ToolBarConstraints());
    }

    @Inject private IEditorProvider editorProvider;

    protected void showMessage(Failure failure) {
        new MessageDialog(failure.getMessage(), "Failure Message", editorProvider).setVisible(true);
    }

    private void fireResultPaneSelectedEvent(SourceLine line) {
        IResultPaneSelectionListener[] list = listeners.getListeners(IResultPaneSelectionListener.class);
        for (IResultPaneSelectionListener listener : list) {
            listener.resultSelected(line);
        }
    }

    private class ResultTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        String[] columnNames = { "Description", "File", "Location" };

        public String getColumnName(int column) {
            return columnNames[column];
        }

        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            if (pbResult == null)
                return 0;
            return pbResult.failureCount();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Failure result = pbResult.getFailureAt(rowIndex);
            SourceLine line;
            if (result.getTraceback().length == 0)
                line = new SourceLine("", "", -1);
            else
                line = result.getTraceback()[0];
            File file = new File(line.fileName);
            switch (columnIndex) {
            case 0:
                return result.getMessage();
            case 1:
                return file.getName();
            case 2:
                return "line " + line.lineNumber + " in function " + line.functionName;
            default:
                return "";
            }
        }
    }

    /* default */void setSelectionIndex(int index, int index2) {
        resultTable.getSelectionModel().setSelectionInterval(index, index2);
    }

    /* default */int getSelectionMode() {
        return resultTable.getSelectionModel().getSelectionMode();
    }

    /* default */String getMessageText() {
        if (pbResult == null)
            return "";
        if (pbResult.failureCount() > 0) {
            String errorString = pbResult.failureCount() == 1 ? " error" : " errors";
            return pbResult.failureCount() + errorString;
        }
        return "No Errors";
    }

    public void addResult(PlaybackResult result) {
        pbResult.add(result);
        this.resultTableModel.fireTableDataChanged();
        label.setText(getMessageText());
        if (pbResult.failureCount() > 0)
            label.setForeground(Color.RED);
        else
            label.setForeground(Color.GREEN);
        setSelectionIndex(0, 0);
    }

    public void clear() {
        pbResult.clear();
        this.resultTableModel.fireTableDataChanged();
        label.setText(getMessageText());
    }

    public Component getComponent() {
        return panel;
    }

    public DockKey getDockKey() {
        return DOCK_KEY;
    }
}
