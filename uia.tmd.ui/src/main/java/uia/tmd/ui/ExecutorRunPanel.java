package uia.tmd.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.JobRunner;
import uia.tmd.TaskListener;
import uia.tmd.ui.edit.TaskExecutorPanel;

public class ExecutorRunPanel extends JPanel implements TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

    private static final long serialVersionUID = -6194042035824829196L;

    private JTextArea messageArea;

    private MainFrame frame;

    public ExecutorRunPanel() {
        setLayout(new BorderLayout(0, 0));

        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setPreferredSize(new Dimension(300, 30));
        add(toolbarPanel, BorderLayout.NORTH);

        JScrollPane messageScrol = new JScrollPane();
        messageScrol.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        messageScrol.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(messageScrol, BorderLayout.CENTER);

        this.messageArea = new JTextArea();
        this.messageArea.setEditable(false);
        this.messageArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        messageScrol.setViewportView(this.messageArea);
    }

    public MainFrame getFrame() {
        return this.frame;
    }

    public void setFrame(MainFrame frame) {
        this.frame = frame;
    }

    public void run(final JobRunner te) {
        this.messageArea.setText("");
        final TaskExecutorPanel panel = new TaskExecutorPanel();
        if (JOptionPane.showConfirmDialog(this, panel) != JOptionPane.YES_OPTION) {
            return;
        }
        te.addTaskListener(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ExecutorRunPanel.this.frame.setExecutable(false);
                    te.run(panel.save());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    ExecutorRunPanel.this.frame.setExecutable(true);
                }
            }
        }).start();
    }

    private void appendMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.messageArea.setText(this.messageArea.getText() + message + "\n");
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    appendMessage(message);
                }
            });
        }
    }

    private void append(final TaskEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.messageArea.setText(this.messageArea.getText() + String.format("RUN> %s (%s)\n",
                    evt.parentPath + "/" + evt.taskName,
                    evt.count));
            this.messageArea.setText(this.messageArea.getText() + String.format("     %s\n", evt.sql));
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    append(evt);
                }
            });
        }
    }

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        LOGGER.info(String.format("QRY> %s", evt));
        appendMessage("");
        append(evt);
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {
        LOGGER.info(String.format("DEL> %s", evt));
    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
        LOGGER.info(String.format("INS>  %s", evt));
        append(evt);
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {
        LOGGER.info(String.format("DEL>  %s", evt));
        append(evt);
    }

    @Override
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
        LOGGER.error(String.format("%s> %s", evt), ex);
        appendMessage(String.format("RUN> %s failed\n", evt.parentPath + "/" + evt.taskName));
        appendMessage(ex.getMessage() + "\n");
    }

    @Override
    public void taskDone(JobRunner jobRunner) {
    }
}
