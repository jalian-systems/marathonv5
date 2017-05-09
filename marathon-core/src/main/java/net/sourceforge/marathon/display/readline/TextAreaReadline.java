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
package net.sourceforge.marathon.display.readline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jline.History;

public class TextAreaReadline implements EventHandler<KeyEvent> {

    public static final Logger LOGGER = Logger.getLogger(TextAreaReadline.class.getName());

    private static final String EMPTY_LINE = "";
    private TextField area;
    private TextArea output;
    private String currentLine;
    private final InputStream inputStream = new Input();
    private final OutputStream outputStream = new Output(1);
    private final OutputStream errorStream = new Output(2);

    private static class InputBuffer {
        public final byte[] bytes;
        public int offset = 0;

        public InputBuffer(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    public enum Channel {
        AVAILABLE, READ, BUFFER, EMPTY, LINE, GET_LINE, SHUTDOWN, FINISHED
    }

    public static class ReadRequest {
        public final byte[] b;
        public final int off;
        public final int len;

        public ReadRequest(byte[] b, int off, int len) {
            this.b = b;
            this.off = off;
            this.len = len;
        }

        public int perform(Join join, InputBuffer buffer) {
            final int available = buffer.bytes.length - buffer.offset;
            int len = this.len;
            if (len > available) {
                len = available;
            }
            if (len == available) {
                join.send(Channel.EMPTY, null);
            } else {
                buffer.offset += len;
                join.send(Channel.BUFFER, buffer);
            }
            System.arraycopy(buffer.bytes, buffer.offset, this.b, this.off, len);
            return len;
        }
    }

    private static final Spec INPUT_SPEC = new Spec() {
        {
            addReaction(new FastReaction(Channel.SHUTDOWN, Channel.BUFFER) {
                @Override public void react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                }
            });
            addReaction(new FastReaction(Channel.SHUTDOWN, Channel.EMPTY) {
                @Override public void react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                }
            });
            addReaction(new FastReaction(Channel.SHUTDOWN, Channel.FINISHED) {
                @Override public void react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                }
            });
            addReaction(new FastReaction(Channel.FINISHED, Channel.LINE) {
                @Override public void react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                }
            });
            addReaction(new SyncReaction(Channel.AVAILABLE, Channel.BUFFER) {
                @Override public Object react(Join join, Object[] args) {
                    InputBuffer buffer = (InputBuffer) args[1];
                    join.send(Channel.BUFFER, buffer);
                    return buffer.bytes.length - buffer.offset;
                }
            });
            addReaction(new SyncReaction(Channel.AVAILABLE, Channel.EMPTY) {
                @Override public Object react(Join join, Object[] args) {
                    join.send(Channel.EMPTY, null);
                    return 0;
                }
            });
            addReaction(new SyncReaction(Channel.AVAILABLE, Channel.FINISHED) {
                @Override public Object react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                    return 0;
                }
            });
            addReaction(new SyncReaction(Channel.READ, Channel.BUFFER) {
                @Override public Object react(Join join, Object[] args) {
                    return ((ReadRequest) args[0]).perform(join, (InputBuffer) args[1]);
                }
            });
            addReaction(new SyncReaction(Channel.READ, Channel.EMPTY, Channel.LINE) {
                @Override public Object react(Join join, Object[] args) {
                    final ReadRequest request = (ReadRequest) args[0];
                    final String line = (String) args[2];
                    if (line.length() != 0) {
                        byte[] bytes;
                        try {
                            bytes = line.getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            bytes = line.getBytes();
                        }
                        return request.perform(join, new InputBuffer(bytes));
                    } else {
                        return -1;
                    }
                }
            });
            addReaction(new SyncReaction(Channel.READ, Channel.FINISHED) {
                @Override public Object react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                    return -1;
                }
            });
            addReaction(new SyncReaction(Channel.GET_LINE, Channel.LINE) {
                @Override public Object react(Join join, Object[] args) {
                    return args[1];
                }
            });
            addReaction(new SyncReaction(Channel.GET_LINE, Channel.FINISHED) {
                @Override public Object react(Join join, Object[] args) {
                    join.send(Channel.FINISHED, null);
                    return EMPTY_LINE;
                }
            });
        }
    };
    private final Join inputJoin = INPUT_SPEC.createJoin();
    private Readline readline;
    private Style promptStyle = new Style(Color.rgb(0xa4, 0x00, 0x00), Font.font("Verdana", FontWeight.MEDIUM, 12));
    private Style inputStyle = new Style(Color.rgb(0x20, 0x4a, 0x87), Font.font("Verdana", FontWeight.MEDIUM, 12));
    private Style outputStyle = new Style(Color.DIMGRAY, Font.font("Verdana", FontWeight.MEDIUM, 12));
    private Style resultStyle = new Style(Color.rgb(0x20, 0x4a, 0x87), Font.font("Verdana", FontWeight.MEDIUM, 12));
    private Style errorStyle = new Style(Color.RED, Font.font("Verdana", FontWeight.MEDIUM, 12));
    private StringBuffer functionText = new StringBuffer();

    public TextAreaReadline(TextField text, TextArea output, final String message) {
        this.area = text;
        this.output = output;
        readline = new Readline();
        inputJoin.send(Channel.EMPTY, null);
        text.setOnKeyPressed(this);
        if (message != null) {
            append(message, promptStyle);
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    protected void upAction(KeyEvent event) {
        event.consume();
        if (!readline.getHistory().next()) {
            currentLine = getLine();
        } else {
            readline.getHistory().previous(); // undo check
        }
        if (!readline.getHistory().previous()) {
            return;
        }
        String oldLine = readline.getHistory().current().trim();
        replaceText(oldLine);
    }

    protected void downAction(KeyEvent event) {
        event.consume();
        if (!readline.getHistory().next()) {
            return;
        }
        String oldLine;
        if (!readline.getHistory().next()) {
            oldLine = currentLine;
        } else {
            readline.getHistory().previous(); // undo check
            oldLine = readline.getHistory().current().trim();
        }
        replaceText(oldLine);
    }

    protected void replaceText(String replacement) {
        area.setText(replacement);
        area.positionCaret(replacement.length());
    }

    protected String getLine() {
        return area.getText();
    }

    protected void enterAction(KeyEvent event) {
        event.consume();
        if (functionText.length() > 0) {
            String function = functionText.toString();
            append(">> ", promptStyle);
            append(function, inputStyle);
            inputJoin.send(Channel.LINE, function);
        } else {
            String text = area.getText();
            append(">> ", promptStyle);
            append(text, inputStyle);
            append("\n", inputStyle);
            String line = getLine();
            inputJoin.send(Channel.LINE, line);
        }
        functionText = new StringBuffer();
        area.clear();
    }

    private void collectAction() {
        String text = area.getText();
        functionText.append(text + "\n");
        readline.getHistory().addToHistory(text);
        area.clear();
    }

    public String readLine(final String prompt) {
        if (Platform.isFxApplicationThread()) {
            throw new RuntimeException("Cannot call readline from event dispatch thread");
        }
        Platform.runLater(new Runnable() {
            @Override public void run() {
                area.positionCaret(area.getLength());
                readline.getHistory().moveToEnd();
            }
        });
        final String line = (String) inputJoin.call(Channel.GET_LINE, null);
        if (line != null && line.length() > 0) {
            return line.trim();
        } else {
            return null;
        }
    }

    public void positionToLastLine() {
        boolean lastLine = area.getLength() == area.getCaretPosition();
        if (!lastLine) {
            lastLine = !area.getText(area.getCaretPosition(), area.getLength()).contains("\n");
        }
        if (!lastLine) {
            area.positionCaret(area.getLength());
        }
    }

    public void shutdown() {
        inputJoin.send(Channel.SHUTDOWN, null);
    }

    /**
     * Output methods
     *
     * @param fill
     **/
    protected void append(String toAppend, Style style) {
        output.appendText(toAppend);
    }

    private void writeLineUnsafe(final String line, int type) {
        if (line.startsWith("=>")) {
            append(line, resultStyle);
        } else if (line.startsWith("****")) {
            append(line.substring(4), resultStyle);
        } else {
            if (type == 1) {
                append(line, outputStyle);
            } else {
                append(line, errorStyle);
            }
        }
    }

    private void writeLine(final String line, final int type) {
        if (Platform.isFxApplicationThread()) {
            writeLineUnsafe(line, type);
        } else {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    writeLineUnsafe(line, type);
                }
            });
        }
    }

    private class Input extends InputStream {
        private volatile boolean closed = false;

        @Override public int available() throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }
            return (Integer) inputJoin.call(Channel.AVAILABLE, null);
        }

        @Override public int read() throws IOException {
            byte[] b = new byte[1];
            if (read(b, 0, 1) == 1) {
                return b[0];
            } else {
                return -1;
            }
        }

        @Override public int read(byte[] b, int off, int len) throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }
            if (Platform.isFxApplicationThread()) {
                throw new IOException("Cannot call read from event dispatch thread");
            }
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || off + len > b.length) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            final ReadRequest request = new ReadRequest(b, off, len);
            return (Integer) inputJoin.call(Channel.READ, request);
        }

        @Override public void close() {
            closed = true;
            inputJoin.send(Channel.SHUTDOWN, null);
        }
    }

    private class Output extends OutputStream {
        private final int type;

        public Output(int type) {
            this.type = type;
        }

        @Override public void write(int b) throws IOException {
            writeLine("" + b, type);
        }

        @Override public void write(byte[] b, int off, int len) {
            try {
                writeLine(new String(b, off, len, "UTF-8"), type);
            } catch (UnsupportedEncodingException ex) {
                writeLine(new String(b, off, len), type);
            }
        }

        @Override public void write(byte[] b) {
            try {
                writeLine(new String(b, "UTF-8"), type);
            } catch (UnsupportedEncodingException ex) {
                writeLine(new String(b), type);
            }
        }
    }

    public History getHistory() {
        return readline.getHistory();
    }

    public void setHistoryFile(File file) throws IOException {
        readline.getHistory().setHistoryFile(file);
    }

    @Override public void handle(KeyEvent event) {
        if (event.getEventType() != KeyEvent.KEY_PRESSED) {
            return;
        }
        KeyCode code = event.getCode();
        switch (code) {
        case ENTER:
            positionToLastLine();
            if (event.isShiftDown()) {
                collectAction();
            } else {
                if (functionText.length() > 0) {
                    collectAction();
                }
                enterAction(event);
                area.setEditable(false);
            }
            break;
        case UP:
            positionToLastLine();
            upAction(event);
            break;
        case DOWN:
            positionToLastLine();
            downAction(event);
            break;
        case LEFT:
        case D:
            if (event.isControlDown()) {
                event.consume();
                inputJoin.send(Channel.LINE, EMPTY_LINE);
            }
            break;
        default:
            break;
        }
    }

    public class Style {

        private Color fillColor;
        private Font font;

        public Style(Color fillColor, Font font) {
            this.fillColor = fillColor;
            this.font = font;
        }

        public Color getFillColor() {
            return fillColor;
        }

        public Font getFont() {
            return font;
        }
    }

}
