from json import JSONEncoder
import numpy as np


class ModelJSONEncoder(JSONEncoder):
    def default(self, obj):
        return obj.__dict__


class TrainingParser():
    def __init__(self, obj):
        self.obj = obj

    def parse(self):
        res = {}
        for key in self.obj:
            item = self.obj[key]
            if isinstance(item, dict):
                res[key] = TrainingParser(item).parse()
            elif not (isinstance(item, float) and np.isnan(item)):
                res[key] = item
            else:
                res[key] = None
        return res
