#!/bin/bash

set -e

docker image pull cassandra:3.11.10

docker run --rm -d --name tmp cassandra:3.11.10
docker cp tmp:/etc/cassandra etc_cassandra-3.11.10_vanilla
docker stop tmp

mkdir -p ./docker/spark-cassandra/etc
cp -a etc_cassandra-3.11.10_vanilla ./docker/spark-cassandra/etc/cassandra
rm -r etc_cassandra-3.11.10_vanilla