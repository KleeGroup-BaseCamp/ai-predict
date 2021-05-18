from pyspark.sql import SQLContext, SparkSession
from pyspark.conf import SparkConf
import pandas as pd

def connect_spark(clusters:list):
    packages = ["com.datastax.spark:spark-cassandra-connector-assembly_2.11:2.5.2", "com.datastax.spark:spark-cassandra-connector-driver_2.11:2.5.2", "org.apache.spark:spark-sql_2.11:2.4.7"]
    spark_conf = SparkConf()
    spark_conf.setAll([('spark.master', 'spark://127.0.0.1:7077'),
                   ('spark.app.name', 'myApp'),
                   ("spark.driver.host", "192.168.1.72"),
                   ('spark.cassandra.connection.host', ','.join(clusters)),
                   ('spark.jars.packages', ','.join(packages)) ])
    spark = SparkSession.builder.appName('AIPredict Cassandra Connection') \
    .config(conf = spark_conf).getOrCreate()
    return spark

def get_cassandra_data(spark:SparkSession, keyspace:str, table:str):
    df = spark.read.format("org.apache.spark.sql.cassandra") \
    .options(table=table, keyspace=keyspace, user="cassandra", password="cassandra").load()
    data = df.toPandas()
    spark.stop()
    return data

def get_data(db_config:dict, features:list, labels:list):
    # connect to cassandra through spark
    clusters = db_config["clusters"]
    spark = connect_spark(clusters)
    # get the table
    keyspace = db_config["keyspace"]
    table = db_config["table"]
    data = get_cassandra_data(spark, keyspace, table)
    #split features and labels
    X, y = data[features], data[labels]
    return X, y

