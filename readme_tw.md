資料遷移工具
=============

`uia-tmd` API 主要功能是將某表格的資料及與其相關聯資料，透過 __客製化設定__，從來源資料庫遷移至目的資料庫。

__客製化設定__ 可以重新定義表格的主鍵。

- [使用範例](tutorial_tw.md)
- [XML 設計概要](xmldesign_tw.md)


## 套件說明
- uia-tmd - 核心 API

- uia-tmd-access - 資料庫存取驅動
    - PostgreSQL
    - Oracle
    - HANA

## 應用程式
### uia-tmd-ui
此應用程式提供 UI 編輯客製化設定檔案。

### uia-tmd-zztop
此應用程式將同步的紀錄儲存在 PostgreSQL 9.3 內。

#### CLI
java -jar tmd-zztop.jar `sync` -f conf/mvs.xml -j Run
