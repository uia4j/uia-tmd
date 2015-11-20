# XML 設計概要

XML 結構由下面四大區塊組成，描述一個執行單體執行時，進行工作的條件。

* executorSpece - 執行單體定義。
* taskSpace - 工作定義。
* tableSpace - 表格定義。
* databaseSpace - 資料庫定義。

## executorSpece
由多個 executor 組成。
```
<executorSpece>
    <executor name="job1" source="svr1" target="svr2" task="task1" />
    ...
</executorSpece>
```    
### tag: executor
定義執行個體，描述來源資料庫、目的資料庫和指定執行的工作。
* name - 名稱。
* source - 來源資料庫。參考 databaseSpace 中的資料庫設定。
* target - 目的資料庫。參考 databaseSpace 中的資料庫設定。
* task - 工作名稱。參考 taskSpace 中的工作設定。

# taskSpace
由多個 task 組成。
```
<taskSpece>
    <task name="job1">
        <sourceSelect />
        <targetUpdate />
        <nexts />
    </task>
    ...
</taskSpece>
```    
## tag: task
定義工作個體，描述表格的對應關係與關聯。
* name - 工作名稱，executor 中 task 參數的參考值。

#### - element:
* sourceSelect - 資料來源的選擇計畫
* targetUpdate - 資料目的的更新計畫
* nexts - 下階層的計畫。類似資料庫中外鍵 (foreign key) 的設計。

### tag: sourceSelect
```
<sourceSelect table="" />
```
* table - 表格名稱。

### tag: targetUpdate
```
<targetUpdate table="" />
```
如果目的表格設計與來源完全相同，可忽略設定。
* table - 表格名稱。

### tag: nexts
定義子階層執行計畫。由多個 plan 組成。
```
<nexts>
    <plan />
    ...
</nexts>
```

#### - element:
* plan - 子階層計畫。

### tag: plan
* name - 計畫名稱。
```
<plan name="">
    <rule>
        <criteria name="" validate="equals">...</criteria>
        ...
    </rule>
    <join>
        <column source="">...</columrn>
        ...
    </join>
</plan>
```

#### - element:
* rule - 執行規則，可忽略。rule 下由多個 crietria 組合。
* join - 關聯方式。join 下由多個 column 組合。

### tag: criteria
定義計畫可否被執行的驗證方式。
* name - 欄位名稱。
* validate - 驗證方式。內容為 "startsWith" 或 "equals"。

##### - value
驗證參考值。

### tag: column
定義上下表格的關聯方式。
* source - 母表格的資料欄位名稱，可忽略。

##### - value
子表格的資料欄位名稱。


# tableSpace
由多個 table 組成。
```
<tableSpace>
    <table name="">
        <pk>
            <name>...</name>
            ...
        </pk>    
    </table>
    ...
</tableSpace>
```
### tag: table
定義表格的主鍵。若資料庫表格設計時已有正確定義，可忽略設定。
* name - 表格名稱。

#### - element:
* pk - 主鍵。pk 下由多個 name 組成。name 用於描述主鍵欄位名稱。

# databaseSpace
由多個 dbServer 組成。
```
<databaseSpace>
    <dbServer />
    ...
</databaseSpace>
```
### tag: dbServer
```
<dbServer>
    <id></id>
    <host></host>
    <port></port>
    <dbName></dbName>
    <user></user>
    <password></password>
    <dbType>MSSQL</dbType>
</dbServer>
```
#### - element:
* id - 識別名稱，executor 中 source 和 target 參數的參考值。
* host - 主機位置
* port - 通訊埠
* dbName - 資料庫名稱。
* user - 使用者帳號
* password - 密碼
* dbType - "MSSQL" 或是 "PGSQL"。
