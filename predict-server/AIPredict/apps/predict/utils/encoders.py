import pandas as pd

try:
    import sklearn.preprocessing as skp
except:
    skp = None


class FeatureEncoder(object):
    """This is the supper class of all AIPredict encoders
    """

    def __init__(self) -> None:
        self.label = "" # reference of the encoder
        self.encoder = None # encoder instance
        self.params = {} # encoder parameters

    def transform(self, data):
        return self.encoder.transform(data)

    def get_params(self):
        return self.params
    
    def get_label(self):
        return self.label
    
    def set_params(self, params={}):
        self.params = params
    
    def get_encoder(self):
        return encoder

    def fit(self, data) -> dict:
        self.encoder.fit(data)
        return self.get_params()


class StandardScaler(FeatureEncoder):

    def __init__(self):
        self.label = "StandardScaler"
        if skp:
            self.encoder = skp.StandardScaler()
        else:
            self.encoder = None
        self.params = {"mean": None, "var": None, "scale": None}
    
    def fit(self, data):
        res = self.encoder.fit(data)
        self.params["mean"] = float(res.mean_)
        self.params["var"] = float(res.var_)
        self.params["scale"] = float(res.scale_)
        return self.get_params()

    def set_params(self, params={}):
        self.params = params
        self.encoder.mean_ = self.params["mean"]
        self.encoder.var_ = self.params["var"]
        self.encoder.scale_ = self.params["scale"]
        return True

class LabelEncoder(FeatureEncoder):

    def __init__(self):
        self.label = "LabelEncoder"
        if skp:
            self.encoder = skp.LabelEncoder()
        else:
            self.encoder = None
        self.params = {"classes": None}
    
    def fit(self, data):
        res = self.encoder.fit(data)
        self.params["classes"] = [list(res.classes_[0])]
        return self.get_params()

    def set_params(self, params={}):
        self.params = params
        self.encoder.classes_ = self.params["classes"]
        return True

class OneHotEncoder(FeatureEncoder):

    def __init__(self):
        self.label = "OneHotEncoder"
        if skp:
            self.encoder = skp.OneHotEncoder(handle_unknown='ignore')
        else:
            self.encoder = None
        self.params = {"categories": None, "drop_idx": None, "others": False}
    
    def fit(self, data, other, max_category=None):
        if other:
            if not max_category:
                raise ValueError("max_category must be filled if other is true")
            categories = data.count_values()[:max_category].index.tolist()
            data.apply(lambda x: x if x in categories else "other" )
            self.other = True
        res = self.encoder.fit(data)
        self.params["categories"] = [list(res.categories_[0])]
        self.params["drop_idx"] = res.drop_idx_
        return self.get_params()

    def set_params(self, params={}):
        self.params = params
        self.encoder.categories_ = self.params["categories"]
        self.encoder.drop_idx_ = self.params["drop_idx"]
        return True
    
    def transform(self, data):
        if self.params["others"]:
            column = data.columns[0]
            data[column] = data[column].apply(lambda x: x if x in self.params["categories"][0] else "others" )
        return pd.DataFrame(self.encoder.transform(data).toarray(), columns=self.encoder.get_feature_names())
    
class MinMaxScaler(FeatureEncoder):

    def __init__(self):
        self.label = "MinMaxScaler"
        if skp:
            self.encoder = skp.MinMaxScaler()
        else:
            self.encoder = None
        self.params = {"min": None, "scale": None}
    
    def fit(self, data):
        res = self.encoder.fit(data)
        self.params["min"] = float(res.min_)
        self.params["scale"] = float(res.scale_)
        return self.get_params()

    def set_params(self, params={}):
        self.params = params
        self.encoder.min_ = self.params["min"]
        self.encoder.scale_ = self.params["scale"]
        return True

encoders = {
    "MinMaxScaler" : MinMaxScaler(),
    "LabelEncoder" : LabelEncoder(),
    "OneHotEncoder" : OneHotEncoder(),
    "StandardScaler" : StandardScaler(),

}
def pick_encoder_from_label(label:str):
    return encoders[label]