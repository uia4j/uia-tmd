package uia.tmd.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import uia.tmd.TaskExecutor;
import uia.tmd.TaskExecutorListener;
import uia.tmd.Where;
import uia.tmd.ui.edit.ExecutorSimpleCriteriaPanel;

public class ExecutorRunPanel extends JPanel implements TaskExecutorListener {

    private static Logger LOGGER = Logger.getLogger(ExecutorRunPanel.class);

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

    public void run(final TaskExecutor te) {
        this.messageArea.setText("");
        final ExecutorSimpleCriteriaPanel panel = new ExecutorSimpleCriteriaPanel();
        if (JOptionPane.showConfirmDialog(this, panel) != JOptionPane.YES_OPTION) {
            return;
        }
        te.addListener(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                FileAppender appender = createAppender(te.getName() + "_" + System.currentTimeMillis());
                LOGGER.addAppender(appender);
                try {
                    ExecutorRunPanel.this.frame.setExecutable(false);
                    te.run(panel.save().toArray(new Where[0]));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    ExecutorRunPanel.this.frame.setExecutable(true);
                    LOGGER.removeAppender(appender);
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

    private void append(final TaskExecutorEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.messageArea.setText(this.messageArea.getText() + String.format("RUN> %s (%s)\n",
                    evt.path,
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
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        LOGGER.info(String.format("QRY> %s(%s)\n%50s - %s\n%50s - %s", evt.path, evt.count, "", evt.sql, "", evt.criteria));
        appendMessage("");
        append(evt);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        LOGGER.info(String.format("DEL> %s(%s)\n%50s - %s\n%50s - %s", evt.path, evt.count, "", evt.sql, "", evt.criteria));
    }

    @Override
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt) {
        LOGGER.info(String.format("INS> %s\n%50s - %s", evt.path, "", evt.sql));
        append(evt);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        LOGGER.info(String.format("DEL> %s(%s)\n%50s - %s\n%50s - %s", evt.path, evt.count, "", evt.sql, "", evt.criteria));
        append(evt);
    }

    @Override
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex) {
        LOGGER.error(String.format("%s> %s", evt.path, evt.sql));
        LOGGER.error(String.format("%s> %s", evt.path, evt.criteria), ex);
        appendMessage(String.format("RUN> %s failed\n", evt.path));
        appendMessage(ex.getMessage() + "\n");
    }

    @Override
    public void done(TaskExecutor executor) {

    }

    private FileAppender createAppender(String fileName) {
        FileAppender appender = new FileAppender();
        appender.setAppend(true);
        appender.setName("tx");
        appender.setFile("hist\\" + fileName + ".txt");

        PatternLayout layOut = new PatternLayout();
        layOut.setConversionPattern("%-5p %d{HH:mm:ss} %-35c - %m%n");
        appender.setLayout(layOut);
        appender.activateOptions();     // must call here

        return appender;
    }
}
