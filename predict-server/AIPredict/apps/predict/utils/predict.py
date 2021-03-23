from AIPredict.apps.predict.utils.preprocessing import FeatureEngineering
import shap
import numpy as np

class Predictor(object):
    """Wraps a model and a preprocessing dictionnary to 
    """
    def __init__(self, preprocessing:dict, model_label:str, model):

        self.preprocessing = preprocessing
        self.model_label = model_label
        self.model = model
    
    def apply_preprocessing(self, data):
        # applies the preprocessing dictionary to the input data
        preprocessed_data = FeatureEngineering(data=data, bundle=self.preprocessing)
        preprocessed_data.transform()
        return preprocessed_data.get_data()
    
    def predict(self, data):
        #calls the predict moethod of the model if possible
        if "predict" in dir(self.model):
            preprocessed_data = self.apply_preprocessing(data)
            return self.model.predict(preprocessed_data)
    
    def predict_proba(self, data):
        #calls the predict_proba method of the model if possible
        if "predict_proba" in dir(self.model):
            preprocessed_data = self.apply_preprocessing(data)
            return self.model.predict_proba(preprocessed_data)
    
    def explain_prediction(self, data):
        #triggers an explainer for Tree based models
        preprocessed_data = self.apply_preprocessing(data)
        explainer = shap.TreeExplainer(self.model)
        shap_values = explainer(preprocessed_data)
        return shap_values
    
    def cluster(self):
        #calls labels_ and cluster_centers_ attributes from a clustering model
        label = None
        centers = None
        if "labels_" in dir(self.model):
            label = self.model.labels_
        if "cluster_centers_" in dir(self.model):
            centers = self.model.cluster_centers_
        return label, centers