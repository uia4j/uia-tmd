package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uia.tmd.model.xml.TmdType;
import uia.tmd.ui.NaviPanel;

public class TmdNodeValue implements NodeValue {

    private final TmdType tmdType;

    public TmdNodeValue(TmdType tmdType) {
        this.tmdType = tmdType;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/tmd.png"));
    }

    @Override
    public String getName() {
        return null;
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
        props.put("Node Type", "Root Space");
        panel.updateProperties(props);
    }

    @Override
    public void delete(NaviPanel panel) {
    }

    @Override
    public void expand(NaviPanel panel) {
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
        return "ROOT";
    }
}
