import uuid
import os
from pathlib import Path
import json
import pickle
from zipfile import ZipFile
import shutil

import requests

from AIPredict.apps.predict.views import DeployBundle

def create_folder(config, version):
    # gets name to build path
    name = config["meta"]["name"]
    path = Path(".", "bundles", name)
    # checks if the version is already trained or activated, then create a new version, else update the version  
    status = config["algorithm"]["status"]
    if status == "trained" or status=="active":
        last_version = os.listdir(path)[-1]
        #create new version
        version = int(last_version[1:]) + 1 
        v = "v" + str(version)
        path  = path / v
        os.mkdir(path)
    else:
        v = "v" + str(version)
        path = path / v
        shutil.rmtree(path)
        os.mkdir(path)
    return path, name, version

def save_model(path:Path, model, config:dict):
    """
        Save a pickled trained machine learning model
    """
    with open(path / "model.pkl", "wb") as p:
        pickle.dump(model, p)

def save_config(path, config, version, score):
    config["meta"]["version"] = version
    config["algorithm"]["score"] = score
    config["algorithm"]["status"] = "trained"
    with open(path / "bundle.json", "w") as p:
        json.dump(config, p)

def save(model, config, version, score):
    path, name, version = create_folder(config, version)
    save_model(path, model, config)
    save_config(path, config, version, score)
    return path, name, version

def archive_files(path):
    with ZipFile(path/'bundle.zip', 'w') as zipObj:
        zipObj.write(path/"bundle.json", "bundle.json")
        zipObj.write(path/"model.pkl", "model.pkl")

def send(path):
    archive_files(path)
    with open(path/"bundle.zip", 'rb') as f:  
        response = requests.post('http://127.0.0.1:8000/deploy/', files={'archive': ("bundle.zip", f, 'application/zip')})
    return response

def get_config(path):
    with open(path / "bundle.json") as j:
        config = json.load(j)
    return config

def delete_files(path):
    shutil.rmtree(path)