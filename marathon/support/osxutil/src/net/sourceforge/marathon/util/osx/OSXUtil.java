package net.sourceforge.marathon.util.osx;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

@SuppressWarnings("deprecation") public class OSXUtil {
    private Application application = Application.getApplication();
    private IOSXApplicationListener window;

    public OSXUtil(IOSXApplicationListener window) {
        this.window = window;
        application.setEnabledAboutMenu(true);
        application.setEnabledPreferencesMenu(true);
        application.addApplicationListener(new ApplicationListener() {
            public void handleAbout(ApplicationEvent arg0) {
                OSXUtil.this.window.handleAbout();
                arg0.setHandled(true);
            }

            public void handleOpenApplication(ApplicationEvent arg0) {
            }

            public void handleOpenFile(ApplicationEvent arg0) {
            }

            public void handlePreferences(ApplicationEvent arg0) {
                OSXUtil.this.window.handlePreferences();
                arg0.setHandled(true);
            }

            public void handlePrintFile(ApplicationEvent arg0) {
            }

            public void handleQuit(ApplicationEvent arg0) {
                arg0.setHandled(OSXUtil.this.window.handleQuit());
            }

            public void handleReOpenApplication(ApplicationEvent arg0) {
            }
        });
    }
}
