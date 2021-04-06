import zipfile
import json
from pathlib import Path, PurePath
import os
import shutil
import pickle
import numpy as np
import dill
import keras.models
import uuid


from django.core.files.uploadedfile import InMemoryUploadedFile
from AIPredict.settings.production import BUNDLE_PATH
from AIPredict.apps.predict.validators import validate_archive_content, validate_bundle_meta

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
    validate_bundle_meta(bundle)
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

    return name, version, str(path)

def upload_files(archive:InMemoryUploadedFile):
    #uploads the archive to ./bundle/temp/uuid
    temp_path, archive_name = handle_uploaded_file(archive)
    #unzips the archive and then deletes it
    unzip_bundle(temp_path, archive_name)
    #stores the bundle.json and the model.pkl files to their final location ./bundle/[bundle_name]/[bundle_version] 
    # and extracts the needed data to generate the bundle model
    name, version, path = store_bundle(temp_path)
    return name, version, path

def remove_files(name, version):
    path = build_bundle_path(name=name, version=version)
    # deletes the version folder (but keep the bundle folder)
    shutil.rmtree(path)
    bundle_path = build_bundle_path(name)
    # if the bundle has no other imported version, deletes it
    if os.listdir(bundle_path) == []:
        shutil.rmtree(bundle_path)

def get_model(path):
    framework = get_framework(path)

    if framework == "custom":
        with open(path / "model.pkl", "rb") as m:
            model = dill.load(m)
        return model

    elif framework == "keras":
        return keras.models.load_model(path / "model.h5")

    else:
        with open(path / "model.pkl", "rb") as m:
            model = pickle.load(m)
    return model

def get_preprocessing(path):
    with open(path / "bundle.json", "rb") as d:
        data = json.load(d)
    return data["preprocessing"]

def get_framework(path):
    with open(path / "bundle.json", "rb") as f:
        bundle = json.load(f)
    return bundle["meta"]["framework"]

def build_bundle_path(name=None, version=None, target=None):
    path = Path(".", "bundles")
    if name:
        path = path / name
        if version != None:
            v = "v"+str(version)
            path = path / v
            if target:
                path = path / target
    return path