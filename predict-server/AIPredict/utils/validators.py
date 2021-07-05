from typing import Dict, List
import logging
import importlib
from cerberus import Validator
import builtins
import numpy as np
from pandas import DataFrame
from pathlib import Path
import os

from django.core.validators import ValidationError
from django.core.files.uploadedfile import InMemoryUploadedFile
from django.utils.datastructures import MultiValueDict

from AIPredict.settings.development import TRAIN_DB
from AIPredict.settings.production import BUNDLE_PATH

logger = logging.getLogger(__name__)
class BundleCreationValidator:
    
    def __init__(self, config:Dict[str, object]) -> None:
        self.config = config
        self.validator = Validator()
        self.model = None
    
    def validate(self) -> Dict[str, object]:
        self._validata_metadata(self.config["meta"])
        self._validate_algorithm(self.config["algorithm"])
        self._validate_dataset(self.config["dataset"])
        self._validate_parameters(self.config["parameters"])
        return self.config

    def _validata_metadata(self, metadata:Dict[str, str]) -> Dict[str, str]:
        schema = {
            "name": {"required":True, "type": "string", "regex": "^[a-z0-9-_]+$"},
            "version": {"required":True, "type": "integer", "min": 0},
            "language": {"type": "string"},
            "framework": {"type": "string"},
            "datasource": {"type": "string"},
            "description": {"type": "string"},
            "status": {"type": "string"}
        }
        is_valid = self.validator.validate(metadata, schema)
        if not is_valid:
            raise ValidationError(self._parse_error(self.validator.errors))
        return metadata

    def _validate_algorithm(self, algorithm:Dict[str, str]) -> Dict[str, str]:
        schema = {
            "name": {"required":True, "type": "string"},
            "package": {"required":True, "type": "string"},
            "score": {"type": "dict", 'schema': {'scoreMean': {'type': 'float'}, 'scoreStd': {'type': 'float'}}}
        }
        #check scheme
        is_valid = self.validator.validate(algorithm, schema)
        if not is_valid:
            raise ValidationError(self._parse_error(self.validator.errors))
        #check package
        try:
            i = importlib.import_module(algorithm["package"])
        except ModuleNotFoundError:
            raise ValidationError("No package named %s found on the server" %algorithm["package"])
        #check module
        try:
            self.model = getattr(i, algorithm["name"])
        except AttributeError:
            raise ValidationError("No function named %s found on the server" %(algorithm["package"]+"."+algorithm["name"]))        
        
        return algorithm

    def _validate_dataset(self, dataset:Dict[str, Dict[str, object]]) -> Dict[str, Dict[str, object]]:
        db_config_scheme = {
            "key": {"required":True, "type": "string"},
            "table": {"required":True, "type": "string"},
            "database": {"required":True, "type": "string"}
        }
        data_config_scheme = {
            "data": {
            "type": "list", 
            "schema":{
                "type":"dict",
                "schema":{
                    "id": {"type": "integer"},
                    "name":{"type":"string", "required":True},
                    "domain":{"type":"string", "required":True},
                    "is_label":{"type":"boolean", "required":True},
                    "ifna":{"type": ["number", "string"], "nullable":True},
                    "preprocessing":{"type":"string"}
                    }
                }
            }
        }
        #check db_config scheme
        is_valid = self.validator.validate(dataset["db_config"], db_config_scheme)
        if not is_valid:
            raise ValidationError(self._parse_error(self.validator.errors))
        if not dataset["db_config"]["database"] in TRAIN_DB:
            raise ValidationError("The database key %s is unknown." %dataset["db_config"]["key"])
        #check data_config scheme
        is_valid = self.validator.validate({"data": dataset["data_config"]}, data_config_scheme)
        if not is_valid:
            raise ValidationError(self._parse_error())
        
        #checks domain and ifna values
        for feature in dataset["data_config"]:
            if feature["domain"] not in dataset["domains"]:
                raise ValidationError("The domain of %s is unknown." %feature["name"])
            domain = [getattr(builtins, d) for d in dir(builtins) if isinstance(getattr(builtins, d), type) and d==dataset["domains"][feature["domain"]]][0]
            ifna = feature["ifna"]
            if ifna and ifna != "_required" and not isinstance(ifna, domain):
                if domain in [int, float]:
                    try:
                        domain(ifna)
                    except:
                        raise ValidationError("The type of the %s impute value is %s but a %s is expected." %(feature["name"], type(feature["ifna"]).__name__, domain.__name__))
                    continue
                elif domain == bool and not ifna in ["True", "False"]:
                    raise ValidationError("The type of the %s impute value is %s but a %s is expected." %(feature["name"], type(feature["ifna"]).__name__, domain.__name__))
        return dataset

    def _validate_parameters(self, parameters:Dict[str, object]) -> Dict[str, object]:
        if not "n_jobs" in parameters or not isinstance(parameters["n_jobs"], int):
            raise ValidationError("The number of jobs (int) must be included in the parameters")
        if not "cv" in parameters or not isinstance(parameters["cv"], int):
            raise ValidationError("The cross validation split numbr (int) must be included in the parameters")
        if not "metrics" in parameters or not isinstance(parameters["metrics"], str):
            raise ValidationError("A metrics (str) must be included in the parameters")
        if not "grid_search" in parameters or not isinstance(parameters["grid_search"], bool):
            raise ValidationError("A boolean must be included in the parameters to define wether or not grid search is enabled.")
        self._validate_hyperparameters(parameters["hyperparameters"], parameters["grid_search"])
        return parameters
            
    def _validate_hyperparameters(self, hyperparameters:Dict[str, object], grid_search:List[object]) -> Dict[str, object]:
        hyperkeys = list(self.model().get_params().keys())
        for key in hyperparameters:
            if grid_search and not isinstance(hyperparameters[key], list):
                raise ValidationError("Hyperparameters values must be lists if grid search is enabled.")
            if key not in hyperkeys:
                raise ValidationError("The hyperparameter %s is unexpected." %(key))
        return hyperparameters

    def _parse_error(self, errors:Dict[str, List[str]]) -> str:
        error_string = ""
        for error in errors:
            error_string += error + " " + ",".join(errors[error]) + ", "
        logger.error(error_string)
        return error_string

