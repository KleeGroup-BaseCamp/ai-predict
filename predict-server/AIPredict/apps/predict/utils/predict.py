from AIPredict.apps.predict.utils.preprocessing import FeatureEngineering
import shap
import numpy as np
from AIPredict.apps.predict.utils.files import *

class Predictor(object):
    """Wraps a model and a preprocessing dictionnary to 
    """
    def __init__(self, preprocessing:dict, model, framework:str):

        self.preprocessing = preprocessing
        self.model = model
        self.framework = framework
    
    def apply_preprocessing(self, data):
        # applies the preprocessing dictionary to the input data
        preprocessed_data = FeatureEngineering(data=data, bundle=self.preprocessing)
        preprocessed_data.transform()
        return preprocessed_data.get_data()
    
    def predict(self, data):
        #calls the predict moethod of the model if possible
        preprocessed_data = self.apply_preprocessing(data)

        if self.framework == "keras" and "predict" in dir(self.model):
            prediction = self.model.predict(preprocessed_data)
            return [np.argmax(pred) for pred in prediction] 

        if "predict" in dir(self.model):
            return self.model.predict(preprocessed_data)
    
    def predict_proba(self, data):
        #calls the predict_proba method of the model if possible
        preprocessed_data = self.apply_preprocessing(data)
        
        if self.framework == "keras" and "predict" in dir(self.model):
            return self.model.predict(preprocessed_data)
        
        if "predict_proba" in dir(self.model):
            try:
                return self.model.predict_proba(preprocessed_data)
            except:
                return None
    
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