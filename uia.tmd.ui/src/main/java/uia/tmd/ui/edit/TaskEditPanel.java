package uia.tmd.ui.edit;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import uia.tmd.TaskFactory;
import uia.tmd.model.xml.AbstractTableType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

public class TaskEditPanel extends JPanel {

    private static final long serialVersionUID = 3276232673164511174L;

    private JTextField nameField;

    private JComboBox<String> sourceBox;

    private JComboBox<String> targetBox;

    private JTextField descField;

    private JCheckBox deleteBox;

    private List<String> tableNames;

    private TaskType task;

    public TaskEditPanel() {
        this.tableNames = new ArrayList<String>();

        setLayout(null);
        setPreferredSize(new Dimension(676, 159));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setBounds(10, 13, 83, 15);
        add(nameLabel);

        this.nameField = new JTextField();
        this.nameField.setBounds(103, 10, 222, 21);
        add(this.nameField);

        JLabel descLabel = new JLabel("Description");
        descLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        descLabel.setBounds(10, 41, 83, 15);
        add(descLabel);

        this.descField = new JTextField();
        this.descField.setText((String) null);
        this.descField.setColumns(10);
        this.descField.setBounds(103, 38, 558, 21);
        add(this.descField);

        JLabel sourceLabel = new JLabel("Source Table");
        sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sourceLabel.setBounds(10, 70, 83, 15);
        add(sourceLabel);

        this.sourceBox = new JComboBox<String>();
        this.sourceBox.setEditable(true);
        this.sourceBox.setBounds(103, 66, 222, 21);
        this.sourceBox.setModel(new DefaultComboBoxModel<String>());
        this.sourceBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent evt) {
                TaskEditPanel.this.targetBox.setSelectedItem(TaskEditPanel.this.sourceBox.getSelectedItem());
            }

        });
        add(this.sourceBox);

        JLabel targetLabel = new JLabel("Target Table");
        targetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        targetLabel.setBounds(346, 69, 83, 15);
        add(targetLabel);

        this.targetBox = new JComboBox<String>();
        this.targetBox.setEditable(true);
        this.targetBox.setBounds(439, 66, 222, 21);
        this.targetBox.setModel(new DefaultComboBoxModel<String>());
        add(this.targetBox);

        this.deleteBox = new JCheckBox("Delete After Executing");
        this.deleteBox.setBounds(103, 93, 222, 23);
        add(this.deleteBox);
    }

    public void configure(TaskFactory factory) {
        for (AbstractTableType table : factory.getTables().values()) {
            this.tableNames.add(table.getName());
            ((DefaultComboBoxModel<String>) this.sourceBox.getModel()).addElement(table.getName());
            ((DefaultComboBoxModel<String>) this.targetBox.getModel()).addElement(table.getName());
        }

        if (this.tableNames.size() > 0) {
            this.sourceBox.setSelectedIndex(0);
            this.nameField.setText((String) this.sourceBox.getSelectedItem());
        }

    }

    public TaskType save() {
        if (this.task == null) {
            this.task = new TaskType();
            this.task.setName(this.nameField.getText());
            this.task.setSourceSelect(new SourceSelectType());
            this.task.getSourceSelect().setTable((String) this.sourceBox.getSelectedItem());
            this.task.setTargetUpdate(new TargetUpdateType());
            this.task.getTargetUpdate().setTable((String) this.targetBox.getSelectedItem());
        }

        this.task.setDesc(this.descField.getText());
        this.task.getSourceSelect().setDelete(this.deleteBox.isSelected());
        return this.task;
    }

    public void load(TaskType task) {
        this.task = task;
        if (!this.tableNames.contains(task.getSourceSelect().getTable())) {
            ((DefaultComboBoxModel<String>) this.sourceBox.getModel()).addElement(task.getSourceSelect().getTable());
            ((DefaultComboBoxModel<String>) this.targetBox.getModel()).addElement(task.getSourceSelect().getTable());
            this.tableNames.add(task.getSourceSelect().getTable());
        }
        if (task.getTargetUpdate().getTable() != null && !this.tableNames.contains(task.getTargetUpdate().getTable())) {
            ((DefaultComboBoxModel<String>) this.targetBox.getModel()).addElement(task.getTargetUpdate().getTable());
            this.tableNames.add(task.getTargetUpdate().getTable());
        }

        this.sourceBox.setSelectedItem(task.getSourceSelect().getTable());
        if (task.getTargetUpdate() == null || task.getTargetUpdate().getTable() == null) {
            this.targetBox.setSelectedItem(task.getSourceSelect().getTable());
        }
        else {
            this.targetBox.setSelectedItem(task.getTargetUpdate().getTable());
        }
        this.descField.setText(this.task.getDesc());
        this.deleteBox.setSelected(task.getSourceSelect().isDelete() == null
                ? false
                : task.getSourceSelect().isDelete().booleanValue());

        this.nameField.setEditable(false);
        this.sourceBox.setEnabled(false);
        this.targetBox.setEnabled(false);
    }
}
