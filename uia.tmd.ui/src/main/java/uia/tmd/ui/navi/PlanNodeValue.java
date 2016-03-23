package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.ui.NaviPanel;

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
    public void appendNode(NaviPanel panel) {

    }

    @Override
    public void select(NaviPanel panel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Plan");
        if (this.plan.getJoin() != null) {
            int i = 1;
            for (ColumnType col : this.plan.getJoin().getColumn()) {
                props.put("Join" + i, String.format("%s.%s = %s.%s",
                        this.task.getName(),
                        col.getSource(),
                        this.plan.getTaskName(),
                        col.getValue()));
                i++;
            }
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
    public boolean edit(NaviPanel panel) {
        return false;
    }

    @Override
    public String toString() {
        return this.plan.getTaskName();
    }
}
