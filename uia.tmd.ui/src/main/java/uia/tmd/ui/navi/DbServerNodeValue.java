package uia.tmd.ui.navi;

import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.ui.NaviPanel;
import uia.tmd.ui.edit.DbServerEditPanel;

public class DbServerNodeValue implements NodeValue {

    public final DbServerType db;

    public DbServerNodeValue(DbServerType db) {
        this.db = db;
    }

    @Override
    public Icon getIcon(boolean nodeSelected) {
        return new ImageIcon(NodeValue.class.getResource("/resources/images/dbServer.png"));
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
        props.put("Node Type", "DB Server");
        props.put("Name", this.db.getId());
        props.put("DB Type", this.db.getDbType());
        props.put("Host", this.db.getHost());
        props.put("Port", "" + this.db.getPort());
        props.put("Database", this.db.getDbName());
        props.put("Suser", this.db.getUser());
        naviPanel.updateProperties(props);
        naviPanel.nodeSelected(this.db);
    }

    @Override
    public void delete(NaviPanel naviPanel) {

    }

    @Override
    public String getName() {
        return this.db.getId();
    }

    @Override
    public void expand(NaviPanel naviPanel) {

    }

    @Override
    public void execute(NaviPanel naviPanel) {

    }

    @Override
    public boolean edit(NaviPanel naviPanel) {
        DbServerEditPanel panel = new DbServerEditPanel();
        panel.load(this.db);
        int code = JOptionPane.showConfirmDialog(naviPanel.getFrame(), panel, "Configure Database Server", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            panel.save();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.db.getId();
    }
}
