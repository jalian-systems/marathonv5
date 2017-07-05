/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.screencapture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class ImagePanel extends SplitPane {

    public static final Logger LOGGER = Logger.getLogger(ImagePanel.class.getName());

    private static final Color ANNOTATION_COLOR = new Color(1.0f, 1.0f, 0.0f, 0.5f);
    private static final Color SELECTED_ANNOTATION_COLOR = new Color(0.8f, 0.8f, 0.0f, 0.8f);

    private static void ensureVisible(ScrollPane pane, Node node) {
        Bounds viewport = pane.getViewportBounds();
        double contentHeight = pane.getContent().getBoundsInLocal().getHeight();
        double contentWidth = pane.getContent().getBoundsInLocal().getWidth();
        double nodeMinY = node.getBoundsInParent().getMinY();
        double nodeMaxY = node.getBoundsInParent().getMaxY();
        double nodeMinX = node.getBoundsInParent().getMinX();
        double nodeMaxX = node.getBoundsInParent().getMaxX();
        double viewportMinY = (contentHeight - viewport.getHeight()) * pane.getVvalue();
        double viewportMaxY = viewportMinY + viewport.getHeight();
        double viewportMinX = (contentWidth - viewport.getWidth()) * pane.getHvalue();
        double viewportMaxX = viewportMinX + viewport.getWidth();
        if (nodeMinY < viewportMinY) {
            pane.setVvalue(nodeMinY / (contentHeight - viewport.getHeight()));
        } else if (nodeMaxY > viewportMaxY) {
            pane.setVvalue((nodeMaxY - viewport.getHeight()) / (contentHeight - viewport.getHeight()));
        }
        if (nodeMinX < viewportMinX) {
            pane.setHvalue(nodeMinX / (contentWidth - viewport.getWidth()));
        } else if (nodeMaxX > viewportMaxX) {
            pane.setHvalue((nodeMaxX - viewport.getWidth()) / (contentWidth - viewport.getWidth()));
        }

    }

    public static class Annotation extends Rectangle {

        private String text;

        public Annotation() {
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override public boolean equals(Object arg0) {
            return super.equals(arg0);
        }

        @Override public int hashCode() {
            return super.hashCode();
        }

        public String getText() {
            return text;
        }
    }

    private boolean edit;
    private Canvas canvas;
    private Pane anchorPane = new Pane();
    private GraphicsContext graphics;
    private ObservableList<Annotation> annotations = FXCollections.observableArrayList();
    private TableView<Annotation> annotationTable = new TableView<Annotation>();
    private File imageFile;
    private Image image;
    private ScrollPane scrollPane;

    public ImagePanel(File inputStream, boolean edit) throws FileNotFoundException, IOException {
        this.imageFile = inputStream;
        this.edit = edit;
        image = new Image(this.imageFile.toURI().toString());
        initComponents();
    }

    private void initComponents() {
        createLeftPane();
        createRightPane();
        scrollPane = new ScrollPane(anchorPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        getItems().addAll(scrollPane, annotationTable);
        setDividerPositions(0.7);

    }

    private void drawGraphics() {
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.drawImage(image, 0, 0);
        if (annotations.size() > 0) {
            for (int i = 0; i < annotations.size(); i++) {
                Annotation annotationFX = annotations.get(i);
                double x = annotationFX.getX();
                double y = annotationFX.getY();
                graphics.setFill(ANNOTATION_COLOR);
                graphics.fillRect(x, y, annotationFX.getWidth(), annotationFX.getHeight());
                graphics.setFill(Color.RED);
                graphics.fillArc(x - 25, y - 25, 50, 50, 270, 90, ArcType.ROUND);
                graphics.setFill(Color.WHITE);
                graphics.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 14));
                if (i > 8) {
                    graphics.fillText(Integer.toString(i + 1), x + 5, y + 15);
                } else {
                    graphics.fillText(Integer.toString(i + 1), x + 5, y + 15);
                }
            }
        }
    }

    private void createLeftPane() {
        canvas = new Canvas(image.getWidth(), image.getHeight());
        canvas.addEventFilter(MouseEvent.ANY, new ImagePanelMouseListener());
        graphics = canvas.getGraphicsContext2D();
        anchorPane.setMaxWidth(image.getWidth());
        anchorPane.setMaxHeight(image.getHeight());
        anchorPane.getChildren().add(canvas);
        initializeAnnotations();
        drawGraphics();
    }

    private void initializeAnnotations() {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(iis);
                IIOMetadata imageMetadata = reader.getImageMetadata(0);
                org.w3c.dom.Node root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
                NodeList childNodes = root.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    org.w3c.dom.Node item = childNodes.item(i);
                    if (item.getNodeName().equals("tEXt")) {
                        org.w3c.dom.Node textNode = item;
                        NodeList entryNodes = textNode.getChildNodes();
                        for (int j = 0; j < entryNodes.getLength(); j++) {
                            org.w3c.dom.Node entry = entryNodes.item(j);
                            if (entry.getNodeName().equals("tEXtEntry")) {
                                NamedNodeMap attributes = entry.getAttributes();
                                String kw = attributes.getNamedItem("keyword").getNodeValue();
                                String value = attributes.getNamedItem("value").getNodeValue();
                                Pattern p = Pattern.compile("a1810-(\\d+)-(\\d+\\.\\d+)-(\\d+\\.\\d+)-(\\d+\\.\\d+)-(\\d+\\.\\d+)");
                                Matcher matcher = p.matcher(kw);
                                if (matcher.matches()) {
                                    Annotation annotation = new Annotation();
                                    annotation.setX(Double.parseDouble(matcher.group(2)));
                                    annotation.setY(Double.parseDouble(matcher.group(3)));
                                    annotation.setWidth(Double.parseDouble(matcher.group(4)));
                                    annotation.setHeight(Double.parseDouble(matcher.group(5)));
                                    annotation.setText(value);
                                    annotation.setFill(ANNOTATION_COLOR);
                                    annotations.add(annotation);
                                }
                            }
                        }
                    }
                }
                reader.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) private void createRightPane() {
        annotationTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Annotation>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Annotation> c) {
                drawGraphics();
                markSelected();
            }
        });
        annotationTable.setEditable(edit);
        annotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        annotationTable.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                removeAnnotation();
            }
        });
        TableColumn<Annotation, String> messageColumn = new TableColumn<Annotation, String>("Annotation");
        PropertyValueFactory<Annotation, String> value = new PropertyValueFactory<>("text");
        messageColumn.setCellValueFactory(value);
        messageColumn.setCellFactory(new Callback<TableColumn<Annotation, String>, TableCell<Annotation, String>>() {
            @Override public TableCell<Annotation, String> call(TableColumn<Annotation, String> param) {
                return new TextAreaTableCell();
            }
        });
        messageColumn.prefWidthProperty().bind(annotationTable.widthProperty().subtract(25));

        TableColumn<Annotation, String> numCol = new TableColumn<>("#");
        numCol.setCellFactory(new Callback<TableColumn<Annotation, String>, TableCell<Annotation, String>>() {
            @Override public TableCell<Annotation, String> call(TableColumn<Annotation, String> p) {
                return new TableCell() {
                    @Override protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(null);
                        setText(empty ? null : getIndex() + 1 + "");
                    }
                };
            }
        });
        numCol.setPrefWidth(25);

        annotationTable.setItems(annotations);
        annotationTable.getColumns().addAll(numCol, messageColumn);
    }

    private void markSelected() {
        ObservableList<Annotation> selectedItems = annotationTable.getSelectionModel().getSelectedItems();
        if (selectedItems == null) {
            return;
        }
        for (Annotation selectedItem : selectedItems) {
            if (selectedItem == null) {
                continue;
            }
            graphics.setStroke(Color.RED);
            graphics.strokeRect(selectedItem.getX(), selectedItem.getY(), selectedItem.getWidth(), selectedItem.getHeight());
        }
    }

    @SuppressWarnings("unchecked") public void save(File file) {
        try {
            VBox box = new VBox();
            TableView<Annotation> tv = new TableView<>();
            TableColumn<Annotation, String> messageColumn = new TableColumn<Annotation, String>("Annotation");
            PropertyValueFactory<Annotation, String> value = new PropertyValueFactory<>("text");
            messageColumn.setCellValueFactory(value);
            messageColumn.setCellFactory(new Callback<TableColumn<Annotation, String>, TableCell<Annotation, String>>() {
                @Override public TableCell<Annotation, String> call(TableColumn<Annotation, String> param) {
                    return new TextAreaTableCell();
                }
            });
            messageColumn.prefWidthProperty().bind(tv.widthProperty().subtract(25));

            TableColumn<Annotation, String> numCol = new TableColumn<>("#");
            numCol.setCellFactory(new Callback<TableColumn<Annotation, String>, TableCell<Annotation, String>>() {
                @Override @SuppressWarnings({ "rawtypes" }) public TableCell<Annotation, String> call(
                        TableColumn<Annotation, String> p) {
                    return new TableCell() {
                        @Override protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(null);
                            setText(empty ? null : getIndex() + 1 + "");
                        }
                    };
                }
            });
            numCol.setPrefWidth(25);

            tv.setItems(annotations);
            tv.getColumns().addAll(numCol, messageColumn);
            box.getChildren().addAll(new ImageView(canvas.snapshot(new SnapshotParameters(), null)), tv);
            new Scene(box);
            ImageIO.write(SwingFXUtils.fromFXImage(box.snapshot(new SnapshotParameters(), null), null), "png",
                    new File(file.getParentFile(), "ext-" + file.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save the image to " + new File(file.getParentFile(), "ext-" + file.getName()), e);
        }
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save the image to " + file, e);
        }

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
        if (!readers.hasNext()) {
            throw new RuntimeException("Could not find a writer for png format");
        }

        ImageReader reader = readers.next();
        ImageInputStream iis;
        try {
            iis = ImageIO.createImageInputStream(new FileInputStream(file));
            reader.setInput(iis);
            BufferedImage pngImage = reader.read(0);
            IIOMetadata imageMetadata = reader.getImageMetadata(0);

            org.w3c.dom.Node root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
            IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
            for (int i = 0; i < annotations.size(); i++) {
                textNode.appendChild(getAnnotationNode(annotations.get(i), i));
            }
            root.appendChild(textNode);
            imageMetadata.mergeTree(imageMetadata.getNativeMetadataFormatName(), root);
            IIOImage imageWrite = new IIOImage(pngImage, new ArrayList<BufferedImage>(), imageMetadata);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            if (!writers.hasNext()) {
                throw new RuntimeException("Could not find a writer for png format");
            }

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(file));
            writer.setOutput(ios);
            writer.write(imageWrite);
            writer.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private org.w3c.dom.Node getAnnotationNode(Annotation annotation, int i) {
        i++;
        String nodeKeyword = "a1810-" + i + "-" + annotation.getX() + "-" + annotation.getY() + "-" + annotation.getWidth() + "-"
                + annotation.getHeight();
        IIOMetadataNode node = new IIOMetadataNode("tEXtEntry");
        node.setAttribute("keyword", nodeKeyword);
        node.setAttribute("value", annotation.getText());
        return node;
    }

    public void removeAnnotation() {
        ObservableList<Annotation> selectedItems = annotationTable.getSelectionModel().getSelectedItems();
        if (selectedItems == null) {
            return;
        }
        annotations.removeAll(selectedItems);
        drawGraphics();
    }

    class ImagePanelMouseListener implements EventHandler<MouseEvent> {

        private boolean new_rectangle_is_being_drawn;
        private double starting_point_x;
        private double starting_point_y;
        private Annotation newRect;

        @Override public void handle(MouseEvent e) {
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                requestFocus();
                if (new_rectangle_is_being_drawn == false) {
                    starting_point_x = e.getX();
                    starting_point_y = e.getY();
                    newRect = new Annotation();
                    newRect.setFill(SELECTED_ANNOTATION_COLOR);
                    new_rectangle_is_being_drawn = true;
                    anchorPane.getChildren().add(newRect);
                }
            }
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (!edit) {
                    return;
                }
                if (new_rectangle_is_being_drawn == true) {
                    double current_ending_point_x = e.getX();
                    double current_ending_point_y = e.getY();
                    if (current_ending_point_x < 0) {
                        current_ending_point_x = 0;
                    }
                    if (current_ending_point_y < 0) {
                        current_ending_point_y = 0;
                    }
                    if (current_ending_point_x > canvas.getWidth()) {
                        current_ending_point_x = canvas.getWidth();
                    }
                    if (current_ending_point_y > canvas.getHeight()) {
                        current_ending_point_y = canvas.getHeight();
                    }

                    adjust_rectangle_properties(starting_point_x, starting_point_y, current_ending_point_x, current_ending_point_y,
                            newRect);
                    ensureVisible(scrollPane, newRect);
                }
            }
            if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                anchorPane.getChildren().remove(newRect);
                newRect.setFill(ANNOTATION_COLOR);
                newRect.setText("Annotation");
                if (newRect.getWidth() > 10 && newRect.getHeight() > 10) {
                    System.out.println("ImagePanel.ImagePanelMouseListener.handle(" + newRect + ")");
                    annotations.add(newRect);
                }
                drawGraphics();
                newRect = null;
                new_rectangle_is_being_drawn = false;
            }
        }

        void adjust_rectangle_properties(double starting_point_x, double starting_point_y, double ending_point_x,
                double ending_point_y, Rectangle given_rectangle) {
            given_rectangle.setX(starting_point_x);
            given_rectangle.setY(starting_point_y);
            given_rectangle.setWidth(ending_point_x - starting_point_x);
            given_rectangle.setHeight(ending_point_y - starting_point_y);

            if (given_rectangle.getWidth() < 0) {
                given_rectangle.setWidth(-given_rectangle.getWidth());
                given_rectangle.setX(given_rectangle.getX() - given_rectangle.getWidth());
            }

            if (given_rectangle.getHeight() < 0) {
                given_rectangle.setHeight(-given_rectangle.getHeight());
                given_rectangle.setY(given_rectangle.getY() - given_rectangle.getHeight());
            }
        }
    }

    class TextAreaTableCell extends TableCell<Annotation, String> {

        private TextArea textArea;

        public TextAreaTableCell() {
            textArea = createTextArea(this);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                textArea.setEditable(true);
                textArea.selectAll();
                textArea.requestFocus();
            }
        }

        @Override public void updateItem(String item, boolean empty) {

            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else if (getGraphic() == null) {
                setGraphic(textArea);
            }
            if (!empty) {
                textArea.setText(getString());
            }
            if (isEditing()) {
                textArea.setEditable(true);
            } else {
                textArea.setEditable(false);
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    private static TextArea createTextArea(TableCell<Annotation, String> cell) {
        TextArea textArea = new TextArea(cell.getItem() == null ? "" : cell.getItem());
        textArea.setPrefRowCount(1);
        textArea.setWrapText(true);
        textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (!textArea.isFocused() && cell.getItem() != null && cell.isEditing()) {
                    cell.commitEdit(textArea.getText());
                }
                cell.getTableView().getItems().get(cell.getIndex()).setText(textArea.getText());
            }
        });
        textArea.addEventFilter(MouseEvent.MOUSE_CLICKED, (event) -> {
            if (event.getClickCount() > 1) {
                cell.getTableView().edit(cell.getTableRow().getIndex(), cell.getTableColumn());
            } else {
                TableViewSelectionModel<Annotation> selectionModel = cell.getTableView().getSelectionModel();
                if (event.isControlDown()) {
                    if (selectionModel.isSelected(cell.getIndex())) {
                        selectionModel.clearSelection(cell.getIndex());
                    } else {
                        selectionModel.select(cell.getIndex());
                    }
                } else {
                    selectionModel.clearAndSelect(cell.getIndex());
                }
            }
        });
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.ENTER && event.isShiftDown() && cell.isEditing()) {
                cell.commitEdit(textArea.getText());
                cell.getTableView().getItems().get(cell.getIndex()).setText(textArea.getText());
                event.consume();
            }
            if (event.getCode() == KeyCode.F2) {
                cell.getTableView().edit(cell.getTableRow().getIndex(), cell.getTableColumn());
            }
        });
        return textArea;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }
}
