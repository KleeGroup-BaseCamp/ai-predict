import json
from pathlib import Path
import time
import logging

from django.shortcuts import render
from django.core.exceptions import ValidationError
from django.http.response import JsonResponse

from django_q.tasks import AsyncTask

from rest_framework import viewsets, status
from rest_framework.response import Response

from AIPredict.apps.train.validators import validate_config, validate_request
from AIPredict.apps.train.utils.train import async_train, async_score, sync_train, sync_score
from AIPredict.apps.train.utils.tools import build_model_class, get_data, train_response, score_response
from AIPredict.apps.train.utils.files import save, send, get_config, delete_files
from AIPredict.apps.predict.utils.files import get_model

# Get an instance of a logger
logger = logging.getLogger(__name__)

class TrainModel(viewsets.ViewSet):

    def deploy(self, request):
        #zip pour plus général
        config = request.data
        try:
            validate_config(config)
        except ValidationError as e:
            response = train_response(modelName=config["meta"]["name"], status="failed", response=str(e))
            return Response(response, status=status.HTTP_400_BAD_REQUEST)
        res = self.train(config)
        return res
        
    
    def retrain(self, request, *args, **kwargs):
        # gets the request bundle version
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        validate_request(bundle, version)
        path = Path(".", "bundles", "train", bundle, "v"+str(version))
        config = get_config(path)
        res = self.train(config)
        return res

    def score(self, request, *args, **kwargs):
        # gets the request bundle version
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        try:
            validate_request(bundle, version)
        except ValidationError as e:
            response = score_response(modelName=bundle, status="failed", version=version)
            return Response(response, status=status.HTTP_400_BAD_REQUEST)

        try:
            path = Path(".", "bundles", "train", bundle, "v"+str(version))
            config = get_config(path)
            model = get_model(path)
            #get dataset
            X, y = get_data(config["dataset"])
            #get parameters
            params = config["parameters"]
            cv = params["cv"]
            metrics = params["metrics"]

            start = time.time()
            score = async_score(model, metrics, cv, X, y)
            t = time.time() - start
            response = score_response(modelName=bundle, status="succeed", version=version, time=t, score=score)
        except Exception as e:
            logger.exception('Error while scoring model: %s version: %s', bundle, version)
            response = score_response(modelName=bundle, status="failed", version=version)
            return Response(response, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return Response(response, status=status.HTTP_200_OK)
    
    def destroy(self, request, *args, **kwargs):
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        validate_request(bundle, version)
        path = Path(".", "bundles", "train", bundle, "v"+str(version))
        delete_files(path)
        return Response(status=status.HTTP_204_NO_CONTENT)

    def train(self, config):
        #start time
        start = time.time()
        #get model class
        algo = config["algorithm"]
        package = algo["package"]
        modelClass = algo["name"]
        #prepare parameters and preprocessing dictionaries
        params = config["parameters"]
        preprocessing = config["preprocessing"]
        #get dataset
        X_train, y_train = get_data(config["dataset"])
        #train
        model = async_train(package=package, modelClass=modelClass, X=X_train, y=y_train, preprocessing=preprocessing, **params)
        #if the score is greater than a threshold value, the bundle is saved
        aim = params["min_score"]
        score = async_score(model, X=X_train, y=y_train, **params)
        t = time.time() - start

        path, name, version = save(model, config, 0, score)
        response = train_response(time=t, modelName=name, version=version, status="deployed", response="Trained in %ds" %t, score=score)
        return Response( response, status=status.HTTP_201_CREATED)

        #if  score >= aim:
            #response = send(path)
            #content = response.json()
            #if response.ok:
            #    response = train_response(time=t, modelName=name, version=version, status="deployed", response="Train in %ds and deployed to the prediction server" %t, score=score, deploy_status="deployed", deploy_response=content)
            #    return Response( response, status=status.HTTP_201_CREATED)
            #else:
            #    response = train_response(time=t, modelName=name, version=version, status="trained", response="Train  in %d but not deployed to the prediction server (deployement failed)" %t, score=score, deploy_status="failed", deploy_response=content)
            #    return Response(response, status=status.HTTP_201_CREATED)
        #else:
        #    response = train_response(time=t, modelName=config["meta"]["name"], status="trained", response="Train  in %d but not saved (low score)" %t, score=score)
        #    return Response(response, status=status.HTTP_200_OK)