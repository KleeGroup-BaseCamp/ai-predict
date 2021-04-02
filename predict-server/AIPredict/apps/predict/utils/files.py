import zipfile
import json
from json import JSONEncoder
from pathlib import Path
import os
import shutil
import pickle
import numpy as np
import dill
import keras.models

class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()

        if isinstance(obj, np.int64) or isinstance(obj, np.int32):
            return int(obj)
        
        if isinstance(obj, np.float64) or isinstance(obj, np.float32):
            return float(obj)

        return JSONEncoder.default(self, obj)

def handle_uploaded_file(f):
    with open('./bundles/temp/bundle.zip', 'wb+') as destination:
        for chunk in f.chunks():
            destination.write(chunk)

def unzip_bundle():
    temp_path = "./bundles/temp/"
    try:
        with zipfile.ZipFile(temp_path+"bundle.zip", 'r') as zip_ref:
                zip_ref.extractall(temp_path)
        os.remove(temp_path+"bundle.zip")
    except FileNotFoundError:
        raise FileNotFoundError("Cannot find a file called bundle.zip", status=status.HTTP_404_NOT_FOUND)

def store_bundle():
    #checks if bundle.json exists
    try:
        with open("./bundles/temp/bundle.json", "r") as f:
            data = json.load(f) 
    except FileNotFoundError:
        raise FileNotFoundError("Cannot find a file called bundle.json in the archive")

     #checks the metadata
    try:    
        meta = data["meta"]
    except KeyError:
        raise KeyError("bundle.json must contains a metadata field")
    try:
        name = meta["name"]
        version = meta["version"]
    except KeyError:
        raise KeyError("The metadata must contain the name (str) and the version (int) of the bundle")

    #create the folder ./bundles/[bundle_name]/[bundle_version]
    path = "./bundles/"+name+"/v"+str(version)
    Path(path).mkdir(parents=True, exist_ok=True)

    #moves the files to the folder (and checks if model.pkl exists)
    shutil.move("./bundles/temp/bundle.json", path+"/bundle.json")

    if "model.pkl" in os.listdir("./bundles/temp/"):
        shutil.move("./bundles/temp/model.pkl", path+"/model.pkl")
    elif "model.h5" in os.listdir("./bundles/temp/"):
        shutil.move("./bundles/temp/model.h5", path+"/model.h5")
    else:
       raise FileNotFoundError("Cannot find a file called model.pkl or model.h5 in the archive")
    return name, version, path

def remove_files(path):
    decomposed_path = path.split("/")
    # deletes the version folder (but keep the bundle folder)
    shutil.rmtree(path)
    bundle_path = "/".join(decomposed_path[:-1])
    # if the bundle has no other imported version, deletes it
    if os.listdir(bundle_path) == []:
        shutil.rmtree(bundle_path)

def get_model(path):

    framework = get_framework(path)

    if framework == "custom":
        with open(path + "model.pkl", "rb") as m:
            model = dill.load(m)
        return model

    elif framework == "keras":
        return keras.models.load_model(path+"model.h5")

    else:    
        with open(path + "model.pkl", "rb") as m:
            model = pickle.load(m)
    return model

def get_preprocessing(path):
    with open(path + "bundle.json", "rb") as d:
        data = json.load(d)
    return data["preprocessing"]

def parse_response(res):
    # removes key with none values from the prediction result dictionnary
    if "predictionNumeric" in res:
        npredict = len(res["predictionNumeric"])
    elif "predictionVector" in res:
        npredict = len(res["predictionVector"])
    else:
        npredict = len(res["predictionLabel"])
    res = {k:v for k,v in res.items() if not v is None}
    res = {"predictionList": [dict(zip(res,t)) for t in zip(*res.values())]}
    return res

def get_framework(path):
    with open(path+"bundle.json", "rb") as f:
        bundle = json.load(f)
    return bundle["meta"]["framework"]