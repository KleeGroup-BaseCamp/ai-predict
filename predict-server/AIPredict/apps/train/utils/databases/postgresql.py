import pandas as pd
from sqlalchemy import create_engine

from django.conf import settings

def build_url(database: dict):
    return database["sql"]+"://"+database["username"]+":"+database["password"]+"@"+database["host"]+"/"+database["keyspace"]

def connect_database(db_name: str):
    database = settings.TRAIN_DB[db_name]
    database_url = build_url(database)
    engine = create_engine(database_url)
    return engine

def get_database_data(conn, table, features, labels):
    #conn.autocommit = True
    # Build query
    columns = [col for col in features+labels]
    query_col = ", ".join(columns)
    query = """
    SELECT %s from %s
    """ % (query_col, table)
    result = pd.read_sql(query, conn)
    #conn.close()
    return result


def get_data(db_config: dict, features: list, labels: list):
    engine = connect_database(db_config["database"])
    with engine.begin() as conn:
        df = get_database_data(conn=conn, table=db_config["table"], features=features, labels=labels)
    
    X, y = df[features], df[labels]
    return X, y
