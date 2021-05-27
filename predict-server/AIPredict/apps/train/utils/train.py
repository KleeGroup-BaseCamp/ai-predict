from typing import Dict, List
from django_q.tasks import AsyncTask
import importlib

from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import cross_val_score

from AIPredict.apps.predict.utils.preprocessing import FeatureEngineering

def build_model(package:str, modelClass:str):
    i = importlib.import_module(package)
    return getattr(i, modelClass)

def _apply_preprocessing(X, y, preprocessing):
    fe = FeatureEngineering(data=X, bundle=preprocessing)
    fe.transform()
    X_transform = fe.get_data()
    return X_transform, y

def sync_train(package:str, modelClass:str, X, y, preprocessing:dict, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    modelBase = build_model(package, modelClass)
    if grid_search:
        model = GridSearchCV(estimator=modelBase(), param_grid=hyperparameters, scoring=metrics, n_jobs=n_jobs, cv=cv)
    else:
        model = modelBase(**hyperparameters)
    X_train, y_train = _apply_preprocessing(X, y, preprocessing)
    model = model.fit(X_train, y_train)
    return model

def sync_score(model, metrics, cv, X, y, preprocessing:dict, *args, **kwargs):
    X_score, y_score = _apply_preprocessing(X, y, preprocessing)
    scores = cross_val_score(model, X_score, y_score, scoring=metrics, cv=cv)
    res = {"scoreMean": scores.mean(), "scoreStd": scores.std()}
    return res

def async_train(package:str, modelClass:str, X, y, preprocessing:dict, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    a = AsyncTask(sync_train,
        package, 
        modelClass, 
        X, 
        y, 
        preprocessing, 
        hyperparameters, 
        metrics, 
        n_jobs, 
        cv, 
        grid_search,
        group='train')
    a.run()
    model = a.result(wait=-1)
    return model

def async_score(model, metrics, cv, X, y, preprocessing, *args, **kwargs):
    a = AsyncTask(sync_score, model, metrics, cv, X, y, preprocessing, group='score')
    a.run()
    score = a.result(wait=-1)
    return score
