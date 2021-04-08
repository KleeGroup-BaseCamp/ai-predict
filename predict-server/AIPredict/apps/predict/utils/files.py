import zipfile
import json
from pathlib import Path, PurePath
import os
import shutil
import pickle
import numpy as np
import dill
import uuid
import logging
from typing import List, Tuple, Dict
try:
    import keras.models as kmodel
except:
    kmodel = None

from django.core.files.uploadedfile import InMemoryUploadedFile
from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.apps.predict.validators import validate_archive_content, validate_bundle, validate_auto_deployed_bundle
from AIPredict.apps.predict.models import Bundle
from django.core.exceptions import ValidationError

try:
    import keras
except:
    keras = None
try:
    import sklearn
except:
    sklearn = None
try:
    import xgboost
except:
    xgboost = None

def handle_uploaded_file(archive:InMemoryUploadedFile):
    archive_name = archive.name
    id = uuid.uuid4()
    folder_path = BUNDLE_PATH / "temp"/ str(id)
    Path(folder_path).mkdir(parents=False, exist_ok=False)
    file_path = folder_path / archive_name
    with open(file_path, 'wb+') as destination:
        for chunk in archive.chunks():
            destination.write(chunk)
    return folder_path, archive_name

def unzip_bundle(temp_path, archive_name):
    file_path = temp_path / archive_name
    try:
        with zipfile.ZipFile(file_path, 'r') as zip_ref:
                zip_ref.extractall(temp_path)
        os.remove(file_path)
    except FileNotFoundError:
        raise FileNotFoundError("Cannot find a file called bundle.zip")

def store_bundle(temp_path):
    #checks if bundle.json exists
    validate_archive_content(temp_path)

    #read the bundle data
    json_path = temp_path / "bundle.json"
    with open(json_path, "r") as f:
        bundle = json.load(f)

    #checks the metadata
    validate_bundle(bundle)
    name = bundle["meta"]["name"]
    version = bundle["meta"]["version"]

    #create the folder ./bundles/[bundle_name]/[bundle_version]
    path = Path(".", "bundles", name, "v"+str(version))
    Path(path).mkdir(parents=True, exist_ok=True)

    #moves the files to the folder (and checks if model.pkl exists)
    shutil.move(json_path, path / "bundle.json")
    if "model.pkl" in os.listdir(temp_path):
        shutil.move(temp_path / "model.pkl", path / "model.pkl")
    elif "model.h5" in os.listdir(temp_path):
        shutil.move(temp_path / "model.h5", path / "model.h5")

    shutil.rmtree(temp_path)

    return name, version

def upload_files(archive:InMemoryUploadedFile):
    #uploads the archive to ./bundle/temp/uuid
    temp_path, archive_name = handle_uploaded_file(archive)
    #unzips the archive and then deletes it
    unzip_bundle(temp_path, archive_name)
    #stores the bundle.json and the model.pkl files to their final location ./bundle/[bundle_name]/[bundle_version] 
    # and extracts the needed data to generate the bundle model
    name, version = store_bundle(temp_path)
    return name, version

def remove_files(name:str, path:Path):
    """Remove the stored files of a bundle

    Args:
        name (str): bundle name
        path (Path): bundle path
    """
    # deletes the version folder (but keep the bundle folder)
    shutil.rmtree(path / "")
    bundle_path = build_bundle_path(name)
    # if the bundle has no other imported version, deletes it
    if os.listdir(bundle_path) == []:
        shutil.rmtree(bundle_path)

def get_model(path:Path) -> object:
    """Read the binarized model from the stored bundle

    Args:
        path (Path): path of the bundle

    Returns:
        object: the trained model. Can be for instance a sklearn model like RandomForestClassifier()
    """
    framework = get_framework(path)

    if framework == "custom":
        with open(path / "model.pkl", "rb") as m:
            model = dill.load(m)
        return model

    elif framework == "keras":
        if kmodel:
            return keras.models.load_model(path / "model.h5")
        else:
            raise ImportError("No module name keras")

    else:
        if sklearn or xgboost:
            with open(path / "model.pkl", "rb") as m:
                model = pickle.load(m)
        else:
            raise ImportError("No module is imported to read the model")
    return model

def get_bundle_item(path:Path, items:str) -> Dict[str, object]:
    """get a field from bundle.json.

    Args:
        path (Path): the path to the bundle folder, e.g. ./bundle/name/v0
        items (str): the item to get from the bundle. Must be one of : meta, algorithm, data, preprocessing

    Returns:
       Dict[str, object]: the corresponding item
    """
    with open(path / "bundle.json", "rb") as d:
        bundle = json.load(d)
    
    if isinstance(items, list) and all(isinstance(item, str) for item in items):
        res = []
        for item in items:
            res.append(bundle[item])
        return res
    else:
        return bundle[items]

def get_framework(path:Path) -> str:
    """get the model framework

    Args:
        path (Path): the path to the bundle folder, e.g. ./bundle/name/v0

    Returns:
        str: get the model framework e.g. scikit-learn
    """
    with open(path / "bundle.json", "rb") as f:
        bundle = json.load(f)
    return bundle["meta"]["framework"]

def build_bundle_path(bundle:str=None, version:int=None, target:str=None, auto:bool=False) -> Path:
    """ build a path in the ./bundles folder to get files and models

    Args:
        bundle (str, optional): the name of the bundle. Defaults to None.
        version (int, optional): the version of the bundle. Defaults to None.
        target (str, optional): the files required. Must be None, bundle.json, model.pkl or model.h5. Defaults to None.
        auto (bool, optional): True if the bundle is auto deployed. Defaults to False.

    Returns:
        Path: built path
    """
    #build the path to a given bundle
    if auto:
        path = Path(".", "bundles", "auto_deploy")
    else:
        path = Path(".", "bundles")
    if bundle:
        path = path / bundle
        if version != None:
            v = "v"+str(version)
            path = path / v
            if target:
                path = path / target
    return path

def get_auto_deployed_bundles() -> List[Tuple[str, int]]:
    """Get all bundles that need to be auto deployed

    Returns:
        List[Tuple[str, int]]: List of all (bundle_name, version) to auto deploy 
    """

    path = BUNDLE_PATH / "auto_deploy"
    bundles = []
    #find all bundles in ./bundles/auto_deploy
    for bundle in os.listdir(path):
        #find all version of a given bundle
        for version in os.listdir(path / bundle):
            v = int(version[1:])
            #validate the bundle but skip if it already exists
            try:
                validate_auto_deployed_bundle(path, bundle, version)
                #add (bundle, version) to bundles
                bundles.append((bundle, v))
            except Exception as e:
                if not e == ValidationError("The bundle name and version must be unique together"):
                    raise e 
    return bundles