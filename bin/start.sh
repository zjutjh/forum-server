#!/bin/bash

java -XX:+UseG1GC -Xms2G -Xmx2G -Xss256k -XX:MaxGCPauseMillis=300 -Xloggc:/logs/gc.log -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -Dproject.name=forum -Dcsp.sentinel.dashboard.server=localhost:9080  -jar forum-start-0.0.1-SNAPSHOT.jar