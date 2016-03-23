package uia.tmd.ui.navi;

import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.ui.NaviPanel;
import uia.tmd.ui.edit.TaskEditPanel;

public class TaskNodeValue implements NodeValue {

    public final TaskType task;

    public TaskNodeValue(TaskType task) {
        this.task = task;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/task.png"));
    }

    @Override
    public boolean appendable(String taskName) {
        if (this.task.getNext() != null) {
            List<PlanType> plans = this.task.getNext().getPlan();
            for (PlanType plan : plans) {
                if (taskName.equals(plan.getTaskName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void appendNode(NaviPanel panel) {
        panel.appendPlan(this.task);
    }

    @Override
    public void select(NaviPanel panel) {
        String src = this.task.getSourceSelect().getTable();
        String tar = this.task.getTargetUpdate().getTable();
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Task");
        props.put("Source Table", src);
        props.put("Target Table", tar == null ? src : tar);
        panel.updateProperties(props);

        panel.nodeSelected(this.task);
    }

    @Override
    public void delete(NaviPanel panel) {

    }

    @Override
    public String getName() {
        return this.task.getName();
    }

    @Override
    public void expand(NaviPanel panel) {

    }

    @Override
    public void execute(NaviPanel panel) {

    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        TaskEditPanel panel = new TaskEditPanel(naviPanel.getFrame().getTaskFactory().getTmd().getTableSpace().getTable());
        panel.load(this.task);
        int code = JOptionPane.showConfirmDialog(naviPanel.getFrame(), panel, "Configure Task", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            panel.save();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.task.getName();
    }
}
