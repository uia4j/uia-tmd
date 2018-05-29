package uia.tmd.ui.edit;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import uia.tmd.Where;
import uia.tmd.WhereEq;

public class ExecutorSimpleCriteriaPanel extends JPanel {

    private static final long serialVersionUID = 4905316473683792343L;

    private JTextArea txtrHandle;

    public ExecutorSimpleCriteriaPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(430, 200));

        JLabel paramLabel = new JLabel("Criteria");
        paramLabel.setBounds(10, 10, 157, 15);
        add(paramLabel);

        this.txtrHandle = new JTextArea();
        this.txtrHandle.setText("SITE=1020");
        this.txtrHandle.setBorder(BorderFactory.createEtchedBorder(1));
        this.txtrHandle.setBounds(10, 35, 410, 155);
        add(this.txtrHandle);
    }

    public List<Where> save() {
        ArrayList<Where> wheres = new ArrayList<Where>();

        String text = this.txtrHandle.getText();
        text = text.replace("\r", "");

        String[] values = text.split("\n");
        for (String value : values) {
            String[] kv = value.split("=");
            if (kv.length < 2) {
                continue;
            }
            wheres.add(new WhereEq(kv[0], kv[1]));
        }

        return wheres;
    }

}
