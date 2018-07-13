package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uia.tmd.model.xml.ParamType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.ui.NaviPanel;
import uia.tmd.ui.edit.PlanEditPanel;

public class PlanNodeValue implements NodeValue {

    public final TaskType task;

    public final PlanType plan;

    public PlanNodeValue(TaskType task, PlanType plan) {
        this.task = task;
        this.plan = plan;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        if (nodeSelected) {
            return new ImageIcon(NodeValue.class.getResource("/resources/images/task.png"));
        }
        else {
            return new ImageIcon(NodeValue.class.getResource("/resources/images/taskNext.png"));
        }
    }

    @Override
    public boolean appendable(String taskName) {
        return false;
    }

    @Override
    public void append(NaviPanel panel) {

    }

    @Override
    public void select(NaviPanel panel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Plan");
        props.put("Task", this.plan.getTaskName());
        props.put("Where", this.plan.getWhere());
        int i = 1;
        for (ParamType param : this.plan.getParam()) {
            String text = "";
            if (param.getSourceColumn() != null) {
                text = "${" + param.getSourceColumn() + "}";
            }
            else {
                text = param.getText();
            }
            if (param.getPrefix() != null) {
                text = "'" + param.getPrefix() + "' + " + text;
            }
            if (param.getPostfix() != null) {
                text = text + " + '" + param.getPostfix() + "'";
            }
            props.put("Param" + i, text);
            i++;
        }

        panel.updateProperties(props);
        panel.nodeSelected(this.plan);

    }

    @Override
    public void delete(NaviPanel panel) {

    }

    @Override
    public String getName() {
        return this.plan.getTaskName();
    }

    @Override
    public void expand(NaviPanel panel) {
        panel.expandPlan();
    }

    @Override
    public void execute(NaviPanel panel) {

    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        PlanEditPanel panel = new PlanEditPanel();
        panel.configure(naviPanel.getFrame().getTaskFactory());
        panel.load(this.plan);
        int code = JOptionPane.showConfirmDialog(naviPanel.getFrame(), panel, "Configure Task", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            panel.save();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.plan.getTaskName();
    }
}