class DeployBundleValidator:
    
    def __init__(self, file):
        self._validate_file(file)
        self.file = list(file.values())[0]
        self.filename = list(file.keys())[0]

    def validate(self) -> InMemoryUploadedFile:
        self.validate_archive()
        return self.filename, self.file

    def _validate_file(self, file:MultiValueDict):
        if not file:
            raise ValidationError("An zip archive must be attached to the POST request")
        return file
    
    def validate_archive(self):
        if self.file.content_type != "application/zip":
            raise ValidationError("An zip archive must be attached to the POST request")

def validate_data(data:DataFrame, bundle:Dict[str, object]):
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




class DataValidator:

    def __init__(self, data:DataFrame, bundle:Dict[str, object]):
        self.data = data
        self.bundle = bundle
    
    def validate(self):
        domains = self.bundle["dataset"]["domains"]
        features = self.bundle["dataset"]["data_config"]
        columns = []
        dtypes = []
        nan = []
        for feature in features.keys():
            item = features[feature]
            if not item["is_label"]:
                columns.append(feature)
                domain = domains[item["domain"]]
                dtypes.append(self._convert_to_dtype(domain))
                nan.append(item["ifna"])

        data_col = self.data.columns
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
    
    def _convert_to_dtype(self, key_type:str):
        types_to_dtypes = {
            "int": np.dtype(np.int64),
            "float": np.dtype(np.float64),
            "str": np.dtype(str)
        }
        return types_to_dtypes[key_type]

class BundleRequestValidator:

    def __init__(self,  with_version:bool=False, **kwargs):
        if not "bundle" in kwargs:
            raise ValidationError("A bundle name must be provided.")
        self.name = kwargs.pop("bundle")
        self.version = None
        if with_version:
            if not "version" in kwargs:
                raise ValidationError("A version number must be provided.")
            self.version = kwargs.pop("version")
        
    def validate(self):
        path = Path(BUNDLE_PATH, "standard", self.name)
        if self.version:
            path = path / str("v" + str(self.version))
        if not os.path.exists(path):
            if self.version:
                raise ValidationError("The bundle %s v%s does not exist." %(self.name, self.version))
            raise ValidationError("The bundle %s does not exist." %(self.name))
