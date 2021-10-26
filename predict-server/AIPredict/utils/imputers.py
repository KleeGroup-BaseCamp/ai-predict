import pandas as pd
import numpy as np
import sklearn.impute as ski
import math


class FeatureImputer(object):
    """This is the supper class of all AIPredict inputers
    """

    def __init__(self) -> None:
        self.label = ""  # reference of the imputer
        self.imputer = None  # imputer instance
        self.params = {}  # imputer parameters

    def transform(self, data):
        return self.imputer.transform(data)

    def get_params(self):
        return self.params

    def get_label(self):
        return self.label

    def set_params(self, params={}):
        self.params = params

    def get_imputer(self):
        return self.inpuimputerter

    def fit(self, data) -> dict:
        self.imputer.fit(data)
        return self.get_params()


class SimpleImputer(FeatureImputer):

    def __init__(self, strategy=None, fill_value=None):
        self.label = "SimpleImputer"
        self.imputer = ski.SimpleImputer(strategy=strategy, fill_value=fill_value)
        self.params = {"strategy": strategy, "fill_value": fill_value}

    def fit(self, data) -> dict:
        res = self.imputer.fit(data)
        self.params["strategy"] = res.strategy
        if hasattr(res, "fill_value"):
            self.params["fill_value"] = res.fill_value
        if hasattr(res, "statistics_"):
            self.params['statistics'] = [res.statistics_[0]]
        if math.isnan(res.missing_values) == False and res.missing_values != np.nan:
            self.params["missing_values"] = res.missing_values

        return self.get_params()

    def set_params(self, params={}):
        self.params = params
        self.imputer.strategy = self.params["strategy"]
        if hasattr(params, "missing_values"):
            self.imputer.missing_values = self.params["missing_values"]
        if hasattr(params, "fill_value"):
            self.imputer.fill_value = self.params["fill_value"]
        self.imputer.statistics_ = np.array(self.params["statistics"])
        return True
