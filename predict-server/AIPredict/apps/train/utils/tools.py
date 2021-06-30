from sklearn.datasets import load_boston, load_iris
from sklearn.model_selection import train_test_split
import pandas as pd

from AIPredict.apps.train.utils.databases import postgresql


def build_model_class(package: str, model: str):
    modules = package.split(".")
    if modules[0] in ["sklearn", "keras", "xgboost"]:
        modules = modules[1:]
    else:
        raise ImportError("The package %s is not imported" % modules[0])
    modules.append(model)
    return ".".join(modules)


def get_data(dataset_config: int):
    db_config = dataset_config["db_config"]
    data_config = dataset_config["data_config"]

    features = [feature["name"]
                for feature in data_config if not feature["is_label"]]
    labels = [feature["name"]
              for feature in data_config if feature["is_label"]]

    key = db_config["key"]
    if key == "postgresql":
        X, y = postgresql.get_data(db_config, features, labels)
    elif key == "cassandra_spark":
        X, y = cassandra_spark.get_data(db_config, features, labels)
    else:
        X, y = None, None
    return X, y


def train_response(modelName: str, status: str, response: str, version: int = None, time: int = None, score: float = None, deploy_status: str = None, deploy_response: str = None):
    response = {
        "time": time,
        "modelName": modelName,
        "version": version,
        "score": score,
        "status": status,
        "response": response,
        # "deploy":
        # {
        #    "status": deploy_status,
        #    "response": deploy_response
        # }
    }
    return response


def score_response(modelName: str, status: str, version: int = None, time: int = None, score: float = None):
    response = {
        "time": time,
        "modelName": modelName,
        "version": version,
        "score": score,
        "status": status
    }
    return response
