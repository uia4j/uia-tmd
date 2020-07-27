ZZTOP
===

ZZTOP 專案目標是提供一個以命令列 (CLI) 的方式執行同步作業，並具備歷程記錄的功能供後續追蹤。


## 無紀錄執行 

命令：java -jar tmd-zztop.jar `sync` -f {A} -j {B} 

* sync - 執行同步作業
* {A} - XML 檔案位置
* {B} - 執行的 job 名稱


例如，同步下面 XML 中的 MASTER_DATA：

`java -jar tmd-zztop.jar sync -f my.mxl -j MASTER_DATA`.



```xml
<?xml version="1.0" encoding="UTF-8"?>
<tmd xmlns="http://tmd.uia/model/xml">
    <jobSpace>
        <job name="MASTER_DATA" source="DEV" target="PROD">
            <item taskName="ma_item" />
            <item taskName="ma_item_group" />
            <item taskName="ma_item_part" />
            <item taskName="ma_item_part_replace" />
        </job>
    </jobSpace>
    <taskSpace>
        <task name="ma_item">
            <sourceSelect table="ma_item" />
            <targetUpdate />
        </task>
        <task name="ma_item_group">
            <sourceSelect table="ma_item_group" />
            <targetUpdate />
        </task>
        <task name="ma_item_part">
            <sourceSelect table="ma_item_part" />
            <targetUpdate />
        </task>
        <task name="ma_item_part_replace">
            <sourceSelect table="ma_item_part_replace" />
            <targetUpdate />
        </task>
        <task name="ma_schedule">
            <sourceSelect table="ma_schedule" />
            <targetUpdate />
        </task>
        <task name="ma_schedule_item">
            <sourceSelect table="ma_schedule_item" />
            <targetUpdate />
        </task>
    </taskSpace>
    <tableSpace />
    <databaseSpace>
        <database>
            <id>IDLE</id>
            <host>0.0.0.0</host>
            <port>0</port>
            <dbName></dbName>
            <user></user>
            <password></password>
            <driverClass>uia.tmd.IdleAccess</driverClass>
        </database>
        <database>
            <id>DEV</id>
            <host>192.168.0.1</host>
            <port>5432</port>
            <dbName>db_dev</dbName>
            <user>user</user>
            <password>1234567890</password>
            <driverClass>uia.tmd.access.PGSQLAccess</driverClass>
        </database>
        <database>
            <id>PROD</id>
            <host>192.168.0.2</host>
            <port>5432</port>
            <dbName>dev_prod</dbName>
            <user>user</user>
            <password>1234567890</password>
            <driverClass>uia.tmd.access.HanaAccess</driverClass>
        </database>
    </databaseSpace>
</tmd>
```
