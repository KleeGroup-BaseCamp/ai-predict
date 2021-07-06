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
        #check schema
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
        db_config_schema = {
            "key": {"required":True, "type": "string"},
            "table": {"required":True, "type": "string"},
            "database": {"required":True, "type": "string"}
        }
        data_config_schema = {
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
        #check db_config schema
        is_valid = self.validator.validate(dataset["db_config"], db_config_schema)
        if not is_valid:
            raise ValidationError(self._parse_error(self.validator.errors))
        if not dataset["db_config"]["database"] in TRAIN_DB:
            raise ValidationError("The database key %s is unknown." %dataset["db_config"]["key"])
        #check data_config schema
        is_valid = self.validator.validate({"data": dataset["data_config"]}, data_config_schema)
        if not is_valid:
            raise ValidationError(self._parse_error(self.validator.errors))
        
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

class DataValidator:

    def __init__(self, data:DataFrame, config:Dict[str, object]):
        self.data = data
        self.data_schema = config["dataset"]["data_config"]
        self.domain = config["dataset"]["domains"]
    
    def validate(self):
        self._validate_schema()
        print("Done")

    def _validate_schema(self) -> DataFrame:
        length = len(self.data)
        schema = {}
        for column in self.data_schema:
            value_schema = self._get_value_schema(column)
            column_schema = {"type": "dict", "schema":{}}
            for i in range(length):
                column_schema["schema"][i] = value_schema
            schema[column["name"]] = column_schema

        validator = Validator()
        is_valid = validator.validate(self.data.to_dict(), schema)
        if not is_valid:
            raise ValidationError(self._parse_error(validator.errors))

    def _get_value_schema(self, value_conf:Dict[str, object]) -> Dict[str, object]:
        value_schema = {"required": True}
        value_type = self.domain[value_conf["domain"]]
        #typing
        if value_type == "str":
            value_schema["type"] = "string"
        elif value_type == "int":
            value_schema["type"] = "integer"
        elif value_type == "float":
            value_schema["type"] = "number"
        elif value_type == "bool":
            value_schema["type"] = "boolean"
        else:
            raise ValidationError("The domain of %s does not match any data type. Type is %s but must be linked to int, float, str or bool." %(value_conf["name"], value_type))
        
        #required
        if not value_conf["ifna"] == "_required":
            value_schema["nullable"] = True
        return value_schema
    
    def _parse_error(self, errors:Dict[str, List[str]]) -> str:
        return str(errors)
    

class TrainDataValidator(DataValidator):

    def __init__(self, X:DataFrame, y:DataFrame, config:Dict[str, object]):
        self.data_schema = config["dataset"]["data_config"]
        self.domain = config["dataset"]["domains"]
        self.data = X.copy(deep=True)
        y_size = len(y.columns)
        x_size = len(X.columns)
        y_values = y.values.transpose()
        y_columns = y.columns
        for i in range(y_size):
            self.data.insert(x_size, column=y_columns[i], value=y_values[i])
            x_size += 1
