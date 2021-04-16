from django.shortcuts import render
from django.http import Http404, JsonResponse
from rest_framework import viewsets, status, views
from rest_framework.response import Response
from django.core.exceptions import ValidationError

import pandas as pd
import json
import numpy as np
import shutil, os

from AIPredict.apps.predict.models import Bundle
from AIPredict.apps.predict.serializers import BundleSerializer
from AIPredict.apps.predict.utils.files import upload_files, remove_files, get_model, build_bundle_path, get_bundle_item, get_framework, get_auto_deployed_bundles
from AIPredict.apps.predict.utils.response import PredictResponseEncoder, parse_response
from AIPredict.apps.predict.utils.predict import Predictor
from AIPredict.apps.predict.validators import validate_file, validate_archive, validate_data

class DeployBundle(viewsets.ModelViewSet):

    queryset = Bundle.objects.all()
    serializer_class = BundleSerializer

    def create(self, request):
        #gets the file from the request
        try:
            file = validate_file(request.FILES)
            archive = validate_archive(file['archive'])
            name, version = upload_files(archive)
        except ValidationError as e:
            shutil.rmtree("./bundles/temp/")
            os.mkdir("./bundles/temp/")
            return Response({"error":str(e)}, status=status.HTTP_400_BAD_REQUEST)
        deploy_bundle = self.deploy(name, version)
        return JsonResponse(deploy_bundle, status=status.HTTP_201_CREATED)
    
    def auto_deploy(self, request):
        bundles = get_auto_deployed_bundles()
        deploy_bundles = {"deployed_bundles": []}
        for bundle in bundles:
            try:
                deploy_bundle = self.deploy(bundle[0], bundle[1], auto=True)
                deploy_bundles["deployed_bundles"].append(deploy_bundle)
            except ValidationError as e:
                return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        return JsonResponse(deploy_bundles, status=status.HTTP_201_CREATED)

    def deploy(self, name, version, auto=False):
        serializer = self.get_serializer(data={"name":name, "version": version, "auto": auto})
        # checks validity
        serializer.is_valid(raise_exception=True)
        # creates and save the bundle in the database
        self.perform_create(serializer)
        return serializer.data

    def activate(self, request, *args, **kwargs):
        # gets the request bundle version
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        # gets the instance to activate
        to_update = Bundle.objects.filter(name=bundle, version=version)
        # checks if the instance exists
        if len(to_update) == 0:
            return Response("The bundle %s v%s does not exist" %(bundle, version), status=status.HTTP_404_NOT_FOUND)
        # checks if this bundle has already an active version and if so, deactivates it 
        activated_instance = Bundle.objects.filter(name=bundle, activated=True)
        if len(activated_instance) > 0:
            activated_instance.update(activated=False)
        # activates the instance
        to_update.update(activated=True)
        return Response(status=status.HTTP_200_OK)

    def destroy(self, request, *args, **kwargs):
        # gets the request bundle version
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        # gets the instance to remove
        to_remove = Bundle.objects.filter(name=bundle, version=version)
        #checks if the instance 
        if len(to_remove) == 0:
            return Response("The bundle %s v%s does not exist" %(bundle, version), status=status.HTTP_404_NOT_FOUND)
        # removes files from ./bundles
        auto = to_remove[0].auto
        path = build_bundle_path(bundle=bundle, version=version, auto=auto)
        remove_files(bundle, path)
        # removes the instance from the database
        self.perform_destroy(to_remove)
        return Response(status=status.HTTP_204_NO_CONTENT)

class Prediction(viewsets.ViewSet):

    def predict(self, request, *args, **kwargs):
        # gets the request bundle version
        bundle = kwargs["bundle"]
        version = kwargs["version"]
        instance = Bundle.objects.filter(name=bundle, version=version)[0]
        if not instance.activated:
            try:
                activated_instance = Bundle.objects.filter(name=bundle, activated=True)[0]
                return Response("The bundle %s v%s is not activated. Please activate it with /activate/%s/%s/ or use %s v%s." 
                    %(bundle, version, bundle, version, bundle, activated_instance.version),
                    status=status.HTTP_403_FORBIDDEN)
            except IndexError:
                return Response("The bundle %s has no activated version. Please use /activate/%s/%s/ to activate version %s" 
                    %(bundle, bundle, version, version),
                    status=status.HTTP_403_FORBIDDEN)

        # gets the model and the preprocessing dictionnary
        auto = instance.auto
        path = build_bundle_path(bundle=bundle, version=version, auto=auto)
        model = get_model(path)
        preprocessing = get_bundle_item(path, "preprocessing")
        algo = get_bundle_item(path, "algorithm")
        predictor = Predictor(preprocessing=preprocessing, framework=get_framework(path), model=model)
        
        #extracts data from the request        
        data = pd.DataFrame(request.data)

        try:
            data = validate_data(data, path)
        except ValidationError as e:
            return Response(str(e), status=status.HTTP_400_BAD_REQUEST)

        try:
            prediction = predictor.predict(data=data)
            return JsonResponse(prediction)
        except Exception as e:
            return Response(str(e), status=status.HTTP_400_BAD_REQUEST)