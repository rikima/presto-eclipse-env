#!/bin/sh

export JAVA_HOME=/usr/java/latest

$JAVA_HOME/bin/java -jar presto-cli-executable.jar --server http://127.0.0.1:8880/ --catalog hive --schema default