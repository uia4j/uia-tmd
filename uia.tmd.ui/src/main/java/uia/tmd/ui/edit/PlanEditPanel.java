package uia.tmd.ui.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import uia.tmd.TaskFactory;
import uia.tmd.model.xml.ParamType;
import uia.tmd.model.xml.PlanType;

public class PlanEditPanel extends JPanel {

    private static final long serialVersionUID = 512658072253693507L;

    private JComboBox<String> taskBox;

    private JTextField descField;

    private JTextArea statementField;

    private JTable paramsTable;

    private TaskFactory factory;

    private PlanType plan;

    private ArrayList<ParamType> params;

    public PlanEditPanel() {
        this.params = new ArrayList<ParamType>();

        setLayout(null);
        setPreferredSize(new Dimension(785, 307));

        JLabel taskLabel = new JLabel("Task");
        taskLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        taskLabel.setBounds(17, 13, 94, 15);
        add(taskLabel);

        this.taskBox = new JComboBox<String>();
        this.taskBox.setBackground(Color.WHITE);
        this.taskBox.setBounds(121, 10, 288, 21);
        add(this.taskBox);

        JLabel descLabel = new JLabel("Description");
        descLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        descLabel.setBounds(17, 44, 94, 15);
        add(descLabel);

        this.descField = new JTextField();
        this.descField.setBounds(121, 41, 654, 21);
        add(this.descField);
        this.descField.setColumns(10);

        JLabel statementLabel = new JLabel("Statement");
        statementLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        statementLabel.setBounds(17, 76, 94, 15);
        add(statementLabel);

        this.statementField = new JTextArea();
        this.statementField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                reloadParameters();
            }
        });
        this.statementField.setText("SFC_BO=? and SITE='1020'");
        this.statementField.setBorder(BorderFactory.createEtchedBorder(1));
        this.statementField.setBounds(121, 72, 654, 81);
        add(this.statementField);

        JLabel paramsLabel = new JLabel("Statement");
        paramsLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        paramsLabel.setBounds(17, 164, 94, 15);
        add(paramsLabel);

        JScrollPane paramsPane = new JScrollPane();
        paramsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        paramsPane.setBounds(121, 163, 654, 127);
        add(paramsPane);

        this.paramsTable = new JTable(new ParamTableModel());
        this.paramsTable.setRowHeight(25);
        this.paramsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        this.paramsTable.getColumnModel().getColumn(0).setResizable(false);
        this.paramsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        this.paramsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        this.paramsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        this.paramsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        this.paramsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        paramsPane.setViewportView(this.paramsTable);
    }

    public void configure(TaskFactory factory) {
        this.factory = factory;
        this.factory.getTasks().getTask().forEach(t -> this.taskBox.addItem(t.getName()));
        this.taskBox.setSelectedIndex(0);
    }

    public PlanType save() {
        if (this.plan == null) {
            this.plan = new PlanType();
            this.plan.setTaskName((String) this.taskBox.getSelectedItem());
        }

        this.plan.setDesc(this.descField.getText());
        this.plan.setWhere(this.statementField.getText());
        this.plan.getParam().clear();
        this.plan.getParam().addAll(this.params);
        return this.plan;
    }

    public void load(PlanType plan) {
        if (plan == null) {
            return;
        }

        this.plan = plan;
        this.taskBox.setSelectedItem(this.plan.getTaskName());
        this.descField.setText(this.plan.getDesc());
        this.statementField.setText(this.plan.getWhere());

        this.params.clear();
        for (ParamType _p : this.plan.getParam()) {
            ParamType p = new ParamType();
            p.setSourceColumn(_p.getSourceColumn());
            p.setText(_p.getText());
            p.setPrefix(_p.getPrefix());
            p.setPostfix(_p.getPostfix());
            this.params.add(p);
        }

        this.taskBox.setEnabled(false);

        updateUI();
    }

    private void reloadParameters() {
        String value = this.statementField.getText();
        int p = 0;
        int paramCount = 0;
        while (p >= 0) {
            p = value.indexOf('?', p);
            if (p > 0) {
                paramCount++;
                p++;
            }
        }

        for (int i = this.params.size(); i < paramCount; i++) {
            this.params.add(new ParamType());
        }
        for (int i = this.params.size(); i > paramCount; i--) {
            this.params.remove(i - 1);
        }

        if (this.paramsTable != null) {
            this.paramsTable.updateUI();
        }
    }

    public class ParamTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -6398042370211616386L;

        public ParamTableModel() {
            reloadParameters();
        }

        @Override
        public int getRowCount() {
            return PlanEditPanel.this.params.size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            String v = value == null ? null : value.toString();
            if (v != null && v.trim().isEmpty()) {
                v = null;
            }
            ParamType paramType = PlanEditPanel.this.params.get(rowIndex);
            switch (columnIndex) {
                case 1:
                    paramType.setSourceColumn(v);
                    break;
                case 2:
                    paramType.setText(v);
                    break;
                case 3:
                    paramType.setPrefix(v);
                    break;
                case 4:
                    paramType.setPostfix(v);
                    break;
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 1:
                    return "Source Column";
                case 2:
                    return "Text";
                case 3:
                    return "Prefix";
                case 4:
                    return "Postfix";
                default:
                    return "";
            }
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ParamType paramType = PlanEditPanel.this.params.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return paramType.getSourceColumn();
                case 2:
                    return paramType.getText();
                case 3:
                    return paramType.getPrefix();
                case 4:
                    return paramType.getPostfix();
                default:
                    return null;
            }
        }
    }
}
