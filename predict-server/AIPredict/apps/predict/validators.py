import json
from pathlib import Path
from pandas import DataFrame
import numpy as np
import os
from typing import Dict
import re
import importlib

from django.core.exceptions import ValidationError
from django.core.files.uploadedfile import InMemoryUploadedFile
from django.utils.datastructures import MultiValueDict


def validate_file(file:MultiValueDict):
    if not "archive" in file:
        raise ValidationError("An zip archive must be attached to the POST request")
    return file

def validate_archive(archive:InMemoryUploadedFile):
    if archive.content_type != "application/zip":
        raise ValidationError("An zip archive must be attached to the POST request")
    return archive

def validate_archive_content(temp_path:Path):
    json_path = temp_path / "bundle.json"
    pkl_path = temp_path / "model.pkl"
    h5_path = temp_path / "model.h5"
    
    if not json_path.is_file():
        raise ValidationError("Cannot find a file called bundle.json in the archive")
    
    if not (pkl_path.is_file() or h5_path.is_file()):
        raise ValidationError("Cannot find a file called model.pkl or model.h5 in the archive")
    
    return temp_path

### Validation of the bundle

def validate_bundle_meta(meta:Dict[str, object]) -> Dict[str, object]:
    """Checks meta fields in the bundle json

    Args:
        meta (Dict[str, object]): The meta to validate

    Returns:
        Dict[str, object]: The validated meta
    """
    required_fields = {"name": str, "version": int, "language":str, "framework":str, "URLDataSources":str, "SearchLoaderID":str, "secret":str}
    validate_fields("meta", meta, required_fields)
    #checks if name is correct to allow http calls (alphanumeric, - and _ are accepted)
    name = meta["name"]
    if not re.match('^[\w-]+$',meta["name"]):
        raise ValidationError("The bundle name (in the configuration meta) supports only alphanumeric, _ and -.")
    #checks unicity of name and version together
    version = meta["version"]
    instances = None
    if instances:
        raise ValidationError("The bundle name and version must be unique together")
    return meta


def validate_bundle_algorithm(algo:Dict[str, object]) -> Dict[str, object]:
    """Checks algorithm fields in the bundle json

    Args:
        algo (Dict[str, object]): The algorithm field to validate

    Returns:
        Dict[str, object]: The validated algorithm field
    """
    required_fields = {"package": str, "name": str, "type":str}
    validate_fields("algorithm", algo, required_fields)

    #checks if the package in installed
    package = algo["package"]
    model = algo["name"]
    try:
        i = importlib.import_module(package)
    except ModuleNotFoundError:
        raise ValidationError("The package %s is not installed on the server." %package)
    
    #checks if the model exists in the package
    if model not in dir(i):
        raise ValidationError(" %s not found in %s." %(model, package))

    return algo

def validate_bundle_dataset(dataset:Dict[str, object]) -> Dict[str, object]:
    """Checks dataset fields in the bundle json

    Args:
        dataset (Dict[str, object]): The dataset field to validate

    Returns:
        Dict[str, object]: The validated dataset field
    """
    required_fields = {"db_config": dict, "data_config": dict, "domains": dict}
    validate_fields("dataset", dataset, required_fields)

    #validate db_config
    db_config = dataset["db_config"]
    if not ("db_type" in db_config and isinstance(db_config["db_type"], str)):
        raise ValidationError("The dataset.db_config must include a db_type string to indicate the database type (e. g. postgresql")
    
    #validate domain
    domains = dataset["domains"]
    for domain in domains:
        domain_type = domains[domain]
        if not isinstance(domain_type, str):
            raise ValidationError("dataset.domains.%s must be filled as a string" %(domain))
        try:
            eval(domain_type)
        except NameError:
            raise ValidationError("dataset.domains.%s must match a python type" %(domain))
        if not isinstance(eval(domain_type), type):
            raise ValidationError("dataset.domains.%s must match a python type" %(domain))

    #validate data_config
    data_config = dataset["data_config"]
    data_required_field = {"domain": str, "is_label":bool}
    for data in data_config:
        data_dict = data_config[data]
        validate_fields("dataset.data_config.%s" %data, data_dict, data_required_field)
        #checks domain
        domain = data_dict["domain"]
        if not domain in domains:
            raise ValidationError("dataset.data_config.%s domain (%s) not found in dataset.domains" %(data, domain))
        #checks ifna if the data is a feature
        is_label = data_dict["is_label"]
        if not is_label:
            #check if ifna is filled
            if not "ifna" in data_dict:
                raise ValidationError("dataset.data_config.%s is a feature and thus need a ifna value of type %s" %(data, domain))
            #checks ifna type
            domain_type = domains[domain]
            if not data_dict["ifna"]=="_required":
                if not isinstance(data_dict["ifna"], eval(domain_type)): 
                    if not (eval(domain_type)==float and isinstance(data_dict["ifna"], int)):
                        raise ValidationError("dataset.data_config.%s ifna type is incorrect (is %s but must be %s)" %(data, str(type(data_dict["ifna"])), domain_type))
    
    return dataset

