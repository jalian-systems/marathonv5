
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.runtime.ws.WSRecordingServer;

public class ReloadScript {
    protected static final Logger logger = Logger.getLogger(ReloadScript.class.getName());
    private WebDriver driver;
    private String script;

    public ReloadScript() throws IOException {
        driver = new ChromeDriver();
        driver.get("http://marathontestingx.com/wp-login.php");
        driver.manage().window().maximize();
        final int port = findPort();
        logger.info("Starting server on port: " + port);
        WSRecordingServer wsrs = new WSRecordingServer(port) {
            @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                super.onClose(conn, code, reason, remote);
                System.out.println("WSRecordingServer.onClose(" + code + ":" + reason + ")");
                if(code == 1006)
                    System.exit(0);
                ((JavascriptExecutor) driver).executeScript(script, port);
            }
        };
        wsrs.start();
        wsrs.startRecording(new IRecorder() {
            @Override public void record(IScriptElement element) {
                logger.info("Record: " + element + "\n");
            }

            @Override public void abortRecording() {
            }

            @Override public void insertChecklist(String name) {
            }

            @Override public String recordInsertScriptElement(WindowId windowId, String script) {
                return null;
            }

            @Override public void recordInsertChecklistElement(WindowId windowId, String fileName) {
            }

            @Override public void recordShowChecklistElement(WindowId windowId, String fileName) {
            }

            @Override public boolean isCreatingObjectMap() {
                return false;
            }

            @Override public void updateScript() {
            }

        });
        script = readScript();
        ((JavascriptExecutor) driver).executeScript(script, port);
    }

    private String readScript() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("marathon.js")));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new WebDriverException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                }
        }
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        ReloadScript reloadScript = new ReloadScript();
        synchronized (reloadScript) {
            reloadScript.wait();
        }
    }

}
