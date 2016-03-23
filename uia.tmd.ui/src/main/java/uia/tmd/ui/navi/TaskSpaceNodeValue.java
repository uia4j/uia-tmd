package uia.tmd.ui.navi;

import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uia.tmd.model.xml.TaskSpaceType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.ui.NaviPanel;

public class TaskSpaceNodeValue implements NodeValue {

    public final TaskSpaceType ts;

    public TaskSpaceNodeValue(TaskSpaceType ts) {
        this.ts = ts;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/taskSpace.png"));
    }

    @Override
    public void appendNode(NaviPanel panel) {
        panel.appendTask();
    }

    @Override
    public boolean appendable(String taskName) {
        List<TaskType> tasks = this.ts.getTask();
        for (TaskType task : tasks) {
            if (taskName.equals(task.getName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void select(NaviPanel panel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Task Space");
        panel.updateProperties(props);

        panel.nodeSelected(this.ts);
    }

    @Override
    public void delete(NaviPanel panel) {

    }

    @Override
    public void execute(NaviPanel panel) {

    }

    @Override
    public String getName() {
        return "TaskSpace";
    }

    @Override
    public void expand(NaviPanel panel) {

    }

    @Override
    public boolean edit(NaviPanel panel) {
        return false;
    }

    @Override
    public String toString() {
        return "Task List";
    }
}
