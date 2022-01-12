#!/bin/bash
export JAVA_HOME=/opt/jdk1.8.0_202
export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/sapjco.jar
export JRE_HOME=$JAVA_HOME/jre
export PATH=$JAVA_HOME/bin:$PATH
export LD_LIBRARY_PATH=$JAVA_HOME/jre/lib/amd64/server
java -version
cd /opt/purge/
nohup java -XX:+UseG1GC -XX:MaxGCPauseMillis=600 -Xmx4096m -jar tmd-zztop.jar so_sync_day -o 535 -j SO_SYNC_AND_DELETE &

