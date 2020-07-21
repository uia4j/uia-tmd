package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uia.tmd.model.xml.ItemType;
import uia.tmd.ui.NaviPanel;

public class ItemNodeValue implements NodeValue {

    public final ItemType itemType;

    public ItemNodeValue(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public String getName() {
        return this.itemType.getTaskName();
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/images/task.png"));
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
        props.put("Node Type", "Item");
        props.put("Task", this.itemType.getTaskName());
        props.put("Driver", this.itemType.getDriverName());
        naviPanel.updateProperties(props);

        naviPanel.nodeSelected(this.itemType);
    }

    @Override
    public void delete(NaviPanel naviPanel) {
        naviPanel.removeExecutor(this.itemType);
    }

    @Override
    public void expand(NaviPanel naviPanel) {
        naviPanel.expandPlan();
    }

    @Override
    public void execute(NaviPanel naviPanel) {
    	
    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        return false;
    }

    @Override
    public String toString() {
        return this.itemType.getTaskName();
    }
}
