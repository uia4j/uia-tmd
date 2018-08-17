package ui.tmd.zztop.qtz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uia.tmd.zztop.db.QtzClock;

import ui.tmd.zztop.QtzJobRunner;

public class QtzJob implements Job {

    private static final Logger LOGGER = LogManager.getLogger(QtzJob.class);

    public QtzJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            QtzClock qc = (QtzClock) context.getJobDetail().getJobDataMap().get(QtzClock.class.getSimpleName());
            LOGGER.info("zzt> " + qc.getTmdJobBo() + "> run by quartz:" + context.getFireTime() + "), next:" + context.getNextFireTime());

            QtzJobRunner runner = new QtzJobRunner();
            runner.run(qc.getTmdJobBo(), null);
        }
        catch (Exception e) {

        }
    }
}
