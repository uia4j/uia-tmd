package uia.tmd.ui.edit;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import uia.tmd.model.xml.DatabaseType;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.JobType;
import uia.tmd.model.xml.TmdType;

public class ExecutorEditPanel extends JPanel {

    private static final long serialVersionUID = -7947622814357795549L;

    private JobType executor;

    private JTextField nameField;

    private JComboBox<String> sourceBox;

    private JComboBox<String> targetBox;

    private JTextField descField;

    private JCheckBox deleteBox;

    private JTable itemTable;

    public ExecutorEditPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(712, 374));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        nameLabel.setBounds(10, 13, 106, 15);
        add(nameLabel);

        this.nameField = new JTextField();
        this.nameField.setBounds(126, 10, 222, 21);
        add(this.nameField);
        this.nameField.setColumns(10);

        JLabel descLabel = new JLabel("Description");
        descLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        descLabel.setBounds(10, 44, 106, 15);
        add(descLabel);

        this.descField = new JTextField();
        this.descField.setBounds(126, 41, 570, 21);
        add(this.descField);

        JLabel sourceLabel = new JLabel("Source Database");
        sourceLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        sourceLabel.setBounds(10, 75, 106, 15);
        add(sourceLabel);

        this.sourceBox = new JComboBox<String>();
        this.sourceBox.setBounds(126, 72, 222, 21);
        add(this.sourceBox);

        JLabel targetLabel = new JLabel("Target Database");
        targetLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        targetLabel.setBounds(358, 72, 106, 15);
        add(targetLabel);

        this.targetBox = new JComboBox<String>();
        this.targetBox.setBounds(474, 72, 222, 21);
        add(this.targetBox);

        JLabel taskLabel = new JLabel("Task");
        taskLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        taskLabel.setBounds(10, 131, 106, 15);
        add(taskLabel);

        this.deleteBox = new JCheckBox("Delete After Executing");
        this.deleteBox.setBounds(126, 99, 222, 23);
        add(this.deleteBox);

        JScrollPane itemScroll = new JScrollPane();
        itemScroll.setBounds(126, 128, 570, 236);
        add(itemScroll);

        this.itemTable = new JTable(new ItemTableModel());
        itemScroll.setViewportView(this.itemTable);
    }

    public JobType save() {
        if (this.executor == null) {
            this.executor = new JobType();
        }
        this.executor.setName(this.nameField.getText());
        this.executor.setSource((String) this.sourceBox.getSelectedItem());
        this.executor.setTarget((String) this.targetBox.getSelectedItem());
        this.executor.setDesc(this.descField.getText());
        this.executor.setSourceDelete(this.deleteBox.isSelected());
        return this.executor;
    }

    public void load(TmdType tmd, JobType executor) {
        this.executor = executor;

        for (DatabaseType db : tmd.getDatabaseSpace().getDatabase()) {
            this.sourceBox.addItem(db.getId());
            this.targetBox.addItem(db.getId());
        }

        if (this.executor != null) {
            this.nameField.setText(this.executor.getName());
            this.sourceBox.setSelectedItem(this.executor.getSource());
            this.targetBox.setSelectedItem(this.executor.getTarget());
            this.descField.setText(this.executor.getDesc());
            this.deleteBox.setSelected(this.executor.isSourceDelete());
        }

        this.itemTable.updateUI();
    }

    public class ItemTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -3433644415472125736L;

        @Override
        public int getRowCount() {
            if (ExecutorEditPanel.this.executor == null) {
                return 0;
            }

            if (ExecutorEditPanel.this.executor.getItem() == null) {
                return 0;
            }

            return ExecutorEditPanel.this.executor.getItem().size();
        }

        @Override
        public int getColumnCount() {
            if (ExecutorEditPanel.this.executor == null) {
                return 3;
            }

            if (ExecutorEditPanel.this.executor.getItem() == null) {
                return 3;
            }

            if (ExecutorEditPanel.this.executor.getItem().size() == 0) {
                return 3;
            }

            return 3 + ExecutorEditPanel.this.executor.getItem().stream()
                    .mapToInt(i -> i.getArgs().getArg() == null ? 0 : i.getArgs().getArg().size())
                    .max()
                    .getAsInt();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (ExecutorEditPanel.this.executor == null) {
                return null;
            }

            if (ExecutorEditPanel.this.executor.getItem() == null) {
                return null;
            }

            if (ExecutorEditPanel.this.executor.getItem().size() == 0) {
                return null;
            }

            ItemType item = ExecutorEditPanel.this.executor.getItem().get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return item.getTaskName();
                case 1:
                    return item.getDriverName();
                case 2:
                    return item.getDesc();
            }

            return columnIndex > 2 ? item.getArgs().getArg().get(columnIndex - 3) : null;
        }

    }
}
