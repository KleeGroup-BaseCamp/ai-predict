from django.shortcuts import render
from rest_framework import viewsets, status
from rest_framework.response import Response
from django.http.response import JsonResponse
from django_q.tasks import AsyncTask

import json
from pathlib import Path
import time

from AIPredict.apps.train.validators import validate_config, validate_request
from AIPredict.apps.train.utils.train import Trainer, async_train, async_score, sync_train, sync_score
from AIPredict.apps.train.utils.tools import build_model_class, get_data, train_response, score_response
from AIPredict.apps.train.utils.files import save, send, get_config, delete_files
from AIPredict.apps.predict.utils.files import get_model
from django.core.exceptions import ValidationError


class TrainModel(viewsets.ViewSet):

    def deploy(self, request):
        config = request.data
        try:
            validate_config(config)
        except ValidationError as e:
            response = train_response(modelName=config["meta"]["name"], status="failed", response=str(e))
            return Response(response, status=status.HTTP_400_BAD_REQUEST)
        try:
            res = self.train(config)
            return res
        except Exception as e:
            response = train_response(modelName=config["meta"]["name"], status="failed", response=str(e))
            return Response(response, status=status.HTTP_400_BAD_REQUEST)
        
    
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

        path = Path(".", "bundles", "train", bundle, "v"+str(version))
        config = get_config(path)
        model = get_model(path)
        #get dataset
        X, y = get_data(config["dataset"])
        #get parameters
        params = config["parameters"]
        cv = params["cv"]
        metrics = params["metrics"]

        try:
            start = time.time()
            score = sync_score(model, metrics, cv, X, y)
            t = time.time() - start
            response = score_response(modelName=bundle, status="succeed", version=version, time=t, score=score)
        except Exception as e:
            response = score_response(modelName=bundle, status="failed", version=version)

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
        model_class = algo["name"]
        #prepare parameters for the Trainer
        params = config["parameters"]
        params["package"] = package
        params["model_class"] = model_class
        #init Trainer
        trainer = Trainer(**params)
        #get dataset
        X_train, y_train = get_data(config["dataset"])

        #train
        model = sync_train(trainer, X_train, y_train)
        #if the score is greater than a threshold value, the bundle is saved
        aim = params["min_score"]
        metrics = trainer.metrics
        cv = trainer.cv
        score = sync_score(model, metrics, cv, X=X_train, y=y_train)
        t = time.time() - start
        if  score >= aim:
            path, name, version = save(model, config, 0, score)
            response = send(path)
            content = response.json()
            if response.ok:
                response = train_response(time=t, modelName=name, version=version, status="deployed", response="Train in %ds and deployed to the prediction server" %t, score=score, deploy_status="deployed", deploy_response=content)
                return Response( response, status=status.HTTP_201_CREATED)
            else:
                response = train_response(time=t, modelName=name, version=version, status="trained", response="Train  in %d but not deployed to the prediction server (deployement failed)" %t, score=score, deploy_status="failed", deploy_response=content)
                return Response(response, status=status.HTTP_201_CREATED)
        else:
            response = train_response(time=t, modelName=config["meta"]["name"], status="trained", response="Train  in %d but not saved (low score)" %t, score=score)
            return Response(response, status=status.HTTP_200_OK)