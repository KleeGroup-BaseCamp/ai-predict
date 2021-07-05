from django.http import JsonResponse
from rest_framework import viewsets, status
from rest_framework.response import Response
from django.core.exceptions import ValidationError

import logging
import pandas as pd
import shutil
import os

from AIPredict.utils.version import VersionController
from AIPredict.apps.predict.utils.predict import Predictor
from AIPredict.apps.predict.utils.deploy import BundleDeployer
from AIPredict.utils.validators import DeployBundleValidator, DataValidator, BundleRequestValidator

logger = logging.getLogger(__name__)
class DeployBundle(viewsets.ViewSet):

    def create(self, request):
        """
        Handle a bundle importation request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            POST: deploy/
        """
        try:
            filename, file = DeployBundleValidator(request.FILES).validate()
            name, version = BundleDeployer(filename, file).deploy()
            bundle = VersionController(name, version)
            bundle.set_item("meta", "status", "deployed")
        except ValidationError as e:
            shutil.rmtree("./bundles/temp/")
            os.mkdir("./bundles/temp/")
            logger.error("Bundle deployement failed : " + e)
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)
        
        logger.info("The bundle %s v%s has been deployed successfully" %(name, version))
        return JsonResponse(bundle.serialize(), status=status.HTTP_201_CREATED)

    def activate(self, request, *args, **kwargs):
        """
        Handle a bundle activation request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            PUT: activate/<str: bundle>/<int:version>/'
        """
        try:
            BundleRequestValidator(True, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)

        # gets the request bundle name and version
        name = kwargs.pop("bundle")
        version = kwargs.pop("version")
        # Load bundle and activate it
        bundle = VersionController(name, version)
        bundle.activate()
        return Response(bundle.serialize(), status=status.HTTP_200_OK)


class Prediction(viewsets.ViewSet):

    def predict(self, request, *args, **kwargs):
        """
        Handle a prediction request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            POST: predict/<str: bundle>/<int:version>/'
        
        Response:
            {
                'predictionList':
                [
                    {
                        'predictionLabel': 'Class1',
                        'predictionProba': [0.7134958505630493, 0.0031012804247438908, 0.28340286016464233]
                    }
                ]
            }
        """
        try:
            BundleRequestValidator(True, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)

        # gets the request bundle name and version
        name = kwargs.pop("bundle")
        version = kwargs.pop("version")

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
        """TODO
        try:
            data = DataValidator(data, bundle.get_bundle()).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)"""

        try:
            prediction = predictor.predict(data=data)
            return Response(prediction, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)
