package uia.tmd.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import uia.tmd.model.xml.DatabaseSpaceType;
import uia.tmd.model.xml.DatabaseType;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.JobSpaceType;
import uia.tmd.model.xml.JobType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskSpaceType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;
import uia.tmd.ui.edit.DatabaseEditPanel;
import uia.tmd.ui.edit.ExecutorEditPanel;
import uia.tmd.ui.edit.PlanEditPanel;
import uia.tmd.ui.edit.TaskEditPanel;
import uia.tmd.ui.navi.DatabaseSpaceNodeValue;
import uia.tmd.ui.navi.DbServerNodeValue;
import uia.tmd.ui.navi.ExecutorNodeValue;
import uia.tmd.ui.navi.ExecutorSpaceNodeValue;
import uia.tmd.ui.navi.ItemNodeValue;
import uia.tmd.ui.navi.NodeValue;
import uia.tmd.ui.navi.PlanNodeValue;
import uia.tmd.ui.navi.TaskNodeValue;
import uia.tmd.ui.navi.TaskSpaceNodeValue;
import uia.tmd.ui.navi.TmdNodeValue;

public class NaviPanel extends JPanel {

    private static final long serialVersionUID = -1389167781459992146L;

    private static final int WIDTH = 420;

    private MainFrame frame;

    private JTree spaceTree;

    private JButton addButton;

    private JButton removeButton;

    private JButton runButton;

    private JScrollPane propsScroll;

    private JTable propsTable;

    private DefaultMutableTreeNode executorSpaceNode;

    private DefaultMutableTreeNode taskSpaceNode;

    private DefaultMutableTreeNode databaseSpaceNode;

    private TmdType tmdType;

    private String[] propKeys;

    private Map<String, String> propValues;

    private JobType selectedExecutor;

    private DatabaseType selectedDbServer;

    private TaskType selectedTask;

    public NaviPanel() {
        this.propKeys = new String[0];
        this.propValues = new TreeMap<String, String>();

        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(WIDTH, 400));

        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(null);
        toolbarPanel.setPreferredSize(new Dimension(WIDTH, 30));
        add(toolbarPanel, BorderLayout.NORTH);

        this.addButton = new JButton();
        this.addButton.setEnabled(false);
        this.addButton.setIcon(new ImageIcon(NaviPanel.class.getResource("/images/add-small.png")));
        this.addButton.setToolTipText("Add new task");
        this.addButton.setBounds(0, 3, 24, 24);
        this.addButton.addActionListener(evt -> {
            getSelectedTreeNodeValue().append(NaviPanel.this);
        });
        toolbarPanel.add(this.addButton);

        this.removeButton = new JButton();
        this.removeButton.setEnabled(false);
        this.removeButton.setIcon(new ImageIcon(NaviPanel.class.getResource("/images/remove-small.png")));
        this.removeButton.setToolTipText("Remove selected task");
        this.removeButton.setBounds(26, 3, 24, 24);
        this.removeButton.addActionListener(evt -> {
            getSelectedTreeNodeValue().delete(NaviPanel.this);
        });
        toolbarPanel.add(this.removeButton);

        this.runButton = new JButton();
        this.runButton.setEnabled(false);
        this.runButton.setIcon(new ImageIcon(NaviPanel.class.getResource("/images/run-small.png")));
        this.runButton.setToolTipText("Remove selected task");
        this.runButton.setBounds(73, 3, 24, 24);
        this.runButton.addActionListener(a -> {
            NaviPanel.this.frame.runExecutor(NaviPanel.this.selectedExecutor.getName());
        });
        toolbarPanel.add(this.runButton);

        JScrollPane spaceScroll = new JScrollPane();
        spaceScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(spaceScroll);

        this.spaceTree = new JTree();
        this.spaceTree.addTreeSelectionListener(evt -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) NaviPanel.this.spaceTree.getLastSelectedPathComponent();
            if (node == null || node.getUserObject() == null) {
                return;
            }

