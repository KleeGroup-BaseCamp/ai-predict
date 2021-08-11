from pathlib import Path
import json
from typing import List, Tuple, Dict
import dill
import pickle
import os
import shutil
import zipfile
import numpy as np

from AIPredict.utils.imports import *
from AIPredict.settings.production import BUNDLE_PATH

class VersionController:

    def __init__(self, name: str, version: int, load_bundle: bool = True) -> None:
        """Constructor

        Args:
            name (str): name of the bundle
            version (int): version of the bundle
            load_bundle (bool): load the bundle.json
        """
        # set basic parameters
        self.name = name
        self.version = version
        self.path = Path(".", BUNDLE_PATH, "standard",
                         name, str("v"+str(version)))

        # extract bundle
        self.bundle = None
        self.status = None
        if load_bundle:
            with open(self.path / "bundle.json", "r") as jsonBundle:
                self.bundle = json.load(jsonBundle)
            # get status
            self.status = self.bundle["meta"]["status"]

    def get_path(self) -> Path:
        return self.path

    def get_bundle(self) -> Dict:
        return self.bundle

    def get_status(self) -> str:
        return self.status

    def is_active(self) -> bool:
        return self.status == "active"

    def load_bundle(self):
        with open(self.path, "r") as jsonBundle:
            self.bundle = json.load(jsonBundle)

    def get_model(self):
        """Read the binarized model from the stored bundle

        Returns:
            object: the trained model. Can be for instance a sklearn model like RandomForestClassifier()
        """
        framework = self.bundle["meta"]["framework"]
        if framework == "custom":
            with open(self.path / "model.pkl", "rb") as m:
                model = dill.load(m)
            return model

        elif framework == "keras":
            if kmodel:
                return keras.models.load_model(self.path / "model.h5")
            else:
                raise ImportError("No module name keras")

        else:
            if sklearn or xgboost:
                with open(self.path / "model.pkl", "rb") as m:
                    model = pickle.load(m)
            else:
                raise ImportError("No module is imported to read the model")
        return model

    def get_category(self, category: str):
        """get a category from bundle.json.

        Args:
            category (str): the category to get from the bundle. Must be one of : meta, algorithm, data, preprocessing

        Returns:
        Dict[str, object]: the corresponding bundle category
        """
        return self.bundle[category]

    def get_item(self, category: str, item: str):
        """get an item from bundle.json.

        Args:
            category (str): the category to get from the bundle.
            item (str): the item to get from the category.

        Returns:
        Dict[str, object]: the corresponding bundle category
        """
        return self.bundle[category][item]

    def set_item(self, category: str, item: str, value):
        """updates an item of bundle.json.

        Args:
            category (str): the category of the item to update get from the bundle.
            item (str): the item to update from the category.
            value (object): the new value

        Returns:
        Dict[str, object]: the corresponding bundle category
        """
        self.bundle[category][item] = value
        with open(self.path / "bundle.json", "w") as f:
            json.dump(self.bundle, f)

    def deactivate(self):
        self.set_item("algorithm", "status", "deployed")

    def activate(self):
        root_path = Path(BUNDLE_PATH, "standard", self.name)
        versions = os.listdir(self.path.parent)
        for version in versions:
            v = int(version[1:])
            if self.version != v:
                other = VersionController(self.name, v)
                other.deactivate()
        self.set_item("meta", "status", "active")

    def build_model_class(self):
        """Build the model associated to the bundle.

        Args:
            category (str): the category of the item to update get from the bundle.
            item (str): the item to update from the category.
            value (object): the new value

        Returns:
        Dict[str, object]: the corresponding bundle category
        """
        package = self.get_item("algorithm", "package")
        model = self.get_item("algorithm", "name")
        modules = package.split(".")
        if modules[0] in ["sklearn", "keras", "xgboost"]:
            modules = modules[1:]
        else:
            raise ImportError("The package %s is not imported" % modules[0])
        modules.append(model)
        return ".".join(modules)

    def remove(self):
        shutil.rmtree(self.path)

    def serialize(self):
        res = {
            "name": self.name,
            "version": self.version,
            "status": self.status,
            "score": np.round(self.get_item("algorithm", "score")["scoreMean"], 3)
        }
        return res

    def archive(self):
        zipname = self.name + "-v" + str(self.version) + ".zip"
        zip_path = self.path / zipname
        with zipfile.ZipFile(zip_path, 'w') as zipObj:
            zipObj.write(self.path / "bundle.json", "bundle.json")
            if os.path.exists(self.path / "model.pkl"):
                zipObj.write(self.path / "model.pkl", "model.pkl")
            if os.path.exists(self.path / "model.h5"):
                zipObj.write(self.path / "model.h5", "model.h5")
        return zip_path

    def remove_archive(self):
        os.remove(self.path / str(self.name + "-v" + str(self.version) + ".zip"))
    
    def used(self):
        with open(self.path, "r") as jsonBundle:
            bundle = json.load(jsonBundle)
        
        bundle["use"] += 1

        with open(self.path, "w") as jsonBundle:
            json.dump(bundle, jsonBundle)
