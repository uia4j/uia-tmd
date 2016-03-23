package uia.tmd.ui.edit;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import uia.tmd.model.xml.DbServerType;

public class DbServerEditPanel extends JPanel {

    private static final long serialVersionUID = -4590021905345159475L;

    private JTextField nameField;

    private JTextField addressField;

    private JSpinner portField;

    private JTextField userField;

    private JTextField pwdField;

    private JTextField dbNameField;

    private JComboBox dbTypeBox;

    private DbServerType dbServer;

    public DbServerEditPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(400, 200));

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(10, 13, 77, 15);
        add(nameLabel);

        this.nameField = new JTextField();
        this.nameField.setBounds(97, 10, 198, 21);
        add(this.nameField);

        JLabel dbNameLabel = new JLabel("Database Name");
        dbNameLabel.setBounds(10, 75, 77, 15);
        add(dbNameLabel);

        this.dbNameField = new JTextField();
        this.dbNameField.setBounds(97, 72, 198, 21);
        add(this.dbNameField);

        JLabel addressLabel = new JLabel("Server Address");
        addressLabel.setBounds(10, 44, 77, 15);
        add(addressLabel);

        this.addressField = new JTextField();
        this.addressField.setBounds(97, 41, 198, 21);
        add(this.addressField);

        this.portField = new JSpinner();
        this.portField.setModel(new SpinnerNumberModel(1433, 1, 65535, 1));
        this.portField.setValue(1433);
        this.portField.setBounds(305, 41, 71, 21);
        add(this.portField);

        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(10, 106, 77, 15);
        add(userLabel);

        this.userField = new JTextField();
        this.userField.setBounds(97, 103, 198, 21);
        add(this.userField);

        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setBounds(10, 137, 77, 15);
        add(pwdLabel);

        this.pwdField = new JTextField();
        this.pwdField.setBounds(97, 134, 198, 21);
        add(this.pwdField);

        JLabel dbTypeLabel = new JLabel("Database Type");
        dbTypeLabel.setBounds(10, 168, 77, 15);
        add(dbTypeLabel);

        this.dbTypeBox = new JComboBox();
        this.dbTypeBox.setModel(new DefaultComboBoxModel(new String[] { "Mircosoft SQL Server", "PostgreSQL", "PI OLEDB" }));
        this.dbTypeBox.setSelectedIndex(0);
        this.dbTypeBox.setBounds(97, 165, 198, 21);
        add(this.dbTypeBox);
    }

    public DbServerType save() {
        this.dbServer.setId(this.nameField.getText());
        this.dbServer.setHost(this.addressField.getText());
        this.dbServer.setPort(((Integer) this.portField.getValue()).intValue());
        this.dbServer.setDbName(this.dbNameField.getText());
        this.dbServer.setUser(this.userField.getText());
        this.dbServer.setPassword(this.pwdField.getText());
        switch (this.dbTypeBox.getSelectedIndex()) {
            case 1:
                this.dbServer.setDbType("PGSQL");
                break;
            case 2:
                this.dbServer.setDbType("PI");
                break;
            default:
                this.dbServer.setDbType("MSSQL");
        }
        return this.dbServer;
    }

    public void load(DbServerType dbServer) {
        this.dbServer = dbServer;
        this.nameField.setText(this.dbServer.getId());
        this.addressField.setText(this.dbServer.getHost());
        this.portField.setValue(this.dbServer.getPort());
        this.dbNameField.setText(this.dbServer.getDbName());
        this.userField.setText(this.dbServer.getUser());
        this.pwdField.setText(this.dbServer.getPassword());
        if ("PGSQL".equalsIgnoreCase(this.dbServer.getDbType())) {
            this.dbTypeBox.setSelectedIndex(1);
        }
        else if (("PI".equalsIgnoreCase(this.dbServer.getDbType()))) {
            this.dbTypeBox.setSelectedIndex(2);
        }
        else {
            this.dbTypeBox.setSelectedIndex(0);
        }
    }
}
