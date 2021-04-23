from pyspark.sql import SQLContext, SparkSession
import pandas as pd

def connect_spark(clusters:list):
    spark = SparkSession.builder.appName('AIPredict Cassandra Connection') \
    .config('spark.cassandra.connection.host', ','.join(clusters)).getOrCreate()
    return spark

def get_cassandra_data(spark:SparkSession, keyspace:str, table:str):
    df = spark.read.format("org.apache.spark.sql.cassandra") \
    .options(table=table, keyspace=keyspace).load()
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

