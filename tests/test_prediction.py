from django.test import TestCase
from rest_framework.test import APIClient

import pandas as pd
import numpy as np

from server.apps.predict.utils.predict import Predictor
from server.apps.predict.utils.files import *

data_to_predict ='{"zone":{"80":1,"182":4},"area":{"80":3268,"182":9363},"population":{"80":684,"182":231},"language":{"80":6,"182":1},"religion":{"80":4,"182":1},"bars":{"80":0,"182":0},"stripes":{"80":3,"182":13},"colors":{"80":4,"182":3},"red":{"80":0,"182":1},"green":{"80":1,"182":0},"blue":{"80":1,"182":1},"gold":{"80":0,"182":0},"white":{"80":1,"182":1},"black":{"80":0,"182":0},"orange":{"80":1,"182":0},"mainhue":{"80":"orange","182":"white"},"circles":{"80":1,"182":0},"crosses":{"80":0,"182":0},"saltires":{"80":0,"182":0},"quarters":{"80":0,"182":1},"sunstars":{"80":0,"182":50},"crescent":{"80":0,"182":0},"triangle":{"80":0,"182":0},"icon":{"80":1,"182":0},"animate":{"80":0,"182":0},"text":{"80":0,"182":0},"topleft":{"80":"orange","182":"blue"},"botright":{"80":"green","182":"red"}}'

class PredictionTest(TestCase):
    
    def test_predictor_initialisation(self):
        test_path = "./tests/test_bundle/"

        with open(test_path +"model.pkl", "rb") as m:
            model = pickle.load(m)
        with open(test_path + "bundle.json", "rb") as d:
            data = json.load(d)
        
        predictor = Predictor(model_label="test", model=model, preprocessing=data["preprocessing"])
        return True
    
    def test_predictor_predict(self):
        test_path = "./tests/test_bundle/"

        with open(test_path +"model.pkl", "rb") as m:
            model = pickle.load(m)
        with open(test_path + "bundle.json", "rb") as d:
            data = json.load(d)
        
        predictor = Predictor(model_label="test", model=model, preprocessing=data["preprocessing"])
        to_predict = pd.read_json(data_to_predict)
        self.assertListEqual(list(predictor.predict(to_predict)), [5, 1])
        self.assertListEqual([list(i) for i in predictor.predict_proba(to_predict)], [[0, 0, 0.03, 0.18, 0.79, 0], [0.76, 0.12, 0.03, 0.01, 0.03, 0.05]])

    def test_predictor_explanation(self):
        test_path = "./tests/test_bundle/"

        with open(test_path +"model.pkl", "rb") as m:
            model = pickle.load(m)
        with open(test_path + "bundle.json", "rb") as d:
            data = json.load(d)
        
        predictor = Predictor(model_label="test", model=model, preprocessing=data["preprocessing"])
        to_predict = pd.read_json(data_to_predict)
        explanation = predictor.explain_prediction(to_predict)
        self.assertTupleEqual(np.shape(explanation.values), (2, 48, 6))

        explanation_ = explanation.values.transpose((0, 2, 1))
        proba = predictor.predict_proba(to_predict)
        shap_values = explanation_.sum(2) + explanation.base_values
        self.assertTrue(np.allclose(proba, shap_values))
        self.assertTrue(np.allclose(proba, shap_values))
