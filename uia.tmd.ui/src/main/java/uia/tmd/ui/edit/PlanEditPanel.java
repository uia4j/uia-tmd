package uia.tmd.ui.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import uia.tmd.DataAccessor;
import uia.tmd.TaskFactory;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;

public class PlanEditPanel extends JPanel {

    private static final long serialVersionUID = 512658072253693507L;

    private DataAccessor da;

    private TaskFactory factory;

    private TaskType main;

    private TaskType next;

    private PlanType plan;

    private JTextField mainField;

    private JComboBox nextBox;

    private JCheckBox fkCheck1;

    private JCheckBox fkCheck2;

    private JCheckBox fkCheck3;

    private JCheckBox fkCheck4;

    private JCheckBox fkCheck5;

    private JComboBox mainFkBox1;

    private JComboBox mainFkBox2;

    private JComboBox mainFkBox3;

    private JComboBox mainFkBox4;

    private JComboBox mainFkBox5;

    private JComboBox nextFkBox1;

    private JComboBox nextFkBox2;

    private JComboBox nextFkBox3;

    private JComboBox nextFkBox4;

    private JComboBox nextFkBox5;

    private JCheckBox[] checkList;

    private JComboBox[] mainBoxList;

    private JComboBox[] nextBoxList;

    public PlanEditPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(520, 260));

        this.mainField = new JTextField();
        this.mainField.setBackground(Color.WHITE);
        this.mainField.setEditable(false);
        this.mainField.setBounds(81, 66, 205, 21);
        add(this.mainField);

        this.nextBox = new JComboBox();
        this.nextBox.setBackground(Color.WHITE);
        this.nextBox.setBounds(296, 66, 205, 21);
        add(this.nextBox);

        this.fkCheck1 = new JCheckBox("1");
        this.fkCheck1.setEnabled(false);
        this.fkCheck1.setSelected(true);
        this.fkCheck1.setBounds(6, 105, 51, 23);
        add(this.fkCheck1);

        this.mainFkBox1 = new JComboBox();
        this.mainFkBox1.setBackground(Color.WHITE);
        this.mainFkBox1.setBounds(81, 106, 205, 21);
        add(this.mainFkBox1);

        this.nextFkBox1 = new JComboBox();
        this.nextFkBox1.setBackground(Color.WHITE);
        this.nextFkBox1.setBounds(296, 106, 205, 21);
        add(this.nextFkBox1);

        this.fkCheck2 = new JCheckBox("2");
        this.fkCheck2.setBounds(6, 136, 51, 23);
        add(this.fkCheck2);

        this.mainFkBox2 = new JComboBox();
        this.mainFkBox2.setBackground(Color.WHITE);
        this.mainFkBox2.setEnabled(false);
        this.mainFkBox2.setBounds(81, 137, 205, 21);
        add(this.mainFkBox2);

        this.nextFkBox2 = new JComboBox();
        this.nextFkBox2.setBackground(Color.WHITE);
        this.nextFkBox2.setEnabled(false);
        this.nextFkBox2.setBounds(296, 137, 205, 21);
        add(this.nextFkBox2);

        this.fkCheck3 = new JCheckBox("3");
        this.fkCheck3.setBounds(6, 165, 51, 23);
        add(this.fkCheck3);

        this.mainFkBox3 = new JComboBox();
        this.mainFkBox3.setBackground(Color.WHITE);
        this.mainFkBox3.setEnabled(false);
        this.mainFkBox3.setBounds(81, 166, 205, 21);
        add(this.mainFkBox3);

        this.nextFkBox3 = new JComboBox();
        this.nextFkBox3.setBackground(Color.WHITE);
        this.nextFkBox3.setEnabled(false);
        this.nextFkBox3.setBounds(296, 166, 205, 21);
        add(this.nextFkBox3);

        this.fkCheck4 = new JCheckBox("4");
        this.fkCheck4.setBounds(6, 196, 51, 23);
        add(this.fkCheck4);

        this.mainFkBox4 = new JComboBox();
        this.mainFkBox4.setBackground(Color.WHITE);
        this.mainFkBox4.setEnabled(false);
        this.mainFkBox4.setBounds(81, 197, 205, 21);
        add(this.mainFkBox4);

        this.nextFkBox4 = new JComboBox();
        this.nextFkBox4.setBackground(Color.WHITE);
        this.nextFkBox4.setEnabled(false);
        this.nextFkBox4.setBounds(296, 197, 205, 21);
        add(this.nextFkBox4);

        this.fkCheck5 = new JCheckBox("5");
        this.fkCheck5.setBounds(6, 227, 51, 23);
        add(this.fkCheck5);

        this.mainFkBox5 = new JComboBox();
        this.mainFkBox5.setBackground(Color.WHITE);
        this.mainFkBox5.setEnabled(false);
        this.mainFkBox5.setBounds(81, 228, 205, 21);
        add(this.mainFkBox5);

        this.nextFkBox5 = new JComboBox();
        this.nextFkBox5.setBackground(Color.WHITE);
        this.nextFkBox5.setEnabled(false);
        this.nextFkBox5.setBounds(296, 228, 205, 21);
        add(this.nextFkBox5);

        this.checkList = new JCheckBox[5];
        this.checkList[0] = this.fkCheck1;
        this.checkList[1] = this.fkCheck2;
        this.checkList[2] = this.fkCheck3;
        this.checkList[3] = this.fkCheck4;
        this.checkList[4] = this.fkCheck5;
        this.mainBoxList = new JComboBox[5];
        this.mainBoxList[0] = this.mainFkBox1;
        this.mainBoxList[1] = this.mainFkBox2;
        this.mainBoxList[2] = this.mainFkBox3;
        this.mainBoxList[3] = this.mainFkBox4;
        this.mainBoxList[4] = this.mainFkBox5;
        this.nextBoxList = new JComboBox[5];
        this.nextBoxList[0] = this.nextFkBox1;
        this.nextBoxList[1] = this.nextFkBox2;
        this.nextBoxList[2] = this.nextFkBox3;
        this.nextBoxList[3] = this.nextFkBox4;
        this.nextBoxList[4] = this.nextFkBox5;

        JLabel mainLabel = new JLabel("Main Table");
        mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setBounds(81, 41, 204, 15);
        add(mainLabel);

        JLabel nextLabel = new JLabel("Related Table");
        nextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nextLabel.setBounds(296, 41, 205, 15);
        add(nextLabel);

        JComboBox mainDbBox = new JComboBox();
        mainDbBox.setBackground(Color.WHITE);
        mainDbBox.setBounds(81, 10, 420, 21);
        add(mainDbBox);
    }

    public void close() {
        try {
            this.da.disconnect();
        }
        catch (SQLException e) {

        }
    }

    public PlanType save() {
        boolean newPlan = this.plan == null;
        if (newPlan) {
            this.plan = new PlanType();
            this.plan.setTaskName((String) this.nextBox.getSelectedItem());
            this.plan.setJoin(new PlanType.Join());
        }

        this.plan.getJoin().getColumn().clear();

        ColumnType col1 = new ColumnType();
        col1.setSource((String) this.mainFkBox1.getSelectedItem());
        col1.setValue((String) this.nextFkBox1.getSelectedItem());
        this.plan.getJoin().getColumn().add(col1);

        if (newPlan) {
            if (this.main.getNext() == null) {
                this.main.setNext(new TaskType.Next());
            }
            this.main.getNext().getPlan().add(this.plan);
        }

        return this.plan;
    }

    public void configure(TaskFactory factory) {
        this.factory = factory;
        try {
            DbServerType db = factory.getTmd().getDatabaseSpace().getDbServer().get(0);
            this.da = (DataAccessor) Class.forName(db.getDbType()).newInstance();
            this.da.initial(db, factory.getTables());
            this.da.connect();
        }
        catch (Exception e) {

        }
    }

    public void load(TaskType main) {
        this.main = main;
        this.next = null;
        this.plan = null;

        this.mainFkBox1.removeAllItems();
        this.mainFkBox2.removeAllItems();
        this.mainFkBox3.removeAllItems();
        this.mainFkBox4.removeAllItems();
        this.mainFkBox5.removeAllItems();

        this.nextFkBox1.removeAllItems();
        this.nextFkBox2.removeAllItems();
        this.nextFkBox3.removeAllItems();
        this.nextFkBox4.removeAllItems();
        this.nextFkBox5.removeAllItems();

        try {
            this.mainField.setText(main.getSourceSelect().getTable());
            List<ColumnType> mainCols = this.da.prepareColumns(main.getSourceSelect().getTable());
            for (ColumnType col : mainCols) {
                this.mainFkBox1.addItem(col.getValue());
                this.mainFkBox2.addItem(col.getValue());
                this.mainFkBox3.addItem(col.getValue());
                this.mainFkBox4.addItem(col.getValue());
                this.mainFkBox5.addItem(col.getValue());
            }

            this.next = null;
            this.nextBox.removeAllItems();
            this.nextBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent evt) {
                    nextSelected();
                }

            });
            for (TaskType task : this.factory.getTasks().getTask()) {
                this.nextBox.addItem(task.getName());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        updateUI();
    }

    public void load(TaskType main, PlanType plan) {
        this.main = main;
        this.next = this.factory.getTask(plan.getTaskName());
        this.plan = plan;

        this.mainFkBox1.removeAllItems();
        this.mainFkBox2.removeAllItems();
        this.mainFkBox3.removeAllItems();
        this.mainFkBox4.removeAllItems();
        this.mainFkBox5.removeAllItems();

        this.nextFkBox1.removeAllItems();
        this.nextFkBox2.removeAllItems();
        this.nextFkBox3.removeAllItems();
        this.nextFkBox4.removeAllItems();
        this.nextFkBox5.removeAllItems();

        try {
            this.mainField.setText(this.main.getName());
            List<ColumnType> mainCols = this.da.prepareColumns(this.main.getSourceSelect().getTable());
            for (ColumnType col : mainCols) {
                this.mainFkBox1.addItem(col.getValue());
                this.mainFkBox2.addItem(col.getValue());
                this.mainFkBox3.addItem(col.getValue());
                this.mainFkBox4.addItem(col.getValue());
                this.mainFkBox5.addItem(col.getValue());
            }

            this.nextBox.removeAllItems();
            this.nextBox.addItem(this.next.getName());
            List<ColumnType> nextCols = this.da.prepareColumns(this.next.getSourceSelect().getTable());
            for (ColumnType col : nextCols) {
                this.nextFkBox1.addItem(col.getValue());
                this.nextFkBox2.addItem(col.getValue());
                this.nextFkBox3.addItem(col.getValue());
                this.nextFkBox4.addItem(col.getValue());
                this.nextFkBox5.addItem(col.getValue());
            }

            int i = 0;
            for (int x = plan.getJoin().getColumn().size(); i < x; i++) {
                ColumnType fk = plan.getJoin().getColumn().get(i);
                this.checkList[i].setSelected(true);
                this.mainBoxList[i].setSelectedItem(fk.getSource());
                this.mainBoxList[i].setEnabled(true);
                this.nextBoxList[i].setSelectedItem(fk.getValue());
                this.nextBoxList[i].setEnabled(true);
            }
            for (; i < 5; i++) {
                this.checkList[i].setSelected(false);
                this.mainBoxList[i].setEnabled(false);
                this.nextBoxList[i].setEnabled(false);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        updateUI();
    }

    private void nextSelected() {
        this.nextFkBox1.removeAllItems();
        this.nextFkBox2.removeAllItems();
        this.nextFkBox3.removeAllItems();
        this.nextFkBox4.removeAllItems();
        this.nextFkBox5.removeAllItems();

        try {
            this.next = this.factory.getTask((String) this.nextBox.getSelectedItem());
            List<ColumnType> nextCols = this.da.prepareColumns(this.next.getSourceSelect().getTable());
            for (ColumnType col : nextCols) {
                this.nextFkBox1.addItem(col.getValue());
                this.nextFkBox2.addItem(col.getValue());
                this.nextFkBox3.addItem(col.getValue());
                this.nextFkBox4.addItem(col.getValue());
                this.nextFkBox5.addItem(col.getValue());
            }
        }
        catch (Exception ex) {

        }
    }
}
