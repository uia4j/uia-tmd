Data Migration Tool
============================

[Chinese](readme_tw.md)

The purpose of `uia-tmd` api is to move specific rows and their relative data from one source to another depending on customize definition in the __plan file__.

The plan file can define primary keys of tables.

- [Tutorial](tutorial.md)
- XML Design Concept

## Description of packages
- uia-tmd - core api

- uia-tmd-access - database access drivers.
    - PostgreSQL
    - Oracle
    - HANA

## Applications
### uia-tmd-ui
This application provides UI tool to edit the plan file.

### uia-tmd-zztop
This application saves sync history in PostgreSQL 9.3.

#### CLI
java -jar tmd-zztop.jar `sync` -f conf/mvs.xml -j Run
