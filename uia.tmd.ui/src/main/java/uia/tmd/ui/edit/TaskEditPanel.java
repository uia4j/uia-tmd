package uia.tmd.ui.edit;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TableType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

public class TaskEditPanel extends JPanel {

    private static final long serialVersionUID = 3276232673164511174L;

    private JTextField nameField;

    private JComboBox sourceBox;

    private JComboBox targetBox;

    private TaskType task;

    public TaskEditPanel(List<TableType> tables) {

        setLayout(null);
        setPreferredSize(new Dimension(335, 105));

        DefaultComboBoxModel sourceModel = new DefaultComboBoxModel();
        DefaultComboBoxModel targetModel = new DefaultComboBoxModel();
        for (TableType table : tables) {
            sourceModel.addElement(table.getName());
            targetModel.addElement(table.getName());
        }

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setBounds(10, 13, 83, 15);
        add(nameLabel);

        this.nameField = new JTextField();
        this.nameField.setBounds(103, 10, 222, 21);
        add(this.nameField);
        this.nameField.setColumns(10);

        JLabel sourceLabel = new JLabel("Source Table");
        sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sourceLabel.setBounds(10, 44, 83, 15);
        add(sourceLabel);

        this.sourceBox = new JComboBox();
        this.sourceBox.setBounds(103, 41, 222, 21);
        this.sourceBox.setModel(sourceModel);
        this.sourceBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent evt) {
                TaskEditPanel.this.targetBox.setSelectedIndex(TaskEditPanel.this.sourceBox.getSelectedIndex());
                TaskEditPanel.this.nameField.setText((String) TaskEditPanel.this.sourceBox.getSelectedItem());
            }

        });
        add(this.sourceBox);

        JLabel targetLabel = new JLabel("Target Table");
        targetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        targetLabel.setBounds(10, 75, 83, 15);
        add(targetLabel);

        this.targetBox = new JComboBox();
        this.targetBox.setBounds(103, 72, 222, 21);
        this.targetBox.setModel(targetModel);
        add(this.targetBox);

        this.sourceBox.setSelectedIndex(0);
        this.nameField.setText((String) this.sourceBox.getSelectedItem());

    }

    public TaskType save() {
        if (this.task == null) {
            this.task = new TaskType();
            this.task.setSourceSelect(new SourceSelectType());
            this.task.setTargetUpdate(new TargetUpdateType());
        }

        this.task.setName(this.nameField.getText());
        this.task.getSourceSelect().setTable((String) this.sourceBox.getSelectedItem());
        this.task.getTargetUpdate().setTable((String) this.targetBox.getSelectedItem());
        return this.task;
    }

    public void load(TaskType task) {
        this.task = task;

        this.sourceBox.setSelectedItem(task.getSourceSelect().getTable());
        if (task.getTargetUpdate() == null || task.getTargetUpdate().getTable() == null) {
            this.targetBox.setSelectedItem(task.getSourceSelect().getTable());
        }
        else {
            this.targetBox.setSelectedItem(task.getTargetUpdate().getTable());
        }
        this.sourceBox.setEnabled(false);
        this.targetBox.setEnabled(false);
    }
}
