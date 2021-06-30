import os
import random
from zipfile import ZipFile

from django.shortcuts import render
from django.http import HttpResponse
from rest_framework import viewsets, status
from rest_framework.response import Response

from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.utils.version import VersionController
from AIPredict.utils.bundle import BundleController


class BundlesView(viewsets.ViewSet):

    def bundles(self, request, *args, **kwargs):
        path = BUNDLE_PATH / "standard"
        names = os.listdir(path)
        res = []
        for name in names:
            res.append(BundleController(name).serialize())
        return Response(res, status=status.HTTP_200_OK)

    def bundle(self, request, *args, **kwargs):
        name = kwargs.pop('bundle')
        if "version" in kwargs:
            version = kwargs.pop('version')
            return Response(VersionController(name, version).get_bundle(), status=status.HTTP_200_OK)
        else:
            bundle = BundleController(name)
            res = {
                "bundle": bundle.serialize(),
                "versions": bundle.version_serialize()
            }
            return Response(res, status=status.HTTP_200_OK)

    def remove(self, request, *args, **kwargs):
        if "version" in kwargs:
            bundle = VersionController(
                kwargs["bundle"], kwargs["version"], False)
            bundle.remove()
        elif "bundle" in kwargs:
            bundle = BundleController(kwargs["bundle"])
            bundle.remove()
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        return Response(status=status.HTTP_204_NO_CONTENT)

    def download(self, request, *args, **kwargs):
        if "version" in kwargs:
            bundle = VersionController(
                kwargs["bundle"], kwargs["version"], False)
            path = bundle.get_path()
            zipname, zip_path = bundle.archive()
        elif "bundle" in kwargs:
            bundle = BundleController(kwargs["bundle"])
            path = bundle.get_path()
            zipname, zip_path = bundle.archive()
        with open(zip_path, 'rb') as fh:
            response = HttpResponse(
                fh.read(), content_type="application/zip")
            response['Content-Disposition'] = 'inline; filename=' + \
                os.path.basename(zip_path)
        bundle.remove_archive()
        return response
