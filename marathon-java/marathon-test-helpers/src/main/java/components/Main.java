package components;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    public static class ResourceListModel extends AbstractListModel implements ListModel {

        private List<Class> klasses;

        public ResourceListModel(List<String> resources) {
            this.klasses = new ArrayList<Class>();
            for (String resource : resources) {
                Class klass = checkClass(resource);
                if (klass != null)
                    klasses.add(klass);
            }
            Collections.sort(klasses, new Comparator<Class> () {
                @Override public int compare(Class arg0, Class arg1) {
                    return arg0.getName().compareTo(arg1.getName());
                }
            });
        }

        private Class checkClass(String resource) {
            try {
                Class<?> klass = Class.forName(resource);
                Method method = klass.getMethod("main", String[].class);
                if (method.getReturnType() == Void.TYPE
                        && ((method.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) == (Modifier.PUBLIC | Modifier.STATIC)))
                    return klass;
            } catch (Throwable e) {
            }
            return null;
        }

        @Override public int getSize() {
            return klasses.size();
        }

        @Override public Object getElementAt(int index) {
            return klasses.get(index).getName();
        }

        public void load(int index) {
            Class klass = klasses.get(index);
            try {
                klass.getMethod("main", String[].class).invoke(null, (Object) new String[] {});
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        Frame[] frames = Frame.getFrames();
                        for (Frame frame : frames) {
                            if(!frame.getTitle().equals("Demo Programs") && frame instanceof JFrame)
                                ((JFrame)frame).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        }
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public Main() {
        super("Demo Programs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        List<String> resources = FindResources.findClasses("Demo");
        final ResourceListModel dataModel = new ResourceListModel(resources);
        final JList list = new JList(dataModel);
        list.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1) {
                    int index = list.locationToIndex(e.getPoint());
                    if(index != -1)
                        dataModel.load(index);
                }
            }
        });
        add(new JScrollPane(list));
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
