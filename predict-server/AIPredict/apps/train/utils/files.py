import uuid
import os
from pathlib import Path
import json
import pickle
from zipfile import ZipFile
import shutil

def create_folder(config, version):
    # gets name to build path
    name = config["meta"]["name"]
    path = Path(".", "bundles", "standard", name)
    # checks if the version is already trained or activated, then create a new version, else update the version
    status = None
    if "status" in config["meta"].keys():
        status = config["meta"]["status"]
    if status in ["trained", "active", "deployed"]:
        versions = os.listdir(path)

        def sort(x):
            return int(x[1:])

        last_version = sorted(versions, key=sort)[-1]

        # create new version
        version = int(last_version[1:]) + 1
        v = "v" + str(version)
        path = path / v
        os.mkdir(path)
    else:
        v = "v" + str(version)
        path = path / v
        if os.path.exists(path):
            shutil.rmtree(path)
        os.makedirs(path)
    return path, name, version


def save_model(path: Path, model, config: dict):
    """
        Save a pickled trained machine learning model
    """
    with open(path / "model.pkl", "wb") as p:
        pickle.dump(model, p)


def save_config(path, config, version, score=None):
    config["meta"]["version"] = version
    if not score:
        score = {"scoreMean": None, "scoreStd": None}
    config["algorithm"]["score"] = score
    config["meta"]["status"] = "trained"
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


def get_config(path):
    with open(path / "bundle.json") as j:
        config = json.load(j)
    return config


def delete_files(path):
    shutil.rmtree(path)
