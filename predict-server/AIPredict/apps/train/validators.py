from typing import Dict
import re
import importlib


from django.core.exceptions import ValidationError


def validate_config_meta(meta:Dict[str, object]) -> Dict[str, object]:
    """Checks meta fields in the configuration json

    Args:
        meta (Dict[str, object]): The meta to validate

    Returns:
        Dict[str, object]: The validated meta
    """
    required_fields = {"name": str, "version": int, "language":str, "framework":str, "URLDataSources":str, "SearchLoaderID":str, "secret":str}
    validate_fields("meta", meta, required_fields)
    #checks if name is correct to allow http calls (alphanumeric, - and _ are accepted)
    if not re.match('^[\w-]+$',meta["name"]):
        raise ValidationError("The model name (in the configuration meta) supports only alphanumeric, _ and -.")
    return meta

def validate_config_algo(algo:Dict[str, object]) -> Dict[str, object]:
    """Checks algorithm fields in the configuration json

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

def validate_config_dataset(dataset:Dict[str, object]) -> Dict[str, object]:
    """Checks dataset fields in the configuration json

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
            if not isinstance(data_dict["ifna"], eval(domain_type)):
                raise ValidationError("dataset.data_config.%s ifna type is incorrect (is %s but must be %s)" %(data, domain, domain_type))
    
    return dataset

def validate_config_parameters(param:Dict[str, object]) -> Dict[str, object]:
    """Checks parameters fields in the configuration json

    Args:
        param (Dict[str, object]): The parameters field to validate

    Returns:
        Dict[str, object]: The validated parameters field
    """
    required_fields = {"hyperparameters": dict, "metrics": str, "min_score": float}
    validate_fields("parameters", param, required_fields)
    return param

def validate_config_preprocessing(preprocessing:Dict[str, object]) -> Dict[str, object]:
    return preprocessing

def validate_config(config:Dict[str, object]) -> Dict[str, object]:
    validate_config_meta(config["meta"])
    validate_config_algo(config["algorithm"])
    validate_config_dataset(config["dataset"])
    validate_config_parameters(config["parameters"])
    validate_config_preprocessing(config["preprocessing"])
    return config

def validate_request(bundle, version):
    pass

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