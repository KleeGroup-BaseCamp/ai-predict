import pandas as pd
from AIPredict.settings.development import TRAIN_DB
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandra.query import SimpleStatement
from threading import Event

def pandas_factory(colnames, rows):
    return pd.DataFrame(rows, columns=colnames)

def cassandra_connect(db_name:str, table:str, fetch_size:int):
    #get database connection parameters
    database = TRAIN_DB[db_name]
    #set credentials
    credential = PlainTextAuthProvider(username=database["username"], password=database["password"])
    #initialize connection
    cluster = Cluster(database["cluster"], auth_provider=credential)
    session = cluster.connect(database["keyspace"])
    #prepare the query
    query = """select * from %s""" %table
    statement = SimpleStatement(query, fetch_size=fetch_size)
    #get first page result
    result = session.execute(statement)
    column_names = result.column_names
    #initialize the dataframe
    df = pd.DataFrame(columns=column_names)
    #iterate through the pages and the rows to fill the dataframe
    for row in result:
        df = df.append(pd.DataFrame([row], columns=column_names))
    #close the connection
    cluster. shutdown()
    return df

def get_data(db_config:dict, features:list, labels:list):
    data = cassandra_connect(db_config["key"], db_config["table"], db_config["fetch_size"])
    #split features and labels
    X, y = data[features], data[labels]
    return X, y

