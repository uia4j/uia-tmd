package uia.tmd.ui.edit;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import uia.tmd.IdleAccess;
import uia.tmd.access.HanaAccess;
import uia.tmd.access.MSSQLAccess;
import uia.tmd.access.ORAAccess;
import uia.tmd.access.PGSQLAccess;
import uia.tmd.access.PIDBAccess;
import uia.tmd.model.xml.DatabaseType;

public class DatabaseEditPanel extends JPanel {

    private static final long serialVersionUID = -4590021905345159475L;

    private JTextField nameField;

    private JTextField addressField;

    private JSpinner portField;

    private JTextField userField;

    private JTextField pwdField;

    private JTextField dbNameField;

    private JComboBox<String> dbTypeBox;

    private DatabaseType database;

    public DatabaseEditPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(400, 200));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        nameLabel.setBounds(10, 13, 97, 15);
        add(nameLabel);

        this.nameField = new JTextField();
        this.nameField.setBounds(117, 10, 178, 21);
        add(this.nameField);

        JLabel dbNameLabel = new JLabel("Database Name");
        dbNameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        dbNameLabel.setBounds(10, 75, 97, 15);
        add(dbNameLabel);

        this.dbNameField = new JTextField();
        this.dbNameField.setBounds(117, 72, 178, 21);
        add(this.dbNameField);

        JLabel addressLabel = new JLabel("Server Address");
        addressLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        addressLabel.setBounds(10, 44, 97, 15);
        add(addressLabel);

        this.addressField = new JTextField();
        this.addressField.setBounds(117, 41, 178, 21);
        add(this.addressField);

        this.portField = new JSpinner();
        this.portField.setModel(new SpinnerNumberModel(1433, 1, 65535, 1));
        this.portField.setValue(1433);
        this.portField.setBounds(305, 41, 71, 21);
        add(this.portField);

        JLabel userLabel = new JLabel("User");
        userLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        userLabel.setBounds(10, 106, 97, 15);
        add(userLabel);

        this.userField = new JTextField();
        this.userField.setBounds(117, 103, 178, 21);
        add(this.userField);

        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        pwdLabel.setBounds(10, 137, 97, 15);
        add(pwdLabel);

        this.pwdField = new JTextField();
        this.pwdField.setBounds(117, 134, 178, 21);
        add(this.pwdField);

        JLabel dbTypeLabel = new JLabel("Database Type");
        dbTypeLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        dbTypeLabel.setBounds(10, 168, 97, 15);
        add(dbTypeLabel);

        this.dbTypeBox = new JComboBox<String>();
        this.dbTypeBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Idle", "Hana", "SQL Server", "Oracle", "PostgreSQL" }));
        this.dbTypeBox.setSelectedIndex(0);
        this.dbTypeBox.setBounds(117, 165, 178, 21);
        add(this.dbTypeBox);
    }

    public DatabaseType save() {
        this.database.setId(this.nameField.getText());
        this.database.setHost(this.addressField.getText());
        this.database.setPort(((Integer) this.portField.getValue()).intValue());
        this.database.setDbName(this.dbNameField.getText());
        this.database.setUser(this.userField.getText());
        this.database.setPassword(this.pwdField.getText());
        switch (this.dbTypeBox.getSelectedIndex()) {
            case 1:
                this.database.setDriverClass(HanaAccess.class.getName());
                break;
            case 2:
                this.database.setDriverClass(MSSQLAccess.class.getName());
                break;
            case 3:
                this.database.setDriverClass(ORAAccess.class.getName());
                break;
            case 4:
                this.database.setDriverClass(PGSQLAccess.class.getName());
                break;
            default:
                this.database.setDriverClass(IdleAccess.class.getName());
        }
        return this.database;
    }

    public void load(DatabaseType dbServer) {
        this.database = dbServer;
        this.nameField.setText(this.database.getId());
        this.addressField.setText(this.database.getHost());
        this.portField.setValue(this.database.getPort());
        this.dbNameField.setText(this.database.getDbName());
        this.userField.setText(this.database.getUser());
        this.pwdField.setText(this.database.getPassword());
        if (HanaAccess.class.getName().equals(this.database.getDriverClass())) {
            this.dbTypeBox.setSelectedIndex(1);
        }
        else if (MSSQLAccess.class.getName().equals(this.database.getDriverClass())) {
            this.dbTypeBox.setSelectedIndex(2);
        }
        else if (ORAAccess.class.getName().equals(this.database.getDriverClass())) {
            this.dbTypeBox.setSelectedIndex(3);
        }
        else if (PGSQLAccess.class.getName().equals(this.database.getDriverClass())) {
            this.dbTypeBox.setSelectedIndex(4);
        }
        else if (PIDBAccess.class.getName().equals(this.database.getDriverClass())) {
            this.dbTypeBox.setSelectedIndex(5);
        }
        else {
            this.dbTypeBox.setSelectedIndex(0);
        }
    }
}