def validate_bundle_preprocessing(preprocessing:Dict[str, object]) -> Dict[str, object]:
    return preprocessing

def validate_bundle(bundle:dict):
    validate_bundle_meta(bundle["meta"])
    validate_bundle_algorithm(bundle["algorithm"])
    validate_bundle_dataset(bundle["dataset"])
    validate_bundle_preprocessing(bundle["preprocessing"])

    return bundle

def validate_data(data:DataFrame, path:Path):
    with open(path / "bundle.json", "rb") as d:
        bundle = json.load(d)
    
    domains = bundle["dataset"]["domains"]
    features = bundle["dataset"]["data_config"]
    columns = []
    dtypes = []
    nan = []
    for feature in features.keys():
        item = features[feature]
        if not item["is_label"]:
            columns.append(feature)
            domain = domains[item["domain"]]
            dtypes.append(domains_to_dtypes[domain])
            nan.append(item["ifna"])

    data_col = data.columns
    for col in data_col:
        if not col in columns:
            data = data.drop(columns=[col]) 
    #check data types and columns
    data = data[columns]
    to_check = DataFrame(data.dtypes).transpose()
    scheme = DataFrame([dtypes], columns=columns)
    if not scheme.equals(to_check):
        raise ValidationError("The input data does not match the prerequisite.")
    for i in range(0, len(columns)):
        column = columns[i]
        na = nan[i]
        if na == "_required":
            if data[[column]].isnull().values.any():
                raise ValidationError("%s is required but NaN.")
        else:
            data[[column]] = data[[column]].fillna(na)
    return data


###
def validate_auto_deployed_bundle(path, bundle, version):
    list_dir = list_dir = os.listdir(path / bundle / version)
    if not "bundle.json" in list_dir:
        raise ValidationError("The auto deployed bundle \"%s %s\" can not be load because bundle.json is missing" %(bundle, version))
    if not ("model.pkl" in list_dir or "model.h5" in list_dir):
        raise ValidationError("The auto deployed bundle \"%s %s\" can not be load because a binary model is missing" %(bundle, version))
    with open(path / bundle / version / "bundle.json", "rb") as b:
        bundle = json.load(b)
    validate_bundle(bundle)
    return path, bundle, version


def validate_fields(item:str, config_item:Dict[str, object], required_fields:Dict[str, object]):
    missing = []
    wrong_type = []
    for field in required_fields.keys():
        if not field in config_item:
            missing.append(field)
        elif not isinstance(config_item[field], required_fields[field]):
            wrong_type.append((field, required_fields[field]))
    
    #if some fields are missing, raise ValidationError
    if missing:
        raise ValidationError("Some required fields of %s are missing in the configuration json : %s" %(item, str(missing)))
    #if some fields are badly typed, raise ValidationError
    if wrong_type:
        raise ValidationError("Some fields of %s do not have the correct type. The correct types are %s" %(item, str(wrong_type)))

domains_to_dtypes = {
    "int": np.dtype(np.int64),
    "float": np.dtype(np.float64),
    "str": np.dtype(str)
}

domains_to_types = {
    "Integer": int,
    "Float": float,
    "String": str
}