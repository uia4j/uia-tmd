package uia.tmd.ui.edit;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TaskExecutorPanel extends JPanel {

    private static final long serialVersionUID = 4905316473683792343L;

    private JTextArea statementField;

    public TaskExecutorPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(598, 151));

        JLabel statementLabel = new JLabel("Statement");
        statementLabel.setBounds(10, 10, 157, 15);
        add(statementLabel);

        this.statementField = new JTextArea();
        this.statementField.setText("SITE='1020'");
        this.statementField.setBorder(BorderFactory.createEtchedBorder(1));
        this.statementField.setBounds(10, 35, 578, 106);
        add(this.statementField);
    }

    public String save() {
        String where = this.statementField.getText();
        return where.trim().isEmpty() ? null : where;
    }

}
