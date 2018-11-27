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
package net.sourceforge.marathon.screencapture.swing;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Node;

import com.jgoodies.forms.builder.ButtonBarBuilder;

import net.sourceforge.marathon.runtime.api.swing.EscapeDialog;
import net.sourceforge.marathon.screencapture.swing.ImagePanel.Annotation;

public class AnnotateScreenCapture extends EscapeDialog {
    private static final long serialVersionUID = 1L;
    private ImagePanel imagePanel;
    private JSplitPane splitPane;
    private int returnValue;
    private boolean edit = true;
    private JButton cancel;
    private JButton okButton;

    public static final int APPROVE_OPTION = 1;
    public static final int CANCEL_OPTION = 2;

    public AnnotateScreenCapture(File imageFile, boolean edit) throws IOException {
        setModal(true);
        this.edit = edit;
        imagePanel = new ImagePanel(new FileInputStream(imageFile), edit);
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane splitPane = createSplitPane();
        panel.add(splitPane, BorderLayout.CENTER);
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(panel);
        setSize(1024, 768);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                splitPane.setDividerLocation(0.7);
            }
        });
        setLocationRelativeTo(null);
    }

    private JPanel createButtonPanel() {
        okButton = edit ? new JButton("Save") : new JButton("Done");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnValue = APPROVE_OPTION;
                dispose();
            }
        });
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (imagePanel.isDirty()) {
                    if (JOptionPane.showConfirmDialog(AnnotateScreenCapture.this,
                            "Your modifications will be lost. Do you want to continue?", "Abort",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                        return;
                }
                returnValue = CANCEL_OPTION;
                dispose();
            }
        });
        if (edit) {
            JButton[] buttons = { okButton, cancel };
            return ButtonBarBuilder.create().addGlue().addButton(buttons).getPanel();
        }
        JButton[] buttons = { okButton };
        return ButtonBarBuilder.create().addGlue().addButton(buttons).getPanel();
    }

    private JSplitPane createSplitPane() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setLeftComponent(new JScrollPane(imagePanel));
        splitPane.setRightComponent(getAnnotationPanel());
        splitPane.resetToPreferredSizes();
        return splitPane;
    }

    private Component getAnnotationPanel() {
        JScrollPane scrollPane = new JScrollPane(new AnnotationPanel(imagePanel, !edit), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension dimension = scrollPane.getSize();
        dimension.width = 250;
        scrollPane.setPreferredSize(dimension);
        return scrollPane;
    }

    public File saveToFile(File captureFile) throws FileNotFoundException, IOException {
        writePNG(captureFile);

        ImageReader reader = getPNGImageReader();
        ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(captureFile));
        reader.setInput(iis);
        BufferedImage pngImage = reader.read(0);
        IIOMetadata imageMetadata = reader.getImageMetadata(0);

        Node root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
        ArrayList<Annotation> annotations = imagePanel.getAnnotations();
        for (int i = 0; i < annotations.size(); i++) {
            textNode.appendChild(getAnnotationNode((Annotation) annotations.get(i), i));
        }
        root.appendChild(textNode);

        imageMetadata.mergeTree(imageMetadata.getNativeMetadataFormatName(), root);

        IIOImage imageWrite = new IIOImage(pngImage, new ArrayList<BufferedImage>(), imageMetadata);

        ImageWriter writer = getPNGImageWriter();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(captureFile));
        writer.setOutput(ios);
        writer.write(imageWrite);
        writer.dispose();

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        ImagePanel finalImage = new ImagePanel(new FileInputStream(captureFile), false);
        frame.add(finalImage, BorderLayout.CENTER);
        frame.pack();
        File savedFile = new File(captureFile.getParentFile(), "ext-" + captureFile.getName());
        getScreenShot(frame.getContentPane(), savedFile);
        frame.dispose();

        return savedFile;
    }

    private BufferedImage getScreenShot(Component comp, File outputFile) {
        BufferedImage bufferedImage = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
        comp.paint(bufferedImage.getGraphics());
        try {
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;

    }

    private Node getAnnotationNode(Annotation annotation, int i) {
        String nodeKeyword = "a1810-" + i + "-" + annotation.x + "-" + annotation.y + "-" + annotation.width + "-"
                + annotation.height;
        IIOMetadataNode node = new IIOMetadataNode("tEXtEntry");
        node.setAttribute("keyword", nodeKeyword);
        node.setAttribute("value", annotation.getText());
        return node;
    }

    private ImageReader getPNGImageReader() {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
        if (!readers.hasNext()) {
            throw new RuntimeException("Could not find a writer for png format");
        }
        return (ImageReader) readers.next();
    }

    private void writePNG(File file) throws IOException, FileNotFoundException {
        ImageWriter writer = getPNGImageWriter();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(file));

        writer.setOutput(ios);
        BufferedImage image = imagePanel.getImage();
        updateAnnotations(image);
        writer.write(image);
        writer.dispose();
    }

    private void updateAnnotations(BufferedImage image) {
        Graphics graphics = image.getGraphics();
        ArrayList<Annotation> annotations = imagePanel.getAnnotations();
        for (int i = 0; i < annotations.size(); i++) {
            Annotation a = annotations.get(i);
            a.drawDecoration(graphics, i, false);
        }
    }

    private ImageWriter getPNGImageWriter() {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        if (!writers.hasNext()) {
            throw new RuntimeException("Could not find a writer for png format");
        }
        return (ImageWriter) writers.next();
    }

    public int showDialog() {
        setVisible(true);
        return returnValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final String fileName;
            if (args.length > 0)
                fileName = args[0];
            else
                fileName = chooseFile(JFileChooser.OPEN_DIALOG);
            if (fileName == null)
                System.exit(0);
            final File file = new File(fileName);
            annotateImage(file);
            System.exit(0);
        });
    }

    private static void annotateImage(final File file) {
        AnnotateScreenCapture marathonAnnotate = null;
        try {
            marathonAnnotate = new AnnotateScreenCapture(file, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (marathonAnnotate.showDialog() == AnnotateScreenCapture.APPROVE_OPTION) {
            String saveFileName = chooseFile(JFileChooser.SAVE_DIALOG);
            try {
                marathonAnnotate.saveToFile(new File(saveFileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    private static String chooseFile(final int type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String n = f.getName();
                String extension = null;
                int dotIndex = n.lastIndexOf('.');
                if (dotIndex != -1 && dotIndex < n.length() - 1)
                    extension = n.substring(dotIndex + 1);
                if (extension == null)
                    return false;
                if (type == JFileChooser.OPEN_DIALOG)
                    return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")
                            || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("gif");
                else
                    return extension.equalsIgnoreCase("png");
            }

            public String getDescription() {
                return "Image Files";
            }

        });
        int ret;
        if (type == JFileChooser.OPEN_DIALOG)
            ret = chooser.showOpenDialog(null);
        else
            ret = chooser.showSaveDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    private static KeyStroke hookKS = KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.META_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);

    public static void initialize(KeyStroke ks) {
        hookKS = ks;
        initialize();
    }

    public static void initialize() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
                if (!ks.equals(hookKS))
                    return false;
                Component root = SwingUtilities.getRootPane(e.getComponent());
                if (root == null)
                    root = SwingUtilities.getRoot(e.getComponent());
                int x = root.getLocationOnScreen().x;
                int y = root.getLocationOnScreen().y;
                Rectangle rectangle = new Rectangle(x, y, root.getWidth(), root.getHeight());
                try {
                    File file = File.createTempFile("screencap", "png");
                    file.deleteOnExit();

                    Robot robot = new Robot();
                    BufferedImage image = robot.createScreenCapture(rectangle);
                    ImageIO.write(image, "png", file);
                    annotateImage(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
                return true;
            }
        });
    }

    @Override
    public JButton getOKButton() {
        return okButton;
    }

    @Override
    public JButton getCloseButton() {
        if (edit)
            return cancel;
        return okButton;
    }

    public ArrayList<Annotation> getAnnotations() {
        return imagePanel.getAnnotations();
    }

    // @formatter:off
    private static final String HTML = 
            "<h2>Attached Screenshot</h2>" +
                    "<div class=\"spacer\">&nbsp;</div>" +
                    "" +
                    "<img src=\"data:image/png;base64, %encodedImage%\" alt=\"screen_capture\" usemap=\"#annotation_map\">" +
                    "%annotationMap%" +
                    "<div class=\"spacer\">&nbsp;</div>" +
                    "" +
                    "<h2>Annotations</h2>" +
                    "<table id=\"annotation-table\">" +
                    "   <tr>" +
                    "       <th align=\"left\" style=\"width: 20px\">S.No.</th>" +
                    "       <th align=\"left\">Annotation</th>" +
                    "   </tr>" +
                    "%annotationEntries%" +
                    "</table>" +
                    "" ;
    // @formatter:on
    public String getHtmlDoc(File file, List<Annotation> annotations) {
        Path path = Paths.get(file.getPath());
        try {
            byte[] readAllBytes = Files.readAllBytes(path);
            String encodedImage = Base64.getEncoder().encodeToString(readAllBytes);
            String annotationEntries = getAnnotationTableRows(annotations);
            String annotationMap = getImageMap(annotations);
            return HTML.replace("%encodedImage%", encodedImage).replace("%annotationMap%", annotationMap)
                    .replace("%annotationEntries%", annotationEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getImageMap(List<Annotation> annotations) {
        if (annotations.size() < 1) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        String mapOpen = "<map name=\"annotation_map\">";

        String mapClose = "</map>";
        sb.append(mapOpen);
        for (int i = 0; i < annotations.size(); i++) {
            Annotation a = annotations.get(i);
            String s = "<a><area href=\"#a_" + (i + 1) + "\" alt=\"Annotation\" shape=\"\" coords=\"" + a.x + "," + a.y + ","
                    + (a.x + a.width) + "," + (a.y + a.height) + "\"></a>";
            sb.append(s);
        }
        sb.append(mapClose);
        return sb.toString();

    }

    private String getAnnotationTableRows(List<Annotation> annotations) {
        if (annotations.size() < 1) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < annotations.size(); i++) {
            //@formatter:off
            sb.append("<tr>\n" + 
                        "                            <td id=\"a_" + (i + 1) + "\">" + (i + 1) + "</td>\n"+
                        "                            <td>" + quoteCharacters(annotations.get(i).getText()) + "</td>\n"+
                        "                        </tr>");
          //@formatter:on
        }
        return sb.toString();
    }

    private static String quoteCharacters(String s) {
        StringBuffer result = null;
        for (int i = 0, max = s.length(), delta = 0; i < max; i++) {
            char c = s.charAt(i);
            String replacement = null;

            if (c == '&') {
                replacement = "&amp;";
            } else if (c == '<') {
                replacement = "&lt;";
            } else if (c == '\r') {
                replacement = "&#13;";
            } else if (c == '>') {
                replacement = "&gt;";
            } else if (c == '"') {
                replacement = "&quot;";
            } else if (c == '\'') {
                replacement = "&apos;";
            }

            if (replacement != null) {
                if (result == null) {
                    result = new StringBuffer(s);
                }
                result.replace(i + delta, i + delta + 1, replacement);
                delta += replacement.length() - 1;
            }
        }
        if (result == null) {
            return s;
        }
        return result.toString();
    }

}