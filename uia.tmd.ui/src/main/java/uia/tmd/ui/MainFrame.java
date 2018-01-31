package uia.tmd.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.PropertyConfigurator;

import uia.tmd.TaskFactory;
import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TableType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

public class MainFrame extends JFrame implements WindowListener {

    private static final long serialVersionUID = 2018368254049980297L;

    private JMenuBar menuBar;

    private JMenu fileMenu;

    private JMenuItem saveasMenuItem;

    private JMenuItem exitMenuItem;

    private JMenu helpMenu;

    private JMenuItem aboutMenuItem;

    private NaviPanel naviPanel;

    private TablePanel tablePanel;

    private ExecutorRunPanel resultPanel;

    private TaskFactory factory;

    private File lastFile;

    public static void main(String[] args) {
        try {
            // locale
            Locale.setDefault(Locale.US);

            // log4j
            String rootPath = System.getProperty("user.dir") + System.getProperty("file.separator");
            PropertyConfigurator.configureAndWatch(
                    rootPath + "log.properties",
                    1000 * 60);

            // look & feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // FontUIResource f = new FontUIResource(new Font("Monospaced", Font.PLAIN, 11));
            FontUIResource f = new FontUIResource(new Font("Tahoma", Font.PLAIN, 11));
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    UIManager.put(key, f);
                }
            }
        }
        catch (Exception e) {
        }

        new MainFrame().setVisible(true);
    }

    public MainFrame() {
        setTitle("Database Migration Tool");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocation(100, 100);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addWindowListener(this);

        JSplitPane splitPane1 = new JSplitPane();
        getContentPane().add(splitPane1, BorderLayout.CENTER);

        this.naviPanel = new NaviPanel();
        this.naviPanel.setFrame(this);
        splitPane1.setLeftComponent(this.naviPanel);

        JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setResizeWeight(1.0);
        splitPane2.setBorder(BorderFactory.createEmptyBorder());
        splitPane1.setRightComponent(splitPane2);

        this.resultPanel = new ExecutorRunPanel();
        this.resultPanel.setFrame(this);
        splitPane2.setLeftComponent(this.resultPanel);

        this.tablePanel = new TablePanel();
        this.tablePanel.setFrame(this);
        splitPane2.setRightComponent(this.tablePanel);

        initialMenu();
    }

    public void createTask(TableType table) {
        TaskType task = new TaskType();
        task.setName(table.getName());
        task.setSourceSelect(new SourceSelectType());
        task.getSourceSelect().setTable(table.getName());
        task.setTargetUpdate(new TargetUpdateType());
        this.naviPanel.appendTask(task);
    }

    public void runExecutor(String executorName) {
        try {
            this.resultPanel.run(this.factory.createExecutor(executorName));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExecutable(boolean executable) {
        this.naviPanel.setExecutable(executable);
    }

    public TaskFactory getTaskFactory() {
        return this.factory;
    }

    @Override
    public void windowActivated(WindowEvent evt) {

    }

    @Override
    public void windowClosed(WindowEvent evt) {

    }

    @Override
    public void windowClosing(WindowEvent evt) {

    }

    @Override
    public void windowDeactivated(WindowEvent evt) {

    }

    @Override
    public void windowDeiconified(WindowEvent evt) {

    }

    @Override
    public void windowIconified(WindowEvent evt) {

    }

    @Override
    public void windowOpened(WindowEvent evt) {
        load("conf\\wip.xml");
    }

    private void load(String filePath) {
        try {
            this.lastFile = new File(filePath);
            this.factory = new TaskFactory(this.lastFile);
            this.naviPanel.load();
            this.tablePanel.load();

            setTitle("TMD File - " + filePath);
        }
        catch (Exception e) {
            setTitle("TMD File - load failed");
            e.printStackTrace();
        }
    }

    private void saveAs() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(this.lastFile);
            fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".xml");
                }

                @Override
                public String getDescription() {
                    return "Table Definition Ma (*.xml)";
                }

            });
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.lastFile = fc.getSelectedFile();
                TmdTypeHelper.save(this.factory.getTmd(), this.lastFile);
            }

        }
        catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void initialMenu() {
        this.menuBar = new JMenuBar();
        setJMenuBar(this.menuBar);

        // Menu Bar> File
        this.fileMenu = new JMenu("File");
        this.menuBar.add(this.fileMenu);

        this.saveasMenuItem = new JMenuItem("Save As");
        this.saveasMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                saveAs();
            }

        });
        this.fileMenu.add(this.saveasMenuItem);
        this.fileMenu.addSeparator();

        this.exitMenuItem = new JMenuItem("Exit");
        this.exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }

        });
        this.fileMenu.add(this.exitMenuItem);

        // Menu Bar> Help
        this.helpMenu = new JMenu("Help");
        this.menuBar.add(this.helpMenu);

        this.aboutMenuItem = new JMenuItem("About");
        this.helpMenu.add(this.aboutMenuItem);
    }
}
