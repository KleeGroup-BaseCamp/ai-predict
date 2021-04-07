import json
from pathlib import Path
from pandas import DataFrame
import numpy as np

from django.core.exceptions import ValidationError
from django.core.files.uploadedfile import InMemoryUploadedFile
from django.utils.datastructures import MultiValueDict

from AIPredict.apps.predict.models import Bundle


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

def validate_bundle_meta(meta:dict):

    # checks required fields
    if "name" in meta and isinstance(meta["name"], str):
        name = meta["name"]
    else:
        raise ValidationError("The bundle metadata must contain the name (str) of the bundle")
    if "version" in meta and isinstance(meta["version"], int):
        version = meta["version"]
    else:
        raise ValidationError("The bundle metadata must contain the version (int) of the bundle")
    if not ("framework" in meta and isinstance(meta["framework"], str)):
        raise ValidationError("The bundle metadata must contain the framework (str) of the bundle")
    
    #check unicity
    instance = Bundle.objects.filter(name=name, version=version)
    if instance:
        raise ValidationError("The bundle name and version must be unique together")
    return meta

def validate_bundle_algorithm(algo:dict):
    if not ("name" in algo and isinstance(algo["name"], str)):
        raise ValidationError("The bundle algorithm field requires the algorithm name. e.g. RandomForestClassifier")
    if not ("type" in algo and isinstance(algo["type"], str)):
        raise ValidationError("The bundle algorithm field requires the algorithm type. e.g. Classifier")
    if algo["type"] in ["Classifier", "classifier"]:
        if not ("labels" in algo and isinstance(algo["labels"], list)):
            raise ValidationError("The bundle algorithm field requires the classifier labels. e.g. [\"Class1\", \"Class2\"]")
    return algo

def validate_bundle_data(bundle:dict):
    req_fields = {"domain", "is_label"}

    data = bundle["data"]
    domains = bundle["domains"]

    for feature_name in data:
        feature = data[feature_name]
        if req_fields - set(feature.keys()):
            raise ValidationError("All fields in the bundle data must have a domain and is_label attributes")
        if feature["domain"] not in domains:
            print(domains, feature["domain"])
            raise ValidationError("The domain of %s feature is not matching any known domain." %feature_name)
        if not feature["is_label"]:
            if "ifna" not in feature:
                raise ValidationError("The feature %s is not a label and requires a ifna attributes" %feature_name)
    
    for domain in domains:
        if domains[domain] not in domains_to_dtypes:
            raise ValidationError("The domain %s  is badly configured. Allowed domain configuration are %s" %(domain, str(list(domains_to_dtypes.keys()))))
    return bundle

def validate_bundle(bundle:dict):
    primary_fields = {"meta", "algorithm", "data", "domains", "preprocessing"}
    diff = primary_fields - set(bundle.keys())
    if  diff:
        raise ValidationError("Some fields are missing in bundle.json: %s" %str(diff))
    validate_bundle_meta(bundle["meta"])
    validate_bundle_algorithm(bundle["algorithm"])
    validate_bundle_data(bundle)
    return bundle

def validate_data(data:DataFrame, path:Path):
    with open(path / "bundle.json", "rb") as d:
        bundle = json.load(d)
    
    domains = bundle["domains"]
    features = bundle["data"]
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
    #check data types and columns
    to_check = DataFrame(data.dtypes).transpose()
    scheme = DataFrame([dtypes], columns=columns)
    if not scheme.equals(to_check):
        raise ValidationError("The input data does not match the prerequisite.")
    
    #
    for i in range(0, len(columns)):
        column = columns[i]
        na = nan[i]
        if na == "_required":
            if data[[column]].isnull().values.any():
                raise ValidationError("%s is required but NaN.")
        else:
            data[[column]] = data[[column]].fillna(na)
    return data
       

domains_to_dtypes = {
    "Integer": np.dtype(np.int64),
    "Float": np.dtype(np.float64),
    "String": np.dtype(str)
}