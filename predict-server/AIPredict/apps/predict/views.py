from django.http import JsonResponse
from rest_framework import viewsets, status
from rest_framework.response import Response
from django.core.exceptions import ValidationError

import pandas as pd
import shutil
import os

from AIPredict.utils.version import VersionController
from AIPredict.apps.predict.utils.predict import Predictor
from AIPredict.apps.predict.utils.deploy import BundleDeployer
from AIPredict.utils.validators import DeployBundleValidator, DataValidator


class DeployBundle(viewsets.ViewSet):

    def create(self, request):
        # gets the file from the request
        try:
            filename, file = DeployBundleValidator(request.FILES).validate()
            name, version = BundleDeployer(filename, file).deploy()
        except ValidationError as e:
            shutil.rmtree("./bundles/temp/")
            os.mkdir("./bundles/temp/")
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        # create bundle
        bundle = VersionController(name, version)
        bundle.set_item("algorithm", "status", "deployed")
        return JsonResponse(bundle.serialize(), status=status.HTTP_201_CREATED)

    def activate(self, request, *args, **kwargs):
        # gets the request bundle version
        name = kwargs["bundle"]
        version = kwargs["version"]
        # Load bundle and activate it
        bundle = VersionController(name, version)
        bundle.activate()
        return Response(bundle.serialize(), status=status.HTTP_200_OK)


class Prediction(viewsets.ViewSet):

    def predict(self, request, *args, **kwargs):
        # gets the request bundle version
        name = kwargs["bundle"]
        version = kwargs["version"]
        bundle = VersionController(name, version)
        if not bundle.is_active():
            return Response("The bundle %s v%s is not activated. Please activate it with /activate/%s/%s/ to activate it."
                            % (name, version, bundle, version),
                            status=status.HTTP_403_FORBIDDEN)

        # gets the model and the preprocessing dictionnary
        model = bundle.get_model()
        preprocessing = bundle.get_category("preprocessing")
        predictor = Predictor(preprocessing=preprocessing, framework=bundle.get_item(
            "meta", "framework"), model=model)
        # extracts data from the request
        data = pd.DataFrame(request.data)
        try:
            data = DataValidator(data, bundle.get_bundle()).validate()
        except ValidationError as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

        try:
            prediction = predictor.predict(data=data)
            return JsonResponse(prediction)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
