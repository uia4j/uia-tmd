package ui.tmd.zztop.qtz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import uia.tmd.zztop.db.QtzClock;
import uia.tmd.zztop.db.dao.QtzClockDao;

import ui.tmd.zztop.DB;

public class QtzJobFactory {

    private static final Logger LOGGER = LogManager.getLogger(QtzJobFactory.class);

    private Scheduler scheduler;

    private TreeMap<String, BiFunction<TriggerBuilder<Trigger>, QtzClock, TriggerBuilder<SimpleTrigger>>> functions;

    public QtzJobFactory() {
        this.functions = new TreeMap<String, BiFunction<TriggerBuilder<Trigger>, QtzClock, TriggerBuilder<SimpleTrigger>>>();
        this.functions.put("day", this::byDay);
        this.functions.put("min", this::byMin);
        this.functions.put("hour", this::byHour);
    }

    public void start() throws SchedulerException {
        this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        this.scheduler.start();
        create();
    }

    public void shutdown() throws SchedulerException {
        this.scheduler.shutdown();
    }

    private void create() throws SchedulerException {
        String group = "Tmd";

        List<QtzClock> qcs = null;
        try (Connection conn = DB.create()) {
            QtzClockDao dao = new QtzClockDao(conn);
            qcs = dao.selectAll();

        }
        catch (Exception ex) {
            throw new SchedulerException(ex);
        }

        LOGGER.info("zzt> QtzClock found:" + qcs.size());
        for (QtzClock qc : qcs) {
            if (qc.getEndTime() != null && qc.getEndTime().getTime() <= System.currentTimeMillis()) {
                continue;
            }

            LOGGER.info(String.format("zzt> %s> period:%s %s, from:%s to %s, triggerNow:%s",
                    qc.getTmdJobBo(),
                    qc.getClockInterval(),
                    qc.getClockType(),
                    qc.getStartTime(),
                    qc.getEndTime(),
                    qc.getTriggerStartup()));

            // JobDataMap
            JobDataMap jobData = new JobDataMap();
            jobData.put(QtzClock.class.getSimpleName(), qc);

            // Define the job
            JobDetail job = newJob(QtzJob.class)
                    .setJobData(jobData)
                    .withIdentity(qc.getId(), group)
                    .build();

            // Trigger the job to run
            BiFunction<TriggerBuilder<Trigger>, QtzClock, TriggerBuilder<SimpleTrigger>> f = this.functions.get(qc.getClockType().toLowerCase());
            if (f == null) {
                LOGGER.warn("zzt> " + qc.getTmdJobBo() + "> clockType not found:" + qc.getClockType().toLowerCase());
                continue;
            }

            Trigger trigger = f.apply(newTrigger().withIdentity(qc.getId(), group), qc)
                    .build();

            if (trigger != null) {
                // Tell quartz to schedule the job using our trigger
                this.scheduler.scheduleJob(job, trigger);
            }

            if ("Y".equalsIgnoreCase(qc.getTriggerStartup())) {
                JobDetail job0 = newJob(QtzJob.class)
                        .setJobData(jobData)
                        .withIdentity(qc.getId() + "_now", group)
                        .build();
                this.scheduler.scheduleJob(
                        job0,
                        newTrigger()
                                .withIdentity(qc.getId() + "_now", group)
                                .startNow()
                                .build());
            }
        }
    }

    private TriggerBuilder<SimpleTrigger> byDay(TriggerBuilder<Trigger> builder, QtzClock clock) {
        int hrs = clock.getClockInterval().intValue() * 24;
        Date startTime = calculateStartTime(clock, 3600000L * hrs);

        if (startTime == null) {
            LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byDay, startTime: " + startTime + ", out of date");
            return null;
        }

        LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byDay, startTime: " + startTime);
        if (clock.getEndTime() == null) {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(hrs)
                            .repeatForever());
        }
        else {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(hrs))
                    .endAt(clock.getEndTime());
        }
    }

    private TriggerBuilder<SimpleTrigger> byMin(TriggerBuilder<Trigger> builder, QtzClock clock) {
        int mins = clock.getClockInterval().intValue();
        Date startTime = calculateStartTime(clock, 60000L * mins);

        if (startTime == null) {
            LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byMin, startTime: " + startTime + ", out of date");
            return null;
        }

        LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byMin, startTime: " + startTime);
        if (clock.getEndTime() == null) {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(mins)
                            .repeatForever());
        }
        else {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(mins))
                    .endAt(clock.getEndTime());
        }
    }

    private TriggerBuilder<SimpleTrigger> byHour(TriggerBuilder<Trigger> builder, QtzClock clock) {
        int hrs = clock.getClockInterval().intValue();
        Date startTime = calculateStartTime(clock, 3600000L * hrs);

        if (startTime == null) {
            LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byHour, startTime: " + startTime + ", out of date");
            return null;
        }

        LOGGER.warn("zzt> " + clock.getTmdJobBo() + "> byHour, startTime: " + startTime);
        if (clock.getEndTime() == null) {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(hrs)
                            .repeatForever());
        }
        else {
            return builder
                    .startAt(startTime)
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(hrs))
                    .endAt(clock.getEndTime());
        }
    }

    private Date calculateStartTime(QtzClock clock, long multi) {
        if (clock.getStartTime() == null) {
            return new Date(System.currentTimeMillis() + 5000);
        }

        long time = clock.getStartTime().getTime();
        long now = System.currentTimeMillis();
        while (time < now) {
            time += multi;
        }

        return clock.getEndTime() != null && time > clock.getEndTime().getTime()
                ? null
                : new Date(time);

    }
}
