import json
from pathlib import Path

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

def validate_bundle_meta(bundle:dict):

    if "meta" in bundle:
        meta = bundle["meta"]
    else:
        raise ValidationError("bundle.json must contains a metadata field")
    if "name" in meta and isinstance(meta["name"], str):
        name = meta["name"]
    else:
        raise ValidationError("The metadata must contain the name (str) of the bundle")
    if "version" in meta and isinstance(meta["version"], int):
        version = meta["version"]
    else:
        raise ValidationError("The metadata must contain the version (int) of the bundle")
    instance = Bundle.objects.filter(name=name, version=version)
    if instance:
        raise ValidationError("The bundle name and version must be unique together")
    return bundle