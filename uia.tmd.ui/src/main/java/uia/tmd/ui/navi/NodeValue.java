package uia.tmd.ui.navi;

import javax.swing.Icon;

import uia.tmd.ui.NaviPanel;

public interface NodeValue {

    public Icon getIcon(boolean nodeSelected);

    public String getName();

    public boolean appendable(String taskName);

    public void execute(NaviPanel naviPanel);

    public void appendNode(NaviPanel naviPanel);

    public void select(NaviPanel naviPanel);

    public void delete(NaviPanel naviPanel);

    public void expand(NaviPanel naviPanel);

    public boolean edit(NaviPanel naviPanel);
}
