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
    name = config["meta"]["name"]
    path = Path(".", "bundles", "train", name)
    if path.exists() and len(os.listdir(path)) > version:
        version = len(os.listdir(path))
    v = "v"+str(version)
    path = path / v
    path.mkdir(parents=True, exist_ok=True)
    return path, name, version

def save_model(path, model, config):
    with open(path / "model.pkl", "wb") as p:
        pickle.dump(model, p)

def save_config(path, config, version, score):
    config["meta"]["version"] = version
    config["algorithm"]["score"] = score
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
