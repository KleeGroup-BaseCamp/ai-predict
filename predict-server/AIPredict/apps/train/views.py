from AIPredict.utils.parsers import ModelJSONEncoder, TrainingParser
from AIPredict.utils.preprocessing import FeatureEngineering, pick_encoder_from_label
from AIPredict.utils.version import VersionController
from AIPredict.apps.train.utils.files import save, save_config, create_folder
from AIPredict.apps.train.utils.tools import get_data, train_response, score_response
from AIPredict.apps.train.utils.train import async_train, async_score
from AIPredict.utils.validators import BundleCreationValidator, BundleRequestValidator, TrainDataValidator
from AIPredict.utils.models import get_major_parameters

from rest_framework.response import Response
from rest_framework import viewsets, status
from django.core.validators import ValidationError

import json
import time
import logging
import shap
import numpy as np

# import the logging library
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)


class TrainModel(viewsets.ViewSet):

    def deploy(self, request):
        """
        Handle a request to deploy and train a bundle.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            POST: deploy-train/'
        
        """
        # get the new bundle data
        config = request.data
        try:
            BundleCreationValidator(config).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_406_NOT_ACCEPTABLE)
        #train the bundle
        res = self.train(config)
        """except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)"""
        return res

    def retrain(self, request, *args, **kwargs):
        """
        Handle a train request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            POST: train/<str:bundle>/<int:version>/'
        
        """
        try:
            BundleRequestValidator(True, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_406_NOT_ACCEPTABLE)

        # gets the request bundle name and version
        name = kwargs.pop("bundle")
        version = kwargs.pop("version")
        bundle = VersionController(name, version)
        try:
            res = self.train(bundle.get_bundle())
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        return res

    def score(self, request, *args, **kwargs):
        """
        Handle a score request.
        
        Endpoint:
            POST: score/<str:bundle>/<int:version>/'
        
        """
        try:
            BundleRequestValidator(True, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_406_NOT_ACCEPTABLE)

        # gets the request bundle name and version
        name = kwargs.pop("bundle")
        version = kwargs.pop("version")
        #get bundle to score
        bundle = VersionController(name, version)
        model = bundle.get_model()
        # get dataset
        X, y = get_data(bundle.get_category("dataset"))
        try:
            TrainDataValidator( X, y, bundle.get_bundle()).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_406_NOT_ACCEPTABLE)
        # get parameters
        params = bundle.get_category("parameters")
        cv = params["cv"]
        metrics = params["metrics"]
        preprocessing = bundle.get_category("preprocessing")
        start = time.time()
        score = async_score(model, metrics, cv, X, y, preprocessing)
        t = time.time() - start
        response = score_response(
            modelName=name, status="succeed", version=version, time=t, score=score)

        return Response(response, status=status.HTTP_200_OK)

    def train(self, config):
        logger.info("Start training of %s v%s" %(config["meta"]["name"], config["meta"]["version"]))
        # start time
        start = time.time()
        # get model class
        algo = config["algorithm"]
        package = algo["package"]
        model_name = algo["name"]
        grid_search = config["parameters"]["grid_search"]
        # prepare parameters and preprocessing dictionaries
        params = config["parameters"]
        # get dataset
        try:
            X_train, y_train = get_data(config["dataset"])
            logger.info("Data extracted")
        except Exception as e:
            logger.error("Cannot extract data from the database: " + str(e))
            return Response({"error": "Cannot extract data from the database"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            TrainDataValidator( X_train, y_train, config).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_406_NOT_ACCEPTABLE)
        # configure preprocessing
        try:
            X_fe = FeatureEngineering(data=X_train)
            y_fe = FeatureEngineering(data=y_train)
            for feature in config["dataset"]["data_config"]:
                if feature["preprocessing"] != "":
                    if feature["is_label"]:
                        y_fe.add(feature["name"], pick_encoder_from_label(
                        feature["preprocessing"]))
                    else:
                        X_fe.add(feature["name"], pick_encoder_from_label(
                            feature["preprocessing"]))
            preprocessing = {}
            preprocessing.update(X_fe.get_bundle())
            preprocessing.update(y_fe.get_bundle())
            config["preprocessing"] = preprocessing
            X_fe.transform()
            y_fe.transform()
            X_process = X_fe.get_data()
            y_process = y_fe.get_data()
            logger.info("Preprocessing applyed")
        except Exception as e:
            logger.error("Cannot apply preprocessing properly: " + str(e))
            return Response({"error": "Cannot apply preprocessing properly. See logs for details"}, status=status.HTTP_400_BAD_REQUEST)

        # train
        model = async_train(package=package, model_name=model_name,
                                X=X_process, y=y_process, **params)
        if isinstance(model, str):
            logger.error("Training failed : %s" %model)
            return Response({"error": model}, status=status.HTTP_400_BAD_REQUEST)
        # score
        score = async_score(model, X=X_process, y=y_process,**params)
        if isinstance(score, str):
            logger.error("Scoring failed : %s" %model)
            return Response({"error": model}, status=status.HTTP_400_BAD_REQUEST)
        elif np.isnan(score["scoreMean"]) or np.isnan(score["scoreStd"]):
            score = {"scoreMean": None, "scoreStd": None}
        # write parameters
        model_params = json.loads(json.dumps(
            model.get_params(), cls=ModelJSONEncoder))
        if grid_search:
            config["training_parameters"] = TrainingParser(model_params).parse()
        else:
            config["training_parameters"] = {"estimator": TrainingParser(model_params).parse()}
        
        # write training data
        t = time.time() - start
        training_data = {
            "time": t, "x_shape": X_process.shape, "y_shape": y_process.shape}
        config["training_data"] = training_data

        logger.info("Training succeeded. Starting explanation computation.")
        # explanation
        # init explanation
        if grid_search:
            estimator = model.best_estimator_
        else:
            estimator = model
        # compute features importance
        if "predict_proba" in dir(estimator):
            try:
                explainer = shap.KernelExplainer(estimator.predict_proba, X_process)
            except:
                explainer = shap.KernelExplainer(estimator.predict, X_process)
        else:
            explainer = shap.KernelExplainer(model.predict, X_process)
        shap_values = np.asarray(explainer.shap_values(X_process))
        # compute features importance
        if len(shap_values.shape) == 2:
            shap_exp =np.mean(np.absolute(shap_values), 0)
            exp_array = shap_exp
        elif len(shap_values.shape) == 3:
            shap_exp = []
            shap_values_transpose = shap_values
            for i in range(len(shap_values_transpose)):
                shap_exp.append(
                    np.mean(np.absolute(shap_values_transpose[i]), 0))
            exp_array = np.sum(shap_exp, 0)

        # sort features
        tuples_exp = sorted(
            zip(exp_array, X_train.columns), reverse=True)
        values = []
        feature_names = []
        for value, name in tuples_exp:
            values.append(float(value))
            feature_names.append(name)
        config["explanation"] = {
            "feature": feature_names, "importance": values}
        logger.info("Explanation successfully computed. Start model saving.")
        #set moajor parameters
        config["major_parameters"] = get_major_parameters(package, model_name, model, grid_search)
        config["use"] = 0
        # save model
        path, name, version = save(
            model, config, config["meta"]["version"], score)
        response = train_response(time=t, modelName=name, version=version,
                                  status="deployed", response="Trained in %ds" % t, score=score)
        logger.info("Model successfully saved.")
        return Response(response, status=status.HTTP_201_CREATED)
