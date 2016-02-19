package components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.marathon.testhelpers.ComponentUtils;

import com.sun.swingset3.demos.tree.TreeDemo;

public class SwingSet3TreeDemo extends JFrame {

    public SwingSet3TreeDemo() {
        super(SwingSet3TreeDemo.class.getName());
        initComponents();
    }
    
    private void initComponents() {
        TreeDemo demo = new TreeDemo();
        JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, demo);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        root.setUserObject("Root node [] , / Special");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JButton("Click-Me"), BorderLayout.NORTH);
        contentPane.add(demo, BorderLayout.CENTER);
        setSize(640, 480);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JFrame f = new SwingSet3TreeDemo();
                f.setVisible(true);
            }
        });
    }
}
