from django.shortcuts import render
from django.http import Http404, JsonResponse
from rest_framework import viewsets, status, views
from rest_framework.response import Response
from django.core.exceptions import ValidationError

import pandas as pd
import json
import numpy as np
import shutil
import os

from AIPredict.utils.bundle import Bundle
from AIPredict.apps.predict.utils.files import upload_files
from AIPredict.apps.predict.utils.response import PredictResponseEncoder, parse_response
from AIPredict.apps.predict.utils.predict import Predictor
from AIPredict.apps.predict.validators import validate_file, validate_archive, validate_data


class DeployBundle(viewsets.ViewSet):

    def create(self, request):
        # gets the file from the request
        try:
            file = validate_file(request.FILES)
            archive = validate_archive(file['archive'])
            name, version = upload_files(archive)
        except ValidationError as e:
            shutil.rmtree("./bundles/temp/")
            os.mkdir("./bundles/temp/")
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        # create bundle
        bundle = Bundle(name, version)
        bundle.set_item("algorithm", "status", "deployed")
        return JsonResponse(bundle.serialize(), status=status.HTTP_201_CREATED)

    def activate(self, request, *args, **kwargs):
        # gets the request bundle version
        name = kwargs["bundle"]
        version = kwargs["version"]
        # Load bundle and activate it
        bundle = Bundle(name, version)
        bundle.activate()
        return Response(bundle.serialize(), status=status.HTTP_200_OK)

    def destroy(self, request, *args, **kwargs):
        # gets the request bundle version
        name = kwargs["bundle"]
        version = kwargs["version"]
        # gets the bundle to remove
        bundle = Bundle(name, version, False)
        bundle.remove()
        return Response(status=status.HTTP_204_NO_CONTENT)


class Prediction(viewsets.ViewSet):

    def predict(self, request, *args, **kwargs):
        # gets the request bundle version
        name = kwargs["bundle"]
        version = kwargs["version"]
        bundle = Bundle(name, version)
        if not bundle.is_active():
            return Response("The bundle %s v%s is not activated. Please activate it with /activate/%s/%s/ to activate it."
                            % (name, version, bundle, version),
                            status=status.HTTP_403_FORBIDDEN)

        # gets the model and the preprocessing dictionnary
        model = bundle.get_model()
        preprocessing = bundle.get_category("preprocessing")
        algo = bundle.get_category("algorithm")
        predictor = Predictor(preprocessing=preprocessing, framework=bundle.get_item(
            "meta", "framework"), model=model)
        # extracts data from the request
        data = pd.DataFrame(request.data)
        try:
            data = validate_data(data, bundle.get_path())
        except ValidationError as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

        try:
            prediction = predictor.predict(data=data)
            return JsonResponse(prediction)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
