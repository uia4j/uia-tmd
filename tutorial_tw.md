# 範例說明

假定系統中有兩個表格，儲存員工 (employee) 個人資料與 出勤紀錄 (employee_manhour)。結構與關連如下：
### table: employee
* id - 主鍵。
* name
* birthday
* sex

### table: employee_manhour
* id - 主鍵。
* employee - 外鍵，關聯至 employee.id。
* time_in
* time_leave

當員工退休時，系統要將其個人資料從資料庫 "local1" 搬移到 "local2"。

# 設計

## XML

* 定義 "Retire" 工作，執行任務 "EMPLOYEE"，將 employee 的資料從 local1 搬移到 local2。
```xml
<jobSpace>
    <job name="Retire" source="local1" target="local2">
        <item taskName="EMPLOYEE" />
    </job>
</jobSpace>
```

* 定義任務 "EMPLOYEE" 處理 employee 表格，並設定任務 "MANHOUR" 緊接在 "EMPLOYEE" 後。"EMPLOYEE" 和 "MANHOUR" 間的關聯是 __employee__.id=__employee_manhour__.employee。
```xml
<task name="EMPLOYEE">
    <sourceSelect table="employee" />
    <targetUpdate />
    <next>
        <plan taskName="MANHOUR" where="employee=?">
            <param sourceColumn="id" />
        </plan>
    </next>
</task>
```

* 定義任務 "MANHOUR" 處理 employee_manhour 表格。
```xml
<task name="MANHOUR">
    <sourceSelect table="employee_manhour" />
    <targetUpdate />
</task>
```

* 定義 "local1" & "local2" 資料庫。
```xml
<database>
    <id>local1</id>
    <host>localhost</host>
    <port>5432</port>
    <dbName>db1</dbName>
    <user>user1</user>
    <password>1234</password>
    <driverClass>uia.tmd.access.PGSQLAccess</driverClass>
</database>
<database>
    <id>local2</id>
    <host>localhost</host>
    <port>5432</port>
    <dbName>db2</dbName>
    <user>user2</user>
    <password>1234</password>
    <dbType>uia.tmd.access.PGSQLAccess</dbType>
</database>
```

完整的 XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tmd>
    <jobSpace>
        <job name="Retire" source="local1" target="local2">
            <item taskName="EMPLOYEE">
            </item>
        </job>
    </jobSpace>
    <taskSpace>
        <task name="EMPLOYEE">
            <sourceSelect table="employee" />
            <targetUpdate />
            <next>
                <plan taskName="MANHOUR" where="employee=?">
                    <param sourceColumn="id" />
                </plan>
            </next>
        </task>
        <task name="MANHOUR">
            <sourceSelect table="employee_manhour" />
            <targetUpdate />
        </task>
    </taskSpace>
    <tableSpace>
    </tableSpace>
    <databaseSpace>
        <database>
            <id>local1</id>
            <host>localhost</host>
            <port>5432</port>
            <dbName>db1</dbName>
            <user>user1</user>
            <password>1234</password>
            <driverClass>uia.tmd.access.PGSQLAccess</driverClass>
        </database>
        <database>
            <id>local2</id>
            <host>localhost</host>
            <port>5432</port>
            <dbName>db2</dbName>
            <user>user2</user>
            <password>1234</password>
            <driverClass>uia.tmd.access.PGSQLAccess</driverClass>
        </database>
    </databaseSpace>
</tmd>
```

## java code
當一個編號為 0098712 的員工退休時，系統建立一個 "Retire" 工作，並依據 "id=0098712" 條件執行，對此員工的資料進行備份。
```java
TaskFactory factory = new TaskFactory(new File("sample.xml"));
JobRunner runner = factory.createRunner("Retire");
runner.run("id='0098712'");
```
