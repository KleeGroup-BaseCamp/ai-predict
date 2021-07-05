import os
import logging

from django.core.validators import ValidationError
from django.http import HttpResponse
from rest_framework import viewsets, status
from rest_framework.response import Response

from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.utils.version import VersionController
from AIPredict.utils.bundle import BundleController
from AIPredict.utils.validators import BundleRequestValidator

logger = logging.getLogger(__name__)

class BundlesView(viewsets.ViewSet):

    def bundles(self, request, *args, **kwargs):
        """
        Handle request to get the list of all bundles.

        Used by the AI Predict Manager UI.

        Endpoint:
            GET : bundles/
        
        Response:
            [
                {
                    "name": "bundle_name",
                    "performance": 37.0 ,
                    "use": 123,
                    "active": true,
                    "activeVersion": 1,
                    "versions": [0, 1, 3],
                    "scores": [0.35, 0.55, 0.51],
                    "bestVersion": 1,
                    "description": "description of your data",
                    "datasource": "http://url/of/your/data/"                    
                }
            ]
        """ 
        #get all bundles      
        path = BUNDLE_PATH / "standard"
        names = os.listdir(path)
        res = []
        #get the list of all bundle details
        for name in names:
            res.append(BundleController(name).serialize())
        return Response(res, status=status.HTTP_200_OK)

    def bundle(self, request, *args, **kwargs):
        """
        Handle request to get details of a bundles or content of a version.

        Used by the AI Predict Manager UI.

        Endpoint:
            bundle (GET): bundle/<str: bundle>/
            version (GET): bundle/<str: bundle>/<int:version>/
        
        Response:
            bundle (GET):
                    {
                        "bundle":
                        {
                            "name": "bundle_name",
                            "performance": 37.0 ,
                            "use": 123,
                            "active": true,
                            "activeVersion": 1,
                            "versions": [0, 1, 3],
                            "scores": [0.35, 0.55, 0.51],
                            "bestVersion": 1,
                            "description": "description of your data",
                            "datasource": "http://url/of/your/data/"                   
                        },
                        "versions:
                        [
                            {
                                "name":"bundle_name",
                                "version":0,
                                "status":"trained",
                                "score":0.35
                            },
                            {
                                "name":"bundle_name",
                                "version":1,
                                "status":"active",
                                "score":0.55
                            },
                            {
                                "name":"bundle_name",
                                "version":3,
                                "status":"trained",
                                "score":0.53
                            },
                        ]
                    }
            version (GET): bundle.json
        """ 
        try:
            BundleRequestValidator("version" in kwargs, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)
            
        name = kwargs.pop('bundle')
        # single version
        if "version" in kwargs:
            version = kwargs.pop('version')
            return Response(VersionController(name, version).get_bundle(), status=status.HTTP_200_OK)
        # whole bundle
        else:
            bundle = BundleController(name)
            res = {
                "bundle": bundle.serialize(),
                "versions": bundle.version_serialize()
            }
            return Response(res, status=status.HTTP_200_OK)

    def remove(self, request, *args, **kwargs):
        """
        Handle a bundle or version removing request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            bundle (DELETE): remove/<str:bundle>/
            version (DELETE): remove/<str:bundle>/<int:version>/
        """
        # request validation
        try:
            BundleRequestValidator("version" in kwargs, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            name = kwargs.pop("bundle")
            version = None
            # remove a unique version
            if "version" in kwargs:
                version = kwargs.pop("version")
                logger.info("Start %s v%s removing" % (name, version))
                bundle = VersionController(name, version, False)
                bundle.remove()
            # remove a whole bundle
            else:
                logger.info("Start %s removing" % (name))
                bundle = BundleController(name)
                bundle.remove()
            logger.info("Removing succeeded")
            return Response(status=status.HTTP_204_NO_CONTENT)
        except Exception as e:
            if version:
                logger.error("%s v%s removing failed : %s" % (name, version, e))
            else:
                logger.error("%s removing failed : %s" % (name, e))
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

    def download(self, request, *args, **kwargs):
        """
        Handle a bundle or version download request.
        
        Used by the AI Predict Manager UI.

        Endpoint:
            bundle (GET): download/<str:bundle>/
            version (GET): download/<str:bundle>/<int:version>/
        """
        # request validation
        try:
            BundleRequestValidator("version" in kwargs, **kwargs).validate()
        except ValidationError as e:
            return Response({"error": e}, status=status.HTTP_400_BAD_REQUEST)

        try:
            name = kwargs.pop("bundle")
            version = None
            # zip a unique version
            if "version" in kwargs:
                version = kwargs.pop("version")
                logger.info("Start %s v%s download" % (name, version))
                bundle = VersionController(name, version, False)
                zip_path = bundle.archive()
            # zip a whole bundle
            else :
                logger.info("Start %s download" % (name))
                bundle = BundleController(name)
                zip_path = bundle.archive()
            # download the archive
            with open(zip_path, 'rb') as fh:
                response = HttpResponse(
                    fh.read(), content_type="application/zip")
                response['Content-Disposition'] = 'inline; filename=' + \
                    os.path.basename(zip_path)
            bundle.remove_archive()
            logger.info("Download succeeded")
            return response
        except Exception as e:
            if version:
                logger.error("%s v%s download failed : %s" % (name, version, e))
            else:
                logger.error("%s download failed : %s" % (name, e))
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
