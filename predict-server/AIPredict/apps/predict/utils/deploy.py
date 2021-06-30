import uuid
import zipfile
import os
import shutil
import json
from pathlib import Path
from typing import Tuple

from django.core.files.uploadedfile import InMemoryUploadedFile

from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.utils.validators import BundleCreationValidator


class BundleDeployer:

    def __init__(self, filename:str, file: InMemoryUploadedFile):
        self.file = file
        self.filename = filename

        self.id = uuid.uuid4()
        self.temp_path = Path(BUNDLE_PATH, "temp", str(self.id))

        self.name = None
        self.version = None

    def deploy(self) -> Tuple[str, int]:
        self._upload_archive()
        self._extract()
        self._store()
        return self.name, self.version

    def _upload_archive(self):
        self.temp_path.mkdir(parents=False, exist_ok=False)
        with open(self.temp_path / self.filename, 'wb+') as destination:
            for chunk in self.file.chunks():
                destination.write(chunk)

    def _extract(self):
        try:
            with zipfile.ZipFile(self.temp_path / self.filename, 'r') as zip_ref:
                zip_ref.extractall(self.temp_path)
            os.remove(self.temp_path / self.filename)
        except FileNotFoundError:
            raise FileNotFoundError("Cannot find a file called bundle.zip")
    
    def _store(self):
        # read the bundle data
        json_path = self.temp_path / "bundle.json"
        with open(json_path, "r") as f:
            bundle = json.load(f)

        # checks the bundle.json
        BundleCreationValidator(bundle).validate()
        self.name = bundle["meta"]["name"]
        self.version = bundle["meta"]["version"]

        # create the folder ./bundles/[bundle_name]/[bundle_version]
        path = Path(".", "bundles", "standard", self.name, "v"+str(self.version))
        Path(path).mkdir(parents=True, exist_ok=True)

        # moves the files to the folder (and checks if model.pkl exists)
        shutil.move(json_path, path / "bundle.json")
        if "model.pkl" in os.listdir(self.temp_path):
            shutil.move(self.temp_path / "model.pkl", path / "model.pkl")
        elif "model.h5" in os.listdir(self.temp_path):
            shutil.move(self.temp_path / "model.h5", path / "model.h5")

        shutil.rmtree(self.temp_path)