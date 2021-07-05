from django_q.tasks import AsyncTask
import importlib

from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import cross_val_score

from AIPredict.utils.preprocessing import FeatureEngineering

def build_model(package:str, modelClass:str):
    i = importlib.import_module(package)
    return getattr(i, modelClass)

def apply_preprocessing(X, y, preprocessing):
    X_fe = FeatureEngineering(X, preprocessing)
    y_fe = FeatureEngineering(y, preprocessing)
    X_fe.transform()
    y_fe.transform()
    return X_fe.get_data(), y_fe.get_data()

def sync_train(package:str, modelClass:str, X, y, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    try:
        modelBase = build_model(package, modelClass)
    except Exception as e:
        raise Exception("The given module %s.%s cannot be found on the server" %(package, modelClass))
    if grid_search:
        model = GridSearchCV(estimator=modelBase(), param_grid=hyperparameters, scoring=metrics, n_jobs=n_jobs, cv=cv)
    else:
        model = modelBase(**hyperparameters)
    try:
        model = model.fit(X, y)
    except Exception as e:
        raise Exception("An error occured during training. See logs for more details")
    return model

def sync_score(model, metrics, cv, X, y, preprocessing, *args, **kwargs):
    X_score, y_score = apply_preprocessing(X, y, preprocessing)
    scores = cross_val_score(model, X, y, scoring=metrics, cv=cv)
    res = {"scoreMean": scores.mean(), "scoreStd": scores.std()}
    return res

def async_train(package:str, modelClass:str, X, y, hyperparameters:dict, metrics:str, n_jobs:int=None, cv:int=None, grid_search:bool=False, *args, **kwargs):
    a = AsyncTask(sync_train,
        package, 
        modelClass, 
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

def async_score(model, metrics, cv, X, y, preprocessing, *args, **kwargs):
    a = AsyncTask(sync_score, model, metrics, cv, X, y, preprocessing, group='score', ack_failure=True)
    a.run()
    score = a.result(wait=-1)
    return score

