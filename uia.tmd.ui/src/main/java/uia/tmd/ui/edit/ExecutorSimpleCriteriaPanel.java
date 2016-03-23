package uia.tmd.ui.edit;

import java.awt.Dimension;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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
        this.txtrHandle.setText("HANDLE=ShopOrderBO:1600,20150928-CFZ-001");
        this.txtrHandle.setBorder(BorderFactory.createEtchedBorder(1));
        this.txtrHandle.setBounds(10, 35, 410, 155);
        add(this.txtrHandle);
    }

    public Map<String, Object> saveParamMap() {
        TreeMap<String, Object> criteria = new TreeMap<String, Object>();

        String text = this.txtrHandle.getText();
        text = text.replace("\r", "");

        String[] values = text.split("\n");
        for (String value : values) {
            String[] kv = value.split("=");
            if (kv.length < 2) {
                continue;
            }
            criteria.put(kv[0], kv[1]);
        }

        return criteria;
    }

}
