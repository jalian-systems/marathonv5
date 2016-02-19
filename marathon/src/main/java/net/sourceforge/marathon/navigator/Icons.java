package net.sourceforge.marathon.navigator;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {
    private static final String ICON_PATH = "net/sourceforge/marathon/navigator/icons/";
    public static final Icon NAVIGATOR = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "navigator.gif"));
    public static final Icon COPY_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "enabled/copy.gif"));
    public static final Icon DELETE_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/delete.gif"));
    public static final Icon NEWFILE_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/newfile.gif"));
    public static final Icon NEWFOLDER_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/newfolder.gif"));
    public static final Icon PASTE_ENABLED = new ImageIcon(Icons.class.getClassLoader()
            .getResource(ICON_PATH + "enabled/paste.gif"));
    public static final Icon REFRESH_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/refresh.gif"));
    public static final Icon GOUP_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "enabled/goup.gif"));
    public static final Icon GOINTO_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/gointo.gif"));
    public static final Icon HOME_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "enabled/home.gif"));
    public static final Icon COLLAPSEALL_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "enabled/collapseall.gif"));
    public static final Icon COPY_DISABLED = new ImageIcon(Icons.class.getClassLoader()
            .getResource(ICON_PATH + "disabled/copy.gif"));
    public static final Icon DELETE_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/delete.gif"));
    public static final Icon NEWFILE_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/newfile.gif"));
    public static final Icon NEWFOLDER_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/newfolder.gif"));
    public static final Icon PASTE_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/paste.gif"));
    public static final Icon REFRESH_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/refresh.gif"));
    public static final Icon GOUP_DISABLED = new ImageIcon(Icons.class.getClassLoader()
            .getResource(ICON_PATH + "disabled/goup.gif"));
    public static final Icon GOINTO_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/gointo.gif"));
    public static final Icon HOME_DISABLED = new ImageIcon(Icons.class.getClassLoader()
            .getResource(ICON_PATH + "disabled/home.gif"));
    public static final Icon COLLAPSEALL_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "disabled/collapseall.gif"));
    public static final Icon PROPERTIES_ENABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "properties.gif"));;
    public static final Icon PROPERIES_DISABLED = new ImageIcon(Icons.class.getClassLoader().getResource(
            ICON_PATH + "properties.gif"));
    public static final Icon INFO = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "info.gif"));
    public static final Icon FILE = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "file_obj.gif"));
    public static final Icon FOLDER = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "fldr_obj.gif"));
    public static final Icon FOLDER_CLOSED = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "fldr_closed.gif"));
    public static final Icon PROJECT = new ImageIcon(Icons.class.getClassLoader().getResource(ICON_PATH + "prj_obj.gif"));
}
