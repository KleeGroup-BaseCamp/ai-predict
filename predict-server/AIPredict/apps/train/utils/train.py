from django_q.tasks import AsyncTask
import importlib

# import the logging library
import logging

from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import cross_val_score

import numpy as np

from AIPredict.utils.preprocessing import FeatureEngineering

# Get an instance of a logger
logger = logging.getLogger(__name__)

def build_model(package:str, model_name:str):
    i = importlib.import_module(package)
    return getattr(i, model_name)

def apply_preprocessing(X, y, preprocessing):
    X_fe = FeatureEngineering(X, preprocessing)
    y_fe = FeatureEngineering(y, preprocessing)
    X_fe.transform()
    y_fe.transform()
    return X_fe.get_data(), y_fe.get_data()

def sync_train(package:str, model_name:str, X, y, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    try:
        logger.info("Loading %s %s", package, model_name)
        modelBase = build_model(package, model_name)
    except Exception as e:
        raise Exception("The given module %s.%s cannot be found on the server" %(package, model_name))
    if grid_search:
        logger.info("GridSearch CrossValidation:%s hyperparameters:%s", cv, hyperparameters)
        model = GridSearchCV(estimator=modelBase(), param_grid=hyperparameters, scoring=metrics, n_jobs=n_jobs, cv=cv)
    else:
        logger.info("No GridSearch hyperparameters:%s", hyperparameters)
        model = modelBase(**hyperparameters)
    try:
        #logger.info(np.where(np.isnan(X)))
        model = model.fit(X, y)
    except Exception as e:
        raise Exception("An error occured during training. See logs for more details")
    return model

def sync_score(model, metrics, cv, X, y, preprocessing, *args, **kwargs):
    if preprocessing:
        X_score, y_score = apply_preprocessing(X, y, preprocessing)
    else:
        X_score, y_score = X, y
    scores = cross_val_score(model, X_score, y_score, scoring=metrics, cv=cv)
    res = {"scoreMean": scores.mean(), "scoreStd": scores.std()}
    return res

def async_train(package:str, model_name:str, X, y, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    a = AsyncTask(sync_train,
        package, 
        model_name, 
        X, 
        y, 
        hyperparameters, 
        metrics, 
        n_jobs, 
        cv, 
        grid_search,
        group='train',
        ack_failure=True)
    a.run()
    model = a.result(wait=-1)
    return model

def async_score(model, metrics, cv, X, y, preprocessing=None, *args, **kwargs):
    a = AsyncTask(sync_score, model, metrics, cv, X, y, preprocessing, group='score', ack_failure=True)
    a.run()
    score = a.result(wait=-1)
    return score

