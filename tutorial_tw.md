# 範例說明

假定系統中有兩個表格，紀錄員工與出勤紀錄。結構與關連如下：
### table: employee
* id - 主鍵。

### table: employeeManhour
* id - 主鍵。
* employee - 關聯 employee 主鍵。

當員工退休後，將其資料從資料庫 local1 搬移到 local2。

# 設計

## XML

* 定義 "Retire" Executor，執行工作 job1，將資料從 local1 搬移到 local2。
```
<executorSpace>
    <executor name="Retire" source="local1" target="local2" task="employee" />
</executorSpace>
```

* 定義 "job1" 處理 employee 表格。以及與 job2 表格 employeeManhour 的關聯。
```
<task name="job1">
    <sourceSelect table="employee" />
    <targetUpdate />
    <nexts>
        <plan name="job2">
            <join>
                <column source="id">employee</column>
            </join>
        </plan>
    </nexts>
</task>
```

* 定義 "job2" 處理 employeeManhour 表格。
```
<task name="job2">
    <sourceSelect table="employeeManhour" />
    <targetUpdate />
</task>
```

* 定義 local1 & local2 資料庫。
```
<dbServer>
    <id>local1</id>
    <host>localhost</host>
    <port>1433</port>
    <dbName>db1</dbName>
    <user>user1</user>
    <password>1234</password>
    <dbType>MSSQL</dbType>
</dbServer>
<dbServer>
    <id>local2</id>
    <host>localhost</host>
    <port>1433</port>
    <dbName>db2</dbName>
    <user>user2</user>
    <password>1234</password>
    <dbType>MSSQL</dbType>
</dbServer>
```

完整的 XML:

```

<?xml version="1.0" encoding="UTF-8"?>
<tmd>
	<executorSpace>
		<executor name="Retire" source="local1" target="local2" task="employee" />
	</executorSpace>
	<taskSpace>
        <task name="job1">
            <sourceSelect table="employee" />
            <targetUpdate />
            <nexts>
                <plan name="job2">
                    <join>
                        <column source="id">employee</column>
                    </join>
                </plan>
            </nexts>
        </task>
        <task name="job2">
            <sourceSelect table="employeeManhour" />
            <targetUpdate />
        </task>
    </taskSpace>
    <tableSpace />
    <databaseSpace>
        <dbServer>
            <id>local1</id>
            <host>localhost</host>
            <port>1433</port>
            <dbName>db1</dbName>
            <user>user1</user>
            <password>1234</password>
            <dbType>MSSQL</dbType>
        </dbServer>
        <dbServer>
            <id>local2</id>
            <host>localhost</host>
            <port>1433</port>
            <dbName>db2</dbName>
            <user>user2</user>
            <password>1234</password>
            <dbType>MSSQL</dbType>
        </dbServer>
    </databaseSpace>
</tmd>
```

## java code
```
TaskFactory factory = new TaskFactory(new File("sample.xml"));
TaskExecutor executor = factory.createExecutor("job1");

TreeMap<String, Object> where = new TreeMap<String, Object>();
where.put("ID", "0098712");

executor.run();
```
