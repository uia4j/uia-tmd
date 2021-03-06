package uia.tmd.zztop.qtz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uia.tmd.zztop.cmd.SyncCmd;
import uia.tmd.zztop.db.QtzClock;

public class QtzJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(QtzJob.class);

    public QtzJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            QtzClock qc = (QtzClock) context.getJobDetail().getJobDataMap().get(QtzClock.class.getSimpleName());
            LOGGER.info("zzt> " + qc.getTmdJobBo() + "> run by quartz:" + context.getFireTime() + "), next:" + context.getNextFireTime());

            SyncCmd cmd = new SyncCmd();
            cmd.run("conf/tmd_plans.xml", qc.getTmdJobBo(), null, null);
        }
        catch (Exception e) {

        }
    }
}
