# XML 設計概要

XML 結構由下面四大區塊組成，描述一個執行單體執行時，進行工作的條件。

* jobSpece - 工作定義。
* taskSpace - 任務定義。
* tableSpace - 表格定義。
* databaseSpace - 資料庫定義。

## jobSpace
由多個工作 (job) 組成，每個工作包含多個項目 (item)，每個項目對應一個任務 (task)。
```xml
<jobSpace>
    <job name="job1" source="svr1" target="svr2">
        <item taskName="T1" />
        <item taskName="T2" />
    </job>
    <job name="job2" source="svr1" target="svr2">
        <item taskName="T3" />
    </job>
</executorSpace>
```    
### tag: job
定義執行個體，描述來源資料庫、目的資料庫和指定執行的工作。
* name - 名稱。
* source - 來源資料庫。參考 databaseSpace 中的資料庫設定。
* target - 目的資料庫。參考 databaseSpace 中的資料庫設定。

### tag: item
定義工作項目，描述對應的任務。
* taskName - 任務名稱。

# taskSpace
由多個任務 (task) 組成。每個任務可包含子任務 (plan)。
```xml
<taskSpace>
    <task name="T1">
        <sourceSelect tableName="table1" />
        <targetUpdate />
        <next>
            <plan taskName="T2" where="xxx=?">
                <param sourceColumn="id" />
            </plan>
        </next>
    </task>
    <task name="T2">
        <sourceSelect tableName="table2" />
        <targetUpdate />
        <next />
    </task>
</taskSpace>
```    
## tag: task
定義任務，描述處理的表格與相關聯任務。
* name - 任務名稱，item 中 taskName 的參考值。

# tableSpace
由多個 table 組成。
```xml
<tableSpace>
    <table name="T1">
        <pk>
            <column>id</column>
        </pk>    
    </table>
    <table name="T2">
        <pk>
            <column>id</column>
        </pk>    
    </table>
</tableSpace>
```
TODO:

# databaseSpace
由多個 dbServer 組成。
```xml
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
```
### tag: database
```xml
```
#### - element:
* id - 識別名稱，executor 中 source 和 target 參數的參考值。
* host - 主機位置
* port - 通訊埠
* dbName - 資料庫名稱。
* user - 使用者帳號
* password - 密碼
* driverClass - 資料庫驅動。
    * __uia.tmd.IdleAccess__ 測試用
    * __uia.tmd.access.HanaAccess__ - SAP HANA
    * __uia.tmd.access.ORAAccess__ - Oracle
    * __uia.tmd.access.PGSQLAccess__ - PostgreSQL
