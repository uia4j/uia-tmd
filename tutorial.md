# Example

Suggest that there are two tables, one is __employee__, another is __employee_manhour__. Structure and relation of these two tables are below:
### employee
* id - Primary key.
* name
* birthday
* sex

### employee_manhour
* id - Primary key.
* employee - Foreign key to id of __employee__.
* time_in
* time_leave

When one employee retires，system need to move his personal data from "local1" database to "local2".

# Design

## XML

* Define "Retire" job to run "EMPLOYEE" task which moves personal data from "local1" to "local2".
```xml
<jobSpace>
    <job name="Retire" source="local1" target="local2">
        <item taskName="EMPLOYEE" />
    </job>
</jobSpace>
```

* Define "EMPLOYEE" task to handle data in employee table, and link "MANHOUR" task. The relation between "EMPLOYEE" and "MANHOUR" is __employee__.id=__employee_manhour__.employee
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

* Define "MANHOUR" task to handle data in employee_manhour table.
```xml
<task name="MANHOUR">
    <sourceSelect table="employee_manhour" />
    <targetUpdate />
</task>
```

* Define data sources "local1" & "local2":
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

Full XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tmd>
    <jobSpace>
        <job name="Retire" source="local1" target="local2">
            <item taskName="EMPLOYEE" />
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
When one whose id is __0098712__ retired, your application can create "Retire" job and execute with criteria "id=0098712" and backup data of this employee.

```java
TaskFactory factory = new TaskFactory(new File("sample.xml"));
JobRunner runner = factory.createRunner("Retire");
runner.run("id='0098712'");
```
