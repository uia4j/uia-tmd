# Example

Suggest that there are two tables in system. One is __employee__, another is __employee_manhour__. Structure and relation of tables are below:
### table: __employee__
* id - Primary key.

### table: __employee_manhour__
* id - Primary key.
* employee - Foreign key to id of __employee__.

When one employee retires，system need to move his personal data from "local1" database to "local2".

# Design

## XML

* Define "Retire" executor to run "job1" which moves data of employee from "local1" to "local2".
```xml
<executorSpace>
    <executor name="Retire" source="local1" target="local2" task="job1" />
</executorSpace>
```

* Define "job1" task to handle table of employee, and make "job2" to be next task. The relation between "job1" and "job2" is __employee__.id=__employee_manhour__.employee
```xml
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

* Define "job2" task to handle table of employee_manhour.
```xml
<task name="job2">
    <sourceSelect table="employee_manhour" />
    <targetUpdate />
</task>
```

* Define data sources of "local1" & "local2"
```xml
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

Full XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tmd>
	<executorSpace>
		<executor name="Retire" source="local1" target="local2" task="job1" />
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
            <sourceSelect table="employee_manhour" />
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
When one whose id is __0098712__ retired, system creates "Retire" executor to finish the job describes above.

"Retire" exeuctes "job1", and "job1" queries data from __employee__ which id is "0098712".

```java
TaskFactory factory = new TaskFactory(new File("sample.xml"));
TaskExecutor executor = factory.createExecutor("Retire");

executor.run(new Where[] { new WhereEq("id", "0098712") });
```
