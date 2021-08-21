from AIPredict.utils.imports import *
from AIPredict.utils.preprocessing import FeatureEngineering
from AIPredict.apps.predict.utils.response import parse_response, PredictResponseEncoder

import numpy as np
import json
import logging

logger = logging.getLogger(__name__)

class Predictor(object):
    """Wraps a model and a preprocessing dictionnary to 
    """
    def __init__(self, preprocessing:dict, model, framework:str):

        self.preprocessing = preprocessing
        self.model = model
        self.framework = framework
    
    def _apply_preprocessing(self, data):
        # applies the preprocessing dictionary to the input data
        preprocessed_data = FeatureEngineering(data=data, bundle=self.preprocessing)
        preprocessed_data.transform()
        return preprocessed_data.get_data()
    
    def _predict(self, data):
        #calls the predict moethod of the model if possible
        logger.info('Pre processing ...')
        preprocessed_data = self._apply_preprocessing(data)

        if self.framework == "keras" and "predict" in dir(self.model):
            logger.info('Predicting ...')
            raw_pred = self.model.predict(preprocessed_data)
            prediction = [np.argmax(pred) for pred in raw_pred]

        elif "predict" in dir(self.model):
            logger.info('Predicting ...')
            #For xgboost, data columns must be reordered here in the same order as the order used in train
            prediction = self.model.predict(preprocessed_data)

        return prediction

    def _predict_proba(self, data):
        #calls the predict_proba method of the model if possible
        preprocessed_data = self._apply_preprocessing(data)
        if self.framework == "keras" and "predict" in dir(self.model):
            return self.model.predict(preprocessed_data)
        
        if "predict_proba" in dir(self.model):
            try:
                return self.model.predict_proba(preprocessed_data)
            except:
                return None
    
    def _explain_prediction(self, data):
        #triggers an explainer for Tree based models
        preprocessed_data = self._apply_preprocessing(data)
        if shap:
            explainer = shap.TreeExplainer(self.model)
            shap_values = explainer(preprocessed_data)
            return shap_values
    
    def predict(self, data):
        res = {}
        #Basic Prediction
        
        prediction = self._predict(data)
        
        if all(not isinstance(pred, str) for pred in prediction):
            if len(np.shape(prediction))>1:
                res["predictionVector"] = prediction
            else:
                res["predictionNumeric"] = prediction
        else:
            res["predictionLabel"] = prediction
        
        #Classes probabilities for classification
        res["predictionProba"] = self._predict_proba(data=data)


        #Prediction explanation (if the model allows it)
        logger.info('explaining')
        try:
            explanation = self._explain_prediction(data).values
            if len(np.shape(explanation)) == 2:
                res["explanation1D"] = self._explain_prediction(data).values
            elif len(np.shape(explanation)) == 3:
                res["explanation2D"] = self._explain_prediction(data).values
        except Exception as e:
            res["explanation1D"] = None
            res["explanation2D"] = None
        
        #Parse response and encode properly
        res = parse_response(res)
        res = json.dumps(res, cls=PredictResponseEncoder)
        return json.loads(res)
