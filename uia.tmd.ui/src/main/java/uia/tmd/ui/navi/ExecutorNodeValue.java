package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uia.tmd.model.xml.ExecutorType;
import uia.tmd.ui.NaviPanel;
import uia.tmd.ui.edit.ExecutorEditPanel;

public class ExecutorNodeValue implements NodeValue {

    public final ExecutorType executor;

    public ExecutorNodeValue(ExecutorType executor) {
        this.executor = executor;
    }

    @Override
    public String getName() {
        return this.executor.getTask();
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/executor.png"));
    }

    @Override
    public boolean appendable(String taskName) {
        return false;
    }

    @Override
    public void appendNode(NaviPanel naviPanel) {
    }

    @Override
    public void select(NaviPanel naviPanel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Executor");
        props.put("Name", this.executor.getName());
        props.put("Task Name", this.executor.getTask());
        props.put("DB Source", this.executor.getSource());
        props.put("DB Target", this.executor.getTarget());
        naviPanel.updateProperties(props);

        naviPanel.nodeSelected(this.executor);
    }

    @Override
    public void delete(NaviPanel naviPanel) {
        naviPanel.removeExecutor(this.executor);
    }

    @Override
    public void expand(NaviPanel naviPanel) {
        naviPanel.expandPlan();
    }

    @Override
    public void execute(NaviPanel naviPanel) {
        naviPanel.getFrame().runExecutor(this.executor.getName());
    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        ExecutorEditPanel panel = new ExecutorEditPanel();
        panel.load(naviPanel.getFrame().getTaskFactory().getTmd(), this.executor);
        int code = JOptionPane.showConfirmDialog(naviPanel.getFrame(), panel, "Configure Executor", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            panel.save();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.executor.getName();
    }
}
