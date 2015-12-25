# Example

Suggest that there are two tables in system. One is about employee, another is about manhour of employee. Structure and relation of tables are below:
### table: employee
* id - Primary key.

### table: employee_manhour
* id - Primary key.
* employee - foreign key to id of employee.

When one employee retires，system need to move his personal data from "local1" database to "local2".

# Design

## XML

* Define "Retire" executor to run "job1" which moves data of employee from "local1" to "local2".
```
<executorSpace>
    <executor name="Retire" source="local1" target="local2" task="job1" />
</executorSpace>
```

* Define "job1" task to handle table of employee, and make "job2" to be next task. The relation between "job1" and "job2" is employee.id=employee_manhour.employee
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

* Define "job2" task to handle table of employee_manhour.
```
<task name="job2">
    <sourceSelect table="employee_manhour" />
    <targetUpdate />
</task>
```

* Define data sources of "local1" & "local2"
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

Full XML:

```

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
When one whose id is 0098712 retires, system creates "Retire" executor to finish the job describes above.

"Retire" exeuctes "job1", and "job1" handles table of employee which primary key is id, so the criteria is id="0098712".

```
TaskFactory factory = new TaskFactory(new File("sample.xml"));
TaskExecutor executor = factory.createExecutor("Retire");

TreeMap<String, Object> where = new TreeMap<String, Object>();
where.put("id", "0098712");

executor.run(where);
```
