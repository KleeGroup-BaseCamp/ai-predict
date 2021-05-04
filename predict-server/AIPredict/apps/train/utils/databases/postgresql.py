import pandas as pd
import psycopg2

def connect_psql(database, user, password, host, port, *args, **kwargs):
    conn = psycopg2.connect(database=database, user=user, password=password, host=host, port=port)
    return conn

def get_psql_data(conn, table, features, labels):
    conn.autocommit = True
    # Build query
    columns = [col for col in features+labels]
    query_col = ", ".join(columns)
    query = """
    SELECT %s from %s
    """ %(query_col, table)
    result = pd.read_sql(query, conn);
    conn.close()
    return result

def get_data(db_config:dict, features:list, labels:list):
    conn = connect_psql(**db_config)
    df = get_psql_data(conn=conn, table=db_config["table"], features=features, labels=labels)
    X, y = df[features], df[labels]
    return X, y
