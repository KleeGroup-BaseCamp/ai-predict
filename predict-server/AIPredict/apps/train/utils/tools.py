from sklearn.datasets import load_boston, load_iris
from sklearn.model_selection import train_test_split
import pandas as pd
import psycopg2

def build_model_class(package:str, model:str):
    modules = package.split(".")
    if modules[0] in ["sklearn", "keras", "xgboost"]:
        modules = modules[1:]
    else:
        raise ImportError("The package %s is not imported" %modules[0])
    modules.append(model)
    return ".".join(modules)

def get_data(dataset_config:int):
    db_config = dataset_config["db_config"]
    data_config = dataset_config["data_config"]

    features = [feature for feature in data_config.keys() if not data_config[feature]["is_label"]]
    labels = [feature for feature in data_config.keys() if data_config[feature]["is_label"]]

    db_type = db_config["db_type"]
    if db_type=="postgresql":
        conn = connect_psql(**db_config)
        data = get_psql_data(conn=conn, table=db_config["table"], features=features, labels=labels)
        df = pd.DataFrame(data, columns=features+labels)
        X, y = df[features], df[labels]
    else:
        X, y = None, None
    return X, y

def connect_psql(database, user, password, host, port, *args, **kwargs):
    conn = psycopg2.connect(database=database, user=user, password=password, host=host, port=port)
    return conn

def get_psql_data(conn, table, features, labels):
    conn.autocommit = True
    cursor = conn.cursor()
    columns = [col for col in features+labels]
    query_col = ", ".join(columns)
    query = """
    SELECT %s from %s
    """ %(query_col, table)
    cursor.execute(query)
    result = cursor.fetchall()
    conn.commit()
    conn.close()
    return result

def train_response(modelName:str, status:str, response:str, version:int=None, time:int=None, score:float=None, deploy_status:str=None, deploy_response:str=None):
    response = {
        "time": time,
        "modelName": modelName,
        "version": version,
        "score": score,
        "status": status,
        "response": response,
        "deploy":
        {
            "status": deploy_status,
            "response": deploy_response
        }
    }
    return response

def score_response(modelName:str, status:str, version:int=None, time:int=None, score:float=None):
    response = {
        "time": time,
        "modelName": modelName,
        "version": version,
        "score": score,
        "status": status
    }
    return response