            ((NodeValue) node.getUserObject()).select(NaviPanel.this);
            ((NodeValue) node.getUserObject()).expand(NaviPanel.this);

        });
        this.spaceTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
                    NodeValue nv = getSelectedTreeNodeValue();
                    if (nv.edit(NaviPanel.this)) {
                        getSelectedTreeNode().removeAllChildren();
                        ((DefaultTreeModel) NaviPanel.this.spaceTree.getModel()).reload(getSelectedTreeNode());
                        nv.select(NaviPanel.this);
                        nv.expand(NaviPanel.this);
                    }
                }
            }
        });
        this.spaceTree.setCellRenderer(new DefaultTreeCellRenderer() {

            private static final long serialVersionUID = -8200134279290294940L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean focus) {
                Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof NodeValue) {
                    NodeValue nv = (NodeValue) node.getUserObject();
                    setIcon(nv.getIcon(selected));
                }
                return comp;
            }

        });
        spaceScroll.setViewportView(this.spaceTree);

        this.propsScroll = new JScrollPane();
        this.propsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.propsScroll.setPreferredSize(new Dimension(WIDTH, 219));
        add(this.propsScroll, BorderLayout.SOUTH);

        this.propsTable = new JTable();
        this.propsTable.setRowHeight(25);
        this.propsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.propsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.propsTable.setModel(new PropertiesTableModel());
        this.propsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        this.propsTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        this.propsScroll.setViewportView(this.propsTable);
    }

    public void setExecutable(boolean executable) {
        this.runButton.setEnabled(executable);
    }

    public boolean isExecutable() {
        return this.runButton.isEnabled();
    }

    public MainFrame getFrame() {
        return this.frame;
    }

    public void setFrame(MainFrame frame) {
        this.frame = frame;
    }

    public void load() throws Exception {
        this.tmdType = this.frame.getTaskFactory().getTmd();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new TmdNodeValue(this.tmdType));

        // executor
        this.executorSpaceNode = new DefaultMutableTreeNode(new ExecutorSpaceNodeValue(this.tmdType.getJobSpace()));
        rootNode.add(this.executorSpaceNode);
        for (JobType value : this.tmdType.getJobSpace().getJob()) {
            DefaultMutableTreeNode executorNode = new DefaultMutableTreeNode(new ExecutorNodeValue(value));
            this.executorSpaceNode.add(executorNode);
        }

        // task
        this.taskSpaceNode = new DefaultMutableTreeNode(new TaskSpaceNodeValue(this.tmdType.getTaskSpace()));
        rootNode.add(this.taskSpaceNode);
        for (TaskType value : this.frame.getTaskFactory().getTasks().getTask()) {
            DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(new TaskNodeValue(value));
            this.taskSpaceNode.add(taskNode);
            if (value.getNext() != null) {
                List<PlanType> plans = value.getNext().getPlan();
                for (PlanType plan : plans) {
                    taskNode.add(new DefaultMutableTreeNode(new PlanNodeValue(value, plan)));
                }
            }
        }

        // database
        this.databaseSpaceNode = new DefaultMutableTreeNode(new DatabaseSpaceNodeValue(this.tmdType.getDatabaseSpace()));
        rootNode.add(this.databaseSpaceNode);
        for (DatabaseType value : this.tmdType.getDatabaseSpace().getDatabase()) {
            DefaultMutableTreeNode dbserverNode = new DefaultMutableTreeNode(new DbServerNodeValue(value));
            this.databaseSpaceNode.add(dbserverNode);
        }

        this.spaceTree.setModel(new DefaultTreeModel(rootNode));
        this.spaceTree.setSelectionInterval(0, 0);
    }

    public void nodeSelected(TmdType tmd) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;
        this.selectedTask = null;

        this.addButton.setEnabled(false);
        this.removeButton.setEnabled(false);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(JobSpaceType executor) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;
        this.selectedTask = null;

        this.addButton.setEnabled(true);
        this.removeButton.setEnabled(false);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(JobType executor) {
        this.selectedDbServer = null;
        this.selectedExecutor = executor;
        this.selectedTask = null;

        this.addButton.setEnabled(false);
        this.removeButton.setEnabled(true);
        this.runButton.setEnabled(true);
    }

    public void nodeSelected(ItemType item) {
        this.selectedDbServer = null;
        this.selectedTask = null;

        this.addButton.setEnabled(false);
        this.removeButton.setEnabled(true);
        this.runButton.setEnabled(true);
    }

    public void nodeSelected(TaskSpaceType ts) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;
        this.selectedTask = null;

        this.addButton.setEnabled(true);
        this.removeButton.setEnabled(false);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(TaskType task) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;
        this.selectedTask = task;

        this.addButton.setEnabled(true);
        this.removeButton.setEnabled(true);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(PlanType plan) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;

        this.addButton.setEnabled(false);
        this.removeButton.setEnabled(true);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(DatabaseSpaceType ds) {
        this.selectedDbServer = null;
        this.selectedExecutor = null;
        this.selectedTask = null;

        this.addButton.setEnabled(true);
        this.removeButton.setEnabled(false);
        this.runButton.setEnabled(false);
    }

    public void nodeSelected(DatabaseType dbServer) {
        this.selectedDbServer = dbServer;
        this.selectedExecutor = null;
        this.selectedTask = null;

        this.addButton.setEnabled(false);
        this.removeButton.setEnabled(true);
        this.runButton.setEnabled(false);
    }

    public void appendExecutor() {
        ExecutorEditPanel panel = new ExecutorEditPanel();
        panel.load(this.tmdType, null);
        int button = JOptionPane.showConfirmDialog(this.frame, panel, "Create new job", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (button == JOptionPane.YES_OPTION) {
            appendExecutor(panel.save());
        }
    }

    public void appendExecutor(JobType jobType) {
        NodeValue nv = (NodeValue) this.executorSpaceNode.getUserObject();
        if (!nv.appendable(jobType.getName())) {
            JOptionPane.showMessageDialog(this.frame, jobType.getName() + " exists.", "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.frame.getTaskFactory().addJob(jobType);
        this.executorSpaceNode.add(new DefaultMutableTreeNode(new ExecutorNodeValue(jobType)));

        this.spaceTree.updateUI();
    }

    public void removeExecutor(JobType executor) {
        this.executorSpaceNode.remove(getSelectedTreeNode());
        this.frame.getTaskFactory().removeJob(this.selectedExecutor);
        this.spaceTree.updateUI();
    }

    public void removeExecutor(ItemType item) {
        this.executorSpaceNode.remove(getSelectedTreeNode());
        this.spaceTree.updateUI();
    }

    public void appendDatabase() {
        DatabaseEditPanel panel = new DatabaseEditPanel();
        int button = JOptionPane.showConfirmDialog(this.frame, panel, "Select one task", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (button == JOptionPane.YES_OPTION) {
            appendDatabase(panel.save());
        }
    }

    public void appendDatabase(DatabaseType dbServer) {
        NodeValue nv = (NodeValue) this.databaseSpaceNode.getUserObject();
        if (!nv.appendable(dbServer.getDbName())) {
            JOptionPane.showMessageDialog(this.frame, dbServer.getDbName() + " exists.", "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.frame.getTaskFactory().addDatabase(dbServer);
        this.databaseSpaceNode.add(new DefaultMutableTreeNode(new DbServerNodeValue(dbServer)));

        this.spaceTree.updateUI();
    }

    public void removeDatabase(DatabaseType dbServer) {
        this.databaseSpaceNode.remove(getSelectedTreeNode());
        this.frame.getTaskFactory().removeDatabase(this.selectedDbServer);
        this.spaceTree.updateUI();
    }

    public void appendTask() {
        TaskEditPanel panel = new TaskEditPanel();
        panel.configure(getFrame().getTaskFactory());
        int button = JOptionPane.showConfirmDialog(this.frame, panel, "Select one task", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (button == JOptionPane.YES_OPTION) {
            appendTask(panel.save());
        }
    }

    public void appendTask(TaskType task) {
        NodeValue nv = (NodeValue) this.taskSpaceNode.getUserObject();
        if (!nv.appendable(task.getName())) {
            JOptionPane.showMessageDialog(this.frame, task.getName() + " exists.", "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.frame.getTaskFactory().addTask(task);
        this.taskSpaceNode.add(new DefaultMutableTreeNode(new TaskNodeValue(task)));

        this.spaceTree.updateUI();
    }

    public void removeTask(TaskType task) {
        this.taskSpaceNode.remove(getSelectedTreeNode());
        this.frame.getTaskFactory().removeTask(this.selectedTask);
        this.spaceTree.updateUI();
    }

    public void appendPlan(TaskType task) {
        PlanEditPanel panel = new PlanEditPanel();
        panel.configure(this.frame.getTaskFactory());
        int button = JOptionPane.showConfirmDialog(this.frame, panel, "Add a plan", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (button == JOptionPane.YES_OPTION) {

            PlanType plan = panel.save();
            task.getNext().getPlan().add(plan);
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new PlanNodeValue(task, plan));
            getSelectedTreeNode().add(child);
        }
        this.spaceTree.updateUI();
    }

    public void expandItems() {
        DefaultMutableTreeNode jobNode = getSelectedTreeNode();
        jobNode.removeAllChildren();

        NodeValue nv = getSelectedTreeNodeValue();
        JobType jobType = this.frame.getTaskFactory().getJob(nv.getName());
        if (jobType == null) {
            return;
        }
        List<ItemType> items= jobType.getItem();
        for (ItemType item : items) {
            jobNode.add(new DefaultMutableTreeNode(new ItemNodeValue(item)));
        }
    }

    public void expandPlan() {
        DefaultMutableTreeNode taskNode = getSelectedTreeNode();
        taskNode.removeAllChildren();

        NodeValue nv = getSelectedTreeNodeValue();
        TaskType task = this.frame.getTaskFactory().getTask(nv.getName());
        if (task == null || task.getNext() == null) {
            return;
        }

        List<PlanType> plans = task.getNext().getPlan();
        for (PlanType plan : plans) {
            taskNode.add(new DefaultMutableTreeNode(new PlanNodeValue(task, plan)));
        }
    }

    public void removePlan(PlanType plan) {
        this.taskSpaceNode.remove(getSelectedTreeNode());
        this.selectedTask.getNext().getPlan().remove(plan);
        this.spaceTree.updateUI();
    }

    public void updateProperties(Map<String, String> props) {
        this.propValues = props == null ? new TreeMap<String, String>() : props;
        this.propKeys = this.propValues.keySet().toArray(new String[0]);
        this.propsTable.updateUI();
    }

    private DefaultMutableTreeNode getSelectedTreeNode() {
        return (DefaultMutableTreeNode) this.spaceTree.getLastSelectedPathComponent();
    }

    private NodeValue getSelectedTreeNodeValue() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.spaceTree.getLastSelectedPathComponent();
        return (NodeValue) node.getUserObject();
    }

    /**
     *
     * @author Kan Lin
     *
     */
    class PropertiesTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -3553978563489256767L;

        @Override
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? "Key" : "Value";
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return NaviPanel.this.propKeys.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return " " + NaviPanel.this.propKeys[rowIndex];
            }
            else {
                return " " + NaviPanel.this.propValues.get(NaviPanel.this.propKeys[rowIndex]);
            }
        }
    }
}
