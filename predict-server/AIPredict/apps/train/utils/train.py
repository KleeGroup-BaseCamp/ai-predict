from typing import Dict, List
from django_q.tasks import AsyncTask

from sklearn import *
from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import cross_val_score

class Trainer(object):

    def __init__(self, model_class:str, hyperparameters:Dict[str, List[object]], metrics:str, n_jobs=None, cv=None, grid_search:bool=False, *args, **kwargs):
        self.model_class = eval(model_class)
        self.hyperparameters = hyperparameters
        self.grid_search = grid_search
        self.metrics = metrics
        self.n_jobs = n_jobs
        self.cv = cv
        self.model = None
    
    def _apply_preprocessing(self, data, preprocessing):
        pass

    def _grid_search(self):
        search = GridSearchCV(estimator=self.model_class(), param_grid=self.hyperparameters, scoring=self.metrics, n_jobs=self.n_jobs, cv=self.cv)
        return search
    
    def train(self, X_train, Y_train):
        if self.grid_search:
            model = self._grid_search()
        else:
            model = self.model_class(**self.hyperparameters)
        self.model = model.fit(X_train, Y_train)
        return self.model

def sync_train(trainer:Trainer, X_train, Y_train):
    return trainer.train(X_train, Y_train)

def sync_score(model, metrics, cv, X, y):
    return cross_val_score(model, X, y, scoring=metrics, cv=cv).mean()

def async_train(trainer:Trainer, X_train, Y_train):
    a = AsyncTask(sync_train, trainer=trainer, X_train=X_train, Y_train=Y_train, group='train')
    a.run()
    model = a.result(wait=-1)
    return model

def async_score(model, metrics, cv, X, y):
    a = AsyncTask(sync_score, model, metrics, cv, X, y, group='score')
    a.run()
    score = a.result(wait=-1)
    return model
 

