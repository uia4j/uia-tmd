XML 設計概要
===

XML 結構由下面四大區塊組成。

```xml
<tmd xmlns="http://tmd.uia/model/xml">
    <jobSpace />
    <taskSpace />
    <tableSpace />
    <databaseSpace />
</tmd>
```

* jobSpace - 工作定義
* taskSpace - 任務定義
* tableSpace - 表格定義
* databaseSpace - 資料庫定義


## JobSpace

由多個工作 (job) 組成，每個工作可包含多個項目 (item)，每個項目對應一個任務 (task)。

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

### tags
#### job

定義執行個體，描述來源資料庫、目的資料庫和指定執行的工作。

* name - 名稱。
* source - 來源資料庫。參考 databaseSpace 中的資料庫設定。
* target - 目的資料庫。參考 databaseSpace 中的資料庫設定。

#### item

定義工作項目，描述對應的任務。

* taskName - 任務名稱。

## taskSpace

由多個 task 組成。每個 task 可包含一個以上的子任務 (plan)。

```xml
<taskSpace>
    <task name="T1">
        <sourceSelect table="table1" />
        <targetUpdate />
        <next>
            <plan taskName="T2" where="xxx=?">
                <param sourceColumn="id" />
            </plan>
        </next>
    </task>
    <task name="T2">
        <sourceSelect table="table2" />
        <targetUpdate />
        <next />
    </task>
</taskSpace>
```    

### Tags
#### task

定義任務，描述處理的表格與相關聯任務。

* name - 任務名稱，item 中 taskName 的參考值。

#### sourceSelect

定義來源表格

* name - 表格名稱，參考 tableSpace 中的表格定義。

#### targetUpdate

定義更新表格

* name - 表格名稱，參考 tableSpace 中的表格定義。

#### next

定義 task 定義 task 完成後接續的工作。

#### plan

定義接續的工作

* taskName - 任務名稱。
* where - 根據主 task 的 row 資料進行搜尋。
* plan.param sourceColumn - 來源資料的欄位名稱。

### 範例

```xml
<task name="A">
    <sourceSelect table="ORDER" />
    <targetUpdate />
    <next>
        <plan taskName="B" where="DEPT_ID=?">
            <param sourceColumn="ID" />
        </plan>
    </next>
</task>
<task name="B">
    <sourceSelect table="EMP" />
    <targetUpdate />
    <next />
</task>
```

1. 執行 Task: A。

2. Task A 來源表格名稱為 `ORDER`，同步資料為 `select * from ORDER`。

3. 目標表格無描述，故與來源同名為 `ORDER`，對步驟 2 select 到的 rows 執行 `insert ORDER(...) values(...)`。

4. 對 __每一筆 row__ 進行 plan: B，執行時套用 `where DEPT_ID=?`，? 為 row 的 ID 值。

5. Task B 來源表格名稱為 `EMP`，同步資料為 `select * from EMP where DEPT_ID=?`。

6. 目標表格無描述，故與來源同名為 `EMP`，對步驟 5 select 到的 rows 執行 `insert EMP(...) values(...)`。


## tableSpace

由多個 table 組成。主要目的為補充 Primary key，若表格已有設定，可忽略設定。

```xml
<tableSpace>
    <table name="ORDER">
        <pk>
            <column>ID</column>
        </pk>    
    </table>
    <table name="EMP">
        <pk>
            <column>ID</column>
        </pk>    
    </table>
</tableSpace>
```

## databaseSpace

由多個 database 組成。

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
### tags

#### database

* id - 識別名稱，executor 中 source 和 target 參數的參考值。
* host - 主機位置
* port - 通訊埠
* dbName - 資料庫名稱。
* user - 使用者帳號
* password - 密碼
* driverClass - 資料庫驅動。
    * __uia.tmd.IdleAccess__ Test Only
    * __uia.tmd.access.HanaAccess__ - SAP HANA
    * __uia.tmd.access.ORAAccess__ - Oracle
    * __uia.tmd.access.PGSQLAccess__ - PostgreSQL

### 範例

#### HANA

```xml
<database>
    <id>ME</id>
    <host>192.168.1.1</host>
    <port>30015</port>
    <dbName>WIP</dbName>
    <user>WIP</user>
    <password>1234567890</password>
    <driverClass>uia.tmd.access.HanaAccess</driverClass>
</database>
```

#### PostgreSQL

```xml
<database>
    <id>YOUS</id>
    <host>localhost</host>
    <port>5432</port>
    <dbName>wip</dbName>
    <user>wip</user>
    <password>1234567890</password>
    <driverClass>uia.tmd.access.PGSQLAccess</driverClass>
</database>
```

#### Idle

```xml
<database>
    <id>IDLE</id>
    <host>0.0.0.0</host>
    <port>0</port>
    <dbName></dbName>
    <user></user>
    <password></password>
    <driverClass>uia.tmd.IdleAccess</driverClass>
</database>
```
