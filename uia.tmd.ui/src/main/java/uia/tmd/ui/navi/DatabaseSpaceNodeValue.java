package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uia.tmd.model.xml.DatabaseSpaceType;
import uia.tmd.ui.NaviPanel;

public class DatabaseSpaceNodeValue implements NodeValue {

    public final DatabaseSpaceType ds;

    public DatabaseSpaceNodeValue(DatabaseSpaceType ds) {
        this.ds = ds;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/databaseSpace.png"));
    }

    @Override
    public void appendNode(NaviPanel panel) {

    }

    @Override
    public boolean appendable(String taskName) {
        return false;
    }

    @Override
    public void select(NaviPanel panel) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Node Type", "Database Space");
        panel.updateProperties(props);
    }

    @Override
    public void delete(NaviPanel panel) {

    }

    @Override
    public void execute(NaviPanel panel) {

    }

    @Override
    public String getName() {
        return "DatabaseSpace";
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
        return "Database List";
    }
}
