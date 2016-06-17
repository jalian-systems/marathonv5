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
package net.sourceforge.marathon.kbmap;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class Generator extends JFrame {

    private static final long serialVersionUID = 1L;
    public static final ImageIcon BANNER = new ImageIcon(
            Generator.class.getClassLoader().getResource("net/sourceforge/marathon/kbmap/banner.png"));;

    private static class KeyMap implements Comparable<KeyMap> {
        private boolean shiftDown;
        private char keyChar;
        private int keyCode;

        public KeyMap(boolean shiftDown, char keyChar, int keyCode) {
            this.shiftDown = shiftDown;
            this.keyChar = keyChar;
            this.keyCode = keyCode;
        }

        @Override public String toString() {
            return keyChar + " " + (shiftDown ? "shift " : "") + keyCodeText.get(keyCode).substring(3);
        }

        @Override public int compareTo(KeyMap o) {
            return Character.valueOf(keyChar).compareTo(Character.valueOf(o.keyChar));
        }
    }

    private List<KeyMap> mappings = null;
    private JTextField textField;
    private JButton generateEvents;
    private JButton saveFile;

    public Generator() {
        super(Generator.class.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        BannerPanel bannerPanel = new BannerPanel();
        String[] lines = { "This tool allows to create a keyboard layout map to use with Marathon." };
        BannerPanel.Sheet sheet = new BannerPanel.Sheet("Create a Marathon keyboard layout mapping file", lines, BANNER);
        bannerPanel.addSheet(sheet, "main");
        add(bannerPanel, BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);
        pack();
    }

    private JPanel createActions() {
        generateEvents = new JButton("Generate Events");
        generateEvents.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                textField.requestFocusInWindow();
                new Thread(new Runnable() {
                    @Override public void run() {
                        try {
                            Robot robot = new Robot();
                            mappings = new ArrayList<Generator.KeyMap>();
                            List<Integer> asciiKeycodes = getAsciiKeycodes();
                            tryRobotWith(robot, asciiKeycodes, false);
                            tryRobotWith(robot, asciiKeycodes, true);
                            saveFile.setEnabled(true);
                        } catch (AWTException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }).start();
            }
        });
        saveFile = new JButton("Save file");
        saveFile.setEnabled(false);
        saveFile.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                File marathon = new File(System.getProperty("user.home"), ".marathon");
                marathon.mkdirs();
                JFileChooser chooser = new JFileChooser(marathon);
                Locale locale = InputContext.getInstance().getLocale();
                chooser.setSelectedFile(new File(locale.getLanguage() + "_" + locale.getCountry() + ".kb"));
                chooser.setMultiSelectionEnabled(false);
                int showSaveDialog = chooser.showSaveDialog(null);
                if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
                    try {
                        outputMappings(chooser.getSelectedFile());
                    } catch (IOException e1) {
                        JOptionPane.showConfirmDialog(null, "Unable to write to file: " + e1.getMessage());
                    }
                }
            }

            private void outputMappings(File selectedFile) throws IOException {
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(new FileOutputStream(selectedFile), Charset.forName("utf-8")), true);
                Collections.sort(mappings);
                for (KeyMap keyMap : mappings) {
                    String s;
                    if (keyMap.shiftDown)
                        s = "SHIFT " + keyCodeText.get(keyMap.keyCode).substring(3);
                    else
                        s = "" + keyCodeText.get(keyMap.keyCode).substring(3);
                    writer.println(keyMap.keyChar + " (" + s + ")");
                }
                writer.close();
            }
        });
        JPanel panel = ButtonBarBuilder.create().addButton(generateEvents).addButton(saveFile).build();
        return panel;
    }

    private JPanel createMainPanel() {
        FormLayout layout = new FormLayout("right:pref, 3dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout).border(Borders.DIALOG).rowGroupingEnabled(true);
        textField = new JTextField();
        textField.addKeyListener(new KeyListener() {

            @Override public void keyTyped(KeyEvent e) {
            }

            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
                    return;
                KeyMap m = new KeyMap(e.isShiftDown(), e.getKeyChar(), e.getKeyCode());
                mappings.add(m);
            }

            @Override public void keyPressed(KeyEvent e) {

            }
        });
        builder.append("TextField to receive robot events", textField);
        builder.append("Locale", new JLabel(InputContext.getInstance().getLocale().toString()));
        return builder.getPanel();
    }

    static Map<Integer, String> keyCodeText;

    static {
        keyCodeText = new HashMap<Integer, String>();

        Field[] declaredFields = KeyEvent.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().startsWith("VK_")) {
                try {
                    keyCodeText.put(field.getInt(null), field.getName());
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    public static void main(String[] args) throws AWTException, Throwable {
        final Generator test = new Generator();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                test.setVisible(true);
            }
        });
    }

    private List<Integer> tryRobotWith(Robot robot, List<Integer> asciiKeycodes, boolean withShift) {
        List<Integer> succeeded = new ArrayList<Integer>();
        for (Integer keyCode : asciiKeycodes) {
            try {
                if (withShift)
                    robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                succeeded.add(keyCode);
            } catch (Throwable t) {
            } finally {
                if (withShift)
                    robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
        return succeeded;
    }

    private List<Integer> getAsciiKeycodes() {
        CharsetEncoder asciiEncoder = Charset.forName("ascii").newEncoder();
        Set<Entry<Integer, String>> entrySet = keyCodeText.entrySet();
        List<Integer> out = new ArrayList<Integer>();
        for (Entry<Integer, String> entry : entrySet) {
            String keyText = KeyEvent.getKeyText(entry.getKey());
            if (keyText.length() == 1 && asciiEncoder.canEncode(keyText))
                out.add(entry.getKey());
        }
        return out;
    }
}
