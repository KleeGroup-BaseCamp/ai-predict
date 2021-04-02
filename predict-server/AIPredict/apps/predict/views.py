from django.shortcuts import render
from django.http import Http404, JsonResponse
from rest_framework import viewsets, status, views
from rest_framework.response import Response

import pandas as pd

from AIPredict.apps.predict.models import Bundle
from AIPredict.apps.predict.serializers import BundleSerializer
from AIPredict.apps.predict.utils.files import *
from AIPredict.apps.predict.utils.predict import Predictor

class DeployBundle(viewsets.ModelViewSet):

    queryset = Bundle.objects.all()
    serializer_class = BundleSerializer

    def create(self, request):
        #gets the file from the request
        data = request.FILES
        #uploads the archive to ./bundle/temp
        handle_uploaded_file(request.FILES['archive'])
        #unzips the archive and then deletes it
        unzip_bundle()
        #stores the bundle.json and the model.pkl files to their final location ./bundle/[bundle_name]/[bundle_version] 
        # and extracts the needed data to generate the bundle model
        name, version, path = store_bundle()
        # initializes the serializer
        serializer = self.get_serializer(data={"name":name, "version": version, "path":path})
        # checks validity
        try:
            serializer.is_valid(raise_exception=True)
        except ValueError:
            return Response("A more recent version of this bundle has already been imported", status=status.HTTP_400_BAD_REQUEST)
        # creates and save the bundle in the database
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return JsonResponse(serializer.data, status=status.HTTP_201_CREATED)
    
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
        remove_files(to_remove[0].path)
        # removes the instance from the database
        self.perform_destroy(to_remove)
        return Response(status=status.HTTP_204_NO_CONTENT)

class Prediction(views.APIView):

    def post(self, request, *args, **kwargs):
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
        path = instance.path + "/"
        model = get_model(path)
        preprocessing = get_preprocessing(path)
        # sets up a predictor instance
        predictor = Predictor(preprocessing=preprocessing, framework=get_framework(path), model=model)
        
        #extracts data from the request
        
        body_unicode =request.body.decode('utf-8')
        body = json.loads(body_unicode)
        
        data = pd.DataFrame(body)
        res = {}
        prediction = predictor.predict(data=data)
        if np.all(str(pred).isnumeric() for pred in prediction):
            if len(np.shape(prediction))>1:
                res["predictionVector"] = prediction
            else:
                res["predictionNumeric"] = prediction
        else:
            res["predictionLabel"] = prediction
        res["predictionProba"] = predictor.predict_proba(data=data)
        try:
            explanation = predictor.explain_prediction(data).values
            if len(np.shape(explanation)) == 2:
                res["explanation1D"] = predictor.explain_prediction(data).values
            elif len(np.shape(explanation)) == 3:
                res["explanation2D"] = predictor.explain_prediction(data).values
        except Exception as e:
            res["explanation1D"] = None
            res["explanation2D"] = None
        res = parse_response(res)
        res = json.dumps(res, cls=NumpyArrayEncoder)
        return JsonResponse(json.loads(res))