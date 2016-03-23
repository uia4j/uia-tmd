package uia.tmd.ui.edit;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class ExecutorEditPanel extends JPanel {

    private static final long serialVersionUID = -7947622814357795549L;

    private ExecutorType executor;

    private JTextField nameField;

    private JComboBox sourceBox;

    private JComboBox targetBox;

    private JComboBox taskBox;

    public ExecutorEditPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(370, 140));

        this.nameField = new JTextField();
        this.nameField.setBounds(126, 10, 222, 21);
        add(this.nameField);
        this.nameField.setColumns(10);

        this.sourceBox = new JComboBox();
        this.sourceBox.setBounds(126, 41, 222, 21);
        add(this.sourceBox);

        this.targetBox = new JComboBox();
        this.targetBox.setBounds(126, 72, 222, 21);
        add(this.targetBox);

        this.taskBox = new JComboBox();
        this.taskBox.setBounds(126, 103, 222, 21);
        add(this.taskBox);

        JLabel lblNewLabel = new JLabel("Name");
        lblNewLabel.setBounds(10, 13, 106, 15);
        add(lblNewLabel);

        JLabel lblSourceDatabase = new JLabel("Source Database");
        lblSourceDatabase.setBounds(10, 44, 106, 15);
        add(lblSourceDatabase);

        JLabel lblTargetDatabase = new JLabel("Target Database");
        lblTargetDatabase.setBounds(10, 75, 106, 15);
        add(lblTargetDatabase);

        JLabel lblTaskExecuted = new JLabel("Task");
        lblTaskExecuted.setBounds(10, 106, 106, 15);
        add(lblTaskExecuted);
    }

    public ExecutorType save() {
        this.executor.setName(this.nameField.getText());
        this.executor.setSource((String) this.sourceBox.getSelectedItem());
        this.executor.setTarget((String) this.targetBox.getSelectedItem());
        this.executor.setTask((String) this.taskBox.getSelectedItem());
        return this.executor;
    }

    public void load(TmdType tmd, ExecutorType executor) {
        this.executor = executor;

        this.nameField.setText(this.executor.getName());
        for (DbServerType db : tmd.getDatabaseSpace().getDbServer()) {
            this.sourceBox.addItem(db.getId());
            this.targetBox.addItem(db.getId());
        }
        this.sourceBox.setSelectedItem(this.executor.getSource());
        this.targetBox.setSelectedItem(this.executor.getTarget());

        for (TaskType task : tmd.getTaskSpace().getTask()) {
            this.taskBox.addItem(task.getName());
        }
        this.taskBox.setSelectedItem(this.executor.getTask());
    }
}
