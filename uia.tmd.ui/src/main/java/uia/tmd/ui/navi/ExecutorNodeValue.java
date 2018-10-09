package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uia.tmd.model.xml.JobType;
import uia.tmd.ui.NaviPanel;
import uia.tmd.ui.edit.ExecutorEditPanel;

public class ExecutorNodeValue implements NodeValue {

    public final JobType jobType;

    public ExecutorNodeValue(JobType jobType) {
        this.jobType = jobType;
    }

    @Override
    public String getName() {
        return this.jobType.getName();
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
    public void append(NaviPanel naviPanel) {
    }

    @Override
    public void select(NaviPanel naviPanel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Job");
        props.put("Name", this.jobType.getName());
        props.put("DB Source", this.jobType.getSource());
        props.put("DB Target", this.jobType.getTarget());
        props.put("Description", this.jobType.getDesc());
        props.put("Item Count", "" + this.jobType.getItem().size());
        naviPanel.updateProperties(props);

        naviPanel.nodeSelected(this.jobType);
    }

    @Override
    public void delete(NaviPanel naviPanel) {
        naviPanel.removeExecutor(this.jobType);
    }

    @Override
    public void expand(NaviPanel naviPanel) {
        naviPanel.expandItems();
    }

    @Override
    public void execute(NaviPanel naviPanel) {
        naviPanel.getFrame().runExecutor(this.jobType.getName());
    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        ExecutorEditPanel panel = new ExecutorEditPanel();
        panel.load(naviPanel.getFrame().getTaskFactory().getTmd(), this.jobType);
        int code = JOptionPane.showConfirmDialog(naviPanel.getFrame(), panel, "Job Detail ...", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            panel.save();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.jobType.getName();
    }
}
