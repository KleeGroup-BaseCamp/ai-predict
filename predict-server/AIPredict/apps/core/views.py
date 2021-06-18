import os
import random

from django.shortcuts import render
from rest_framework import viewsets, status
from rest_framework.response import Response

from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.utils.bundle import Bundle
from AIPredict.apps.core.utils.indicators import performance


class BundlesLister(viewsets.ViewSet):

    def bundles(self, request, *args, **kwargs):
        path = BUNDLE_PATH / "standard"
        names = os.listdir(path)
        res = []
        for name in names:
            bundle_path = path / name
            data = {"name": name, "active": False,
                    "use": random.randint(0, 100)}
            versions = os.listdir(bundle_path)
            scores = []
            for version in versions:
                version_path = bundle_path / version
                bundle = Bundle(name, int(version[1:]))
                scores.append(bundle.get_item(
                    "algorithm", "score")["scoreMean"])
                if bundle.is_active():
                    data["active"] = True
                # TODO use
            data["performance"] = performance(scores)
            res.append(data)
        return Response(res, status=status.HTTP_200_OK)
