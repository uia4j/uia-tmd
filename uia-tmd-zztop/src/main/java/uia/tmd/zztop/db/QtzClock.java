package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

import uia.dao.annotation.ColumnInfo;
import uia.dao.annotation.TableInfo;

@TableInfo(name = "zzt_qtz_clock")
public class QtzClock {
	
	public static final String KEY = "zzt_qtz_clock";

	@ColumnInfo(name = "id", primaryKey = true)
    private String id;

	@ColumnInfo(name = "start_time")
    private Date startTime;

	@ColumnInfo(name = "end_time")
    private Date endTime;

	@ColumnInfo(name = "clock_type")
    private String clockType;

	@ColumnInfo(name = "clock_interval")
    private Integer clockInterval;

	@ColumnInfo(name = "tmd_job_bo")
    private String tmdJobBo;

	@ColumnInfo(name = "trigger_startup")
    private String triggerStartup;

    public QtzClock() {
        this.id = System.currentTimeMillis() + ":" + UUID.randomUUID().toString();
        this.startTime = new Date();
        this.clockInterval = 1;
        this.clockType = "day";
        this.triggerStartup = "N";
    }

    public QtzClock(QtzClock data) {
        this.id = data.id;
        this.startTime = data.startTime;
        this.endTime = data.endTime;
        this.clockType = data.clockType;
        this.clockInterval = data.clockInterval;
        this.tmdJobBo = data.tmdJobBo;
        this.triggerStartup = data.triggerStartup;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getClockType() {
        return this.clockType;
    }

    public void setClockType(String clockType) {
        this.clockType = clockType;
    }

    public Integer getClockInterval() {
        return this.clockInterval;
    }

    public void setClockInterval(Integer clockInterval) {
        this.clockInterval = clockInterval;
    }

    public String getTmdJobBo() {
        return this.tmdJobBo;
    }

    public void setTmdJobBo(String tmdJobBo) {
        this.tmdJobBo = tmdJobBo;
    }

    public String getTriggerStartup() {
        return this.triggerStartup;
    }

    public void setTriggerStartup(String triggerStartup) {
        this.triggerStartup = triggerStartup;
    }

    @Override
    public QtzClock clone() {
        return new QtzClock(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
