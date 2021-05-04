#!/bin/bash

mkdir -p "./docker/jars/"
curl -l https://repo1.maven.org/maven2/com/datastax/spark/spark-cassandra-connector_2.11/2.5.2/spark-cassandra-connector_2.11-2.5.2.jar --output ./docker/jars/spark-cassandra-connector_2.11-2.5.2.jar
