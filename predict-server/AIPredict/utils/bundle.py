from pathlib import Path
import json
from typing import List, Tuple, Dict
import dill
import pickle
import os
import shutil
import random
import numpy as np
import zipfile

from AIPredict.utils.imports import *
from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.utils.version import VersionController


class BundleController:

    def __init__(self, name: str) -> None:
        """Constructor

        Args:
            name (str): name of the bundle
            version (int): version of the bundle
            load_bundle (bool): load the bundle.json
        """
        # set basic parameters
        self.name = name
        self.path = Path(".", BUNDLE_PATH, "standard",
                         name)

    def get_path(self) -> Path:
        return self.path

    def get_performance(self, scores: List) -> float:
        return np.round(100 * np.mean(scores)/(1+np.std(scores)*len(scores)))

    def get_versions(self):
        return [int(v[1:]) for v in os.listdir(self.path)]

    def is_active(self) -> bool:
        for version in self.get_versions():
            bundle = BundleController(self.name, version)
            if bundle.is_active():
                return True
        return False

    def remove(self):
        shutil.rmtree(self.path)

    def serialize(self):
        res = {
            "name": self.name,
            "performance": None,
            "use":  random.randint(0, 100),
            "active": False,
            "activeVersion": None,
            "versions": self.get_versions(),
            "scores": []
        }
        raw_scores = []
        best_score = 0
        for version in res["versions"]:
            bundle = VersionController(self.name, version)
            res["datasource"] = bundle.get_item("meta", "datasource")
            res["description"] = bundle.get_item("meta", "description")
            score = bundle.get_item(
                "algorithm", "score")["scoreMean"]
            raw_scores.append(score)
            if score >= best_score:
                best_score = score
                res["bestVersion"] = version
            if bundle.is_active():
                res["active"] = True
                res["activeVersion"] = version
        res["performance"] = self.get_performance(raw_scores)
        res["scores"] = np.round(raw_scores, 2)
        return res

    def version_serialize(self):
        res = []
        for version in self.get_versions():
            res.append(VersionController(self.name, version).serialize())
        return res

    def archive(self):
        zipname = self.name + ".zip"
        zip_path = self.path / zipname
        with zipfile.ZipFile(zip_path, 'w') as zipObj:
            zipObj.write(self.path, self.name)
            for version_folder in os.listdir(self.path):
                if not (len(version_folder) > 4 and version_folder[-3:] == "zip"):
                    pathObj = Path(self.path, version_folder)
                    pathArc = Path(self.name, version_folder)
                    zipObj.write(pathObj, pathArc)
                    zipObj.write(pathObj / "bundle.json",
                                 pathArc / "bundle.json")
                    if os.path.exists(pathObj / "model.pkl"):
                        zipObj.write(pathObj / "model.pkl",
                                     pathArc / "model.pkl")
                    if os.path.exists(pathObj / "model.h5"):
                        zipObj.write(pathObj / "model.h5",
                                     pathArc / "model.h5")

    def remove_archive(self):
        os.remove(self.path / str(self.name+".zip"))
